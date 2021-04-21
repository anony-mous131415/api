package io.revx.api.service.audience.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.revx.api.audience.pojo.AppsFlyerAudienceCreateDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceSyncDto;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.Status;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.mysql.entity.LifeTimeAuthenticationEntity;
import io.revx.api.mysql.repo.LifeTimeTokenRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.security.ApiAuthorizationFilter;
import io.revx.api.security.SecurityConstants;
import io.revx.api.service.audience.AudienceUtils;
import io.revx.api.service.crm.impl.CrmServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.PixelDataScheduleDTO;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Function;
import static io.revx.api.security.SecurityConstants.IS_LIFETIME_TOKEN;

@Component
public class AppsFlyerAudienceServiceImpl {

    @Autowired
    LifeTimeTokenRepository lifeTimeTokenRepository;

    @Autowired
    DataPixelServiceImpl dataPixelServiceImpl;

    @Autowired
    AudienceUtils audienceUtils;

    @Autowired
    SegmentsRepository segmentsRepository;

    @Autowired
    AudienceServiceImpl audienceService;

    @Autowired
    AudienceCacheService audienceCacheService;

    @Autowired
    CustomESRepositoryImpl elastic;

    @Autowired
    ApplicationProperties properties;

    @Autowired
    ApiAuthorizationFilter apiAuthorizationFilter;

    @Autowired
    CrmServiceImpl crmService;

    private static final Logger logger = LogManager.getLogger(AppsFlyerAudienceServiceImpl.class);

    public Long createAppsFlyerAudience(AppsFlyerAudienceCreateDto appsFlyerAudienceDTO) throws Exception {
        logger.info("Inside createAppsFlyerAudience method. Creating AppsFlyer Audience for  : {} ", appsFlyerAudienceDTO);
        UserInfo ui = getUserInfo(appsFlyerAudienceDTO.getApi_key());
        AudienceDTO audienceDTO = createAudienceDTO(appsFlyerAudienceDTO, ui);
        Segments segmentsEntity;
        DataSourceType sourceType = DataSourceType.getById(audienceDTO.getDataSourceType());
        Long pixelId = dataPixelServiceImpl.createAdvertiserToPixel(audienceDTO.getAdvertiser(), sourceType);
        audienceDTO.setPixelId(pixelId);

        segmentsEntity = audienceUtils.getEntityFromDto(audienceDTO);
        segmentsRepository.save(segmentsEntity);
        audienceService.createRuleExpression(audienceDTO, segmentsEntity, null);
        Long containerId=segmentsEntity.getId();

        AudienceDTO responseAudDTO = new AudienceDTO();
        audienceUtils.populateAudienceDTO(segmentsEntity, responseAudDTO);

        elastic.save(getESDTO(responseAudDTO), TablesEntity.AUDIENCE);
        audienceCacheService.remove();

        return containerId;
    }

    public AudienceESDTO getESDTO(AudienceDTO audienceDTO){
        return AudienceUtils.getESDTO(audienceDTO);
    }

    // sets user information to security context
    public void setUserToSecurityContext(UserInfo ui){
        UsernamePasswordAuthenticationToken authentication = apiAuthorizationFilter.getAuthentication(ui);
        if (authentication != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);
    }



    public AudienceDTO createAudienceDTO(AppsFlyerAudienceCreateDto appsFlyerAudienceDTO, UserInfo userInfo) {
        AudienceDTO audienceDTO = new AudienceDTO();
        Advertiser advertiser =  userInfo.getAdvertisers().iterator().next();

        audienceDTO.setAdvertiser(advertiser);
        audienceDTO.setLicensee(userInfo.getSelectedLicensee());
        audienceDTO.setName(appsFlyerAudienceDTO.getName());
        audienceDTO.setUserDataType(2);
        audienceDTO.setActive(false);
        audienceDTO.setDataSourceType(2);

        return audienceDTO;
    }

    public UserInfo getUserInfoFromToken(String jwtToken) {
        UserInfo ui = null;
        boolean valid = validateAuthToken(jwtToken);
        if(valid)
            ui = getUserFromAccessToken(jwtToken);
        return ui;
    }

    // returns true only if the token is valid jwt token and a valid lifetime auth token
    public Boolean validateAuthToken(String jwtToken){
        String username = null;
        boolean isValid = false;
        try {
            username = getUsernameFromToken(jwtToken);
            logger.debug("Validating Token Against  : {}" , username);
        } catch (Exception e) {
            return false;
        }
        if (StringUtils.isNotBlank(username)) {
            isValid = validateLifeTimeAuthToken(jwtToken, username);
            return isValid;
        }
        return true;
    }

    // is lifeTime token if the token contains IS_LIFETIME_TOKN claim
    public Boolean isLifeTimeAuthToken(String token){
        Claims claims = getAllClaimsFromToken(token);
        return  claims.get(IS_LIFETIME_TOKEN) != null;
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SecurityConstants.SIGNING_KEY).parseClaimsJws(token)
                .getBody();
    }

    // returns true only if the token is lifeTimeAuthToken and is active
    public Boolean validateLifeTimeAuthToken(String token, String username){
        logger.debug("username : {}" , username);
        if(Boolean.FALSE.equals(isLifeTimeAuthToken(token)))
            return false;
        Optional<LifeTimeAuthenticationEntity> tokenEntity = lifeTimeTokenRepository.findByLifeTimeAuthToken(token);
        return  tokenEntity.isPresent() && tokenEntity.get().isActive();
    }

    public UserInfo getUserFromAccessToken(String token) {
        return UserInfo.deSerializeUser(getClaimFromToken(token, Claims::getIssuer));
    }


    public void syncAppsFlyerAudience(AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto) throws Exception {
        UserInfo userInfo = getUserInfo(appsFlyerAudienceSyncDto.getApi_key());
        ApiResponseObject<AudienceDTO> audienceObj = audienceService.getAudience(appsFlyerAudienceSyncDto.getContainer_id(), true);
        AudienceDTO audience = audienceObj.getRespObject();
        Long advFromToken =  ((Advertiser) userInfo.getAdvertisers().toArray()[0]).getId();
        Long advFromAudience = audience.getAdvertiser().getId();

        if(!advFromToken.equals(advFromAudience)){
            throw new ValidationException("");
        }
        String url = appsFlyerAudienceSyncDto.getUrl().replace(properties.getPartnerPullKey_Key(), properties.getPartnerPullKey_Value());
        validateUrl(url);
        ServerSyncCoordinatorDTO coordinator = crmService.getSyncCoordinatorByPixelId(audience.getPixelId());
        if(coordinator == null){
            PixelDataScheduleDTO pixelDataSchedule = new PixelDataScheduleDTO();
            pixelDataSchedule.setProtocol(5);
            pixelDataSchedule.setFrequencyUnit(3);
            pixelDataSchedule.setCompressionType(1);
            pixelDataSchedule.setFrequencyValue(1);
            pixelDataSchedule.setUrl(removeProtocol(url));
            audience.setPixelDataSchedule(pixelDataSchedule);
            audienceService.createCRMAudienceDetails(audience, false);
            Segments segment = segmentsRepository.findByIdAndLicenseeId (appsFlyerAudienceSyncDto.getContainer_id(), userInfo.getSelectedLicensee().getId());
            segment.setStatus(Status.ACTIVE);
            segmentsRepository.save(segment);
        }
        else {
            audience.getPixelDataSchedule().setUrl(removeProtocol(url));
            audienceService.createCRMAudienceDetails(audience, true);
            crmService.forceSyncAction(audience.getPixelId());
        }
    }

    private String removeProtocol(String url) {
        return url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","");
    }

    private void validateUrl(String url) throws ValidationException {
        UrlValidator urlValidator = new UrlValidator();
        if (! urlValidator.isValid(url)) {
           throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST, "Invalid URL");
        }
    }

    private UserInfo getUserInfo(String authKey) throws ValidationException {
        boolean valid = validateAuthToken(authKey);
        if (!valid)
            throw new ValidationException(ErrorCode.INVALID_ACCESS_TOKEN);
        UserInfo userInfo = getUserInfoFromToken(authKey);

        // token is not valid if User has more than one advertiser
        if (userInfo.getAdvertisers() == null || userInfo.getAdvertisers().size() != 1) {
            throw new ValidationException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        setUserToSecurityContext(userInfo);
        return userInfo;
    }
}

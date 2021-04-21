package io.revx.auth.service;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.entity.LifeTimeAuthenticationEntity;
import io.revx.auth.repository.LifeTimeTokenRepository;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ResponseMessage;
import io.revx.core.response.UserInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.revx.auth.constants.SecurityConstants.AUTHORITIES_KEY;
import static io.revx.auth.constants.SecurityConstants.IS_LIFETIME_AUTH;

@Service
public class LifeTimeAuthTokenService {

    @Autowired
    public SecurityConstants securityConstants;

    @Autowired
    LifeTimeTokenRepository lifeTimeTokenRepository;

    @Autowired
    AuthValidaterServiceImpl authValidaterServiceImpl;

    private static Logger logger = LogManager.getLogger(LifeTimeAuthTokenService.class);

    public LifeTimeAuthenticationEntity generateLifeTimeAuthToken(String jwtToken) throws Exception {

        // validates the jwtToken and returns UserInfo
        UserInfo uInfo = authValidaterServiceImpl.validateToken(jwtToken);

        final String authorities = uInfo.getAuthorities().stream().collect(Collectors.joining(","));
        logger.debug(" authorities {} ", authorities);
        String lifeTimeToken = Jwts.builder().setSubject(uInfo.getUsername()).claim(AUTHORITIES_KEY, authorities)
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS256, securityConstants.getSIGNING_KEY())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(IS_LIFETIME_AUTH, true)
                .setId(getRandomId()).setIssuer(uInfo.serialize()).compact();

        LifeTimeAuthenticationEntity lifeTimeAuthentication = new LifeTimeAuthenticationEntity();
        lifeTimeAuthentication.setLifeTimeAuthToken(lifeTimeToken);
        lifeTimeAuthentication.setActive(true);
        lifeTimeAuthentication.setCreateOn(System.currentTimeMillis()/1000);
        lifeTimeAuthentication.setLicenseeId(uInfo.getSelectedLicensee().getId());
        lifeTimeAuthentication.setUserId(uInfo.getUserId());
        lifeTimeAuthentication.setModifiedOn(System.currentTimeMillis()/1000);

        return lifeTimeTokenRepository.save(lifeTimeAuthentication);
    }

    public ApiListResponse<LifeTimeAuthenticationEntity> getLifeTimeAuthToken(String jwtToken) throws Exception {

        // validates the jwtToken and returns UserInfo
        UserInfo uInfo = authValidaterServiceImpl.validateToken(jwtToken);
        List<LifeTimeAuthenticationEntity>  lifeTimeAuthenticationEntityList = lifeTimeTokenRepository.findAllByLicenseeId(uInfo.getSelectedLicensee().getId());
        ApiListResponse<LifeTimeAuthenticationEntity> response = new ApiListResponse<>();
        if (lifeTimeAuthenticationEntityList != null && !lifeTimeAuthenticationEntityList.isEmpty()) {
            response.setData(lifeTimeAuthenticationEntityList);
            response.setTotalNoOfRecords(lifeTimeAuthenticationEntityList.size());
        }
        return response;
    }

    // deactivates a lifetime token for given token id
    public ResponseMessage deleteLifeTimeAuthToken(Long tokenId, String token) throws Exception {
        ResponseMessage responseMessage;
        authValidaterServiceImpl.validateToken(token);
        Optional<LifeTimeAuthenticationEntity> tokenEntity = lifeTimeTokenRepository.findById(tokenId);
        if(tokenEntity.isPresent()){
            if(tokenEntity.get().isActive()) {
                lifeTimeTokenRepository.deActivate(tokenId);
                lifeTimeTokenRepository.updateModifiedOn(tokenId, System.currentTimeMillis() / 1000);
                responseMessage = new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS);
            }
            else{
                responseMessage = new ResponseMessage(Constants.ID_ALREADY_INACTIVE , Constants.MSG_ID_ALREADY_INACTIVE);
            }
        }
        else{
            throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
                    new Object[] {"invalid token id"});
        }
        return responseMessage;
    }

    public Boolean isActiveLifeTimeToken(String token){
        Optional<LifeTimeAuthenticationEntity> tokenEntity = lifeTimeTokenRepository.findByLifeTimeAuthToken(token);
        return tokenEntity.isPresent() && tokenEntity.get().isActive();
    }

    private String getRandomId() {
        StringBuilder sb = new StringBuilder();
        sb.append(RandomStringUtils.random(5));
        return sb.toString();
    }
}

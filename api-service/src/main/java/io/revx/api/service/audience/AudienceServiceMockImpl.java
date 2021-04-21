package io.revx.api.service.audience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.revx.core.response.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.audience.pojo.AudienceAccessDTO;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.constant.Constants;
import io.revx.core.enums.DurationUnit;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.DmpAudienceDTO;
import io.revx.core.model.audience.DmpAudience;
import io.revx.core.model.audience.PixelRemoteConfigDTO;
import io.revx.core.model.audience.RuleComponentDTO;
import io.revx.core.model.audience.RuleDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.service.CacheService;

@Component
public class AudienceServiceMockImpl {

  private LoginUserDetailsService loginUserDetailsService;
  private CacheService cacheService;
  private CustomESRepositoryImpl elastic;
  private EntityESService elasticSearch;

  @Autowired
  public void setLoginUserDetailsService(LoginUserDetailsService loginUserDetailsService) {
    this.loginUserDetailsService = loginUserDetailsService;
  }

  @Autowired
  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Autowired
  public void setElastic(CustomESRepositoryImpl elastic) {
    this.elastic = elastic;
  }

  @Autowired
  public void setElasticSearch(EntityESService elasticSearch) {
    this.elasticSearch = elasticSearch;
  }

  public ApiResponseObject<AudienceDTO> createAudience(AudienceDTO audience) throws Exception {

    AudienceDTO aud = new AudienceDTO();
    BeanUtils.copyProperties(audience, aud);
    aud.setLicensee(
        elasticSearch.searchById(TablesEntity.LICENSEE, loginUserDetailsService.getLicenseeId()));
    
    aud.setId(123456L);
    
    ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(aud);
    
    return resp;
  }

  public ApiResponseObject<AudienceDTO> getAudience(Long id) throws Exception {

    AudienceDTO aud = new AudienceDTO();
    aud.setId(id);
    aud.setLicensee(
        elasticSearch.searchById(TablesEntity.LICENSEE, loginUserDetailsService.getLicenseeId()));
    
    aud.setId(123456L);
    aud.setAdvertiser(new BaseModel(5437, "Test"));
    aud.setDataSourceType(1);
    aud.setDescription("Discription");
    aud.setDuration(14400L);
    aud.setDurationUnit(DurationUnit.MINUTE);
    aud.setName("Test audience");
    aud.setPixelId(1234L);
    aud.setActive(true);
    aud.setUserDataType(1);
    
    RuleDTO rule = new RuleDTO();
    rule.setNegate(false);
    rule.setSimpleExpr(true);
    
    RuleComponentDTO ruleComp = new RuleComponentDTO();
    ruleComp.setId(1234);
    ruleComp.setFilterId(1L);
    ruleComp.setNegate(false);
    ruleComp.setOperatorId(3L);
    ruleComp.setValue("*");
    
    rule.setRuleElement(ruleComp);
    
    aud.setRuleExpression(rule);
    aud.setTotalUU(123456L);
    aud.setDailyUU(1234L);
    
    ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(aud);
    
    return resp;
  }
  
  public ApiResponseObject<AudienceDTO> updateAudience(Long id, AudienceDTO audience) throws Exception {

    AudienceDTO aud = new AudienceDTO();
    BeanUtils.copyProperties(audience, aud);
    aud.setLicensee(
        elasticSearch.searchById(TablesEntity.LICENSEE, loginUserDetailsService.getLicenseeId()));
    
    ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(aud);
    
    return resp;
    
  }

  public ApiResponseObject<BaseModel> syncPlatformAudience() {
    
    BaseModel model = new BaseModel(12345L, "Test");
    ApiResponseObject<BaseModel> resp = new ApiResponseObject<>();
    resp.setRespObject(model);
    return resp;
  }

public ApiResponseObject<BaseModel> syncRemoteAudience(Integer audienceId) {
    
    BaseModel model = new BaseModel(audienceId, "Test");
    ApiResponseObject<BaseModel> resp = new ApiResponseObject<>();
    resp.setRespObject(model);
    return resp;
  }

  public ApiResponseObject<BaseModel> checkConnection(PixelRemoteConfigDTO config) {
    BaseModel model = new BaseModel(123, "Test");
    ApiResponseObject<BaseModel> resp = new ApiResponseObject<>();
    resp.setRespObject(model);
    return resp;
  }

  public ApiResponseObject<Map<Integer, ResponseMessage>> activate(String ids) {
    ApiResponseObject<Map<Integer, ResponseMessage>> responseObject = new ApiResponseObject<>();
    Map<Integer, ResponseMessage> response = new HashMap<>();
    
    List<String> idStrList = Arrays.asList(ids.split(","));
    List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());
    
    for(Long i : idList) {
      ResponseMessage responseMessage = new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS);
      response.put(i.intValue(), responseMessage);
    }
    responseObject.setRespObject(response);
    return responseObject;
  }

  public ApiResponseObject<Map<Integer, ResponseMessage>> deactivate(String ids) {
    ApiResponseObject<Map<Integer, ResponseMessage>> responseObject = new ApiResponseObject<>();
    Map<Integer, ResponseMessage> response = new HashMap<>();
    
    List<String> idStrList = Arrays.asList(ids.split(","));
    List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());
    
    for(Long i : idList) {
      ResponseMessage responseMessage = new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS);
      response.put(i.intValue(), responseMessage);
    }
    responseObject.setRespObject(response);
    return responseObject;
  }

  public ApiResponseObject<DmpAudienceDTO> getDmpAudience(Long advertiser_id, Integer start, Integer limit, Integer stype) {
    DmpAudienceDTO dmpAudienceDTO = new DmpAudienceDTO();
    dmpAudienceDTO.setLimit(limit != null ? limit : -1);
    dmpAudienceDTO.setStart(start != null ? start : 1);
    
    ApiResponseObject<DmpAudienceDTO> resp = new ApiResponseObject<>();
    if(advertiser_id%2 == 0) {
      dmpAudienceDTO.setSegment_count(0L);
      resp.setRespObject(dmpAudienceDTO);
      return resp;
    }
    
    dmpAudienceDTO.setSegment_count(85L);
    
    if(start == null)
      start = 1;
    if(limit == null || limit == -1)
      limit = 10;
    
    
    List<DmpAudience> list = new ArrayList<>();
    for(Integer i=0; i< limit ; i++) {
      DmpAudience aud = new DmpAudience();
      aud.setSid(String.valueOf(i.longValue()));
      aud.setSname("Segmetn-"+i);
      aud.setScount(i*12345L);
      if(stype != null)
        aud.setStype(stype);
      else
        aud.setStype(i%2 == 0 ? 1 : 2);
      
      aud.setIsSynced(i%2 == 0 ? true : false);
      
      list.add(aud);
    }
    dmpAudienceDTO.setSegments(list);
    
    
    resp.setRespObject(dmpAudienceDTO);
    
    return resp;
  }
  
  public ApiResponseObject<AudienceAccessDTO> getAcces(Long advertiserId) {
    AudienceAccessDTO accessDTO = new AudienceAccessDTO();
    if(advertiserId%2 == 0) {
      accessDTO.setIsDmpAccess(false);
      accessDTO.setIsPlatformAccess(false);
    }else {
      accessDTO.setIsDmpAccess(true);
      accessDTO.setIsPlatformAccess(true);
    }
    ApiResponseObject<AudienceAccessDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(accessDTO);
    return resp;
  }
}

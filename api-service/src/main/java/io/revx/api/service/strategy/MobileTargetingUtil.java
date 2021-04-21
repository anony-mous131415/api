package io.revx.api.service.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.entity.strategy.TargetingComponent;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.OSMaster;
import io.revx.core.model.OSVersionMaster;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.TargetingFilter;
import io.revx.core.model.strategy.TargetingOperator;
import io.revx.core.model.targetting.ExtendedBaseModel;
import io.revx.core.model.targetting.ExtendedTargetingObject;
import io.revx.core.model.targetting.TargetDeviceTypes;
import io.revx.core.model.targetting.TargetMobileDeviceBrands;
import io.revx.core.model.targetting.TargetMobileDeviceModels;
import io.revx.core.model.targetting.TargetMobileDevices;
import io.revx.core.model.targetting.TargetOperatingSystem;
import io.revx.core.model.targetting.TargetingObject;

@Component
public class MobileTargetingUtil {

  private static final Logger logger = LoggerFactory.getLogger(MobileTargetingUtil.class);

  @Autowired
  TargettingComponentRepository tcRepository;

  @Autowired
  EntityESService elasticSearch;

  @Autowired
  Utility utility;

  @Transactional
  public String createMobileTargetingExpression(StrategyDTO strategy) throws ValidationException {
    String mobileExpr = "", osTExpr = "", mobileModelTExpr = "", mobileBrandsTExpr = "",
        dtTExpr = "";

    if (strategy.getTargetMobileDevices() != null
        && strategy.getTargetMobileDevices().targetOperatingSystems != null) {
      osTExpr = createOSandVersionTargetingExpression(strategy);
    }

    if (strategy.targetMobileDevices != null
        && strategy.targetMobileDevices.targetDeviceTypes != null) {
      dtTExpr = createDeviceTypeTargetingExpression(strategy);
    }

    // TODO validate that both brands and models are not targeted
    if (strategy.targetMobileDevices != null
        && strategy.targetMobileDevices.targetMobileDeviceBrands != null
        && strategy.targetMobileDevices.targetMobileModels != null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST);
    }

    if (strategy.targetMobileDevices != null
        && strategy.targetMobileDevices.targetMobileModels != null) {
      mobileModelTExpr = createMobileModelsTargetingExpression(strategy);
    } else if (strategy.targetMobileDevices != null
        && strategy.targetMobileDevices.targetMobileDeviceBrands != null) {
      // do brand targeting
      mobileBrandsTExpr = createMobileBrandsTargetingExpression(strategy);
    }

    if (osTExpr != null && !(osTExpr.equals(""))) {
      mobileExpr = "(" + osTExpr + ")";
    }

    if (dtTExpr != null && !(dtTExpr.equals(""))) {
      if (mobileExpr.length() > 0)
        mobileExpr = mobileExpr + "&";
      mobileExpr = mobileExpr + dtTExpr;
    }

    if (mobileModelTExpr != null && mobileModelTExpr.length() > 0) {
      if (mobileExpr.length() > 0)
        mobileExpr = mobileExpr + "&";
      mobileExpr = mobileExpr + mobileModelTExpr;
    } else if (mobileBrandsTExpr != null && mobileBrandsTExpr.length() > 0) {
      if (mobileExpr.length() > 0)
        mobileExpr = mobileExpr + "&";
      mobileExpr = mobileExpr + mobileBrandsTExpr;
    }

    return mobileExpr;
  }

  @Transactional
  public String updateMobileCommonExpression(StrategyDTO strategy, StrategyEntity strategyDO,
      PlacementExpressionObject exprObj) throws ValidationException {
    String mobileExpr = "";

    TargetMobileDevices targetDevices = strategy.targetMobileDevices;

    if (targetDevices != null && (targetDevices.targetOperatingSystems != null
        || targetDevices.targetMobileDeviceBrands != null
        || targetDevices.targetMobileModels != null || targetDevices.targetDeviceTypes != null)) {
      // Update mobile common targeting

      String prevExpr = exprObj.getmComTEx();
      if (prevExpr != null && prevExpr.length() > 0) {
        // Delete old targeting
        List<String> idsList = utility.getListOfTCIdsInExpr(prevExpr);
        utility.deleteOldTargetingComponents(idsList);
      }

      mobileExpr = createMobileTargetingExpression(strategy);
    }

    return mobileExpr;
  }

  // OS Targeting expression
  public String createOSandVersionTargetingExpression(StrategyDTO strategy)
      throws ValidationException {

    TargetOperatingSystem targetOS = strategy.targetMobileDevices.targetOperatingSystems;
    if (targetOS == null)
      return "";

    String osTExpr = "";

    boolean selectAllOSs = targetOS.selectAllOperatingSystems;
    ExtendedTargetingObject operatingSystems = targetOS.operatingSystems;

    if ((operatingSystems.excludeList == null || operatingSystems.excludeList.size() == 0)
        && (operatingSystems.includeList == null || operatingSystems.includeList.size() == 0))
      return "";

    if (selectAllOSs == true) {
      // TODO Error handling - Can't exclude Operating systems. Only inclusions allowed
    } else {
      // Consider include list
      List<ExtendedBaseModel> ossIncludeList = operatingSystems.includeList;
      if (ossIncludeList == null || ossIncludeList.size() == 0)
        return "";

      List<String> exprList = new ArrayList<String>();

      for (int i = 0; i < ossIncludeList.size(); i++) {
        ExtendedBaseModel ebm = ossIncludeList.get(i);
        Integer osId = ebm.getId().intValue();
        String osString = Integer.toString(osId);
        OSMaster om = elasticSearch.searchPojoById(TablesEntity.OS, ebm.getId());
        if (om == null || om.getId() == null) {
          throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
              new Object[] {"operating system", osId});
        }
        Long verId = 1l;

        if (ebm.properties != null) {
          BaseModel ovbm = ebm.properties.get(TargetingConstants.OSVERSION);
          verId = ovbm != null ? ovbm.getId() : 1l;
        }
        if (verId == null) {
          throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
              new Object[] {"operating system version", "operating system - " + ebm.getName()});
        }
        OSVersionMaster ovm = elasticSearch.searchPojoById(TablesEntity.OS_VERSION, verId);
        if (ovm == null || ovm.getId() == null) {
          throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
              new Object[] {"operating system version", verId});
        }

        TargetingComponent osTc = new TargetingComponent();
        osTc.setTargetingFilterId(TargetingFilter.OS.getId()); // 6 // -
        // OS
        osTc.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1
        // =
        // IS_ANY_OF
        osTc.setCriteria(osString);

        TargetingComponent verTc = new TargetingComponent();
        verTc.setTargetingFilterId(TargetingFilter.OS_VERSION.getId()); // 19 // -
        // OS_VERSION
        verTc.setTargetingOperatorId(TargetingOperator.IS_GREATER_THAN_OR_EQUAL_TO.getId()); // 8 //
                                                                                             // -
        // IS_GREATER_THAN_OR_EQUAL_TO
        verTc.setCriteria(ovm.getName().toString());
        tcRepository.save(osTc);
        tcRepository.save(verTc);

        if (osTc != null && osTc.getId() != null && verTc != null && verTc.getId() != null) {
          String expr = osTc.getId() + "&" + verTc.getId();
          exprList.add(expr);
        }
      }

      for (int i = 0; i < exprList.size(); i++) {
        osTExpr = osTExpr + "(" + exprList.get(i) + ")";

        if (i != exprList.size() - 1)
          osTExpr = osTExpr + "|";
      }
    }


    return osTExpr;

  }

  //Unused Private Methods
  /*private void validateOSIds(List<BaseModel> list) throws ValidationException {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getId() == null) {
        throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
            new Object[] {"operating_system.id", list.get(i).getId()});
      } else {
        long id = list.get(i).getId();
        if (id < 0) {
          throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
              new Object[] {"operating_system.id", id});
        } else {
          OSMaster om = elasticSearch.searchPojoById(TablesEntity.OS, id);
          if (om == null || om.getId() == null) {
            throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
                new Object[] {"operating system", id});
          }
        }
      }
    }
  }*/

  // Device Type Targeting
  @Transactional
  public String createDeviceTypeTargetingExpression(StrategyDTO strategy)
      throws ValidationException {
    TargetDeviceTypes targetDTs = strategy.targetMobileDevices.targetDeviceTypes;
    if (targetDTs == null)
      return "";

    String dtTExpr = "";
    String dtIdString = "";
    TargetingComponent dtTC = new TargetingComponent();
    boolean selectAllDTs = targetDTs.selectAllMobileDeviceTypes;
    TargetingObject deviceTypes = targetDTs.mobileDeviceTypes;

    if (deviceTypes == null
        || (deviceTypes.blockedList == null || deviceTypes.blockedList.size() == 0)
            && (deviceTypes.targetList == null || deviceTypes.targetList.size() == 0))
      return "";

    if (selectAllDTs == true) {
      // Consider exclude list
      List<BaseModel> excludeList = deviceTypes.blockedList;
      if (excludeList == null || excludeList.size() == 0)
        return "";

      validateDeviceTypeIds(excludeList);

      for (int i = 0; i < excludeList.size(); i++) {
        dtIdString += Long.toString(excludeList.get(i).getId());
        if (i != excludeList.size() - 1)
          dtIdString += ",";
      }
      dtTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId()); // 5
      // =
      // IS_NONE_OF
    } else {
      // Consider include list
      List<BaseModel> includeList = deviceTypes.targetList;
      if (includeList == null || includeList.size() == 0)
        return "";

      validateDeviceTypeIds(includeList);

      for (int i = 0; i < includeList.size(); i++) {
        dtIdString += Long.toString(includeList.get(i).getId());
        if (i != includeList.size() - 1)
          dtIdString += ",";
      }
      dtTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1 // =
      // IS_ANY_OF
    }

    logger.debug("Target Device Types' string is " + dtIdString);

    dtTC.setTargetingFilterId(TargetingFilter.DEV_TYPE.getId()); // 18 =
    // DeviceType
    dtTC.setCriteria(dtIdString);
    tcRepository.save(dtTC);
    dtTExpr = Long.toString(dtTC.getId());
    return dtTExpr;
  }

  private void validateDeviceTypeIds(List<BaseModel> list) throws ValidationException {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).id == null) {
        throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
            new Object[] {"device_type.id", list.get(i).id});
      } else {
        long id = list.get(i).id;
        if (id < 0) {
          throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
              new Object[] {"device_type.id", id});
        } else {
          BaseModel dt = elasticSearch.searchPojoById(TablesEntity.DEVICE, id);
          if (dt == null || dt.getId() == null) {
            throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
                new Object[] {"device type", id});
          }
        }
      }
    }
  }

  // Mobile models Targeting
  @Transactional
  public String createMobileModelsTargetingExpression(StrategyDTO strategy)
      throws ValidationException {

    TargetMobileDevices targetMobileDevices = strategy.targetMobileDevices;
    if (targetMobileDevices == null)
      return null;

    TargetMobileDeviceModels targetMobileModels = targetMobileDevices.targetMobileModels;
    if (targetMobileModels == null)
      return null;

    String mobModelsTExpr = "";
    String mobModelsIdString = "";
    TargetingComponent mmTC = new TargetingComponent();
    boolean selectAllMobileModels = targetMobileModels.selectAllMobileDeviceModels;
    ExtendedTargetingObject mobileModels = targetMobileModels.mobileDeviceModels;

    if ((mobileModels.excludeList == null || mobileModels.excludeList.size() == 0)
        && (mobileModels.includeList == null || mobileModels.includeList.size() == 0))
      return "";

    if (selectAllMobileModels == true) {
      // Consider exclude list
      List<ExtendedBaseModel> mmExcludeList = mobileModels.excludeList;
      if (mmExcludeList == null || mmExcludeList.size() == 0)
        return "";

      validateMobileModelIds(mmExcludeList);

      for (int i = 0; i < mmExcludeList.size(); i++) {
        mobModelsIdString += Long.toString(mmExcludeList.get(i).id);
        if (i != mmExcludeList.size() - 1)
          mobModelsIdString += ",";
      }
      mmTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId()); // 5
      // =
      // IS_NONE_OF
    } else {
      // Consider include list
      List<ExtendedBaseModel> mmIncludeList = mobileModels.includeList;
      if (mmIncludeList == null || mmIncludeList.size() == 0)
        return "";

      validateMobileModelIds(mmIncludeList);

      for (int i = 0; i < mmIncludeList.size(); i++) {
        mobModelsIdString += Long.toString(mmIncludeList.get(i).id);
        if (i != mmIncludeList.size() - 1)
          mobModelsIdString += ",";
      }
      mmTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1 // =
      // IS_ANY_OF
    }

    logger.debug("Target Mobile Models' string is " + mobModelsIdString);

    mmTC.setTargetingFilterId(TargetingFilter.DEV_MODEL.getId()); // 16 =
    // DEV_MODEL
    mmTC.setCriteria(mobModelsIdString);
    tcRepository.save(mmTC);
    mobModelsTExpr = Long.toString(mmTC.getId());
    return mobModelsTExpr;

  }

  private void validateMobileModelIds(List<ExtendedBaseModel> list) throws ValidationException {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).id == null) {
        throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
            new Object[] {"mobile_device_model.id", list.get(i).id});
      } else {
        long id = list.get(i).id;
        if (id < 0) {
          throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
              new Object[] {"mobile_device_model.id", id});
        } else {
          BaseModel dmm = elasticSearch.searchById(TablesEntity.DEVICE_MODEL, id);
          if (dmm == null || dmm.getId() == null) {
            throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
                new Object[] {"mobile device model", id});
          }
        }
      }
    }
  }

  // Mobile Brands Targeting
  @Transactional
  public String createMobileBrandsTargetingExpression(StrategyDTO strategy)
      throws ValidationException {

    TargetMobileDevices targetMobileDevices = strategy.targetMobileDevices;
    if (targetMobileDevices == null)
      return null;

    TargetMobileDeviceBrands targetMobileBrands = targetMobileDevices.targetMobileDeviceBrands;
    if (targetMobileBrands == null)
      return null;

    String mobBrandsTExpr = "";
    String mobBrandsIdString = "";
    TargetingComponent mbTC = new TargetingComponent();
    boolean selectAllMobileBrands = targetMobileBrands.selectAllMobileDeviceBrands;
    TargetingObject mobileBrands = targetMobileBrands.mobileDeviceBrands;

    if ((mobileBrands.blockedList == null || mobileBrands.blockedList.size() == 0)
        && (mobileBrands.targetList == null || mobileBrands.targetList.size() == 0))
      return "";

    if (selectAllMobileBrands == true) {
      // Consider exclude list
      List<BaseModel> mbExcludeList = mobileBrands.blockedList;
      if (mbExcludeList == null || mbExcludeList.size() == 0)
        return "";

      validateMobileBrandIds(mbExcludeList);

      for (int i = 0; i < mbExcludeList.size(); i++) {
        mobBrandsIdString += Long.toString(mbExcludeList.get(i).id);
        if (i != mbExcludeList.size() - 1)
          mobBrandsIdString += ",";
      }
      mbTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId()); // 5
      // =
      // IS_NONE_OF
    } else {
      // Consider include list
      List<BaseModel> mbIncludeList = mobileBrands.targetList;
      if (mbIncludeList == null || mbIncludeList.size() == 0)
        return "";

      validateMobileBrandIds(mbIncludeList);

      for (int i = 0; i < mbIncludeList.size(); i++) {
        mobBrandsIdString += Long.toString(mbIncludeList.get(i).id);
        if (i != mbIncludeList.size() - 1)
          mobBrandsIdString += ",";
      }
      mbTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1
      // =
      // IS_ANY_OF
    }

    logger.debug("Target Mobile Brands' string is " + mobBrandsIdString);

    mbTC.setTargetingFilterId(TargetingFilter.DEV_BRAND.getId());// 15 =
    // DEV_BRAND
    mbTC.setCriteria(mobBrandsIdString);
    tcRepository.save(mbTC);
    mobBrandsTExpr = Long.toString(mbTC.getId());
    return mobBrandsTExpr;
  }

  private void validateMobileBrandIds(List<BaseModel> list) throws ValidationException {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).id == null) {
        throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
            new Object[] {"mobile_device_brand.id", list.get(i).id});
      } else {
        long id = list.get(i).id;
        if (id < 0) {
          throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
              new Object[] {"mobile_device_brand.id", id});
        } else {
          BaseModel dbm = elasticSearch.searchById(TablesEntity.DEVICE_BRAND, id);
          if (dbm == null || dbm.getId() == null) {
            throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
                new Object[] {"mobile device brand", id});
          }
        }
      }
    }
  }

  public void parseMobileSpecificExpressionIntoComponents(PlacementExpressionObject exprObj,
      String mobileExpr) {
    /*
     * ( ( ( MWP-TC & (MWS-TEx) ) | (MAP-TC & (MAS-TEx) ) ) & (MS-TEx) )
     * 
     * MWP-TC : Mobile Web Placement Targeting Component MWS-TEx : Mobile Web Specific Targeting
     * Expression MAP-TC : Mobile App Placement Targeting Component MAS-TEx : Mobile App Specific
     * Targeting Expression MS-TEx : Mobile Specific Targeting Expression (common for Mobile Web and
     * Mobile App placements
     */

    mobileExpr = utility.trimBracketsAndReturnValidExpression(mobileExpr);
    List<String> strList = utility.splitIntoComponentStrings(mobileExpr);
    String mobileCommonExpr = null, mobileSpecPlExpr = null;

    for (String st : strList) {
      Boolean isMobilePlacementSpecficExpression = isMobilePlacementSpecificExpression(st);
      if (isMobilePlacementSpecficExpression.equals(Boolean.TRUE))
        mobileSpecPlExpr = st;
      else {
        if (isMobileCommonExpression(st))
          mobileCommonExpr = st;
      }
    }

    // populate web and app specific targeting expression and placement components into Expression
    // Object
    populateMobilePlacementSpecificDataInExprObj(mobileSpecPlExpr, exprObj);

    // populate mobile common Targeting Expression into Expression Object
    exprObj.setmComTEx(mobileCommonExpr);
  }

  private void populateMobilePlacementSpecificDataInExprObj(String mobileSpecPlExpr,
      PlacementExpressionObject exprObj) {
    // ( ( MWP-TC & (MWS-TEx) ) | (MAP-TC & (MAS-TEx) ) )

    String expr = utility.trimBracketsAndReturnValidExpression(mobileSpecPlExpr);
    if (expr == null) {
      // TODO Error Handling
    } else {
      List<String> strList = utility.splitIntoComponentStrings(expr);
      for (String st : strList) {
        String idStr = utility.findFirstIdInExpression(st);
        TargetingComponent tc = getTargetingComponentForId(idStr);
        if (tc.getTargetingFilterId().equals(TargetingConstants.PLACEMENTTARGETINGID)) {
          if (tc.getCriteria().trim().equals("2")) {
            // Mobile Web Placement Targeting
            exprObj.setMwTC(tc);
            exprObj.setMwTEx("");
            // If we add mobile web specific Targeting, we need to call the function commented below
            // populateMobileWebSpecificTargetingInExprObj(st, exprObj);
          } else if (tc.getCriteria().trim().equals("3")) {
            // Mobile APP Placement Targeting
            exprObj.setMapTC(tc);
            exprObj.setMapTEx("");
            // If we add mobile web specific Targeting, we need to call the function commented below
            // populateMobileAppSpecificTargetingInExprObj(st, exprObj);
          } else if (tc.getCriteria().trim().equals(TargetingConstants.FANPLACEMENTID.toString())) {
            // FAN Placement Targeting
            exprObj.setFanTC(tc);
            exprObj.setFanTEx("");
          }
        }
      }
    }
  }


  public Boolean isMobilePlacementSpecificExpression(String str) {
    String expr = str;
    if (expr == null) {
      // TODO error handling
    } else {
      String idStr = utility.findFirstIdInExpression(expr);
      String exprType = utility.findExpressionType(idStr);
      if (exprType.equals(TargetingConstants.PLACEMENTS)) {
        String tcIdString = utility.trimSorroundingBrackets(idStr);
        if (tcIdString == null || tcIdString.length() == 0) {
          // TODO Error handling
        }
        logger.debug("expr after trimming of brackets : " + tcIdString);
        long tcId = Long.parseLong(tcIdString);
        TargetingComponent tc = tcRepository.getOne(tcId);
        String val = tc.getCriteria();
        if (val.trim().equals("2") || val.trim().equals("3")) {
          return Boolean.TRUE;
        }
      }
    }

    return Boolean.FALSE;
  }

  public Boolean isMobileCommonExpression(String str) {
    // String expr = Utility.trimBracketsAndReturnValidExpression(str);
    String expr = str;
    if (expr == null) {
      // TODO error handling
    } else {
      String idStr = utility.findFirstIdInExpression(expr);
      String exprType = utility.findExpressionType(idStr);
      if (exprType.equals(TargetingConstants.OPERATINGSYSTEM)
          || exprType.equals(TargetingConstants.MOBILEDEVICEBRANDS)
          || exprType.equals(TargetingConstants.MOBILEDEVICEMODELS)
          || exprType.equals(TargetingConstants.MOBILEDEVICETYPES)) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  private TargetingComponent getTargetingComponentForId(String str) {
    TargetingComponent tc = tcRepository.getOne(Long.parseLong(str));
    return tc;
  }

  public void populateDTOFromExpressionObject(StrategyDTO strategy,
      PlacementExpressionObject exprObj) {
    // Mobile Common Targeting Expression = (OS & DT & BRAND) OR (OS & DT & MODEL)

    TargetMobileDevices tmd = new TargetMobileDevices();
    String expr = utility.trimBracketsAndReturnValidExpression(exprObj.getmComTEx());

    List<String> strList = utility.splitIntoComponentStrings(expr);
    for (String str : strList) {
      if (str != null && str.length() > 0) {
        String idStr = utility.findFirstIdInExpression(str);
        String exprType = utility.findExpressionType(idStr);
        if (exprType.equals(TargetingConstants.OPERATINGSYSTEM)
            || exprType.equals(TargetingConstants.MOBILEOSVERSIONS)) {
          populateDTOFromOSTargetingExpression(str, tmd);
        } else {
          TargetingComponent tc = getTargetingComponentForId(str);
          String criteria = tc.getCriteria();
          List<BaseModel> bmList = null;
          List<ExtendedBaseModel> ebmList = null;
          String[] arr = criteria.split(",");
          List<Long> idsList = new ArrayList<>();
          for (String id : arr) {
            idsList.add(Long.parseLong(id));
          }
          if (tc.getTargetingFilterId() == TargetingFilter.DEV_BRAND.getId()) {
            // Mobile Brand
            List<BaseModel> dbmList = (List<BaseModel>) elasticSearch
                .searchPojoByIdList(TablesEntity.DEVICE_BRAND, idsList);
            bmList = createBaseModelListFromDOList(dbmList);
            tmd.targetMobileDeviceBrands = new TargetMobileDeviceBrands();
            if (tc.getTargetingOperatorId() == TargetingOperator.IS_ANY_OF.getId()) {
              tmd.targetMobileDeviceBrands.selectAllMobileDeviceBrands = false;
              tmd.targetMobileDeviceBrands.mobileDeviceBrands = new TargetingObject();
              tmd.targetMobileDeviceBrands.mobileDeviceBrands.targetList.addAll(bmList);
            } else {
              tmd.targetMobileDeviceBrands.selectAllMobileDeviceBrands = true;
              tmd.targetMobileDeviceBrands.mobileDeviceBrands = new TargetingObject();
              tmd.targetMobileDeviceBrands.mobileDeviceBrands.blockedList.addAll(bmList);
            }
          } else if (tc.getTargetingFilterId() == TargetingFilter.DEV_MODEL.getId()) {
            // Mobile Model
            List<BaseModel> dmmList = (List<BaseModel>) elasticSearch
                .searchPojoByIdList(TablesEntity.DEVICE_MODEL, idsList);
            ebmList = createExtendedBaseModelListFromDOList(dmmList,
                TargetingConstants.MOBILEDEVICEMODELS);
            tmd.targetMobileModels = new TargetMobileDeviceModels();
            if (tc.getTargetingOperatorId() == TargetingOperator.IS_ANY_OF.getId()) {
              tmd.targetMobileModels.selectAllMobileDeviceModels = false;
              tmd.targetMobileModels.mobileDeviceModels = new ExtendedTargetingObject();
              tmd.targetMobileModels.mobileDeviceModels.includeList.addAll(ebmList);
            } else {
              tmd.targetMobileModels.selectAllMobileDeviceModels = true;
              tmd.targetMobileModels.mobileDeviceModels = new ExtendedTargetingObject();
              tmd.targetMobileModels.mobileDeviceModels.excludeList.addAll(ebmList);
            }
          } else if (tc.getTargetingFilterId() == TargetingFilter.DEV_TYPE.getId()) {
            // Device Type
            List<BaseModel> dtList =
                (List<BaseModel>) elasticSearch.searchPojoByIdList(TablesEntity.DEVICE, idsList);
            bmList = createBaseModelListFromDOList(dtList);
            tmd.targetDeviceTypes = new TargetDeviceTypes();
            if (tc.getTargetingOperatorId() == TargetingOperator.IS_ANY_OF.getId()) {
              tmd.targetDeviceTypes.selectAllMobileDeviceTypes = false;
              tmd.targetDeviceTypes.mobileDeviceTypes = new TargetingObject();
              tmd.targetDeviceTypes.mobileDeviceTypes.targetList.addAll(bmList);
            } else {
              tmd.targetDeviceTypes.selectAllMobileDeviceTypes = true;
              tmd.targetDeviceTypes.mobileDeviceTypes = new TargetingObject();
              tmd.targetDeviceTypes.mobileDeviceTypes.blockedList.addAll(bmList);
            }
          }
        }
      }
    }

    strategy.targetMobileDevices = tmd;
  }

  private void populateDTOFromOSTargetingExpression(String osTExpr, TargetMobileDevices tmd) {
    // Expr - ((OS1&V1) | (OS2&V2) | (OS3&V3)) e.g. ((222395&222396)|(222397&222398))

    String expr = utility.trimBracketsAndReturnValidExpression(osTExpr);
    List<String> comps = null;
    if (expr.contains("|"))
      comps = utility.splitIntoComponentStrings(expr);
    else {
      comps = new ArrayList<String>();
      comps.add(expr);
    }

    TargetOperatingSystem tos = null;
    if(tmd.targetOperatingSystems != null)
      tos = tmd.targetOperatingSystems;
    else
      tos = new TargetOperatingSystem();
    
    tos.selectAllOperatingSystems = false;
    List<ExtendedBaseModel> ebmList = new ArrayList<ExtendedBaseModel>();
    for (String str : comps) {
      // (OS1&V1)
      String osAndVerExpr = utility.trimBracketsAndReturnValidExpression(str);
      List<String> list = utility.splitIntoComponentStrings(osAndVerExpr);
      TargetingComponent osTc = null, verTc = null;

      for (String st : list) {
        if (st != null && st.length() > 0) {
          TargetingComponent tc = getTargetingComponentForId(st);
          if (tc.getTargetingFilterId().equals(7L)) {
            // Operating System
            osTc = tc;
          } else if (tc.getTargetingFilterId().equals(20L)) {
            // OS Version
            verTc = tc;
          }
        }
      }

      if (osTc != null && osTc.getTargetingOperatorId().equals(6)) {
        // TODO throw error, OS Can't be excluded now
      }

      if (osTc != null && verTc != null) {
        String osCriteria = osTc.getCriteria();
        String[] arr = osCriteria.split(",");
        List<Long> idsList = new ArrayList<>();
        for (String id : arr) {
          idsList.add(Long.parseLong(id));
        }
        List<OSMaster> osList =
            (List<OSMaster>) elasticSearch.searchPojoByIdList(TablesEntity.OS, idsList);
        if (osList.size() != 1) {
          // TODO Error Handling
        } else {
          Long osId = osList.get(0).getId();
          String osName = osList.get(0).getName();
          ExtendedBaseModel ebm = new ExtendedBaseModel(osId, osName);

          String verCriteria = verTc.getCriteria();

          String[] arrOsVersion = verCriteria.split(",");
          // List<long> idsListOsV = new ArrayList<>();
          // for (String id : arrOsVersion) {
          // idsListOsV.add(Float.parseFloat(id));
          // }
          ElasticSearchTerm searchTerm = new ElasticSearchTerm();
          Map<String, Set<String>> filters = new HashMap<String, Set<String>>();
          // filters.put("name", new HashSet<>(Arrays.asList(String.valueOf("7.10"))));
          filters.put("osId", new HashSet<>(Arrays.asList(String.valueOf(osId))));
          // searchTerm.setSerachInIdOrName(verCriteria);
          searchTerm.setFilters(filters);
          EResponse<OSVersionMaster> vosListResp =
              elasticSearch.searchAll(TablesEntity.OS_VERSION, searchTerm);

          if (vosListResp != null && vosListResp.getTotalNoOfRecords() > 0) {
            List<OSVersionMaster> vosList = vosListResp.getData();
            if (vosList.size() > 0) {
              for (OSVersionMaster osVerMaster : vosList) {
                if (Float.parseFloat(osVerMaster.name) == Float.parseFloat(verCriteria)) {
                  Long versionId = osVerMaster.getId();
                  String versionName = osVerMaster.getName();

                  BaseModel ver = new BaseModel();
                  ver.id = versionId;
                  ver.name = versionName;
                  ebm.properties.put(TargetingConstants.OSVERSION, ver);
                  ebmList.add(ebm);
                  break;
                }
              }
            }

            /*
             * if (vosList.size() != 1) { // TODO Error Handling } else { Long versionId =
             * vosList.get(0).getId(); BigDecimal version = vosList.get(0).getVersion(); String
             * versionName = version.toString();
             * 
             * BaseModel ver = new BaseModel(); ver.id = versionId; ver.name = versionName;
             * ebm.properties.put(TargetingConstants.OSVERSION, ver); }
             */
          }

          // ebmList.add(ebm);
        }
      }
    }

    tos.operatingSystems.includeList.addAll(ebmList);

    if (tos != null)
      tmd.targetOperatingSystems = tos; 
    }

  private List<BaseModel> createBaseModelListFromDOList(List entityList) {
    List<BaseModel> bmList = new ArrayList<>();

    for (Object entity : entityList) {
      BaseModel bm = (BaseModel) entity;
      bmList.add(new BaseModel(bm.getId(), bm.getName()));
    }
    return bmList;
  }

  private List<ExtendedBaseModel> createExtendedBaseModelListFromDOList(List entityList,
      String type) {
    List<ExtendedBaseModel> resultList = new ArrayList<>();

    if (TargetingConstants.MOBILEDEVICEMODELS.equalsIgnoreCase(type)) {
      List<BaseModel> eList = (List<BaseModel>) entityList;
      for (BaseModel element : eList) {
        resultList.add(new ExtendedBaseModel(element.getId(), element.getName()));
      }
    }

    return resultList;
  }

}

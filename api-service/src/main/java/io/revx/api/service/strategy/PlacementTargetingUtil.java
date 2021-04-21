package io.revx.api.service.strategy;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemTargetingExpression;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.entity.strategy.TargetingComponent;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemTargetingExpRepo;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.requests.ElasticResponse;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.TargetingFilter;
import io.revx.core.model.strategy.TargetingOperator;


@Component
public class PlacementTargetingUtil {

  private static final Logger logger = LoggerFactory.getLogger(PlacementTargetingUtil.class);

  private MobileTargetingUtil MobileTargetingUtil;
  private AdvertiserLineItemTargetingExpRepo aliTRepo;
  TargettingComponentRepository tcRepository;
  private EntityESService elasticSearch;
  private Utility Utility;
  ApplicationProperties properties;

  @Autowired
  public void setMobileTargetingUtil(io.revx.api.service.strategy.MobileTargetingUtil mobileTargetingUtil) {
    MobileTargetingUtil = mobileTargetingUtil;
  }

  @Autowired
  public void setAliTRepo(AdvertiserLineItemTargetingExpRepo aliTRepo) {
    this.aliTRepo = aliTRepo;
  }

  @Autowired
  public void setTcRepository(TargettingComponentRepository tcRepository) {
    this.tcRepository = tcRepository;
  }

  @Autowired
  public void setElasticSearch(EntityESService elasticSearch) {
    this.elasticSearch = elasticSearch;
  }

  @Autowired
  public void setUtility(io.revx.api.service.strategy.Utility utility) {
    Utility = utility;
  }

  @Autowired
  public void setProperties(ApplicationProperties properties) {
    this.properties = properties;
  }

  private static final String PLACEMENT_DESKTOP = "Desktop";
  private static final String PLACEMENT_MOBILE_WEB = "Mobile Web";
  private static final String PLACEMENT_MOBILE_APP = "Mobile Applications";
  private static final String PLACEMENT_FAN = "Facebook Audience Network";



  public String createPlacementSpecificTargetingExpression(StrategyDTO strategy)
      throws ValidationException {
    String placementTExpr = "";
    String mobileExpression = "";
    TargetingComponent dTc = null, mwTc = null, maTc = null, fanTc = null;

    for (BaseModel bm : strategy.placements) {
      long id = bm.id;
      BaseModel placement = elasticSearch.searchById(TablesEntity.SOURCE_TYPE, id);
      if (placement == null) {
        throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
            new Object[] {"placement.id", id});
      }

      String criteria = Long.toString(placement.getId());
      TargetingComponent tc =
          createTargetingComponent(io.revx.core.model.strategy.TargetingFilter.PLACEMENT.getId(),
              TargetingOperator.IS_EQUAL_TO.getId(), criteria);
      // Targeting Filter 17 = PLACEMENT
      if (placement.getName().trim().equalsIgnoreCase(PLACEMENT_DESKTOP)) {
        // Create TC for Desktop
        dTc = tc;
      } else if (placement.getName().trim().equalsIgnoreCase(PLACEMENT_MOBILE_WEB)) {
        // Create TC for Mobile Web
        mwTc = tc;
      } else if (placement.getName().trim().equalsIgnoreCase(PLACEMENT_MOBILE_APP)) {
        // Create TC for Mobile App
        maTc = tc;
      } else if (placement.getName().trim().equalsIgnoreCase(PLACEMENT_FAN)) {
        fanTc = tc;
      }
    }

    // Add Desktop expression
    if (dTc != null && dTc.getId() != null) {

      // Add Desktop placement targeting expression here
      placementTExpr = "(" + Long.toString(dTc.getId()) + ")";
    }

    // Add Mobile expression
    if (mwTc != null || maTc != null) {
      if (strategy.targetMobileDevices != null) {
        mobileExpression = MobileTargetingUtil.createMobileTargetingExpression(strategy);
      }

      String mobPTEx = "";
      if (mwTc != null && mwTc.getId() != null) {
        mobPTEx = Long.toString(mwTc.getId());
      }
      if (maTc != null && maTc.getId() != null) {
        if (mobPTEx.length() > 0)
          mobPTEx = mobPTEx + "|";

        // Add just Mobile App Placement Targeting Component expression only here. If app specific
        // targeting comes in future, it should also be added in the same clause.
        mobPTEx = mobPTEx + Long.toString(maTc.getId());
      }
      if (fanTc != null && fanTc.getId() != null) {
        if (mobPTEx.length() > 0)
          mobPTEx = mobPTEx + "|";

        // FAN Placement Targeting
        mobPTEx = mobPTEx + Long.toString(fanTc.getId());
      }


      if (mobPTEx.length() > 0) {
        mobPTEx = "(" + mobPTEx + ")";
        if (mobileExpression.length() > 0) {
          mobPTEx = mobPTEx + "&(" + mobileExpression + ")";
        }

        if (placementTExpr.length() > 0)
          placementTExpr = placementTExpr + "|";

        placementTExpr = placementTExpr + "(" + mobPTEx + ")";
      }
    }
    return placementTExpr;
  }

  @Transactional
  public String updatePlacementTargetingExpression(StrategyDTO strategy, StrategyEntity strategyDO)
      throws ValidationException {
    String plTExpr = "", displayEx = "", mobileEx = "", fanEx = "", mobileCommonEx;
    boolean displayPT = false, mobileWPT = false, mobileAPT = false, fanPT = false;
    TargetingComponent dTc = null, mwTc = null, maTc = null;

    Optional<AdvertiserLineItemTargetingExpression> alte =
        aliTRepo.findByStrategyId(strategy.getId());
    if (!alte.isPresent())
      throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
          new Object[] {"strategy .id", strategy.getId()});
    String ptEx = alte.get().getPlacementTargetingExpression();

    if (strategy.placements == null && strategy.targetMobileDevices == null) {
      // User is not updating placement targeting or associated mobile targeting
      return ptEx;
    }

    // Populate placement specific expression equivalent in Expression Object
    PlacementExpressionObject exprObj = parsePlacementSpecificExpressionIntoComponents(ptEx);
    if (exprObj.getMobileExpr() != null && exprObj.getMobileExpr().length() > 0) {
      MobileTargetingUtil.parseMobileSpecificExpressionIntoComponents(exprObj,
          exprObj.getMobileExpr());
    }

    if (strategy.placements != null && strategy.placements.size() > 0) {
      for (BaseModel bm : strategy.placements) {
        Long id = bm.id;
        BaseModel placement = elasticSearch.searchById(TablesEntity.SOURCE_TYPE, id);
        if (placement == null) {
          throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
              new Object[] {"placement.id", id});
        }
        if (id.equals(TargetingConstants.DISPLAYPLACEMENTID)) {
          displayPT = true;
        } else if (id.equals(TargetingConstants.MOBILEWEBPLACEMENTID)) {
          mobileWPT = true;
        } else if (id.equals(TargetingConstants.MOBILEAPPPLACEMENTID)) {
          mobileAPT = true;
        } else if (id.equals(TargetingConstants.FANPLACEMENTID)) {
          fanPT = true;
        }
      }
    } else if (strategy.placements != null && strategy.placements.size() == 0) {
      throw new ValidationException(ErrorCode.STRATEGY_PLACEMENTS_IS_REQ);
    }

    // UPDATE Mobile Common expr if required
    if (strategy.targetMobileDevices != null) {
      // UPDATE Mobile common expression
      mobileCommonEx =
          MobileTargetingUtil.updateMobileCommonExpression(strategy, strategyDO, exprObj);
    } else {
      mobileCommonEx = exprObj.getmComTEx();
    }

    // TODO handle scenario when user update mobile targeting but not placements list

    // UPDATE Display PT if required
    if (displayPT == true) {
      // Display Placement targeted now
      if (exprObj.getDpTC() != null && exprObj.getDpTC().getId() != null) {
        // Display placement already targeted. No need to do anything
        displayEx = Long.toString(exprObj.getDpTC().getId());
      } else {
        // Display placement was not targeted. Target now
        String criteria = Long.toString(TargetingConstants.DISPLAYPLACEMENTID);
        TargetingComponent tc =
            createTargetingComponent(TargetingFilter.PLACEMENT.getId(), 1l, criteria);
        displayEx = Long.toString(tc.getId());
      }
    } else {
      // Display not targeted anymore. Remove it if required
      displayEx = "";
      if (exprObj.getDpTC() != null && exprObj.getDpTC().getId() != null) {
        // Delete old TargetingComponent
        List<String> list = new ArrayList<String>();
        list.add(Long.toString(exprObj.getDpTC().getId()));
        Utility.deleteOldTargetingComponents(list);
      } else {
        // Strategy wasn't targeted to display earlier as well. DO nothing
      }
    }

    // UPDATE Mobile placements
    if (mobileWPT == true || mobileAPT == true) {
      String mwEx = "", maEx = "";
      List<String> tcsToBeDeleted = new ArrayList<String>();
      // Mobile Web placement
      if (mobileWPT == true) {
        if (exprObj.getMwTC() != null && exprObj.getMwTC().getId() != null) {
          mwEx = Long.toString(exprObj.getMwTC().getId());
        } else {
          String criteria = Long.toString(TargetingConstants.MOBILEWEBPLACEMENTID);
          TargetingComponent tc =
              createTargetingComponent(TargetingFilter.PLACEMENT.getId(), 1l, criteria);
          mwEx = Long.toString(tc.getId());
        }
      } else {
        // Remove mobile web TC
        if (exprObj.getMwTC() != null && exprObj.getMwTC().getId() != null) {
          tcsToBeDeleted.add(Long.toString(exprObj.getMwTC().getId()));
        }
      }

      // Mobile App placement
      if (mobileAPT == true) {
        if (exprObj.getMapTC() != null && exprObj.getMapTC().getId() != null) {
          maEx = Long.toString(exprObj.getMapTC().getId());
        } else {
          String criteria = Long.toString(TargetingConstants.MOBILEAPPPLACEMENTID);
          TargetingComponent tc =
              createTargetingComponent(TargetingFilter.PLACEMENT.getId(), 1l, criteria);
          maEx = Long.toString(tc.getId());
        }
      } else {
        // Remove mobile app TC
        if (exprObj.getMapTC() != null && exprObj.getMapTC().getId() != null) {
          tcsToBeDeleted.add(Long.toString(exprObj.getMapTC().getId()));
        }
      }

      if (tcsToBeDeleted.size() > 0)
        Utility.deleteOldTargetingComponents(tcsToBeDeleted);

      // Construct mobile placement expression
      if (mwEx != null && mwEx.length() > 0)
        mobileEx = "(" + mwEx + ")";

      if (mobileEx != null && mobileEx.length() > 0) {
        if (maEx != null && maEx.length() > 0)
          mobileEx = mobileEx + "|" + "(" + maEx + ")";
      } else {
        if (maEx != null && maEx.length() > 0)
          mobileEx = "(" + maEx + ")";
      }

      // UPDATE FAN PT if required
      if (fanPT == true) {
        // FAN Placement targeted now
        if (exprObj.getFanTC() != null && exprObj.getFanTC().getId() != null) {
          // FAN placement already targeted. No need to do anything
          fanEx = Long.toString(exprObj.getFanTC().getId());
        } else {
          // FAN placement was not targeted. Target now
          String criteria = Long.toString(TargetingConstants.FANPLACEMENTID);
          TargetingComponent tc =
              createTargetingComponent(TargetingFilter.PLACEMENT.getId(), 1l, criteria);
          fanEx = Long.toString(tc.getId());
        }

        if (mobileEx != null && mobileEx.length() > 0) {
          if (maEx != null && maEx.length() > 0)
            mobileEx = mobileEx + "|" + "(" + fanEx + ")";
        } else {
          if (maEx != null && maEx.length() > 0)
            mobileEx = "(" + fanEx + ")";
        }

      } else {
        // FAN not targeted anymore. Remove it if required
        fanEx = "";
        if (exprObj.getFanTC() != null && exprObj.getFanTC().getId() != null) {
          // Delete old TargetingComponent
          List<String> list = new ArrayList<String>();
          list.add(Long.toString(exprObj.getFanTC().getId()));
          Utility.deleteOldTargetingComponents(list);
        }
      }

      if (mobileEx != null && mobileEx.length() > 0 && mobileCommonEx != null
          && mobileCommonEx.length() > 0) {
        mobileEx = "(" + mobileEx + ")" + "&" + "(" + mobileCommonEx + ")";
      }

    }

    if (displayEx != null && displayEx.length() > 0)
      plTExpr = "(" + displayEx + ")";

    if (mobileEx != null && mobileEx.length() > 0)
      mobileEx = "(" + mobileEx + ")";

    if (plTExpr != null && plTExpr.length() > 0) {
      if (mobileEx.length() > 0)
        plTExpr = plTExpr + "|" + mobileEx;
    } else {
      plTExpr = plTExpr + mobileEx;
    }

    return plTExpr;
  }

  public void populatePlacementTargetingInDTO(StrategyDTO strategy, StrategyEntity strategyDO)
      throws ValidationException {
    Optional<AdvertiserLineItemTargetingExpression> ali =
        aliTRepo.findByStrategyId(strategyDO.getId());
    if (!ali.isPresent())
      return;
    String ptEx = ali.get().getPlacementTargetingExpression();
    // populateDTOWithTargetAllParams(strategy);
    /*
     * ptEx is placement specific targeting expression. It is supposed to look like : ( ( DP-TC &
     * (DS-TEx) ) | ( ( ( MWP-TC & (MWS-TEx) ) | (MAP-TC & (MAS-TEx) ) ) & (MS-TEx) ) )
     * 
     * In the above expression the elements are : DP-TC : Display Placement Targeting Component
     * DS-TEx : Display Specific Targeting Expression MWP-TC : Mobile Web Placement Targeting
     * Component MWS-TEx : Mobile Web Specific Targeting Expression MAP-TC : Mobile App Placement
     * Targeting Component MAS-TEx : Mobile App Specific Targeting Expression MS-TEx : Mobile
     * Specific Targeting Expression (common for Mobile Web and Mobile App placements
     * 
     */
    if (ptEx == null || ptEx.length() == 0) {
      throw new ValidationException(ErrorCode.STRATEGY_PLACEMENTS_IS_REQ);
    } else {
      if (ptEx.equals(Integer.toString(properties.getDefaultPlacementTargetingId()))) {
        strategy.placements = new ArrayList<BaseModel>();

        BaseModel st = elasticSearch.searchById(TablesEntity.SOURCE_TYPE, 1);
        strategy.placements.add(new BaseModel(st.getId(), st.getName()));

        st = elasticSearch.searchById(TablesEntity.SOURCE_TYPE, 2);
        strategy.placements.add(new BaseModel(st.getId(), st.getName()));

        return;
      }

      PlacementExpressionObject exprObj = parsePlacementSpecificExpressionIntoComponents(ptEx);
      if (exprObj.getMobileExpr() != null && exprObj.getMobileExpr().length() > 0) {
        MobileTargetingUtil.parseMobileSpecificExpressionIntoComponents(exprObj,
            exprObj.getMobileExpr());
      }
      populateDTOFromExpressionObject(strategy, exprObj);
    }
  }

  private void populateDTOFromExpressionObject(StrategyDTO strategy,
      PlacementExpressionObject exprObj) {
    strategy.placements = new ArrayList<BaseModel>();

    if (exprObj.getDpTC() != null && exprObj.getDpTC().getId() != null) {
      String criteria = exprObj.getDpTC().getCriteria();
      Integer id = Integer.parseInt(criteria);
      strategy.placements.add(elasticSearch.searchById(TablesEntity.SOURCE_TYPE, id));
    }

    if (exprObj.getMwTC() != null && exprObj.getMwTC().getId() != null) {
      String criteria = exprObj.getMwTC().getCriteria();
      Integer id = Integer.parseInt(criteria);
      strategy.placements.add(elasticSearch.searchById(TablesEntity.SOURCE_TYPE, id));
    }

    if (exprObj.getMapTC() != null && exprObj.getMapTC().getId() != null) {
      String criteria = exprObj.getMapTC().getCriteria();
      Integer id = Integer.parseInt(criteria);
      strategy.placements.add(elasticSearch.searchById(TablesEntity.SOURCE_TYPE, id));
    }

    if (exprObj.getFanTC() != null && exprObj.getFanTC().getId() != null) {
      String criteria = exprObj.getFanTC().getCriteria();
      Integer id = Integer.parseInt(criteria);
      strategy.placements.add(elasticSearch.searchById(TablesEntity.SOURCE_TYPE, id));
    }


    // Currently no display specific targeting expression,
    // mobile web specific targeting expression and mobile app specific targeting expression
    // Only mobile common targeting expression present
    if (exprObj.getmComTEx() != null && exprObj.getmComTEx().length() > 0)
      MobileTargetingUtil.populateDTOFromExpressionObject(strategy, exprObj);
  }

  public PlacementExpressionObject parsePlacementSpecificExpressionIntoComponents(String expr) {
    PlacementExpressionObject exprObj = new PlacementExpressionObject();
    exprObj.setExpr(expr);

    // String fullExpr = trimBrackets(expr);
    String fullExpr = expr;

    List<String> strList = Utility.splitIntoComponentStrings(expr);
    for (String st : strList) {
      Boolean isDisplayExpression = isDisplayExpression(st, exprObj);
      if (isDisplayExpression.equals(Boolean.TRUE)) {
        exprObj.setDisplayExpr(st);
      } else {
        exprObj.setMobileExpr(st);
      }
    }

    return exprObj;
  }

  public void populateDTOWithTargetAllParams(StrategyDTO strategy) {
    strategy.placements.addAll(addAllPlacements());
  }

  @Transactional
  private TargetingComponent createTargetingComponent(long tfilterId, Long tOperatorId,
      String criteria) {
    TargetingComponent tc = new TargetingComponent();
    tc.setTargetingFilterId(tfilterId);
    tc.setTargetingOperatorId(tOperatorId);
    tc.setCriteria(criteria);
    tcRepository.save(tc);
    return tc;
  }

  private List<BaseModel> addAllPlacements() {
    List<BaseModel> placements = new ArrayList<BaseModel>();
    ElasticResponse sts =
        elasticSearch.searchByGivenFilter(TablesEntity.SOURCE_TYPE, new ArrayList<>());
    if (sts != null) {
      for (BaseModel st : sts.getData()) {
        BaseModel bm = new BaseModel(st.getId(), st.getName());
        placements.add(bm);
      }
    }
    return placements;
  }

  private Boolean isDisplayExpression(String str, PlacementExpressionObject exprObj) {
    String expr = Utility.trimBracketsAndReturnValidExpression(str);
    if (expr == null) {
      // TODO error handling
    } else {
      String idStr = Utility.findFirstIdInExpression(expr);
      String exprType = Utility.findExpressionType(idStr);
      if (exprType.equals(TargetingConstants.PLACEMENTS)) {
        String tcIdString = Utility.trimSorroundingBrackets(idStr);
        if (tcIdString == null || tcIdString.length() == 0) {
          // TODO Error handling
        }
        logger.debug("expr after trimming of brackets : " + tcIdString);
        Long tcId = Long.parseLong(tcIdString);

        TargetingComponent tc = tcRepository.getOne(tcId);
        String val = tc.getCriteria();
        if (val.trim().equals("1")) {
          exprObj.setDpTC(tc);
          return Boolean.TRUE;
        }
      }
    }

    return Boolean.FALSE;
  }
}

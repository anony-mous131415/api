package io.revx.api.audit;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.revx.api.mysql.entity.audit.AuditChange;
import io.revx.api.mysql.entity.audit.AuditLog;
import io.revx.api.mysql.repo.audit.AuditChangeRepository;
import io.revx.api.mysql.repo.audit.AuditLogRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.constant.Constants;
import io.revx.core.model.strategy.StrategyDTO;

@Component
public class StrategyAuditService extends AuditService implements IAuditService<StrategyDTO> {
	private static final Logger logger = LoggerFactory.getLogger(StrategyAuditService.class);

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	AuditLogRepository auditLogRepository;

	@Autowired
	AuditChangeRepository changeRepository;

	public void audit(StrategyDTO oldObj, StrategyDTO newObj) throws Exception {
		logger.debug("audit advertiser line item");
		try {
			// UserInfo ui = loginUserDetailsService.getUserInfo();
			String uinfo = ThreadContext.get(Constants.USER_ID);

			// TODO: This Case will not come in prod
			long userId = uinfo == null ? -1L : Long.parseLong(uinfo);
			AuditLog log = new AuditLog();
			Set<AuditChange> changes = new HashSet<AuditChange>();
			log.setAuditChanges(changes);
			compare(log, oldObj, newObj);
			if (log.getAuditChanges() != null && !log.getAuditChanges().isEmpty()) {
//				throw new NoAuditChangeException("no audit log added because nothing has changed "
//						+ "or the change is not tracked by this service yet");
				log.setEntityId(newObj.id);
				// log.setEntityType(StrategyEntity.class.getName());
				log.setEntityType("STRATEGY");
//				if (newObj != null && newObj.getModifiedTime() != null)
//					log.setTimestamp(newObj.getModifiedTime());
//				else
				log.setTimestamp(System.currentTimeMillis() / 1000);
				log.setUserId(userId);
				log.setType(1L);
				if (oldObj == null)
					log.setMsg("Created new Advertiser Line Item");
				else
					log.setMsg("Updated an Advertiser Line Item");

				// adx.saveOrUpdate(log);
				auditLogRepository.save(log);
				for (AuditChange auditChange : changes) {
					auditChange.setAuditLogId(log.getId());
				}
				changeRepository.saveAll(changes);
				logger.info("saved audit log: {} " , log.getId());
			}
		} catch (Exception e) {
			throw e;
		}

	}

	private void compare(AuditLog log, StrategyDTO o, StrategyDTO n) {

		addChange(log, "Strategy name", o == null ? null : o.name, n.name);
		addChange(log, "End date", o == null ? null : getEndTime(o.getEndTime()), getEndTime(n.getEndTime()));
		// addChange(log, "start date", o == null ? null : o.getStartTime(),
		// n.getStartTime());
		addChange(log, "Status", o == null ? null : getStatusString(o.isActive()), getStatusString(n.isActive()));
		addChange(log, "Flow rate", o == null ? null : o.pricingValue, n.pricingValue);
		addChange(log, "Daily frequency cap", o == null ? null : o.fcapFrequency, n.fcapFrequency);
		addChange(log, "Frequency cap duration", o == null ? null : o.fcapInterval, n.fcapInterval);

		addChange(log, "Budget", o == null ? null : o.budgetValue, n.budgetValue);
		addChange(log, "Type", o == null ? null : o.strategyType, n.strategyType);
		addChange(log, "Pacing budget limit", o == null ? null : o.pacingBudgetValue, n.pacingBudgetValue);
		addChange(log, "Pacing type", o == null ? null : o.pacingType, n.pacingType);
		addChange(log, "Pricing", o == null ? null : o.pricingType, n.pricingType);
		addChange(log, "ROI target", o == null ? null : o.roiTargetType, n.roiTargetType);
		addChange(log, "ROI target value", o == null ? null : o.roiTargetValue, n.roiTargetValue);
		addChange(log, "Bid cap max", o == null ? null : o.getBidCapMax(), n.getBidCapMax());
		addChange(log, "Bid cap min", o == null ? null : o.getBidCapMin(), n.getBidCapMin());

		addChange(log, "Target geographies", o == null ? null : o.targetGeographies, n.targetGeographies);

		addChange(log, "Target browsers", o == null ? null : o.targetBrowsers, n.targetBrowsers);
		addChange(log, "Target days", o == null ? null : o.targetDays, n.targetDays);

		addChange(log, "Target app segments", o == null ? null : o.targetAppSegments, n.targetAppSegments);
		addChange(log, "Target web segments", o == null ? null : o.targetWebSegments, n.targetWebSegments);
		addChange(log, "Target dmp segments", o == null ? null : o.targetDmpSegments, n.targetDmpSegments);
		addChange(log, "Target aggregators", o == null ? null : o.rtbAggregators, n.rtbAggregators);
		addChange(log, "Target placements", o == null ? null : o.placements, n.placements);
		addChange(log, "Target device types",
				(o == null || o.targetMobileDevices == null) ? null : o.targetMobileDevices.targetDeviceTypes,
				n.targetMobileDevices == null ? null : n.targetMobileDevices.targetDeviceTypes);

		// TODO - Mobile brands and models as these are not used currently so not writing logic

		addChange(log, "Target operating systems",
				(o == null || o.targetMobileDevices == null || o.targetMobileDevices.targetOperatingSystems == null
						|| o.targetMobileDevices.targetOperatingSystems.selectAllOperatingSystems) ? null
								: o.targetMobileDevices.targetOperatingSystems,
				(n.targetMobileDevices == null || n.targetMobileDevices.targetOperatingSystems == null ||
						n.targetMobileDevices.targetOperatingSystems.selectAllOperatingSystems ? null
								: n.targetMobileDevices.targetOperatingSystems));

		addChange(log, "Target android app categories",
				(o == null || o.getTargetAndroidCategories() == null ? null : o.getTargetAndroidCategories().getAppCategories()),
				(n.getTargetAndroidCategories() == null ? null : n.getTargetAndroidCategories().getAppCategories()));
		addChange(log, "Target iOS app categories",
				(o == null || o.getTargetIosCategories() == null ? null : o.getTargetIosCategories().getAppCategories()),
				(n.getTargetIosCategories() ==  null ? null : n.getTargetIosCategories().getAppCategories()));

		addChange(log, "Target app ratings",
				(o == null || o.getTargetAppRatings() == null) ? 0 : o.getTargetAppRatings(),
				n.getTargetAppRatings() == null ? 0 : n.getTargetAppRatings());
		addChange(log, "Connection type", o == null ? null : o.getConnectionTypes(), n.getConnectionTypes());
		addChange(log, "Target only published apps", o == null ? null : o.targetOnlyPublishedApp,
				n.targetOnlyPublishedApp);

		addChange(log, "Target sites", (o == null || o.getRtbSites() == null) ? null : o.getRtbSites().rtbSites,
				n.getRtbSites() == null ? null : n.getRtbSites().rtbSites);

		addChange(log, "Target deal category", o == null ? null : o.getTargetDealCategory(), n.getTargetDealCategory());
		addChange(log, "Auction type", o == null ? null : o.getAuctionTypeTargeting(), n.getAuctionTypeTargeting());
	}

	private String getEndTime(BigInteger date) {
		if (date == null || date.compareTo(BigInteger.valueOf(-1)) == 0
				|| date.compareTo(BigInteger.valueOf(7258118399l)) == 0) {
			return "Never ending";
		}

		return LocalDateTime.ofEpochSecond(date.longValue(), 0, ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
	}

	private String getStatusString(boolean status) {
		return (status) ? "Active" : "Inactive";
	}

}

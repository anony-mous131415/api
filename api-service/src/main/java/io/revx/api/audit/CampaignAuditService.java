package io.revx.api.audit;

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
import io.revx.core.model.campaign.CampaignDTO;

@Component
public class CampaignAuditService extends AuditService implements IAuditService<CampaignDTO> {
	private static final Logger logger = LoggerFactory.getLogger(CampaignAuditService.class);

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	AuditLogRepository auditLogRepository;

	@Autowired
	AuditChangeRepository changeRepository;

	@Override
	public void audit(CampaignDTO oldObj, CampaignDTO newObj) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("audit Campaign item");
		try {
			// UserInfo ui = loginUserDetailsService.getUserInfo();
			String uinfo = ThreadContext.get(Constants.USER_ID);
			// TODO: This Case will not come in prod
			// long userId = ui == null ? -1l : ui.getUserId();
			long userId = uinfo == null ? -1l : Long.parseLong(uinfo);
			AuditLog log = new AuditLog();
			Set<AuditChange> changes = new HashSet<AuditChange>();
			log.setAuditChanges(changes);
			compare(log, oldObj, newObj);
//			if (log.getAuditChanges() == null || log.getAuditChanges().size() == 0)
//				throw new NoAuditChangeException("no audit log added because nothing has changed "
//						+ "or the change is not tracked by this service yet");
			if (log.getAuditChanges() != null && !log.getAuditChanges().isEmpty()) {
				log.setEntityId(newObj.id);
				// log.setEntityType(CampaignDTO.class.getName());
				log.setEntityType("CAMPAIGN");
//			if (newObj != null && newObj.getModifiedTime() != null)
//				log.setTimestamp(newObj.getModifiedTime());
//			else
				log.setTimestamp(System.currentTimeMillis() / 1000);
				log.setUserId(userId);
				log.setType(1l);
				if (oldObj == null)
					log.setMsg("created new Campaign Item");
				else
					log.setMsg("updated an Campaign Item");

				// adx.saveOrUpdate(log);
				auditLogRepository.save(log);
				for (AuditChange auditChange : changes) {
					auditChange.setAuditLogId(log.getId());
				}
				changeRepository.saveAll(changes);
				logger.info("saved audit log: " + log.getId());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void compare(AuditLog log, CampaignDTO o, CampaignDTO n) {

		addChange(log, "Campaign name", o == null ? null : o.name, n.name);
		addChange(log, "End date", o == null ? null : getFormattedDateTime(o.getEndTime()),
				getFormattedDateTime(n.getEndTime()));
		addChange(log, "Start time", o == null ? null : getFormattedDateTime(o.getStartTime()),
				getFormattedDateTime(n.getStartTime()));
		addChange(log, "Status", o == null ? null : getStatusString(o.isActive()), getStatusString(n.isActive()));
		addChange(log, "Retargeting campaign", o == null ? null : o.isRetargeting(), n.isRetargeting());
		addChange(log, "Campaign pricing", o == null ? null : o.getPricingId(), n.getPricingId());
		addChange(log, "Lifetime media budget", o == null ? null : o.getLifetimeBudget(), n.getLifetimeBudget());
		addChange(log, "Campaign objective", o == null ? null : o.getObjective(), n.getObjective());

		addChange(log, "Platform margin", o == null ? null : o.getPlatformMargin(), n.getPlatformMargin());
		addChange(log, "IVS distribution", o == null ? null : o.getIvsDistribution(), n.getIvsDistribution());
		addChange(log, "Flow rate", o == null ? null : o.getFlowRate(), n.getFlowRate());
		addChange(log, "Lifetime delivery cap", o == null ? null : o.getLifetimeDeliveryCap(),
				n.getLifetimeDeliveryCap());
		addChange(log, "Daily media budget", o == null ? null : o.getDailyBudget(), n.getDailyBudget());
		addChange(log, "Daily delivery cap", o == null ? null : o.getDailyDeliveryCap(), n.getDailyDeliveryCap());
		addChange(log, "Attribution ratio", o == null ? null : o.getAttributionRatio(), n.getAttributionRatio());
		addChange(log, "Daily user fcap", o == null ? null : o.getDailyUserFcap(), n.getDailyUserFcap());
		addChange(log, "User fcap duration", o == null ? null : o.getUserFcapDuration(), n.getUserFcapDuration());
		addChange(log, "Currency code", o == null ? null : o.getCurrencyCode(), n.getCurrencyCode());
		addChange(log, "Budget", o == null ? null : o.getBudget(), n.getBudget());
		addChange(log, "Life time Budget", o == null ? null : o.getLifetimeBudget(), n.getLifetimeBudget());
		addChange(log, "Frequency cap", o == null ? null : o.getFcap(), n.getFcap());

		addChange(log, "Lifetime user fcap", o == null ? null : o.getLifetimeUserFcap(), n.getLifetimeUserFcap());
		// addChange(log, "daysDuration", o == null ? null : o.getDaysDuration(),
		// n.getDaysDuration());
		// addChange(log, "daysElapsed", o == null ? null : o.getDaysElapsed(),
		// n.getDaysElapsed());
		addChange(log, "Pixel", o == null ? null : o.getPixel(), n.getPixel());
		addChange(log, "Currency", o == null ? null : o.getCurrency(), n.getCurrency());

	}

	private String getFormattedDateTime(Long date) {
		if (date == null || date.compareTo(-1l) == 0 || date.compareTo(7258118399l) == 0) {
			return "Never ending";
		}

		return LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
	}

	private String getStatusString(boolean status) {
		return (status) ? "Active" : "Inactive";
	}

}

package io.revx.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.core.enums.RoleName;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;
import io.revx.core.response.UserInfo;

@Component
public class LoginUserDetailsService {

	private static Logger logger = LogManager.getLogger(LoginUserDetailsService.class);

	public String getAdvertiserCurrencyId() {
		if (isAdvertiserLogin()) {
			for (Advertiser adv : getUserInfo().getAdvertisers()) {
				return adv.getCurrencyCode();
			}
		}
		return getLicenseeCurrencyId();
	}

	public String getAdvertiserCurrencyIdIfAdvLogin() {
		if (isAdvertiserLogin()) {
			for (Advertiser adv : getUserInfo().getAdvertisers()) {
				return adv.getCurrencyCode();
			}
		}
		return null;
	}

	public boolean isAdvertiserLogin() {
		UserInfo ui = getUserInfo();
		return ui.getAdvertisers() != null && ui.getAdvertisers().size() == 1;
	}

	public Licensee getSelectedLicensee() {
		return getUserInfo().getSelectedLicensee();
	}

	public long getLicenseeId() {
		return getUserInfo().getSelectedLicensee().getId();
	}

	public String getLicenseeCurrencyId() {
		return getUserInfo().getSelectedLicensee().getCurrencyCode();
	}

	public UserInfo getUserInfo() {
		UserInfo ui = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserInfo) {
			ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		return ui;
	}

	public String getUserName() {
		UserInfo ui = getUserInfo();
		return ui != null ? ui.getUsername() : "";
	}

	public String getLicenseeName() {
		UserInfo ui = getUserInfo();
		return (ui != null && ui.getSelectedLicensee() != null) ? ui.getSelectedLicensee().getName() : "";
	}

	public ElasticSearchTerm getElasticSearchTerm() {
		UserInfo ui = getUserInfo();
		ElasticSearchTerm est = new ElasticSearchTerm();
		if (ui != null) {
			est.setLicenseeId(ui.getSelectedLicensee().getId());
			if (ui.getAdvertisers() != null) {
				est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
			}
		}
		return est;
	}

	public List<Long> getAdvertisers(Set<Advertiser> advertisers) {
		List<Long> advs = new ArrayList<Long>();
		if (advertisers != null) {
			for (Advertiser adv : advertisers) {
				advs.add(adv.getId());
			}
		}
		return advs;
	}

	public List<Long> getAdvertisers() {
		List<Long> advs = new ArrayList<Long>();
		UserInfo ui = getUserInfo();
		if (ui.getAdvertisers() != null) {
			for (Advertiser adv : ui.getAdvertisers()) {
				advs.add(adv.getId());
			}
		}
		return advs;
	}

	public int getAllLicenseeCount() {
		int count = 0;
		UserInfo ui = getUserInfo();
		if (ui.getAdvLicenseeMap() != null) {
			count = ui.getAdvLicenseeMap().size();
		}
		return count;
	}

	public boolean isFullLicenseeAccess() {
		UserInfo ui = getUserInfo();
		return ui.getAdvLicenseeMap() == null && ui.getSelectedLicensee() != null;
	}

	public Map<Long, Set<Long>> getAllAdvLicenseeMap() {
		UserInfo ui = getUserInfo();
		return ui.getAdvLicenseeMap();

	}

	public boolean isAdminUser() {
		UserInfo ui = getUserInfo();
		if (ui != null && ui.getAuthorities() != null
				&& (isSuperAdminUser() || ui.getAuthorities().contains(RoleName.ADMIN.name())
						|| ui.getAuthorities().contains("ROLE_" + RoleName.ADMIN.name()))) {
			return true;
		}
		return false;
	}

	public boolean isSuperAdminUser() {
		UserInfo ui = getUserInfo();
		if (ui != null && ui.getAuthorities() != null && ui.getAuthorities().contains(RoleName.SADMIN.name())
				|| ui.getAuthorities().contains("ROLE_" + RoleName.SADMIN.name())) {
			return true;
		}
		return false;
	}

	/**
	 * A new role INTERNAL will be introduced. This will not be a hierarchical role,
	 * so getHighestRoleOfLoginUser should not be used.
	 * 
	 * @return boolean: true - internal role, false - not internal user
	 */
	public boolean isInternalUser() {
		UserInfo ui = getUserInfo();
		return ui != null && ui.getAuthorities() != null && ui.getAuthorities().contains(RoleName.INTERNAL.name())
				|| ui != null && ui.getAuthorities() != null && ui.getAuthorities().contains("ROLE_" + RoleName.INTERNAL.name());
	}

	public RoleName getHighestRoleOfLoginUser() {
		RoleName role = RoleName.DEMO;
		UserInfo ui = getUserInfo();
		if (ui != null && ui.getAuthorities() != null) {
			for (String roleStr : ui.getAuthorities()) {
				RoleName tmpRole = RoleName.getRoleByName(roleStr);
				// SuperAdmin Will have 0 ordinal lowest means Highest Role
				if (tmpRole != null && tmpRole.ordinal() < role.ordinal()) {
					role = tmpRole;
				}
			}
		}
		logger.debug(" getHighestRoleOfLoginUser : {} ", role);
		return role;
	}

	public boolean isReadOnlyUser() {
		RoleName role = getHighestRoleOfLoginUser();
		return RoleName.RO.equals(role);
	}

	public boolean isDemoUser() {
		RoleName role = getHighestRoleOfLoginUser();
		return RoleName.DEMO.equals(role);
	}

	public boolean isValidLicensee(long licenseeId) {
		UserInfo uInfo = getUserInfo();
		return uInfo.getSelectedLicensee().getId() == licenseeId;
	}

	public boolean isValidAdvertiser(Advertiser advertiser) {
		boolean isValid = true;
		UserInfo uInfo = getUserInfo();
		List<Long> advs = getAdvertisers();
		logger.debug("Validating advertiser : {} with advIds : {} for user : {}", advertiser, advs, uInfo);
		if (advertiser == null || uInfo.getSelectedLicensee().getId().compareTo(advertiser.getLicenseeId()) != 0
				|| (CollectionUtils.isNotEmpty(advs) && !advs.contains(advertiser.getId()))) {
			isValid = false;
		}
		return isValid;
	}

}

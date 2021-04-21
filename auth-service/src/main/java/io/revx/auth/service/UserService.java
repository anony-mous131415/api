package io.revx.auth.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.revx.auth.entity.AdvertiserEntity;
import io.revx.auth.entity.CurrencyEntity;
import io.revx.auth.entity.LicenseeEntity;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.RolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.AdvertiserRepository;
import io.revx.auth.repository.LicenseeRepository;
import io.revx.auth.repository.LicenseeUserRoleRepo;
import io.revx.auth.repository.UserRepository;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.utils.UserUtils;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;
import io.revx.core.response.UserInfo;

@Service
public class UserService {

	private static Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	LicenseeUserRoleRepo licenseeUserRoleRepo;

	//  @Autowired
	//  Md5PasswordEncoder md5PasswordEncoder;

	@Autowired
	LicenseeRepository licenseeRepository;

	@Autowired
	AdvertiserRepository advertiserRepository;

	public UserInfoEntity create(UserInfoEntity user) {
		//    user.setPassword(md5PasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public List<UserInfoEntity> findAll() {
		return userRepository.findAll();
	}

	public UserInfoEntity findById(int id) {
		return userRepository.findById(id).get();
	}

	public UserInfoEntity findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public UserInfoEntity findByUsername(String username, boolean isActive) {
		return userRepository.findByUsernameAndIsActive(username, isActive);
	}

	public UserInfoEntity update(UserInfoEntity user) {
		//    user.setPassword(md5PasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.USER_PRIVILIGE)
	public Set<Licensee> fetchUserPrivilige(String userName) {
		UserInfoEntity user = findByUsername(userName, true);
		Set<Licensee> licenseeies = null;
		if (user != null) {
			List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
					licenseeUserRoleRepo.findByUserId(user.getId());
			licenseeies = new HashSet<Licensee>();
			if (licenseeUserRolesEntityList != null && licenseeUserRolesEntityList.size() == 1) {
				LicenseeUserRolesEntity lur = licenseeUserRolesEntityList.get(0);
				if (lur.getLicenseeEntity() == null) {
					// This is the Case of SuperAdmin
					populateSuperAdminData(licenseeies);
				}
			} else {
				populateMappingData(licenseeUserRolesEntityList, licenseeies);
			}
		}

		return makeUniq(licenseeies);

	}

	private Set<Licensee> makeUniq(Set<Licensee> licenseeies) {
		Map<Long, Licensee> map = new HashMap<>();
		if (licenseeies != null) {
			for (Licensee licensee : licenseeies) {
				if (!map.containsKey(licensee.getId()))
					map.put(licensee.getId(), licensee);
			}
		}
		return new HashSet<>(map.values());
	}

	private void populateMappingData(List<LicenseeUserRolesEntity> licenseeUserRolesEntityList,
			Set<Licensee> licenseeies) {
		for (LicenseeUserRolesEntity lure : licenseeUserRolesEntityList) {
			if (lure.getLicenseeEntity() != null && lure.getLicenseeEntity().isActive())
				licenseeies.add(new Licensee(lure.getLicenseeEntity().getId(),
						lure.getLicenseeEntity().getLicenseeName(),
						getCurrencyCode(lure.getLicenseeEntity().getCurrencyEntity())));
		}

	}

	private void populateSuperAdminData(Set<Licensee> licenseeies) {
		List<LicenseeEntity> licenseeEntities = licenseeRepository.findByIsActive(true);
		for (LicenseeEntity lEntity : licenseeEntities) {
			licenseeies.add(new Licensee(lEntity.getId(), lEntity.getLicenseeName(),
					getCurrencyCode(lEntity.getCurrencyEntity())));
		}
	}

	public List<LicenseeUserRolesEntity> isValidForLicenseeSwitch(String userName, Long licensee) {
		UserInfoEntity user = findByUsername(userName, true);
		return licenseeUserRoleRepo.findByUserIdAndLicenseeId(user.getId(), licensee);
	}

	public List<LicenseeUserRolesEntity> findUserRolesByuserId(long userId) {
		return licenseeUserRoleRepo.findByUserId(userId);
	}

	public UserInfo getUserInfoIfEligible(UserInfoModel userFromDb, UserLoginRequest loginUser) {
		UserInfo ui = null;
		List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
				licenseeUserRoleRepo.findByUserId(userFromDb.getUserId());
		updateLicenseeAndAdvertiser(userFromDb,licenseeUserRolesEntityList);
		if (isAlreadySeletectedLicenseeWhileLogin(userFromDb)) {
			ui = new UserInfo(userFromDb.getUserId(), userFromDb.getUsername(),
					UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
		} else if (loginUser.getLicenseeId() > 0) {
			Set<RolesEntity> roles =
					getRolesForDirectLoginUser(userFromDb, loginUser, licenseeUserRolesEntityList);
			if (roles != null && roles.size() > 0)
				ui = new UserInfo(userFromDb.getUserId(), userFromDb.getUsername(),
						UserUtils.getAuthority(roles));
			if (licenseeUserRolesEntityList != null && !licenseeUserRolesEntityList.isEmpty()) {
				for (LicenseeUserRolesEntity licenseeUserRolesEntity : licenseeUserRolesEntityList) {
					if (licenseeUserRolesEntity != null
							&& licenseeUserRolesEntity.getLicenseeEntity() != null) {
						AdvertiserEntity adv = licenseeUserRolesEntity.getAdvertiserEntity();
						ui.addAdvertiserAndLicensee(licenseeUserRolesEntity.getLicenseeEntity().getId(),
								adv == null ? -1 : adv.getId());

					}
				}
			}
		}
		UserUtils.populateUserInfoPojoFromModel(userFromDb, ui);
		logger.debug(" UserInfo {} , userFromDb {} ", ui, userFromDb);
		return ui;
	}

	private Set<RolesEntity> getRolesForDirectLoginUser(UserInfoModel userFromDb,
			UserLoginRequest loginUser, List<LicenseeUserRolesEntity> licenseeUserRolesEntityList) {
		Set<RolesEntity> roles = new HashSet<RolesEntity>();
		if (licenseeUserRolesEntityList != null) {
			for (LicenseeUserRolesEntity lur : licenseeUserRolesEntityList) {
				if (lur.getLicenseeEntity() != null
						&& lur.getLicenseeEntity().getId() == loginUser.getLicenseeId()) {
					userFromDb.setSelectedLicensee((userFromDb.getSelectedLicensee() == null)
							? new Licensee(lur.getLicenseeEntity().getId(),
									lur.getLicenseeEntity().getLicenseeName(),
									getCurrencyCode(lur.getLicenseeEntity().getCurrencyEntity()))
									: userFromDb.getSelectedLicensee());
					if (lur.getAdvertiserEntity() != null)
						userFromDb.addAdvertiser(new Advertiser(lur.getAdvertiserEntity().getId(),
								lur.getAdvertiserEntity().getAdvertiserName(),
								getCurrencyCode(lur.getAdvertiserEntity().getCurrencyEntity())));
					roles.add(lur.getRolesEntity());
				}
			}
		}
		return roles;
	}

	private String getCurrencyCode(CurrencyEntity currencyEntity) {
		return currencyEntity == null ? null : currencyEntity.getCurrencyCode();
	}

	private boolean isAlreadySeletectedLicenseeWhileLogin(UserInfoModel userFromDb) {
		return (userFromDb.getSelectedLicensee() != null
				&& userFromDb.getSelectedLicensee().getId() > 0);
	}

	public UserInfo getUserInfoIfEligible(UserInfoModel userFromDb) {
		UserInfo ui = null;
		if (userFromDb.getSelectedLicensee() != null && userFromDb.getSelectedLicensee().getId() > 0) {
			ui = new UserInfo(userFromDb.getUserId(), userFromDb.getUsername(),
					UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
		} else {

		}
		UserUtils.populateUserInfoPojoFromModel(userFromDb, ui);
		return ui;
	}

	public void updateLicenseeAndAdvertiser(UserInfoModel customUser,
			List<LicenseeUserRolesEntity> licenseeUserRolesEntities) {
		Map<Licensee, Set<Advertiser>> licenseeAdvMap = getLicenseeMapping(licenseeUserRolesEntities);
		if (licenseeAdvMap.size() == 1) {
			for (Entry<Licensee, Set<Advertiser>> lurMap : licenseeAdvMap.entrySet()) {
				customUser.setSelectedLicensee(lurMap.getKey());
				customUser.setAdvertisers(lurMap.getValue().size() > 0 ? lurMap.getValue() : null);
			}
		}

	}

	public void updateLicenseeAndAdvertiser(UserInfo uInfo,
			List<LicenseeUserRolesEntity> licenseeUserRolesEntities, Long licensee) {
		if (licenseeUserRolesEntities != null && licenseeUserRolesEntities.size() == 1) {
			LicenseeUserRolesEntity lur = licenseeUserRolesEntities.get(0);
			if (lur.getLicenseeEntity() == null) {
				LicenseeEntity le = licenseeRepository.getOne(licensee.intValue());;
				uInfo.setSelectedLicensee(new Licensee(le.getId(), le.getLicenseeName(),
						getCurrencyCode(le.getCurrencyEntity())));
			} else {
				updateUserInfoAndSelectLicensee(uInfo, licenseeUserRolesEntities, licensee);
			}
		} else {
			updateUserInfoAndSelectLicensee(uInfo, licenseeUserRolesEntities, licensee);
		}

		logger.debug(" uInfo {} ", uInfo);

	}

	private void updateUserInfoAndSelectLicensee(UserInfo uInfo,
			List<LicenseeUserRolesEntity> licenseeUserRolesEntities, Long licensee) {
		Map<Licensee, Set<Advertiser>> licenseeAdvMap = getLicenseeMapping(licenseeUserRolesEntities);
		logger.debug(" licenseeAdvMap {} ", licenseeAdvMap);
		if (licenseeAdvMap != null) {
			for (Entry<Licensee, Set<Advertiser>> lurMap : licenseeAdvMap.entrySet()) {
				logger.debug(" lurMap {} : {} : {}  : {} ", lurMap, lurMap.getKey().getId(), licensee,
						(lurMap.getKey().getId() == licensee));
				if (lurMap.getKey().getId().equals(licensee)) {
					uInfo.setSelectedLicensee(lurMap.getKey());
					uInfo.setAdvertisers(lurMap.getValue().size() > 0 ? lurMap.getValue() : null);
				}
			}

		}

	}

	private Map<Licensee, Set<Advertiser>> getLicenseeMapping(
			List<LicenseeUserRolesEntity> licenseeUserRolesEntities) {
		Map<Licensee, Set<Advertiser>> licenseeAdvMap = new HashMap<>();
		if (licenseeUserRolesEntities != null) {
			for (LicenseeUserRolesEntity lur : licenseeUserRolesEntities) {
				logger.debug(" lur {} ", lur);
				if (lur.getLicenseeEntity() != null) {
					Licensee ls = new Licensee(lur.getLicenseeEntity().getId(),
							lur.getLicenseeEntity().getLicenseeName(),
							getCurrencyCode(lur.getLicenseeEntity().getCurrencyEntity()));
					Set<Advertiser> advs = licenseeAdvMap.computeIfAbsent(ls, k -> new HashSet<Advertiser>());
					if (lur.getAdvertiserEntity() != null)
						advs.add(new Advertiser(lur.getAdvertiserEntity().getId(),
								lur.getAdvertiserEntity().getAdvertiserName(),
								getCurrencyCode(lur.getAdvertiserEntity().getCurrencyEntity())));
				}

			}
		}
		return licenseeAdvMap;
	}

	public List<AdvertiserEntity> findAdvertiserByLicensee(long licenseeId) {
		return advertiserRepository.findByLicenseeId(true, licenseeId);
	}

	public void populateAdvLicenseeMap(UserInfo uip, long userId) {
		List<LicenseeUserRolesEntity> allRoles = findUserRolesByuserId(userId);
		if (allRoles != null && !allRoles.isEmpty()) {
			if (allRoles.size() == 1) {
				LicenseeUserRolesEntity lur = allRoles.get(0);
				if (lur != null) {
					LicenseeEntity le = lur.getLicenseeEntity();
					AdvertiserEntity adv = lur.getAdvertiserEntity();
					if (adv != null) {
						uip.addAdvertiserAndLicensee(le.getId(), adv.getId());
					} else if (le != null) {
						uip.addAdvertiserAndLicensee(le.getId(), -1);
					}
				}
			} else {
				for (LicenseeUserRolesEntity licenseeUserRolesEntity : allRoles) {
					if (licenseeUserRolesEntity != null
							&& licenseeUserRolesEntity.getLicenseeEntity() != null) {
						AdvertiserEntity adv = licenseeUserRolesEntity.getAdvertiserEntity();
						if (adv == null || adv.getId() <= 0) {
							List<AdvertiserEntity> advertisersOfLicensee =
									findAdvertiserByLicensee(licenseeUserRolesEntity.getLicenseeEntity().getId());
							if (advertisersOfLicensee != null && !advertisersOfLicensee.isEmpty()) {
								for (AdvertiserEntity advLicensee : advertisersOfLicensee) {
									uip.addAdvertiserAndLicensee(licenseeUserRolesEntity.getLicenseeEntity().getId(),
											advLicensee.getId());
								}
							}
						} else {
							uip.addAdvertiserAndLicensee(licenseeUserRolesEntity.getLicenseeEntity().getId(),
									adv.getId());
						}

					}
				}
			}
		}

	}
}

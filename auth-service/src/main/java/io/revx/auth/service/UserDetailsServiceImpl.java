package io.revx.auth.service;

import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.LicenseeUserRoleRepo;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.utils.UserUtils;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.model.Licensee;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.Error;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	UserService userService;

	@Autowired
	LicenseeUserRoleRepo licenseeUserRoleRepo;

	@Autowired
	JwtTokenProvider jwtTokenProvider;


	@Override
	public UserInfoModel loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfoEntity user = userService.findByUsername(username);
		if (user == null || !user.isActive()) {
			throw new UserUnAuthenticateException(
					(user == null ? ErrorCode.USER_NAME_NOT_FOUND_ERROR : ErrorCode.USER_NOT_ACTIVE_ERROR),
					username);
		}
		List<LicenseeUserRolesEntity> licenseeUserRolesEntity =
				licenseeUserRoleRepo.findByUserId(user.getId());
		logger.debug(" licenseeUserRoles Here Role Size is  {}" , licenseeUserRolesEntity == null ? 0
				: licenseeUserRolesEntity.size());
		logger.debug(" licenseeUserRoles " + licenseeUserRolesEntity + " ,  for user :" + user);
		licenseeUserRolesEntity.forEach(lur -> {
			logger.debug(" *** " + lur + " ***** ");
		});
		UserInfoModel customUser = new UserInfoModel(user.getUsername(), user.getPassword(),
				UserUtils.getAuthority(licenseeUserRolesEntity));
		customUser.setUserId(user.getId());
		userService.updateLicenseeAndAdvertiser(customUser, licenseeUserRolesEntity);
		return customUser;
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.USER_PRIVILIGE)
	public ApiResponseObject<Set<Licensee>> fetchUserPrivilige(String masterToken) {
		ApiResponseObject<Set<Licensee>> apiResp = new ApiResponseObject<Set<Licensee>>();
		String userName = jwtTokenProvider.getUsernameFromTokenAfterValidation(masterToken);
		logger.debug(" userName :" + userName);
		StringBuffer error = new StringBuffer();
		if (StringUtils.isNotBlank(userName)) {
			Set<Licensee> lp = userService.fetchUserPrivilige(userName);
			apiResp.setRespObject(lp);
		} else {
			error.append("Auth Token or User Is Invalid ");
			apiResp.setError(new Error(error.toString()));
		}
		return apiResp;
	}
}

package io.revx.auth.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.ExpiredJwtException;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoMasterPojo;
import io.revx.auth.requests.PasswordChangeRequest;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.security.Md5PasswordEncoder;
import io.revx.auth.utils.UserUtils;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;

@Service
public class AuthValidaterServiceImpl {

	private static Logger logger = LogManager.getLogger(AuthValidaterServiceImpl.class);
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserService userService;

	@Autowired
	LoginLogoutService loginLogoutService;

	@Autowired
	Md5PasswordEncoder md5PasswordEncoder;

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.TOKEN_VALIDATE)
	public UserInfo validateToken(String jwtToken) throws Exception {
		String username = null;
		UserInfo ui = null;
		Boolean isValid = false;
		try {
			username = jwtTokenProvider.getUsernameFromToken(jwtToken);
			logger.debug("Validaating Token Against  :" + username);
		} catch (ExpiredJwtException e) {
			logger.debug("the token is expired and not valid anymore" + e);
			throw new ApiException(ErrorCode.ACCESS_TOKEN_EXPIRED, e);
		} catch (Exception e) {
			logger.debug("Token is Not Valid");
			throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN, e);
		}
		logger.debug("SecurityContextHolder.getContext().getAuthentication() "
				+ SecurityContextHolder.getContext().getAuthentication());
		if (StringUtils.isNotBlank(username)) {
			isValid = jwtTokenProvider.validateToken(jwtToken, username);
			if (!isValid) {
				throw new ApiException(ErrorCode.ACCESS_TOKEN_EXPIRED);
			} else {
				ui = jwtTokenProvider.getUserFromAccessToken(jwtToken);
			}
		}

		logger.debug(" ui :" + ui);

		return ui;
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.SWITCH)
	public ApiResponseObject<TokenResponse> switchLicensee(Long licensee, String jwtMasterToken)
			throws ApiException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug(" In switchContext :" + auth);
		TokenResponse resp = null;
		if (auth != null) {
			UserInfoMasterPojo user = jwtTokenProvider.getUserInfoFromMasterToken(jwtMasterToken);
			resp = new TokenResponse(user.getUsername(), jwtMasterToken);
			logger.debug(" Now info " + user);
			List<LicenseeUserRolesEntity> rolesList =
					userService.isValidForLicenseeSwitch(user.getUsername(), licensee);
			if (rolesList != null && rolesList.size() > 0) {
				UserInfo uip = new UserInfo(user.getUserId(), user.getUsername(),
						UserUtils.getAuthoritySet(rolesList));
				userService.updateLicenseeAndAdvertiser(uip, rolesList, licensee);
				logger.debug(" Now uip " + uip);
				if (uip != null) {
					userService.populateAdvLicenseeMap(uip, user.getUserId());
				}
				// TODO: Something For Licencee
				user.setSelectedLicensee(uip.getSelectedLicensee());
				resp.setToken(jwtTokenProvider.generateAccessToken(uip));
			} else {
				logger.debug(
						"licensee : " + licensee + " Are Not the Part of UserContext : " + user.getUsername());
				throw new ApiException(ErrorCode.USER_DONT_HAVE_ACCESS_ON_GIVEN_LICENSEE_ERROR,
						user.getUsername(), String.valueOf(licensee));
			}
		} else {
			logger.debug("Not A Valid Session : ReLogin Again");
			throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN);
		}
		ApiResponseObject<TokenResponse> apiResp = new ApiResponseObject<TokenResponse>();
		apiResp.setRespObject(resp);
		return apiResp;
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.PASS_CHANGE)
	public ApiResponseObject<Boolean> changePassword(PasswordChangeRequest passwordChangeRequest,
			String accessToken) throws ApiException {
		String userName = null;
		boolean status = true;
		if (jwtTokenProvider.validateToken(accessToken)) {
			userName = jwtTokenProvider.getUsernameFromToken(accessToken);
			if (allowPasswordChange(userName, passwordChangeRequest)) {
				UserInfoEntity ui = userService.findByUsername(userName);
				logger.debug("user details {}", ui.toString());
				if (isValidOldPassword(ui.getPassword(), passwordChangeRequest.getOldPassword())) {
					ui.setPassword(passwordChangeRequest.getNewPassword());
					userService.update(ui);
				} else {
					status = false;
				}
			} else {
				throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN);
			}
		} else {
			throw new ApiException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		}
		ApiResponseObject<Boolean> apiResp = new ApiResponseObject<Boolean>();
		apiResp.setRespObject(status);
		return apiResp;
	}

	private boolean isValidOldPassword(String oldPasswordDb, String oldPassword) {
		if (StringUtils.isBlank(oldPassword)) {
			return true;
		}
		return md5PasswordEncoder.matches(oldPassword, oldPasswordDb);
	}

	private boolean allowPasswordChange(String userName,
			PasswordChangeRequest passwordChangeRequest) {
		if (StringUtils.equals(userName, passwordChangeRequest.getUsername())
				&& StringUtils.isNotBlank(passwordChangeRequest.getNewPassword())) {
			return true;
		}
		return false;
	}

	public ApiResponseObject<Boolean> logout(String token) {
		return loginLogoutService.logout(token);
	}

	public ApiResponseObject<Boolean> logoutUser(String token, String username) {
		return loginLogoutService.logoutUser(token, username);
	}
}

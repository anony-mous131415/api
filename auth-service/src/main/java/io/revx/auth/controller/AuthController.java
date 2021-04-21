package io.revx.auth.controller;

import io.revx.auth.constants.ApiConstant;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.entity.LifeTimeAuthenticationEntity;
import io.revx.auth.requests.PasswordChangeRequest;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.service.AuthValidaterServiceImpl;
import io.revx.auth.service.ExternalTokenAuthService;
import io.revx.auth.service.LifeTimeAuthTokenService;
import io.revx.auth.service.UserDetailsServiceImpl;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.constant.RoleConstants;
import io.revx.core.model.Licensee;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

@RestController
@Api(value = "Auth Controller", description = "REST API's for Authentication/Authorization",
tags = {"Auth Api"})
public class AuthController {
	private static Logger logger = LogManager.getLogger(AuthController.class);

	@Autowired
	AuthValidaterServiceImpl authValidaterServiceImpl;
	@Autowired
	ExternalTokenAuthService externalTokenAuthService;

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	LifeTimeAuthTokenService lifeTimeAuthTokenService;

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.TOKEN_VALIDATE)
	@ApiOperation("Validating Access/Master Token.")
	@PreAuthorize(RoleConstants.DEMO)
	@GetMapping(value = ApiConstant.USER_INFO, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<UserInfo>> userInfo(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken) throws Exception {
		logger.debug("authValidaterServiceImpl " + authValidaterServiceImpl);
		UserInfo ui = authValidaterServiceImpl.validateToken(accessToken);
		ApiResponseObject<UserInfo> resp = new ApiResponseObject<UserInfo>();
		resp.setRespObject(ui);
		return ResponseEntity.ok().body(resp);
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.USER_PRIVILIGE)
	@ApiOperation("Getting UserDetail.")
	@PreAuthorize(RoleConstants.DEMO)
	@GetMapping(value = ApiConstant.USER_PRIVILEGE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<Set<Licensee>>> userPrivilege(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken) throws Exception {
		ApiResponseObject<Set<Licensee>> status =
				userDetailsServiceImpl.fetchUserPrivilige(accessToken);
		return ResponseEntity.ok().body(status);
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.SWITCH)
	@ApiOperation("Switching From One Advertiser To Another.")
	@PreAuthorize(RoleConstants.DEMO)
	@GetMapping(value = ApiConstant.SWITCH_LICENSEE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<TokenResponse>> switchLicensee(
			@PathVariable Long licenseeId,
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken) throws Exception {
		logger.debug("Auth " + SecurityContextHolder.getContext().getAuthentication());
		ApiResponseObject<TokenResponse> status =
				authValidaterServiceImpl.switchLicensee(licenseeId, accessToken);
		logger.debug("Auth " + SecurityContextHolder.getContext().getAuthentication());
		return ResponseEntity.ok().body(status);
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.SWITCH)
	@ApiOperation("Validating Token From social(facebook/google/office) and getting AccessToken.")
	@GetMapping(value = ApiConstant.LOGIN_SOCIAL, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<TokenResponse>> loginSocial(@PathVariable String client,
			@PathVariable String socialToken) throws Exception {
		return ResponseEntity.ok()
				.body(externalTokenAuthService.getAccessTokenFromExternalToken(client, socialToken));
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.LOGOUT + ".token")
	@ApiOperation("Expire Access/Master Token.")
	@GetMapping(value = ApiConstant.LOGOUT_URL, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<Boolean>> logoutWithToken(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String token) throws Exception {
		logger.debug("authValidaterServiceImpl " + authValidaterServiceImpl);
		return ResponseEntity.ok().body(authValidaterServiceImpl.logout(token));
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.LOGOUT + ".user")
	@ApiOperation("Expire all token of given User and Logout.<Only Super Admin>")
	@PreAuthorize(RoleConstants.SUPER_ADMIN)
	@GetMapping(value = ApiConstant.LOGOUT_URL_USER, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<Boolean>> logoutUser(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String token, @PathVariable String username)
					throws Exception {
		logger.debug("authValidaterServiceImpl " + authValidaterServiceImpl);
		return ResponseEntity.ok().body(authValidaterServiceImpl.logoutUser(token, username));
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.PASS_CHANGE)
	@PreAuthorize(RoleConstants.READ_ONLY)
	@ApiOperation("Change Password For Given User.")
	@PostMapping(value = ApiConstant.CHANGE_PASS_SECRET, produces = MediaType.APPLICATION_JSON_VALUE,
	consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<Boolean>> changePassword(
			@RequestBody(required = true) @Valid PasswordChangeRequest passwordChangeRequest,
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken) throws Exception {
		logger
		.debug("changePasswordRequest " + passwordChangeRequest + " , accessToken =" + accessToken);
		return ResponseEntity.ok()
				.body(authValidaterServiceImpl.changePassword(passwordChangeRequest, accessToken));

	}

	// MOCK
	@ApiOperation("User Login .")
	@PostMapping(value = ApiConstant.LOGIN_URL, produces = MediaType.APPLICATION_JSON_VALUE,
	consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<TokenResponse>> login(
			@RequestBody(required = true) @Valid UserLoginRequest userLoginRequest) {
		throw new IllegalStateException(
				"This method shouldn't be called. It's implemented by Spring Security filters.");
	}

	// controller to create a lifetime auth token and save it to db
	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.GENARATE + GraphiteConstants.LIFETIME_AUTH)
	@PreAuthorize(RoleConstants.SUPER_ADMIN  + "OR " + RoleConstants.ADMIN + "OR " + RoleConstants.READ_WRITE)
	@ApiOperation("Create a life time auth token.")
	@PostMapping(value = ApiConstant.CREATE_LIFE_TIME_AUTH, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<LifeTimeAuthenticationEntity>> createLifeTimeToken(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken) throws Exception {
		ApiResponseObject<LifeTimeAuthenticationEntity> resp = new ApiResponseObject<>();
		resp.setRespObject(lifeTimeAuthTokenService.generateLifeTimeAuthToken(accessToken));
		return ResponseEntity.ok().body(resp);
	}

	// controller to get all life time auth tokens for the selected licensee id
	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.GENARATE + GraphiteConstants.LIFETIME_AUTH)
	@PreAuthorize(RoleConstants.SUPER_ADMIN  + "OR " + RoleConstants.ADMIN + "OR " + RoleConstants.READ_WRITE)
	@ApiOperation("Get all life time auth tokens for the selected licensee.")
	@GetMapping(value = ApiConstant.LIFE_TIME_AUTH, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<ApiListResponse<LifeTimeAuthenticationEntity>>> getLifeTimeToken(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken) throws Exception {
		ApiResponseObject<ApiListResponse<LifeTimeAuthenticationEntity>> resp = new ApiResponseObject<>();
		resp.setRespObject(lifeTimeAuthTokenService.getLifeTimeAuthToken(accessToken));
		return ResponseEntity.ok().body(resp);
	}

	// controller to delete life time auth token by token id
	@LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.GENARATE + GraphiteConstants.LIFETIME_AUTH)
	@PreAuthorize(RoleConstants.SUPER_ADMIN  + "OR " + RoleConstants.ADMIN + "OR " + RoleConstants.READ_WRITE)
	@ApiOperation("delete the life time auth token for the given tokenId.")
	@PutMapping(value = ApiConstant.DELETE_LIFE_TIME_AUTH, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<ResponseMessage>> deleteLifeTimeToken(
			@RequestHeader(SecurityConstants.AUTH_HEADER) String accessToken, @PathVariable long tokenId) throws Exception {
		ApiResponseObject<ResponseMessage> resp = new ApiResponseObject<>();
		ResponseMessage responseMessage = lifeTimeAuthTokenService.deleteLifeTimeAuthToken(tokenId, accessToken);
		resp.setRespObject(responseMessage);
		return ResponseEntity.ok().body(resp);
	}
}

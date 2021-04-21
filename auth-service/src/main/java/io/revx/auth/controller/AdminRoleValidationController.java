package io.revx.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.revx.auth.constants.ApiConstant;
import io.revx.core.constant.RoleConstants;
import io.revx.core.response.ApiResponseObject;

@RestController
public class AdminRoleValidationController {

  @PreAuthorize(RoleConstants.SUPER_ADMIN)
  @GetMapping(ApiConstant.AUTH_BASE + "/sadmin")
  public ResponseEntity<ApiResponseObject<String>> checkSAdmin() {
    String nameofCurrMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    return gerateResponse("Success For Method : " + nameofCurrMethod);
  }

  @PreAuthorize(RoleConstants.ADMIN)
  @GetMapping(ApiConstant.AUTH_BASE + "/admin")
  public ResponseEntity<ApiResponseObject<String>> checkAdmin() {
    String nameofCurrMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    return gerateResponse("Success For Method : " + nameofCurrMethod);
  }

  @PreAuthorize(RoleConstants.READ_WRITE)
  @GetMapping(ApiConstant.AUTH_BASE + "/rw")
  public ResponseEntity<ApiResponseObject<String>> checkRWAccess() {
    String nameofCurrMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    return gerateResponse("Success For Method : " + nameofCurrMethod);
  }

  @PreAuthorize(RoleConstants.READ_ONLY)
  @GetMapping(ApiConstant.AUTH_BASE + "/ro")
  public ResponseEntity<ApiResponseObject<String>> checkRo() {
    String nameofCurrMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    return gerateResponse("Success For Method : " + nameofCurrMethod);
  }

  @PreAuthorize(RoleConstants.DEMO)
  @GetMapping(ApiConstant.AUTH_BASE + "/demo")
  public ResponseEntity<ApiResponseObject<String>> checkDemo() {
    String nameofCurrMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    return gerateResponse("Success For Method : " + nameofCurrMethod);
  }

  @GetMapping(ApiConstant.AUTH_BASE + "/norole")
  public ResponseEntity<ApiResponseObject<String>> checkNoRole() {
    String nameofCurrMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    return gerateResponse("Success For Method : " + nameofCurrMethod);
  }

  private ResponseEntity<ApiResponseObject<String>> gerateResponse(String resp) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    System.out.println(" In switchContext :" + auth);
    ApiResponseObject<String> aresp = new ApiResponseObject<String>();
    aresp.setRespObject(resp);
    return ResponseEntity.ok().body(aresp);
  }
}

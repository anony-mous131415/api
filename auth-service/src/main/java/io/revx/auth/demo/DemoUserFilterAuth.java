package io.revx.auth.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import io.revx.auth.demo.DemoConstants;
import io.revx.core.enums.RoleName;
import io.revx.core.response.UserInfo;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoUserFilterAuth implements Filter {

	private static Logger logger = LogManager.getLogger(DemoUserFilterAuth.class);

	public UserInfo getUserInfo() {
		UserInfo ui = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				&& SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal() != DemoConstants.ANONYMOUS_USER) {
			ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		return ui;
	}

	private void replaceJsonKeyValue(JSONObject respJson, String key, String value) throws JSONException {
		if (respJson.has(key)) {

			if (key.toLowerCase().equals(DemoConstants.NAME) && respJson.has(DemoConstants.ID)) {
				respJson.put(key, value + " " + respJson.getLong(DemoConstants.ID));

			} else {

				respJson.put(key, value);
			}
		}

	}

	private void JsonTraversing(JSONObject cachJson, JSONObject respJson) {
		try {

			JSONArray keys = cachJson.names();
			if (keys == null)
				return;
			for (int i = 0; i < keys.length(); i++) {
				String keyAtCurrentIndex = keys.getString(i);
				if ((respJson.optJSONArray(keyAtCurrentIndex)) == null
						&& (respJson.optJSONObject(keyAtCurrentIndex) == null
								&& cachJson.optJSONObject(keyAtCurrentIndex) == null)) {

					replaceJsonKeyValue(respJson, keyAtCurrentIndex, cachJson.getString(keyAtCurrentIndex));

				} else if (respJson.optJSONObject(keyAtCurrentIndex) != null) {
					JsonTraversing(cachJson.getJSONObject(keyAtCurrentIndex),
							respJson.getJSONObject(keyAtCurrentIndex));
				} else if (respJson.optJSONArray(keyAtCurrentIndex) != null) {
					JSONArray ar = respJson.getJSONArray(keyAtCurrentIndex);
					for (int j = 0; j < ar.length(); j++) {
						if (cachJson.optJSONObject(keyAtCurrentIndex) != null)
							JsonTraversing(cachJson.getJSONObject(keyAtCurrentIndex), ar.getJSONObject(j));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {

			HttpServletRequest req = (HttpServletRequest) request;
			String requestURI = req.getRequestURI();
			logger.info("RequestURI : " + requestURI);

			UserInfo ui = getUserInfo();
			if (ui != null) {
				Set<String> authorities = ui.getAuthorities();
				RoleName role = ui.highestRoleOfLoginUser(authorities);
				if (requestURI.startsWith(DemoConstants.V2_AUTH) && RoleName.DEMO.equals(role)
						&& req.getMethod().equals(DemoConstants.GET)) {

					logger.info("Current user is demo User");

					ServletResponseWrapperCopier capturingResponseWrapper = new ServletResponseWrapperCopier(
							(HttpServletResponse) response);
					chain.doFilter(request, capturingResponseWrapper);
					String capturedResponse = capturingResponseWrapper.getCaptureAsString();
					if (capturedResponse.startsWith("{") && capturedResponse.contains(DemoConstants.RESPONSE_OBJ)) {

						logger.info("Response from api : " + response.toString());

						JSONObject respJson = new JSONObject(capturedResponse);

						File jsonFile = ResourceUtils.getFile(DemoConstants.JSON_PATH);
						String jsonString = new String(Files.readAllBytes(jsonFile.toPath()));
						JSONObject cachedJson = new JSONObject(jsonString);
						JSONArray cachedJsonKeys = cachedJson.names();
						String[] strArr = new String[cachedJsonKeys.length()];
						for (int i = 0; i < cachedJsonKeys.length(); i++) {
							strArr[i] = cachedJsonKeys.getString(i);
						}

						Arrays.sort(strArr, new java.util.Comparator<String>() {
							@Override
							public int compare(String s1, String s2) {
								return s2.length() - s1.length();
							}
						});

						String matchedReqURI = null;
						for (int i = 0; i < strArr.length; i++) {

							if (Pattern.matches(strArr[i], requestURI)) {
								matchedReqURI = strArr[i];
								logger.info("The reqURI: " + requestURI + " matched with the following pattern: "
										+ strArr[i]);
								break;
							}
						}

						if (matchedReqURI != null) {
							if (respJson.optJSONObject(DemoConstants.RESPONSE_OBJ) != null) {

								JsonTraversing(cachedJson.getJSONObject(matchedReqURI),
										respJson.getJSONObject(DemoConstants.RESPONSE_OBJ));

							} else if (respJson.optJSONArray(DemoConstants.RESPONSE_OBJ) != null) {
								JSONArray jar = respJson.getJSONArray(DemoConstants.RESPONSE_OBJ);
								for (int i = 0; i < jar.length(); i++) {
									JSONObject jo = jar.getJSONObject(i);
									JsonTraversing(cachedJson.getJSONObject(matchedReqURI), jo);

								}
							}
						}
						response.getOutputStream().write(respJson.toString().getBytes());

					} else {

						response.getOutputStream().write(capturedResponse.getBytes());

					}

				} else {

					chain.doFilter(request, response);

				}
			} else {
				chain.doFilter(request, response);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}

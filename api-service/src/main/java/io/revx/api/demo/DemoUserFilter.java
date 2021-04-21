package io.revx.api.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import io.revx.api.service.LoginUserDetailsService;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoUserFilter implements Filter {

	private static Logger logger = LogManager.getLogger(DemoUserFilter.class);

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	private void replaceJsonKeyValue(JSONObject respJson, String key, String value) throws JSONException {
		if (respJson.has(key)) {

			if (key.toLowerCase().equals(DemoConstants.NAME) && respJson.has(DemoConstants.ID)) {
				respJson.put(key, value + " " + respJson.getLong(DemoConstants.ID));
			} else {

				respJson.put(key, value);
			}
		}

	}

	private void replaceJsonArrayIndex(JSONArray ar, int index, String value) throws JSONException {
		ar.put(index, value);
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
						if (cachJson.optJSONObject(keyAtCurrentIndex) != null && ar.optJSONObject(j) != null)
							JsonTraversing(cachJson.getJSONObject(keyAtCurrentIndex), ar.getJSONObject(j));
						else if (cachJson.optJSONObject(keyAtCurrentIndex) == null && ar.optJSONArray(j) == null)
							replaceJsonArrayIndex(ar, j, cachJson.getString(keyAtCurrentIndex));
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

			if (requestURI.startsWith(DemoConstants.V2_API) && loginUserDetailsService.getUserInfo() != null
					&& loginUserDetailsService.isDemoUser()) {

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
							logger.info(
									"The reqURI: " + requestURI + " matched with the following pattern: " + strArr[i]);
							break;
						}
					}

					if (matchedReqURI != null) {

						if (matchedReqURI.equals(DemoConstants.MENUCRUMBS)
								&& respJson.optJSONArray(DemoConstants.RESPONSE_OBJ) != null) {
							JSONArray jarr1 = respJson.getJSONArray(DemoConstants.RESPONSE_OBJ);
							for (int i = 0; i < jarr1.length(); i++) {
								JSONObject jobj1 = jarr1.getJSONObject(i);
								if (jobj1.has(DemoConstants.MENU_NAME)) {
									String menuName = jobj1.getString(DemoConstants.MENU_NAME);
									JSONArray jarr2 = jobj1.getJSONArray(DemoConstants.MENU_LIST);
									for (int j = 0; j < jarr2.length(); j++) {
										JSONObject jobj2 = jarr2.getJSONObject(j);
										JsonTraversing(
												cachedJson.getJSONObject(DemoConstants.MENUCRUMBS)
														.getJSONObject(menuName).getJSONObject(DemoConstants.MENU_LIST),
												jobj2);
									}
								}
							}

						} else if (respJson.optJSONObject(DemoConstants.RESPONSE_OBJ) != null) {

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

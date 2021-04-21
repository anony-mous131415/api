package io.revx.api.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("esRestClient")
public class EsRestClient {
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Async
	public CompletableFuture<String> postRestCall(String requestURL, String paramJson) {

		String elasticSearchUrl = "";
		String ES_HOST = env.getProperty("elasticsearch.hourly.host");
		Integer ES_PORT = Integer.parseInt(env.getProperty("elasticsearch.hourly.port"));
		elasticSearchUrl = "http://" + ES_HOST + ":" + ES_PORT + requestURL;

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", "application/json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(paramJson, httpHeaders);

		String response = restTemplate.postForObject(elasticSearchUrl, httpEntity, String.class);

		return CompletableFuture.completedFuture(response);
	}
}

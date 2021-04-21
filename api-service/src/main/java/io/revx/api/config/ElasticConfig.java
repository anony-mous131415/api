package io.revx.api.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@PropertySource({"classpath:elastic.properties"})
@EnableElasticsearchRepositories("io.revx.api.es.repo")
public class ElasticConfig {

  private static Logger logger = LogManager.getLogger(ElasticConfig.class);
  @Value("${elasticsearch.host:elasticdb}")
  private String esHost;

  @Value("${elasticsearch.port:9300}")
  private int esPort;

  @Value("${elasticsearch.clustername:elastic}")
  private String esClusterName;

  @Bean
  @SuppressWarnings("resource")
  public Client client() throws UnknownHostException {
    logger.info(" ElasticConfig Name {} {} {} ", esClusterName, esHost, esPort);
    Client client = new PreBuiltTransportClient(Settings.builder()
        .put("client.transport.sniff", false).put("cluster.name", esClusterName).build())
            .addTransportAddress(new TransportAddress(InetAddress.getByName(esHost), esPort));
    
    return client;

  }

  @Bean
  public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
    return new ElasticsearchTemplate(client());
  }
}

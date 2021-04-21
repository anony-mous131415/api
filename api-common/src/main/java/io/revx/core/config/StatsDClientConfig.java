package io.revx.core.config;

import java.net.DatagramSocket;
import java.net.InetAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.revx.core.constant.GraphiteConstants;

@Configuration
public class StatsDClientConfig {

  @Value(value = "${graphite.prefix:prefix}")
  private String prefix;
  @Value(value = "${graphite.host:localhost}")
  private String host;
  @Value(value = "${graphite.port:2003}")
  private int port;
  @Value(value = "${graphite.isGraphiteEnable:false}")
  private boolean isGraphiteEnable;

  @Bean
  public StatsDClient statsDClient() {

    return isGraphiteEnable ? new NonBlockingStatsDClient(getAppPrefix(), host, port)
        : new NoOpStatsDClient();
  }

  private String getAppPrefix() {
    String appName = prefix;
    String ip = getLastPartIpAddressOfSystem();
    if (StringUtils.isNotBlank(ip)) {
      appName = prefix + ip;
    }
    return StringUtils.isNotBlank(prefix) ? appName : GraphiteConstants.API_PREFIX + ip;
  }

  private String getLastPartIpAddressOfSystem() {
    String ip = null;
    try (final DatagramSocket socket = new DatagramSocket()) {
      socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
      ip = socket.getLocalAddress().getHostAddress();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return StringUtils.isNotBlank(ip)
        ? (StringUtils.substring(ip, Math.max(ip.lastIndexOf('.'), 0), ip.length()))
        : ip;
  }



  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags("app.name", getAppPrefix());
  }

  @Bean
  TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

}

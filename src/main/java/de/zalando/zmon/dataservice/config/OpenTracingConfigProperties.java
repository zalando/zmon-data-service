package de.zalando.zmon.dataservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "opentracing")
public class OpenTracingConfigProperties {

    private String tracingProvider = "jaeger";
    private String openTracingServiceName = "zmon-data-service";

    private String jaegerHost = "localhost";
    private int jaegerPort = 5775;
    private boolean jaegerLogSpans = true;
    private int jaegerFlushIntervalMs = 10;
    private int jaegerMaxQueueSize= 100;

    private String lightStepHost = "localhost";
    private int lightStepPort = 80;
    private String lightStepAccessToken = "";

    public String getJaegerHost() {
        return jaegerHost;
    }

    public void setJaegerHost(String jaegerHost) {
        this.jaegerHost = jaegerHost;
    }

    public int getJaegerPort() {
        return jaegerPort;
    }

    public void setJaegerPort(int jaegerPort) {
        this.jaegerPort = jaegerPort;
    }

    public boolean isJaegerLogSpans() {
        return jaegerLogSpans;
    }

    public void setJaegerLogSpans(boolean jaegerLogSpans) {
        this.jaegerLogSpans = jaegerLogSpans;
    }

    public int getJaegerFlushIntervalMs() {
        return jaegerFlushIntervalMs;
    }

    public void setJaegerFlushIntervalMs(int jaegerFlushIntervalMs) {
        this.jaegerFlushIntervalMs = jaegerFlushIntervalMs;
    }

    public int getJaegerMaxQueueSize() {
        return jaegerMaxQueueSize;
    }

    public void setJaegerMaxQueueSize(int jaegerMaxQueueSize) {
        this.jaegerMaxQueueSize = jaegerMaxQueueSize;
    }

    public String getLightStepHost() {
        return lightStepHost;
    }

    public void setLightStepHost(String lightStepHost) {
        this.lightStepHost = lightStepHost;
    }

    public int getLightStepPort() {
        return lightStepPort;
    }

    public void setLightStepPort(int lightStepPort) {
        this.lightStepPort = lightStepPort;
    }

    public String getLightStepAccessToken() {
        return lightStepAccessToken;
    }

    public void setLightStepAccessToken(String lightStepAccessToken) {
        this.lightStepAccessToken = lightStepAccessToken;
    }

    public String getTracingProvider() {
        return tracingProvider;
    }

    public void setTracingProvider(String tracingProvider) {
        this.tracingProvider = tracingProvider;
    }

    public String getOpenTracingServiceName() {
        return openTracingServiceName;
    }

    public void setOpenTracingServiceName(String openTracingServiceName) {
        this.openTracingServiceName = openTracingServiceName;
    }
}

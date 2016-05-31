package de.zalando.zmon.dataservice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dataservice")
public class DataServiceConfigProperties {

    private String redisHost = "localhost";
    private int redisPort = 6378;
    private int redisPoolSize = 20;

    private List<String> kairosdbWriteUrls;

    private boolean proxyController = false;
    private String proxyControllerUrl = "http://localhost:8080/api/v1/";
    private String proxyControllerBaseUrl = "http://localhost:8080/";
    private boolean proxyControllerOauth2 = false;
    private int proxyControllerConnectTimeout = 1000; // ms
    private int proxyControllerSocketTimeout = 500; // ms

    private boolean proxyScheduler = false;
    private String proxySchedulerUrl = "http://localhost:8085/api/v1/";

    private boolean logCheckData = false;
    private boolean logKairosdbRequests = false;
    private boolean logKairosdbErrors = false;

    private List<Integer> actuatorMetricChecks = new ArrayList<>();

    private List<String> restMetricHosts = new ArrayList<>();
    private int restMetricPort = 8088;

    private Map<String, String> oauth2Scopes = new HashMap<String, String>(0);

    private int kairosdbConnections = 50;
    private int kairosdbTimeout = 1000;
    private int kairosdbSockettimeout = 500;
    private boolean kairosdbEnabled = true;

    private String dataProxyUrl = null;
    private int dataProxyConnections = 50;
    private int dataProxyPoolSize = 50;
    private int dataProxySocketTimeout = 500;
    private int dataProxyTimeout = 1000;

    private String proxyKairosdbUrl = "";
    private int proxyKairosdbConnections = 25;
    private int proxyKairosdbSockettimeout = 500;
    private int proxyKairosdbTimeout = 1000;

    private int asyncPoolCoreSize = 150;
    private int asyncPoolMaxSize = 200;
    private int asyncPoolQueueSize = 5000;

    private int resultSizeWarning = 100;

    public int getResultSizeWarning() {
        return resultSizeWarning;
    }

    public void setResultSizeWarning(int resultSizeWarning) {
        this.resultSizeWarning = resultSizeWarning;
    }

    public boolean isProxyControllerOauth2() {
        return proxyControllerOauth2;
    }

    public void setProxyControllerOauth2(boolean proxyControllerOauth2) {
        this.proxyControllerOauth2 = proxyControllerOauth2;
    }

    public boolean isKairosdbEnabled() {
        return kairosdbEnabled;
    }

    public void setKairosdbEnabled(boolean kairosdbEnabled) {
        this.kairosdbEnabled = kairosdbEnabled;
    }

    public int getAsyncPoolCoreSize() {
        return asyncPoolCoreSize;
    }

    public void setAsyncPoolCoreSize(int asyncPoolCoreSize) {
        this.asyncPoolCoreSize = asyncPoolCoreSize;
    }

    public int getAsyncPoolMaxSize() {
        return asyncPoolMaxSize;
    }

    public void setAsyncPoolMaxSize(int asyncPoolMaxSize) {
        this.asyncPoolMaxSize = asyncPoolMaxSize;
    }

    public int getAsyncPoolQueueSize() {
        return asyncPoolQueueSize;
    }

    public void setAsyncPoolQueueSize(int asyncPoolQueueSize) {
        this.asyncPoolQueueSize = asyncPoolQueueSize;
    }

    public int getDataProxyPoolSize() {
        return dataProxyPoolSize;
    }

    public void setDataProxyPoolSize(int dataProxyPoolSize) {
        this.dataProxyPoolSize = dataProxyPoolSize;
    }

    public String getProxyKairosdbUrl() {
        return proxyKairosdbUrl;
    }

    public void setProxyKairosdbUrl(String proxyKairosdbUrl) {
        this.proxyKairosdbUrl = proxyKairosdbUrl;
    }

    public int getProxyKairosdbConnections() {
        return proxyKairosdbConnections;
    }

    public void setProxyKairosdbConnections(int proxyKairosdbConnections) {
        this.proxyKairosdbConnections = proxyKairosdbConnections;
    }

    public int getProxyKairosdbSockettimeout() {
        return proxyKairosdbSockettimeout;
    }

    public void setProxyKairosdbSockettimeout(int proxyKairosdbSockettimeout) {
        this.proxyKairosdbSockettimeout = proxyKairosdbSockettimeout;
    }

    public int getProxyKairosdbTimeout() {
        return proxyKairosdbTimeout;
    }

    public void setProxyKairosdbTimeout(int proxyKairosdbTimeout) {
        this.proxyKairosdbTimeout = proxyKairosdbTimeout;
    }

    public String getDataProxyUrl() {
        return dataProxyUrl;
    }

    public void setDataProxyUrl(String dataProxyUrl) {
        this.dataProxyUrl = dataProxyUrl;
    }

    public int getDataProxyConnections() {
        return dataProxyConnections;
    }

    public void setDataProxyConnections(int dataProxyConnections) {
        this.dataProxyConnections = dataProxyConnections;
    }

    public int getDataProxySocketTimeout() {
        return dataProxySocketTimeout;
    }

    public void setDataProxySocketTimeout(int dataProxySocketTimeout) {
        this.dataProxySocketTimeout = dataProxySocketTimeout;
    }

    public int getDataProxyTimeout() {
        return dataProxyTimeout;
    }

    public void setDataProxyTimeout(int dataProxyTimeout) {
        this.dataProxyTimeout = dataProxyTimeout;
    }

    public int getKairosdbConnections() {
        return kairosdbConnections;
    }

    public void setKairosdbConnections(int kairosdbConnections) {
        this.kairosdbConnections = kairosdbConnections;
    }

    public int getKairosdbTimeout() {
        return kairosdbTimeout;
    }

    public void setKairosdbTimeout(int kairosdbTimeout) {
        this.kairosdbTimeout = kairosdbTimeout;
    }

    public int getKairosdbSockettimeout() {
        return kairosdbSockettimeout;
    }

    public void setKairosdbSockettimeout(int kairosdbSockettimeout) {
        this.kairosdbSockettimeout = kairosdbSockettimeout;
    }

    /**
     * Enables caching for information about a token.
     * 
     * Default: false
     */
    private boolean tokenInfoCacheEnabled = false;

    /**
     * Maximum size for the cache.
     * 
     * Default : 1000
     */
    private int tokenInfoCacheMaxSize = 1000;

    /**
     * How long these information will be stored in cache. Unit is MINUTES.
     * 
     * Default: 240
     */
    private int tokenInfoCacheTime = 240;

    private String oauth2TokenInfoUrl;

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public int getRedisPoolSize() {
        return redisPoolSize;
    }

    public void setRedisPoolSize(int redisPoolSize) {
        this.redisPoolSize = redisPoolSize;
    }

    public boolean isProxyController() {
        return proxyController;
    }

    public void setProxyController(boolean proxyController) {
        this.proxyController = proxyController;
    }

    public String getProxyControllerUrl() {
        return proxyControllerUrl;
    }

    public int getProxyControllerConnectTimeout() {
        return proxyControllerConnectTimeout;
    }

    public void setProxyControllerConnectTimeout(int proxyControllerConnectTimeout) {
        this.proxyControllerConnectTimeout = proxyControllerConnectTimeout;
    }

    public int getProxyControllerSocketTimeout() {
        return proxyControllerSocketTimeout;
    }

    public void setProxyControllerSocketTimeout(int proxyControllerSocketTimeout) {
        this.proxyControllerSocketTimeout = proxyControllerSocketTimeout;
    }

    public void setProxyControllerUrl(String proxyControllerUrl) {
        this.proxyControllerUrl = proxyControllerUrl;
    }

    public String getProxyControllerBaseUrl() {
        return proxyControllerBaseUrl;
    }

    public void setProxyControllerBaseUrl(String proxyControllerBaseUrl) {
        this.proxyControllerBaseUrl = proxyControllerBaseUrl;
    }

    public boolean isProxyScheduler() {
        return proxyScheduler;
    }

    public void setProxyScheduler(boolean proxyScheduler) {
        this.proxyScheduler = proxyScheduler;
    }

    public String getProxySchedulerUrl() {
        return proxySchedulerUrl;
    }

    public void setProxySchedulerUrl(String proxySchedulerUrl) {
        this.proxySchedulerUrl = proxySchedulerUrl;
    }

    public boolean isLogCheckData() {
        return logCheckData;
    }

    public void setLogCheckData(boolean logCheckData) {
        this.logCheckData = logCheckData;
    }

    public boolean isLogKairosdbRequests() {
        return logKairosdbRequests;
    }

    public void setLogKairosdbRequests(boolean logKairosdbRequests) {
        this.logKairosdbRequests = logKairosdbRequests;
    }

    public boolean isLogKairosdbErrors() {
        return logKairosdbErrors;
    }

    public void setLogKairosdbErrors(boolean logKairosdbErrors) {
        this.logKairosdbErrors = logKairosdbErrors;
    }

    public List<Integer> getActuatorMetricChecks() {
        return actuatorMetricChecks;
    }

    public void setActuatorMetricChecks(List<Integer> actuatorMetricChecks) {
        this.actuatorMetricChecks = actuatorMetricChecks;
    }

    public List<String> getRestMetricHosts() {
        return restMetricHosts;
    }

    public void setRestMetricHosts(List<String> restMetricHosts) {
        this.restMetricHosts = restMetricHosts;
    }

    public int getRestMetricPort() {
        return restMetricPort;
    }

    public void setRestMetricPort(int restMetricPort) {
        this.restMetricPort = restMetricPort;
    }

    public String getOauth2TokenInfoUrl() {
        return oauth2TokenInfoUrl;
    }

    public void setOauth2TokenInfoUrl(String oauth2TokenInfoUrl) {
        this.oauth2TokenInfoUrl = oauth2TokenInfoUrl;
    }

    public Map<String, String> getOauth2Scopes() {
        return oauth2Scopes;
    }

    public void setOauth2Scopes(Map<String, String> oauth2Scopes) {
        this.oauth2Scopes = oauth2Scopes;
    }

    public int getTokenInfoCacheMaxSize() {
        return tokenInfoCacheMaxSize;
    }

    public void setTokenInfoCacheMaxSize(int tokenInfoCacheMaxSize) {
        this.tokenInfoCacheMaxSize = tokenInfoCacheMaxSize;
    }

    public int getTokenInfoCacheTime() {
        return tokenInfoCacheTime;
    }

    public void setTokenInfoCacheTime(int tokenInfoCacheTime) {
        this.tokenInfoCacheTime = tokenInfoCacheTime;
    }

    public boolean isTokenInfoCacheEnabled() {
        return tokenInfoCacheEnabled;
    }

    public void setTokenInfoCacheEnabled(boolean tokenInfoCacheEnabled) {
        this.tokenInfoCacheEnabled = tokenInfoCacheEnabled;
    }

    public List<String> getKairosdbWriteUrls() {
        return kairosdbWriteUrls;
    }

    public void setKairosdbWriteUrls(List<String> kairosdbWriteUrls) {
        this.kairosdbWriteUrls = kairosdbWriteUrls;
    }
}

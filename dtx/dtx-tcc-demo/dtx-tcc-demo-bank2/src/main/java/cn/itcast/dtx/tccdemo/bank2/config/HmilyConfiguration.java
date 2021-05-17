package cn.itcast.dtx.tccdemo.bank1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/17 10:24 ]
 * @since :[ 1.0.0 ]
 */
@Component
@ConfigurationProperties(prefix = "org.dromara.hmily")
public class HmilyConfiguration {

    private String serializer;

    private int recoverDelayTime;

    private int retryMax;

    private int scheduledDelay;

    private int scheduledThreadMax;

    private String repositorySupport;

    private boolean started;

    private final HmilyDbConfig hmilyDbConfig = new HmilyDbConfig();

    public static class HmilyDbConfig {

        private String driverClassName;

        private String url;

        private String username;

        private String password;

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public HmilyDbConfig getHmilyDbConfig() {
        return hmilyDbConfig;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public int getRecoverDelayTime() {
        return recoverDelayTime;
    }

    public void setRecoverDelayTime(int recoverDelayTime) {
        this.recoverDelayTime = recoverDelayTime;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public void setRetryMax(int retryMax) {
        this.retryMax = retryMax;
    }

    public int getScheduledDelay() {
        return scheduledDelay;
    }

    public void setScheduledDelay(int scheduledDelay) {
        this.scheduledDelay = scheduledDelay;
    }

    public int getScheduledThreadMax() {
        return scheduledThreadMax;
    }

    public void setScheduledThreadMax(int scheduledThreadMax) {
        this.scheduledThreadMax = scheduledThreadMax;
    }

    public String getRepositorySupport() {
        return repositorySupport;
    }

    public void setRepositorySupport(String repositorySupport) {
        this.repositorySupport = repositorySupport;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}

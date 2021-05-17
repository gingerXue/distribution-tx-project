package cn.itcast.dtx.tccdemo.bank1.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.dromara.hmily.common.config.HmilyDbConfig;
import org.dromara.hmily.core.bootstrap.HmilyTransactionBootstrap;
import org.dromara.hmily.core.service.HmilyInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DatabaseConfiguration {


    private final ApplicationContext applicationContext;


    @Autowired
    private HmilyConfiguration hmilyConfiguration;
//    private Environment env;

    public DatabaseConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.ds0")
    public DruidDataSource ds0() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }


    @Bean
    public HmilyTransactionBootstrap hmilyTransactionBootstrap(HmilyInitService hmilyInitService) {
        HmilyTransactionBootstrap hmilyTransactionBootstrap = new HmilyTransactionBootstrap(hmilyInitService);
        hmilyTransactionBootstrap.setSerializer(hmilyConfiguration.getSerializer());
        hmilyTransactionBootstrap.setRecoverDelayTime(hmilyConfiguration.getRecoverDelayTime());
        hmilyTransactionBootstrap.setRetryMax(hmilyConfiguration.getRetryMax());
        hmilyTransactionBootstrap.setScheduledDelay(hmilyConfiguration.getScheduledDelay());
        hmilyTransactionBootstrap.setScheduledThreadMax(hmilyConfiguration.getScheduledThreadMax());
        hmilyTransactionBootstrap.setRepositorySupport(hmilyConfiguration.getRepositorySupport());
        hmilyTransactionBootstrap.setStarted(hmilyConfiguration.isStarted());
        HmilyDbConfig hmilyDbConfig = new HmilyDbConfig();
        hmilyDbConfig.setDriverClassName(hmilyConfiguration.getHmilyDbConfig().getDriverClassName());
        hmilyDbConfig.setUrl(hmilyConfiguration.getHmilyDbConfig().getUrl());
        hmilyDbConfig.setUsername(hmilyConfiguration.getHmilyDbConfig().getUsername());
        hmilyDbConfig.setPassword(hmilyConfiguration.getHmilyDbConfig().getPassword());
        hmilyTransactionBootstrap.setHmilyDbConfig(hmilyDbConfig);
        return hmilyTransactionBootstrap;
    }


    /*@Bean
    @ConfigurationProperties(prefix = "org.dromara.hmily")
    public HmilyConfig hmilyConfig(){
        return new HmilyConfig();
    }



    @Bean
    public HmilyTransactionBootstrap hmilyTransactionBootstrap(HmilyInitService hmilyInitService, HmilyConfig hmilyConfig){
        HmilyTransactionBootstrap hmilyTransactionBootstrap = new HmilyTransactionBootstrap(hmilyInitService);
        return hmilyTransactionBootstrap;
    }*/


}

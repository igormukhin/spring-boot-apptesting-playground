package com.example;

import com.example.license.LicenseCache;
import com.example.license.LicenseLoader;
import com.example.license.PeriodicLicenseCacheUpdater;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class LicenseConfiguration {

    @Bean
    public LicenseLoader licenseLoader(ResourceLoader resourceLoader,
                                       @Value("${license.location}") String licenseLocation) {
        return new LicenseLoader(resourceLoader, licenseLocation);
    }

    @Bean
    public LicenseCache licenseCache(LicenseLoader licenseLoader,
                                     TaskScheduler taskScheduler) {
        return new LicenseCache(licenseLoader, taskScheduler);
    }

    @Bean
    public PeriodicLicenseCacheUpdater periodicLicenseCacheUpdater(LicenseCache licenseCache,
                                                                   TaskScheduler taskScheduler) {
        return new PeriodicLicenseCacheUpdater(licenseCache, taskScheduler);
    }

    @Bean
    public TaskScheduler taskScheduler(
            @Value("${taskScheduler.poolSize:2}") int taskSchedulerPoolSize) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(taskSchedulerPoolSize);
        return scheduler;
    }
}

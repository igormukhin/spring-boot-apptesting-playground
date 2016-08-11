package com.example.license;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;

import java.util.Objects;

public class PeriodicLicenseCacheUpdater {

    private LicenseCache licenseCache;
    private TaskScheduler taskScheduler;

    private String updateCron;

    @Autowired
    public PeriodicLicenseCacheUpdater(LicenseCache licenseCache, TaskScheduler taskScheduler) {
        this(licenseCache, taskScheduler, "0 0 12,18 * * *");
    }

    public PeriodicLicenseCacheUpdater(LicenseCache licenseCache, TaskScheduler taskScheduler, String updateCron) {
        this.licenseCache = Objects.requireNonNull(licenseCache);
        this.taskScheduler = Objects.requireNonNull(taskScheduler);

        Assert.hasText(updateCron);
        this.updateCron = updateCron;

        init(taskScheduler, updateCron);
    }

    private void init(TaskScheduler taskScheduler, String updateCron) {
        taskScheduler.schedule(licenseCache::updateAsync, new CronTrigger(updateCron));
    }

}

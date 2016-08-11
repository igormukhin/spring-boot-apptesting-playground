package com.example.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import java.time.Clock;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class LicenseCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseCache.class);

    private LicenseLoader licenseLoader;
    private TaskScheduler taskScheduler;
    private Clock clock;

    private Duration retryDelay;

    private AtomicReference<License> licenseRef = new AtomicReference<>();

    private AtomicLong lastUpdateRequestId = new AtomicLong(1);

    public LicenseCache(LicenseLoader licenseLoader, TaskScheduler taskScheduler) {
        this(licenseLoader, taskScheduler, Duration.ofMinutes(5), Clock.systemDefaultZone());
    }

    /**
     * Creates an instance.
     *
     * @param licenseLoader
     * @param taskScheduler
     * @param retryDelay delay after failure during license fetch
     * @param clock clock to use
     */
    public LicenseCache(LicenseLoader licenseLoader, TaskScheduler taskScheduler,
                        Duration retryDelay, Clock clock) {
        this.licenseLoader = Objects.requireNonNull(licenseLoader);
        this.taskScheduler = Objects.requireNonNull(taskScheduler);
        this.clock = Objects.requireNonNull(clock);

        this.retryDelay = Objects.requireNonNull(retryDelay);

        init();
    }

    private void init() {
        // the first load
        updateAsync();
    }

    /**
     * Returns the license.
     *
     * @return the license
     * @throws LicenseNotAvailableException if the license is not yet loaded
     */
    public License getLicense() throws LicenseNotAvailableException {
        License license = licenseRef.get();
        if (license == null) {
            throw new LicenseNotAvailableException("License not yet loaded.");
        }

        return license;
    }

    /**
     * Schedules an immediate attempt for a license update.
     */
    public void updateAsync() {
        long requestId = lastUpdateRequestId.incrementAndGet();
        scheduleFetchAfter(0L, requestId);
    }

    private void scheduleFetchAfter(long afterMillis, long requestId) {
        long runAt = clock.millis() + afterMillis;
        taskScheduler.schedule(() -> tryToFetch(requestId), new Date(runAt));
    }

    private void tryToFetch(long requestId) {
        if (requestId != lastUpdateRequestId.get()) {
            return;
        }

        try {
            License license = licenseLoader.fetch();
            licenseRef.set(license);
            LOGGER.debug("License fetched.");
        } catch (Exception e) {
            LOGGER.error("Can't fetch the license. Will retry.", e);
            scheduleFetchAfter(retryDelay.toMillis(), requestId);
        }
    }
}

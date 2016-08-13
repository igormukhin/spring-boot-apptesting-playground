package com.example.license;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class LicenseCache {

    private final LicenseLoader licenseLoader;

    private final TaskScheduler taskScheduler;

    private final Duration retryDelay;

    private final AtomicReference<License> licenseRef = new AtomicReference<>();

    private final AtomicLong lastUpdateRequestId = new AtomicLong(1);

    public LicenseCache(LicenseLoader licenseLoader, TaskScheduler taskScheduler) {
        this(licenseLoader, taskScheduler, Duration.ofMinutes(5));
    }

    /**
     * Creates an instance.
     *
     * @param licenseLoader
     * @param taskScheduler
     * @param retryDelay delay after failure during license fetch
     */
    public LicenseCache(@NonNull LicenseLoader licenseLoader,
                        @NonNull TaskScheduler taskScheduler,
                        @NonNull Duration retryDelay) {
        this.licenseLoader = Objects.requireNonNull(licenseLoader);
        this.taskScheduler = Objects.requireNonNull(taskScheduler);
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
        long runAt = System.currentTimeMillis() + afterMillis;
        taskScheduler.schedule(() -> tryToFetch(requestId), new Date(runAt));
    }

    private void tryToFetch(long requestId) {
        if (isFetchRequestBecameIrrelevant(requestId)) {
            return;
        }

        try {
            License license = licenseLoader.fetch();
            licenseRef.set(license);
            log.debug("License fetched.");
        } catch (Exception e) {
            log.error("Can't fetch the license. Will retry.", e);
            scheduleFetchAfter(retryDelay.toMillis(), requestId);
        }
    }

    private boolean isFetchRequestBecameIrrelevant(long requestId) {
        return requestId != lastUpdateRequestId.get();
    }
}

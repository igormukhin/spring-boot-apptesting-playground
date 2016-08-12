package com.example.license;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.ResourceAccessException;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LicenseCacheTest {

    @Mock
    private LicenseLoader licenseLoader;

    private ThreadPoolTaskScheduler taskScheduler;

    @Before
    public void setUp() {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
    }

    @After
    public void tearDown() {
        taskScheduler.destroy();
    }

    @Test
    public void cacheFetchesRightAway() {
        // given
        when(licenseLoader.fetch())
                .thenReturn(new License("MIT"));

        // when
        LicenseCache licenseCache = new LicenseCache(licenseLoader, taskScheduler);

        // then
        awaitForLicense(licenseCache);
        Assertions.assertThat(licenseCache.getLicense().getName()).isEqualTo("MIT");
    }

    public static void awaitForLicense(LicenseCache licenseCache) {
        await().atMost(1, SECONDS).until(() -> {
            try {
                licenseCache.getLicense();
                return true;
            } catch (LicenseNotAvailableException ex) {
                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void cacheRetrysAfterFailure() {
        // given
        when(licenseLoader.fetch())
                .thenThrow(ResourceAccessException.class)
                .thenThrow(ResourceAccessException.class)
                .thenReturn(new License("MIT"));

        // when
        LicenseCache licenseCache = new LicenseCache(licenseLoader, taskScheduler,
                Duration.ofMillis(10L), Clock.systemDefaultZone());

        // then
        awaitForLicense(licenseCache);
        Assertions.assertThat(licenseCache.getLicense().getName()).isEqualTo("MIT");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void cacheRetrysJustOnceAfterItWasAlreadyScheduled() throws InterruptedException {
        // given
        AtomicReference<LicenseCache> licenseCacheRef = new AtomicReference<>();
        Answer<Boolean> firstAnswer = (in) -> {
            licenseCacheRef.get().updateAsync();
            throw new ResourceAccessException("Sample error");
        };

        when(licenseLoader.fetch())
                .thenAnswer(firstAnswer)
                .thenThrow(ResourceAccessException.class)
                .thenReturn(new License("MIT"));

        // when
        licenseCacheRef.set(new LicenseCache(licenseLoader, taskScheduler,
                Duration.ofMillis(20L), Clock.systemDefaultZone()));

        // then
        awaitForLicense(licenseCacheRef.get());
        Assertions.assertThat(licenseCacheRef.get().getLicense().getName()).isEqualTo("MIT");

        Thread.sleep(50L);
        verify(licenseLoader, times(3)).fetch();
    }

}
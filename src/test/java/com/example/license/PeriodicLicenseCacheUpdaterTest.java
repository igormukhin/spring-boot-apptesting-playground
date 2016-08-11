package com.example.license;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PeriodicLicenseCacheUpdaterTest {

    @Test
    @SuppressWarnings("unchecked")
    public void updaterSchedules() {
        // given
        LicenseCache licenseCache = mock(LicenseCache.class);
        TaskScheduler taskScheduler = mock(TaskScheduler.class);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
        when(taskScheduler.schedule(runnableCaptor.capture(), triggerCaptor.capture()))
                .thenReturn(mock(ScheduledFuture.class));

        // when
        new PeriodicLicenseCacheUpdater(licenseCache, taskScheduler, "* * * * * *");

        // then
        verify(taskScheduler, times(1)).schedule(any(), Matchers.<Trigger>any());
        assertThat(triggerCaptor.getValue()).isInstanceOf(CronTrigger.class);
        CronTrigger cronTrigger = (CronTrigger) triggerCaptor.getValue();
        assertThat(cronTrigger.getExpression()).isEqualTo("* * * * * *");

        // when
        runnableCaptor.getValue().run();

        // then
        verify(licenseCache, times(1)).updateAsync();
    }

}
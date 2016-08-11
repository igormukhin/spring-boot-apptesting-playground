package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class BootTestingPracticeApplicationIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private TaskScheduler taskScheduler;

	@Test
	public void contextLoads() {
	}

	@Test
	public void rootGreets() {
		String body = restTemplate.getForObject("/", String.class);
		assertThat(body).isEqualTo("Hello World");
	}

	@Test
	public void schedulerExecutes() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		taskScheduler.scheduleWithFixedDelay(latch::countDown, 10L);
		assertThat(latch.await(1L, TimeUnit.SECONDS)).isTrue();
	}
}

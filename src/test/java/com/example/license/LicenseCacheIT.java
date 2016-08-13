package com.example.license;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestPropertySource(properties = {
            "server.port=48080",
            "license.location=http://localhost:${server.port}/tests/license"
        })
public class LicenseCacheIT {

    @Autowired
    private LicenseCache licenseCache;

    @SuppressWarnings("Duplicates")
    @Test
    public void testLicenseLoadsFromHttpSource() {
        licenseCache.updateAsync();

        LicenseCacheTest.awaitForLicense(licenseCache);

        assertThat(licenseCache.getLicense().getName()).isEqualTo("FromHttpLicense");
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }

    }

    @Controller
    @RequestMapping("tests")
    static class TestController {

        @GetMapping("license")
        @ResponseBody
        public String license() {
            return "{\"name\":\"FromHttpLicense\"}";
        }

    }

}
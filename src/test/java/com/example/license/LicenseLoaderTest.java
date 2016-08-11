package com.example.license;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LicenseLoaderTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Test
    public void fetchReturnsTheLicense() {
        // given
        ByteArrayResource licenseResource = new ByteArrayResource("{\"name\":\"Beer License\"}"
                .getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("someLocation"))
                .thenReturn(licenseResource);

        LicenseLoader ll = new LicenseLoader(resourceLoader, "someLocation");

        // when
        License license = ll.fetch();

        // then
        Assertions.assertThat(license.getName()).isEqualTo("Beer License");
    }

    @Test
    public void fetchThrowsExceptionOnResourceEmpty() {
        // given
        ByteArrayResource licenseResource = new ByteArrayResource("{name:\"Beer License\"}"
                .getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("someLocation"))
                .thenReturn(licenseResource);

        LicenseLoader ll = new LicenseLoader(resourceLoader, "someLocation");

        // when
        Throwable ex = catchThrowable(ll::fetch);

        // then
        Assertions.assertThat(ex).isInstanceOf(ResourceAccessException.class);
    }

    @Test
    public void fetchThrowsExceptionOnMalformedJson() {
        // given
        when(resourceLoader.getResource("someLocation"))
                .thenReturn(new ByteArrayResource(new byte[0]));

        LicenseLoader ll = new LicenseLoader(resourceLoader, "someLocation");

        // when
        Throwable ex = catchThrowable(ll::fetch);

        // then
        Assertions.assertThat(ex).isInstanceOf(ResourceAccessException.class);
    }

    @Test
    public void fetchThrowsExceptionOnResourceFailure() {
        // given
        when(resourceLoader.getResource("someLocation"))
                .thenReturn(new AbstractResource() {
                    @Override
                    public String getDescription() {
                        return "bad resource";
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        throw new IOException("not there");
                    }
                });

        LicenseLoader ll = new LicenseLoader(resourceLoader, "someLocation");

        // when
        Throwable ex = catchThrowable(ll::fetch);

        // then
        Assertions.assertThat(ex).isInstanceOf(ResourceAccessException.class);
    }

}
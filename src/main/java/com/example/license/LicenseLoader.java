package com.example.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class LicenseLoader {

    private ResourceLoader resourceLoader;
    private String location;

    public LicenseLoader(ResourceLoader resourceLoader, String location) {
        this.resourceLoader = Objects.requireNonNull(resourceLoader);
        this.location = Objects.requireNonNull(location);
    }

    public License fetch() {
        try {
            Resource resource = resourceLoader.getResource(location);
            InputStream inputStream = resource.getInputStream();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, License.class);
        } catch (IOException e) {
            throw new ResourceAccessException("Can't load license resource", e);
        }
    }

}

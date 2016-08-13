package com.example.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

@RequiredArgsConstructor
public class LicenseLoader {

    @NonNull
    private final ResourceLoader resourceLoader;

    @NonNull
    private final String location;

    public License fetch() {
        try {
            val resource = resourceLoader.getResource(location);
            val inputStream = resource.getInputStream();

            val mapper = new ObjectMapper();
            return mapper.readValue(inputStream, License.class);
        } catch (IOException e) {
            throw new ResourceAccessException("Can't load license resource", e);
        }
    }

}

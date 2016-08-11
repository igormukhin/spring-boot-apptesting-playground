package com.example.license;

import org.springframework.core.NestedRuntimeException;

/**
 * Exception thrown when the license is currently not available.
 */
public class LicenseNotAvailableException extends NestedRuntimeException {

    public LicenseNotAvailableException(String msg) {
        super(msg);
    }

    public LicenseNotAvailableException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

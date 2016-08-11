package com.example.license;

import java.io.Serializable;

public class License implements Serializable {

    private String name;

    public License() {
    }

    public License(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

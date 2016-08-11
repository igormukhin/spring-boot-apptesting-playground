package com.example.license;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("license")
public class LicenseController {

    private LicenseCache licenseCache;

    public LicenseController(LicenseCache licenseCache) {
        this.licenseCache = licenseCache;
    }

    @GetMapping(path = "", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public License getLicense() {
        return licenseCache.getLicense();
    }

    @GetMapping(path = "reload")
    @ResponseBody
    public String reload() {
        licenseCache.updateAsync();
        return "Reload requested";
    }

}

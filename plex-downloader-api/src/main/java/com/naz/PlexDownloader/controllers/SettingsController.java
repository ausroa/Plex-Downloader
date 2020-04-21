package com.naz.PlexDownloader.controllers;

import com.naz.PlexDownloader.dtos.SystemSettingsDTO;
import com.naz.PlexDownloader.models.plex.PlexUser;
import com.naz.PlexDownloader.models.settings.About;
import com.naz.PlexDownloader.models.settings.SystemSettings;
import com.naz.PlexDownloader.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public HttpStatus validateFullSettingsAccess(@RequestHeader("PLEX-TOKEN") String authToken) {

        if (!settingsService.validateFullSettingsAccess(authToken)) {
            return HttpStatus.NO_CONTENT;
        }

        return HttpStatus.OK;
    }

    @PostMapping("/system")
    @ResponseStatus(HttpStatus.OK)
    public SystemSettings saveSystemSettings(@RequestHeader("PLEX-TOKEN") String authToken,
                                             @RequestBody SystemSettingsDTO systemSettingsDTO) {

        return this.settingsService.saveSystemSettingsForPlexUser(systemSettingsDTO, authToken);
    }

    @GetMapping("/system")
    @ResponseStatus(HttpStatus.OK)
    public SystemSettings retrieveSystemSettings(@RequestHeader("PLEX-TOKEN") String authToken) {

        return this.settingsService.retrieveSystemSettingsByPlexUserAuthToken(authToken);
    }



    @GetMapping("/about")
    @ResponseStatus(HttpStatus.OK)
    public About retrieveAppInfo() {
        return settingsService.retrieveAppInfo();
    }
}

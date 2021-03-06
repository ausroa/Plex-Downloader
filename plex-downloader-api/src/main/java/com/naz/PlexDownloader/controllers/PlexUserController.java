package com.naz.PlexDownloader.controllers;

import com.naz.PlexDownloader.models.plex.PlexUser;
import com.naz.PlexDownloader.services.plex.PlexUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlexUserController {

    @Autowired
    private PlexUserService plexUserService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<PlexUser> retrieveUsers(@RequestHeader("PLEX-TOKEN") String authToken) {

        return this.plexUserService.retrieveUsers(authToken);
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    PlexUser retrieveUserByAuthToken(@RequestHeader("PLEX-TOKEN") String authToken) {
        return plexUserService.retrieveUserByAuthToken(authToken);
    }

    public PlexUserService getPlexUserService() {
        return plexUserService;
    }

    public void setPlexUserService(PlexUserService plexUserService) {
        this.plexUserService = plexUserService;
    }
}

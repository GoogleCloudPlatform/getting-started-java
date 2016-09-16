package com.example.std.gettingstarted.controllers;

import com.example.std.gettingstarted.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Api(description = "version identifier")
@RestController
public class VersionController {

    @Autowired
    Config config;

    private final Map m = new HashMap();

    @PostConstruct
    public void init() {
        m.put("version", config.MAVEN_VERSION);

    }

    @ApiOperation(value = "val",
            consumes = "application/json", produces = "application/json", httpMethod = "GET", response = Map.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invalid data"),
    })
    @RequestMapping(value = "/api/version", method = RequestMethod.GET, produces = "application/json")
    public Map getVersion() {
        return m;
    }

}

package com.prowler.alertmgmt.controller;

import com.prowler.alertmgmt.api.SuspectedLogService;
import com.prowler.alertmgmt.model.SuspectedLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TODO: Cleanup This controller is NOT BEING USED
 */
@RestController
@RequestMapping("/prowler/v1")
public class SuspectedLogsController {
    @Autowired
    private SuspectedLogService service;

    @RequestMapping(value="/suspectedlogs", method = RequestMethod.POST)
    public SuspectedLog addSuspectedLog(@RequestBody SuspectedLog log) {
        return service.create(log);
    }

    @RequestMapping(value="/suspectedlogs", method = RequestMethod.GET)
    public List<SuspectedLog> getAll() {
        return service.getAll();
    }
}

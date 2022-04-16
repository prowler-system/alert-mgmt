package com.prowler.alertmgmt.controller;

import com.prowler.alertmgmt.api.AlertsService;
import com.prowler.alertmgmt.model.Alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
//@Validated
@RequestMapping("/prowler/v1")
public class AlertsController {
    @Autowired
    private AlertsService service;

    @RequestMapping(value="/alerts", method = RequestMethod.GET)
    public List<Alert> search(@RequestParam String application,
                              @RequestParam(required = false) String host,
                              @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME,
                                                         pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                              @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME,
                                                         pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                              @RequestParam(required = false) Integer offset,
                              @RequestParam(required = false) Integer limit) {
        return service.searchAlerts(application, host, startTime, endTime, offset, limit);
    }
}

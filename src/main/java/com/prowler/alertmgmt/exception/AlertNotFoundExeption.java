package com.prowler.alertmgmt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Alert not found")
public class AlertNotFoundExeption extends RuntimeException {
}

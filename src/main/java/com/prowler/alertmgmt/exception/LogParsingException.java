package com.prowler.alertmgmt.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class LogParsingException extends Exception {
    public LogParsingException(String s, JsonProcessingException e) {
        super(s, e);
    }
}

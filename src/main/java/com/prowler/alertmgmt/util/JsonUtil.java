package com.prowler.alertmgmt.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.findAndRegisterModules();
    }
}

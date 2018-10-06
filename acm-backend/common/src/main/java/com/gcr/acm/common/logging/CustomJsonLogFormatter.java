package com.gcr.acm.common.logging;

import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;

import java.io.IOException;
import java.util.Map;

public class CustomJsonLogFormatter extends JacksonJsonFormatter {
    @Override
    public String toJsonString(Map map) throws IOException {
        return super.toJsonString(map) + "\r\n";
    }
}

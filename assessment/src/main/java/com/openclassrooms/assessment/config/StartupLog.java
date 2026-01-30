package com.openclassrooms.assessment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLog implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupLog.class);

    @Override
    public void run(String... args) {
        log.info("### ASSESSMENT STARTUP LOG ACTIVE ###");
    }
}

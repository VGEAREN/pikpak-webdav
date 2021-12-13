package com.vgearen.pikpakwebdav.config;

import com.vgearen.pikpakwebdav.client.PikpakDriverClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PikpakProperties.class)
public class PikpakDriverAutoConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PikpakDriverAutoConfig.class);

    @Autowired
    private PikpakProperties pikpakProperties;

    @Bean
    public PikpakDriverClient pikpakClient(ApplicationContext applicationContext) throws Exception {
        return new PikpakDriverClient(pikpakProperties);
    }



}

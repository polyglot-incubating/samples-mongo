package org.chiwooplatform.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * https://github.com/sepatel/inigma-shared
 * 
 * @author aider
 */
@SpringBootApplication
public class SamplesMongoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamplesMongoApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.indentOutput(true);
        builder.failOnUnknownProperties(false);
        return builder;
    }
}

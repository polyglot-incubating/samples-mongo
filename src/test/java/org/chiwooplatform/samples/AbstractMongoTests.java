package org.chiwooplatform.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.runner.RunWith;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@ImportAutoConfiguration(classes = { MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class })
public class AbstractMongoTests {

    /**
     * Json Data 중 DefaultMongoTypeMapper.DEFAULT_TYPE_KEY 속성인 "_class" 처리 무시
     */
    @Configuration
    public class MongoConfiguration {

        @Bean
        public ObjectMapper objectMapperBuilder() {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.indentOutput(true);
            builder.failOnUnknownProperties(false);
            return builder.build();
        }

        @Autowired
        private MongoDbFactory mongoFactory;

        @Autowired
        private MongoMappingContext mongoMappingContext;

        @Bean
        public MappingMongoConverter mongoConverter() throws Exception {
            final DefaultMongoTypeMapper typeMapper = new DefaultMongoTypeMapper(null);
            DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
            MappingMongoConverter mongoConverter = new MappingMongoConverter(
                    dbRefResolver, mongoMappingContext);
            // this is my customization
            mongoConverter.setTypeMapper(typeMapper);
            // mongoConverter.setMapKeyDotReplacement("_");
            mongoConverter.afterPropertiesSet();
            return mongoConverter;
        }
    }
}

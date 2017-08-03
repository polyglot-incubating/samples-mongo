package org.chiwooplatform.samples.dam.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.samples.AbstractMongoTests;
import org.chiwooplatform.samples.model.BadLanMessage;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
        // "home",
        "default" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class,
        BadLanMessageTemplateTest.MongoConfiguration.class })
public class BadLanMessageTemplateTest {

    @Autowired
    private BadLanMessageTemplate template;

    @EnableMongoRepositories
    @Configuration
    static class MongoConfiguration {
        @Bean
        public BadLanMessageTemplate localizationMongoTemplate(
                MongoTemplate mongoTemplate) {
            log.info("mongoTemplate: {}", mongoTemplate);
            return new BadLanMessageTemplate(mongoTemplate);
        }
    }

    BadLanMessage model() {
        BadLanMessage model = new BadLanMessage();
        model.setId("hello");
        model.addMessage("ko", "안녕하세요");
        return model;
    }

    @Test
    public void loadData() throws Exception {
        List<String> en = toList("/en.txt");
        final List<BadLanMessage> enMsgs = en.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    BadLanMessage message = new BadLanMessage();
                    message.setId(msg[0]);
                    message.addMessage("en", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        List<String> de = toList("/de.txt");
        final List<BadLanMessage> deMsgs = de.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    BadLanMessage message = new BadLanMessage();
                    message.setId(msg[0]);
                    message.addMessage("de", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        List<String> fr = toList("/fr.txt");
        final List<BadLanMessage> frMsgs = fr.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    BadLanMessage message = new BadLanMessage();
                    message.setId(msg[0]);
                    message.addMessage("fr", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        List<String> ko = toList("/ko.txt");
        final List<BadLanMessage> koMsgs = ko.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    BadLanMessage message = new BadLanMessage();
                    message.setId(msg[0]);
                    message.addMessage("ko", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        template.batchUpdate(enMsgs);
        template.batchUpdate(deMsgs);
        template.batchUpdate(frMsgs);
        template.batchUpdate(koMsgs);
    }

    private List<String> toList(final String filename) throws IOException {
        InputStream in = BadLanMessageTemplateTest.class.getResourceAsStream(filename);
        String en = IOUtils.toString(in);
        return Arrays.asList(en.split("\n"));
    }

    @Test
    public void ut1001_save() throws Exception {
        BadLanMessage model = model();
        template.save(model);
        log.info("result: {}", template.findOne(model.getId()));
    }

    @Test
    public void ut1002_findOne() throws Exception {
        BadLanMessage model = model();
        Query query = Query.query(Criteria.where("_id").is(model.getId()));
        BadLanMessage result = template.findOne(query);
        log.info("result: {}", result);
    }

    @Test
    public void ut1003_findQuery() throws Exception {
        final String
        // word = "access";
        word = "User";
        /**
         * 아주 복잡한 Criteria Query 의 경우에는 native query 를 직접 사용 할 수 있다.
         */
        Criteria criteria = new Criteria(word) {
            private String sql(final String key) {
                return new StringBuilder().append("function() {")
                        .append("    var msg = this.message;")
                        .append("    var keys = Object.keys(msg);")
                        .append("    var match = false;")
                        .append("    keys.forEach(function(k) {")
                        .append("        if(msg[k].match(/").append(key).append("/i) ) {")
                        .append("          match = true;  ").append("        }")
                        .append("    });             ")
                        .append("    if(match) return true;").append("    return false; ")
                        .append("}").toString();
            }

            public DBObject getCriteriaObject() {
                DBObject obj = new BasicDBObject();
                obj.put("$where", sql(this.getKey()));
                return obj;
            }
        };

        Query query = Query.query(criteria);
        List<BadLanMessage> results = template.findQuery(query);
        log.info("results: {}", results);
    }
}

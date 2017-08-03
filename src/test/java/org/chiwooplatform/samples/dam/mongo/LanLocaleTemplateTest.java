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
import org.chiwooplatform.samples.model.LanLocale;
import org.chiwooplatform.samples.model.LanMessage;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
        // "home",
        "default" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class,
        LanLocaleTemplateTest.MongoConfiguration.class })
public class LanLocaleTemplateTest {

    @Autowired
    private LanLocaleTemplate template;

    @EnableMongoRepositories
    @Configuration
    static class MongoConfiguration {
        @Bean
        public LanLocaleTemplate localizationMongoTemplate(MongoTemplate mongoTemplate) {
            log.info("mongoTemplate: {}", mongoTemplate);
            return new LanLocaleTemplate(mongoTemplate);
        }
    }

    LanLocale model() {
        LanLocale model = new LanLocale();
        model.setId("hello");
        model.setMessages(Arrays.asList(new LanMessage("ko", "안녕하세요."),
                new LanMessage("en", "Hello.")));
        return model;
    }

    @Test
    public void loadData() throws Exception {
        List<String> en = toList("/en.txt");
        final List<LanLocale> enMsgs = en.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    LanLocale message = new LanLocale();
                    message.setId(msg[0]);
                    message.addMessage("en", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        List<String> de = toList("/de.txt");
        final List<LanLocale> deMsgs = de.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    LanLocale message = new LanLocale();
                    message.setId(msg[0]);
                    message.addMessage("de", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        List<String> fr = toList("/fr.txt");
        final List<LanLocale> frMsgs = fr.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    LanLocale message = new LanLocale();
                    message.setId(msg[0]);
                    message.addMessage("fr", msg[1]);
                    return message;
                }).collect(Collectors.toList());

        List<String> ko = toList("/ko.txt");
        final List<LanLocale> koMsgs = ko.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    LanLocale message = new LanLocale();
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
        InputStream in = LanLocaleTemplateTest.class.getResourceAsStream(filename);
        String en = IOUtils.toString(in);
        return Arrays.asList(en.split("\n"));
    }

    @Test
    public void ut1001_save() throws Exception {
        LanLocale model = model();
        template.save(model);
        log.info("result: {}", template.findOne(model.getId()));
    }

    @Test
    public void ut1002_findOne() throws Exception {
        LanLocale model = model();
        Query query = Query.query(Criteria.where("_id").is(model.getId()));
        LanLocale result = template.findOne(query);
        log.info("result: {}", result);
    }

    @Test
    public void ut1003_findQuery() throws Exception {
        final String word = "access";
        // word = "hello";
        Query query = Query.query(Criteria.where("messages.value").regex(word, "i"));
        List<LanLocale> results = template.findQuery(query);
        log.info("query: {}", query.toString());
        log.info("results: {}", results);
    }
}

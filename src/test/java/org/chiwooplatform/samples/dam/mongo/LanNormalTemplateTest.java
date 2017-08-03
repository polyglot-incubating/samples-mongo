package org.chiwooplatform.samples.dam.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

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
import org.chiwooplatform.samples.model.LanNormal;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
        // "home",
        "default" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class,
        LanNormalTemplateTest.CustomConfiguration.class })
public class LanNormalTemplateTest {

    @Autowired
    private LanNormalTemplate template;
    

    @Autowired
    private ObjectMapper objectMapper;

    @EnableMongoRepositories
    @Configuration
    static class CustomConfiguration {
        @Bean
        public LanNormalTemplate localizationMongoTemplate(MongoTemplate mongoTemplate) {
            log.info("mongoTemplate: {}", mongoTemplate);
            return new LanNormalTemplate(mongoTemplate);
        }
    }

    LanNormal model() {
        LanNormal model = new LanNormal();
        model.setId(model.key("hello", "ko"));
        model.setValue("안녕하세요.");

        return model;
    }

    private List<String> toList(final String filename) throws IOException {
        InputStream in = LanNormalTemplateTest.class.getResourceAsStream(filename);
        String en = IOUtils.toString(in, "UTF-8");
        return Arrays.asList(en.split("\n"));
    }

    private List<LanNormal> toLocaleList(final List<String> list, final String locale) {
        final List<LanNormal> enMsgs = list.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    final LanNormal model = new LanNormal();
                    model.setId(model.key(msg[0], locale));
                    model.setValue(StringEscapeUtils.unescapeJava(msg[1]));

                    return model;
                }).collect(Collectors.toList());
        return enMsgs;
    }

    @Test
    public void loadData() throws Exception {
        final List<LanNormal> english = toLocaleList(toList("/en.txt"), "en");
        final List<LanNormal> germany = toLocaleList(toList("/de.txt"), "de");
        final List<LanNormal> france = toLocaleList(toList("/fr.txt"), "fr");
        final List<LanNormal> korean = toLocaleList(toList("/ko.txt"), "ko");
        template.batchUpdate(english);
        template.batchUpdate(germany);
        template.batchUpdate(france);
        template.batchUpdate(korean);
    }

    @Test
    public void ut1001_save() throws Exception {
        LanNormal model = model();
        template.save(model);
        log.info("result: {}", template.findOne(model.getId()));
    }

    @Test
    public void ut1002_findOne() throws Exception {
        LanNormal model = model();
        Query query = Query.query(Criteria.where("_id").is(model.getId()));
        LanNormal result = template.findOne(query);
        log.info("result: {}", result);
    }

    @Test
    public void ut1003_findOne() throws Exception {
        log.info("objectMapper: {}", objectMapper);
        LanNormal model = model();
        Object id = model.key("AbstractAccessDecisionManager.accessDenied", "ko");
        Query query = Query.query(Criteria.where("_id").is(id));
        LanNormal result = template.findOne(query);
        log.info("result: {}", result);
        objectMapper.writeValue(System.out, result);
        
    }

    @Test
    public void ut1004_findQuery() throws Exception {
        final String word = "access";
        // word = "hello";
        Query query = Query.query(Criteria.where("value").regex(word, "i"));
        List<LanNormal> results = template.findQuery(query);
        log.info("query: {}", query.toString());
        log.info("results: {}", results);
    }

    @Test
    public void ut1005_findQuery() throws Exception {
        final String word = "ass";
        // word = "hello";
        Query query = Query.query(Criteria.where("value").regex(word, "i"));
        List<LanNormal> results = template.findQuery(query);
        log.info("query: {}", query.toString());
        objectMapper.writeValue(System.out, results);
    }

    //
}

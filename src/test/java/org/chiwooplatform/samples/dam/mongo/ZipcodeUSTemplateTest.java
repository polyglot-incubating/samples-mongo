package org.chiwooplatform.samples.dam.mongo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.chiwooplatform.samples.AbstractMongoTests;
import org.chiwooplatform.samples.model.ZipcodeUS;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = { 
        // 
        "home",
        // "default"
})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class,
        ZipcodeUSTemplateTest.CustomConfiguration.class })
public class ZipcodeUSTemplateTest {

    @Autowired
    private ZipcodeUSTemplate template;

    @Autowired
    private ObjectMapper objectMapper;

    @EnableMongoRepositories
    @Configuration
    static class CustomConfiguration {
        @Bean
        public ZipcodeUSTemplate zipcodeUSTemplate(MongoTemplate mongoTemplate) {
            log.info("mongoTemplate: {}", mongoTemplate);
            return new ZipcodeUSTemplate(mongoTemplate);
        }
    }

    private List<String> readInsideFile(InputStream in) {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = new LineNumberReader(new InputStreamReader(in),
                (1024 * 1024));
        List<String> list = new LinkedList<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
                list.add(line);
                // log.info("line: {}", line);
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }

    @SuppressWarnings({ "rawtypes", "resource" })
    public List<String> zipToList(final String filename) throws IOException {
        ZipFile zip = new ZipFile("target/uszip.zip");
        for (Enumeration e = zip.entries(); e.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            if (!entry.isDirectory()) {
                if (filename.equals(entry.getName())) {
                    return readInsideFile(zip.getInputStream(entry));
                }
            }
        }
        return null;
    }

    public boolean downloadUsZipcode(final String filename) throws Exception {
        String toFile = "target/uszip.zip";
        try {
            File f = new File(toFile);
            if (f.exists() && f.length() > 1000L) {
                return true;
            }
            String fromFile = "http://download.geonames.org/export/zip/US.zip";
            FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
            return true;
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Test
    public void loadData() throws Exception {
        final String filename = "US.txt";
        final boolean downloaded = downloadUsZipcode(filename);
        if (!downloaded) {
            return;
        }
        List<String> list = zipToList(filename);
        final List<ZipcodeUS> zipcodes = toLocaleList(list);
        log.info("zipcodes.size(): {}", zipcodes.size());
        template.batchUpdate(zipcodes);
    }

    ZipcodeUS model() {
        ZipcodeUS model = new ZipcodeUS("US", "79553", "Akutanz", "Aleutianzs", "East",
                "012", "", "", "", "54.1243", "165.7254", "1");

        return model;
    }

    private static Function<String[], ZipcodeUS> zip = new Function<String[], ZipcodeUS>() {

        @Override
        public ZipcodeUS apply(String[] t) {
            if (t == null) {
                return new ZipcodeUS();
            }
            String[] d = new String[12];
            for (int i = 0; i < t.length; i++) {
                d[i] = t[i];

            }
            final ZipcodeUS zip = new ZipcodeUS(d[0], d[1], d[2], d[3], d[4], d[5], d[6],
                    d[7], d[8], d[9], d[10], d[11]);
            return zip;
        }
    };

    private List<ZipcodeUS> toLocaleList(final List<String> list) {
        final List<ZipcodeUS> enMsgs = list.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] arr = m.split("\t");
                    final ZipcodeUS model = ZipcodeUSTemplateTest.zip.apply(arr);
                    return model;
                }).collect(Collectors.toList());
        return enMsgs;
    }

    @Test
    public void ut1001_save() throws Exception {
        ZipcodeUS model = model();
        template.save(model);
        Query query = Query.query(Criteria.where("postCode").is(model.getPostCode()));
        log.info("result: {}", template.findOne(query));
    }

    @Test
    public void ut1002_findOne() throws Exception {
        ZipcodeUS model = model();
        Query query = Query.query(Criteria.where("postCode").is(model.getPostCode()));
        ZipcodeUS result = template.findOne(query);
        log.info("result: {}", result);
        objectMapper.writeValue(System.out, result);
    }

    @Test
    public void ut1003_findOne() throws Exception {
        log.info("objectMapper: {}", objectMapper);
        ZipcodeUS model = model();
        Query query = Query.query(Criteria.where("postCode").is(model.getPostCode()));
        ZipcodeUS result = template.findOne(query);
        log.info("result: {}", result);
        objectMapper.writeValue(System.out, result);

    }

    @Test
    public void ut1004_findQuery() throws Exception {
        final String word = "Anchorage";
        Query query = Query.query(Criteria.where("placeName").regex(word, "i"));
        List<ZipcodeUS> results = template.findQuery(query);
        log.info("query: {}", query.toString());
        log.info("results: {}", results);
    }

    @Test
    public void ut1005_findQuery() throws Exception {
        final String word = "Anchorage";
        Query query = Query.query(Criteria.where("placeName").regex(word, "i"))
                // .maxScan(20)
                .limit(20);
        List<ZipcodeUS> results = template.findQuery(query);
        log.info("query: {}", query.toString());
        log.info("results.size(): {}", results.size());
        objectMapper.writeValue(System.out, results);
    }

}

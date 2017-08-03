package org.chiwooplatform.samples.dam.mongo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.chiwooplatform.samples.model.BadLanMessage;

import com.mongodb.WriteResult;

public class BadLanMessageTemplate {

    private final MongoTemplate template;

    private static final String COLLECTION_NAME = BadLanMessage.COLLECTION_NEME;

    @Autowired
    public BadLanMessageTemplate(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    public BadLanMessage findOne(Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        final BadLanMessage model = template.findOne(query, BadLanMessage.class,
                COLLECTION_NAME);
        return model;
    }

    public BadLanMessage findOne(Query query) {
        final BadLanMessage model = template.findOne(query, BadLanMessage.class,
                COLLECTION_NAME);
        return model;
    }

    public List<BadLanMessage> findQuery(Query query) {
        final List<BadLanMessage> list = template.find(query, BadLanMessage.class,
                COLLECTION_NAME);
        return list;
    }

    public void add(BadLanMessage model) {
        template.insert(model, COLLECTION_NAME);
    }

    public void save(BadLanMessage model) {
        template.save(model, COLLECTION_NAME);
    }

    public void batchInsert(List<BadLanMessage> models) {
        template.insert(models, COLLECTION_NAME);
    }

    public void batchUpdate(List<BadLanMessage> models) {
        for (BadLanMessage model : models) {
            Query query = new Query()
                    .addCriteria(Criteria.where("_id").is(model.getId()));
            BadLanMessage result = findOne(query);
            if (result != null) {
                Map<String, String> old = result.getMessage();
                model.getMessage().putAll(old);
                template.save(model);
            }
            else {
                template.insert(model);
            }
        }
    }

    public void remove(Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        WriteResult result = template.remove(query, COLLECTION_NAME);
        if (result.getN() == 1) {

        }
    }

    public void remove(BadLanMessage model) {
        template.remove(model, COLLECTION_NAME);
    }
}
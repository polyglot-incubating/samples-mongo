package org.chiwooplatform.samples.dam.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.chiwooplatform.samples.model.LanLocale;
import org.chiwooplatform.samples.model.LanMessage;

import com.mongodb.WriteResult;

public class LanLocaleTemplate {

    private final MongoTemplate template;

    private static final String COLLECTION_NAME = LanLocale.COLLECTION_NEME;

    @Autowired
    public LanLocaleTemplate(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    public LanLocale findOne(Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        final LanLocale model = template.findOne(query, LanLocale.class,
                COLLECTION_NAME);
        return model;
    }

    public LanLocale findOne(Query query) {
        final LanLocale model = template.findOne(query, LanLocale.class,
                COLLECTION_NAME);
        return model;
    }

    public List<LanLocale> findQuery(Query query) {
        final List<LanLocale> list = template.find(query, LanLocale.class,
                COLLECTION_NAME);
        return list;
    }

    public void add(LanLocale model) {
        template.insert(model, COLLECTION_NAME);
    }

    public void save(LanLocale model) {
        template.save(model, COLLECTION_NAME);
    }

    public void batchInsert(List<LanLocale> models) {
        template.insert(models, COLLECTION_NAME);
    }

    public void batchUpdate(List<LanLocale> models) {
        for (LanLocale model : models) {
            Query query = new Query()
                    .addCriteria(Criteria.where("_id").is(model.getId()));
            LanLocale result = findOne(query);
            if (result != null) {
                List<LanMessage> old = result.getMessages();
                model.getMessages().addAll(old);
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

    public void remove(LanLocale model) {
        template.remove(model, COLLECTION_NAME);
    }
}
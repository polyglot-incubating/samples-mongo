package org.chiwooplatform.samples.dam.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.chiwooplatform.samples.model.LanNormal;

import com.mongodb.WriteResult;

public class LanNormalTemplate {

    private final MongoTemplate template;

    private static final String COLLECTION_NAME = LanNormal.COLLECTION_NEME;

    @Autowired
    public LanNormalTemplate(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    public LanNormal findOne(Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        final LanNormal model = template.findOne(query, LanNormal.class, COLLECTION_NAME);
        return model;
    }

    public LanNormal findOne(Query query) {
        final LanNormal model = template.findOne(query, LanNormal.class, COLLECTION_NAME);
        return model;
    }

    public List<LanNormal> findQuery(Query query) {
        final List<LanNormal> list = template.find(query, LanNormal.class,
                COLLECTION_NAME);
        return list;
    }

    public void add(LanNormal model) {
        template.insert(model, COLLECTION_NAME);
    }

    public void save(LanNormal model) {
        template.save(model, COLLECTION_NAME);
    }

    public void batchInsert(List<LanNormal> models) {
        template.insert(models, COLLECTION_NAME);
    }

    public void batchUpdate(List<LanNormal> models) {
        for (LanNormal model : models) {
            try {
                Query query = new Query()
                        .addCriteria(Criteria.where("_id").is(model.getId()));
                LanNormal result = findOne(query);
                if (result != null) {
                    template.save(model, COLLECTION_NAME);
                }
                else {
                    template.insert(model, COLLECTION_NAME);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        WriteResult result = template.remove(query, COLLECTION_NAME);
        if (result.getN() == 1) {

        }
    }

    public void remove(LanNormal model) {
        template.remove(model, COLLECTION_NAME);
    }
}
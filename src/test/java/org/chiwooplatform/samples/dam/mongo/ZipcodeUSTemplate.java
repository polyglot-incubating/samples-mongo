package org.chiwooplatform.samples.dam.mongo;

import java.util.List;

import org.chiwooplatform.samples.model.ZipcodeUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.WriteResult;

public class ZipcodeUSTemplate {

    private final MongoTemplate template;

    private static final String COLLECTION_NAME = ZipcodeUS.COLLECTION_NEME;

    @Autowired
    public ZipcodeUSTemplate(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    public ZipcodeUS findOne(Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        final ZipcodeUS model = template.findOne(query, ZipcodeUS.class, COLLECTION_NAME);
        return model;
    }

    public ZipcodeUS findOne(Query query) {
        final ZipcodeUS model = template.findOne(query, ZipcodeUS.class, COLLECTION_NAME);
        return model;
    }

    public List<ZipcodeUS> findQuery(Query query) {
        final List<ZipcodeUS> list = template.find(query, ZipcodeUS.class,
                COLLECTION_NAME);
        return list;
    }

    public void add(ZipcodeUS model) {
        template.insert(model, COLLECTION_NAME);
    }

    public void save(ZipcodeUS model) {
        template.save(model, COLLECTION_NAME);
    }

    public void batchInsert(List<ZipcodeUS> models) {
        template.insert(models, COLLECTION_NAME);
    }

    public void batchUpdate(List<ZipcodeUS> models) {
        for (ZipcodeUS model : models) {
            try {
                Query query = new Query()
                        .addCriteria(Criteria.where("postCode").is(model.getPostCode()));
                ZipcodeUS result = findOne(query);
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

    public void remove(ZipcodeUS model) {
        template.remove(model, COLLECTION_NAME);
    }
}
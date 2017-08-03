package org.chiwooplatform.samples.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * <pre>
 * LanLocale 메시지 객체는 MongoDB 에 아래와 같이 저장 된다. message code 에 대한 p-key 가 보장되고, message 의
 * key 에 해당 하는 locale 코드가 동적으로 저장 되는 메시지 형식 또한 단순 하다.
 * <code>
{ 
    "_id" : "AbstractAccessDecisionManager.accessDenied", 
    "_class" : "org.chiwooplatform.samples.model.LanLocale", 
    "messages" : {
        {"lang": "de", "value" : "Zugriff verweigert"}, 
        {"lang": "ko", "value" : "Acc\\u00E8s refus\\u00E9"},
        {"lang": "en", "value" : "Access is denied"},
        {"lang": "fr", "value" : "Acc\\u00E8s refus\\u00E9"}
    }, 
    "createTime" : NumberLong(1501636555186)
}</code>
 * 
 * 여기서, messages 개체의 value 속성 값이 'Access' 라는 단어가 포함 되는 Document 를 찾으려면 어떻게 해야 할까?
 * 
 * BadLanMessage 모델의 경우와는 달리 아래와 같이 쉽게 Query 할 수 있다.
 * <code>
db.lanMessage.find({ "messages.value": "Access is denied" });
</code>
 * </pre>
 * 
 */
@Data
@Document(collection = LanLocale.COLLECTION_NEME)
public class LanLocale {

    public static final String COLLECTION_NEME = "lanLocale";
    private String id;
    private List<LanMessage> messages = new ArrayList<>();
    private Long createTime = System.currentTimeMillis();

    public void addMessage(String lang, String value) {
        LanMessage m = new LanMessage(lang, value);
        messages.add(m);
    }

}

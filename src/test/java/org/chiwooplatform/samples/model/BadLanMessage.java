package org.chiwooplatform.samples.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * <pre>
 * BadLanMessage 메시지 객체는 MongoDB 에 아래와 같이 저장 된다. message code 에 대한 p-key 가 보장되고, message 의
 * key 에 해당 하는 locale 코드가 동적으로 저장 되는 메시지 형식 또한 단순 하다.
 * <code>
{ 
    "_id" : "AbstractAccessDecisionManager.accessDenied", 
    "_class" : "org.chiwooplatform.samples.model.BadLanMessage", 
    "message" : {
        "de" : "Zugriff verweigert", 
        "ko" : "Acc\\u00E8s refus\\u00E9", 
        "en" : "Access is denied", 
        "fr" : "Acc\\u00E8s refus\\u00E9"
    }, 
    "createTime" : NumberLong(1501636555186), 
    "enabled" : true
}</code>
 * 
 * 여기서, message 속성의 값이 'Access' 라는 단어가 포함 되는 Document 를 찾으려면 어떻게 해야 할까? message 내용을 보면
 * locale 을 대표하는 속성 명과, locale 에 해당하는 메시지 속성이 없다는 것이다.
 * 
 * 사용자가 locale 을 미리 알고 있다면 Query는 가능 하다. <br>
 * <code>
db.localeMessage.find({ "message.en": "Access is denied" });
</code>
 
 * <br>
 * 그런데 locale 을 모르고 있다거나, 또는 locale 의 구분 없이 값에 'Access' 가 포함된 Document 를 Query 해야 한다면 어렵게
 * 된다. 문제는 성능도 좋지 않다는 점이다. <code>
db.badLangMessage.find({ 
        $where: function() {
            var msg = this.message;
            var keys = Object.keys(msg);
            var match = false;
            keys.forEach(function(k) {                  
                if(msg[k].match(/access/i) ) { // access -- 검색 할 키워드
                  match = true;  
                }
            });             
            if(match) return true;
            return false; 
        }
  });</code>
 * </pre>
 * 
 */
@Data
@Document(collection = BadLanMessage.COLLECTION_NEME)
public class BadLanMessage {

    public static final String COLLECTION_NEME = "badLanMessage";

    private String id;
    private Map<String, String> message = new HashMap<>();
    private Long createTime = System.currentTimeMillis();
    private boolean enabled = true;

    public void addMessage(String locale, String message) {
        this.message.put(locale, message);
    }

}

package org.chiwooplatform.samples.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <pre>
 * LanNormal 메시지 객체는 MongoDB 에 아래와 같이 저장 된다. 
 * 
 * <code>
{ 
    "_id" : {
        "code" : "AbstractAccessDecisionManager.accessDenied", 
        "lang" : "en"
    }, 
    "value" : "Access is denied",
    "createTime" : NumberLong(1501636555186)
}</code>
 * Key 구성이 code, lang 두 개를 조합한 복합 키로 구성 되어 있다.
</code>
 * </pre>
 * 
 */
@Document
@AllArgsConstructor
@Data
public class LanNormal {

    public static final String COLLECTION_NEME = "lanNormal";

    @Id
    private MessageKey id;

    private String value;

    private String creator;

    private Long createTime;

    public LanNormal() {
        super();
    }

    public MessageKey key(String code, String lang) {
        return new MessageKey(code, lang);
    }

    @AllArgsConstructor
    @Data
    class MessageKey {
        private String code;
        private String lang;
    }
}

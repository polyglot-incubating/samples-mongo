package org.chiwooplatform.samples.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Document
public class ZipcodeUS {

    public static final String COLLECTION_NEME = "zipcodeUS";

    private String countryCode;
    @Indexed(unique = false)
    private String postCode;
    private String placeName;
    private String adminName1;
    private String adminCode1;
    private String adminName2;
    private String adminCode2;
    private String adminName3;
    private String adminCode3;
    private String latitude;
    private String longitude;
    private String accuracy;

    public ZipcodeUS() {
        super();
    }

}

[[overview]]

NoSQL 장점
A. 클라우드 환경에 적합 하다.
    1. 오픈소스
    2. 하드웨어 확장에 유연하게 대처 할 수 있다.
    3. 저 비용으로 병렬처리가 가능 하다.

B. 유연한 데이터 모델 이다.
    1. 비정형 데이터 구조 설계는 
    2. Join 구조를 Embedded 와 Linking 으로 구현

C. Big Data 처리에 효과적
    1. 메모리 매핑 기능을 통해 Read/Write 가 빠름
    2. RDB 와 동일하게 데이터 처리가 가능

MongoDB 주요 특징
1. JSON Type 의 도큐먼트 데이터 저장
2. Sharding(분산)/Replica(복제)기능을 제공
3. MapReduce(분산/병렬)처리 기능 제공
4. Memory Mapping 기술을 기반으로 BigData 처리 
5. 일반적인 CRUD 트랜잭션 처리도 가능

MongoDB 의 주요 요소
RDB         |       MongoDB
table       |       collection
row         |       document
column      |          field
pk          |       object id
relation    |       embedded linking

컬렉션 생성
~~~
db.createCollection("mydata",( {capped: true, size:100000}))
~~~
capped      : capped를 true로하면 size를 정한만큼만 공간을 사용 한다. 
                            할당된 공간을 전부 사용했을시에는 처음썻던 데이터에 덮어쓰는 방식으로 기록 한다.
non capped  : capped 를 false로 하면 size에 명시한 공간을 전부 사용했을시 추가로 공간을 할당 한다.

mydata 컬렉션의 현재 상태 및 정보를 분석
~~~
db.mydata.validate();
~~~

컬렉션 이름 변경
~~~
db.mydata.rename("myData");
~~~

myData 컬렉션 삭제
~~~
db.myData.drop();
~~~


간단한 CURD 테스트
~~~
var data = {eno: 1001, name: "scott", address: "Seoul city"}

db.myData.insert(data);
db.myData.save(data);
db.myData.update({eno: 1001}, {$set: {name:"tiger"}});
db.myData.find({eno: 1001});
db.myData.find({eno: {$gt: 1000} });
db.myData.find({eno: {$lt: 1000}, name:"tiger" });
db.myData.remove({eno: 1001});

~~~


### Environment Properties

"application-gen.yml" is the environment file.
~~~~
server:
  port: ${port:8080}

spring:
  profiles:
    active:  default
  session:
    store-type: hash-map
  data:
    mongodb:
      host: localhost
      port: 27017
      database: I18N_MESSAGES

  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  pattern:
    console: "%d %-5level %logger : %msg%n"
  level:
    root: info
    org.springframework: info
    org.springframework.security: debug
    org.chiwooplatform: debug

---
spring:
  profiles: home
  data:
    mongodb:
      host: 192.168.30.210
      port: 27017
      database: SAMPLES
~~~~


### BadLanMessage
BadLanMessage 메시지 객체는 MongoDB 에 아래와 같이 저장 된다. message code 에 대한 p-key 가 보장 되고, 
message 의 key 에 해당 하는 locale 코드가 동적으로 저장 되는 메시지 형식 또한 단순 하게 구성 된다
.
~~~
{ 
    "_id" : "AbstractAccessDecisionManager.accessDenied", 
    "message" : {
        "de" : "Zugriff verweigert", 
        "ko" : "Acc\\u00E8s refus\\u00E9", 
        "en" : "Access is denied", 
        "fr" : "Acc\\u00E8s refus\\u00E9"
    }, 
    "createTime" : NumberLong(1501636555186), 
    "enabled" : true
}
~~~
저장은 쉽게 할 수 있지만 조회를 하기엔 까다롭다. 예로, message 속성의 값이 'Access' 라는 단어가 포함 되는 Document 를 찾으 려면
Query 를 복잡 하게 작성해야 할 뿐만 아니라 성능 또한 좋지 않게 된다.
message 의 값이 Access 가 포함 Document 를 조회 하는 Query.
~~~
db.badLangMessage.find({ 
        $where: function() {
            var msg = this.message;
            var keys = Object.keys(msg);
            var match = false;
            keys.forEach(function(k) {                  
                if(msg[k].match(/access/i) ) { 
                  match = true;  
                }
            });             
            if(match) return true;
            return false; 
        }
  });
~~~
BadLanMessageTemplateTest 단위 테스트 클래스를 참고 하자.

**BadLanMessageTemplateTest** 테스트 클래스를 참고 하자.

### LanLocale
LanLocale 메시지 객체는 MongoDB 에 아래와 같이 저장 된다. 
~~~
{ 
    "_id" : "AbstractAccessDecisionManager.accessDenied", 
    "messages" : {
        {"lang": "de", "value" : "Zugriff verweigert"}, 
        {"lang": "ko", "value" : "Acc\\u00E8s refus\\u00E9"},
        {"lang": "en", "value" : "Access is denied"},
        {"lang": "fr", "value" : "Acc\\u00E8s refus\\u00E9"}
    }, 
    "createTime" : NumberLong(1501636555186)
}
~~~

message code 에 대한 p-key 가 보장되고, message 의 key 에 해당 하는 locale 코드가 동적으로 저장 되는 메시지 형식 또한 단순 하다.
여기서, messages 개체의 value 속성 값이 'Access' 라는 단어가 포함 되는 Document 를 찾으려면 어떻게 해야 할까?
~~~
db.lanLocale.find({ "_id": /.*Access.*/i });
~~~
BadLanMessage 때와는 달리 상당히 단순화 되었다.
다국어 서비스 모델에서 사용자는 등록되지 않는 다국어 정보를 발견 했다면,
사전에 다국어가 등록 되었는지를 확인 할 것이다. 그럼 검색어는 무엇일까?
당연히 value 속성에 대한 검색어를 입력 할 것이다.
그럼 최적의 scan 성능을 위해선 위 모델에서 messages.value 속성에 대해 인덱스를 생성 할 수도 있다.
하지만, value는 messages 속성 안의 dynamic 컬렉션 객체 이기 때문에 index 를 생성 하기 위한 구조로 적합하지 않는 모형 이다.

**LanLocaleTemplateTest** 테스트 클래스를 참고 하자.

### LanNormal
LanNormal 메시지 객체는 MongoDB 에 아래와 같이 저장 된다. 
~~~
{ 
    "_id" : {
        "code" : "AbstractAccessDecisionManager.accessDenied", 
        "lang" : "en"
    }, 
    "value" : "Access is denied",
    "createTime" : NumberLong(1501636555186)
}
~~~
Key 구성이 code, lang 두 개를 조합한 복합 키로 구성 되어 있다.

key 객체를 따로 구성 해야 하는 약간의 불편함이 따르지만, code 에 대한 메시지를 조회 한거나 최상의 성능을 보장 한다.

다국어 코드 'AbstractAccessDecisionManager.accessDenied' 에 대한 locale 별 메시지를 조회 하려면 아래와 같이 하면 된다.
~~~
db.lanNormal.find({
    "_id.code": "AbstractAccessDecisionManager.accessDenied" 
});
~~~

다국어 메시지에 대한 다국어 코드를 모르는 경우, 아래와 같이 조회 할 수 있다.
~~~
db.lanNormal.find({
    "value": /.*pass.*/i 
});
~~~
위와 같이, value 속성은 자주 검색 될 것이므로 인덱스를 생성 하자.
~~~
db.lanNormal.createIndex({value: 1}, {unique: false, name:'idx_value'});
~~~

**LanNormalTemplateTest** 테스트 클래스를 참고 하자.

### ZipcodeUS
데이타량이 많으 모델에 대한 스키마 및 분산 처리를 해 보자.
미국의 우편코드 체계 DB 를 다운로드 하고, MongoDB 로 올려 보자.
http://download.geonames.org/export/zip/US.zip
스키마 구조는 아래와 같다.

~~~
{ 
    "_id" : ObjectId("5983eaa90a75d85f6cde883b"), 
    "countryCode" : "US", 
    "postCode" : "99553", 
    "placeName" : "Akutan", 
    "adminName1" : "Alaska", 
    "adminCode1" : "AK", 
    "adminName2" : "Aleutians East", 
    "adminCode2" : "013", 
    "adminName3" : "", 
    "adminCode3" : "", 
    "latitude" : "54.143", 
    "longitude" : "-165.7854", 
    "accuracy" : "1"
}
~~~

#### MapReduce
MapReduce 는 데이터 분산 처리의 대명사가 되어 버린 듯한 느낌 이다.
핵심 컨셉은 데이타 소스로부터 데이타 조각(line, row, document)들을 읽어  필터링이나 데이터 변환을 하고, reduce 로 집계를 한다.
map 은 간단히 r = f(x); 이며, reduce 는 map 통해  구해진 데이터 셋으로부터 min, max, sum, avg 와 같은 원하는 결과를 처리를 할 수 있다.   
~~~
data = [1,2,3];
squareData = data.map(function(item) { 
    return item * 2; 
});
squareData.reduce(
    function(total, item){ 
        return total += item
    });
~~~

MongoDB 는 자체적으로 파티셔닝과 분산 처리를 지원 하고, MapReduce 도 지원 한다.

행정 구역별 소계를 구해 보자.
~~~
/**
 * map 함수를 작성 한다. 하나의 document 에 대해 r = f(x) 함수를 구현 한다. 
 * this 키워드는 document 객체를 나타 낸다.
 */
var map = function () {

   /**
    * 변환된 값을 {key, value} 형태로 반환 한다. 
    */
    emit(this.adminName1, 1); 
}

/**
 * reduce 함수를 통해 원하는 값을 구하자.
 * 참고로 key 는 object, values 는 object[] 배열 이다.
 */
var reduce = function (key, values) {    
    var count = 0;
    values.forEach(function(v){ 
        count += v;
    });

   /**
    * 참고로, 배열의 간단한 합계는 아래 한줄 명령어로 대체 할 수 있다. 
    * return Array.sum(values);
    */
    return count;
}

var options = {out: {inline: true }};
db.zipcodeUS.mapReduce( map, reduce, options);
~~~


행정 구역 중  'Florida' 소계만 구해 보자. reduce 가 하는 일은 동일 하니, map 만 변경 하면 된다.
~~~
var map = function () {
    if ('Florida'== this.adminName1) {
        emit(this.adminName1, 1);
    }
    else {
      emit('OTHERS', 1);
    }    
}
db.zipcodeUS.mapReduce( map, reduce, options);
~~~

특정 GEO Location(Map coordinate) 범위 내의 행정구역 소계를 구할 수도 있다.
뉴욕을 중심으로 범위를 정한 후 주소지 소계를 구해 보자.
latitude   는 (from 40.3 to 45.3) 이며 longtitude 는 (from -80.0 to -71.7) 이다.

~~~
var map = function () {
    var lat  = parseFloat(this.latitude);
    var long = parseFloat(this.longitude);
    if( lat >= 40.3 && lat <= 45.3
        && long >= -80.0 && long <= -71.7 ) {
        emit(this.adminName1, 1);
    }
    else {
      emit('OTHERS', 1);
    }
}
~~~

**ZipcodeUSTemplateTest** 테스트 클래스를 참고 하자.

위 특정 지역에 대한 남녀 성 비율, 연령 대, 거주 지역 등  NoSQL 을 사용  하면 다양한 관점에서 빠르게 지원 할 수 있다.

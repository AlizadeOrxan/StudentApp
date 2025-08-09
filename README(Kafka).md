Giriş: Apache Kafka nədir?

Apache Kafka, yüksək ötürücülük qabiliyyətinə malik, paylanmış
(distributed) bir "data streaming" platformasıdır. Əsasən real-time
data axınlarını emal etmək, saxlamaq və bir sistemdən digərinə
etibarlı şəkildə ötürmək üçün istifadə olunur.

Əsas Konseptlər:

* Topic (Mövzu): Mesajların kateqoriyalara ayrıldığı bir kanal.
* Producer (Göndərən): Mesajları müəyyən bir "topic"-ə göndərən tətbiq.
* Consumer (Qəbul edən): "Topic"-lərə abunə olaraq mesajları qəbul edən
  tətbiq.
* Broker: Kafka serveri. Datanı saxlayan və ötürən əsas komponent.

Tələblər

Bu proyekti işə salmaq üçün sisteminizdə aşağıdakılar qurulmalıdır:

* Java Development Kit (JDK) 17 və ya daha yuxarı
* Apache Maven və ya Gradle
* Docker və Docker Compose

Proyektin Qurulması

İlk olaraq, start.spring.io (https://start.spring.io/) saytından və ya
IDE vasitəsilə yeni bir Spring Boot proyekt yaradın və aşağıdakı
"dependency"-ləri əlavə edin:

* Spring Web: RESTful endpoint yaratmaq üçün.
* Spring for Apache Kafka: Kafka inteqrasiyası üçün.

pom.xml faylınızda bu "dependency"-lər olmalıdır:

    1 <dependencies>
    2     <dependency>
    3         <groupId>org.springframework.boot</groupId>
    4         <artifactId>spring-boot-starter-web</artifactId>
    5     </dependency>
    6     <dependency>
    7         <groupId>org.springframework.kafka</groupId>
    8         <artifactId>spring-kafka</artifactId>
    9     </dependency>
10
11     <!-- Test üçün (opsional) -->
12     <dependency>
13         <groupId>org.springframework.kafka</groupId>
14         <artifactId>spring-kafka-test</artifactId>
15         <scope>test</scope>
16     </dependency>
17 </dependencies>

Docker ilə Kafka-nı Başlatmaq

Kafka-nı lokalda asanlıqla işə salmaq üçün docker-compose.yml
faylından istifadə etmək ən effektiv yoldur. Proyektin ana
direktoriyasında bu faylı yaradın:

`docker-compose.yml`

    1 version: '3.8'
    2 services:
    3   zookeeper:
    4     image: confluentinc/cp-zookeeper:7.0.1
    5     container_name: zookeeper
    6     environment:
    7       ZOOKEEPER_CLIENT_PORT: 2181
    8       ZOOKEEPER_TICK_TIME: 2000
    9 
10   kafka:
11     image: confluentinc/cp-kafka:7.0.1
12     container_name: kafka
13     ports:
14       - "9092:9092"
15     depends_on:
16       - zookeeper
17     environment:
18       KAFKA_BROKER_ID: 1
19       KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
20       KAFKA_LISTENER_SECURITY_PROTOCOL_MAP:
PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
21       KAFKA_ADVERTISED_LISTENERS:
PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
22       KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
23       KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
24       KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1

Terminalda aşağıdakı əmri icra edərək Kafka və Zookeeper servislərini
başladın:
1 docker-compose up -d

Spring Boot Konfiqurasiyası

src/main/resources/application.properties faylına Kafka serverinin
ünvanını və digər parametrləri əlavə edin.

`application.properties`

    1 # Server Portu
    2 server.port=8080
    3 
    4 # Kafka Broker Ünvanı
    5 spring.kafka.bootstrap-servers=localhost:9092
    6 
    7 # Kafka Consumer Group ID
    8 # Eyni ID-yə sahib consumer-lər bir qrup sayılır və
      topic-dəki mesajları öz aralarında bölüşürlər.
    9 spring.kafka.consumer.group-id=gemini-group
10
11 # Proyekt daxilində istifadə edəcəyimiz topic adı
12 kafka.topic.name=gemini-topic

Kafka Producer (Göndərən) Yaradılması

Bu servis, Kafka-ya mesaj göndərmək üçün KafkaTemplate istifadə edir.

`src/main/java/com/example/demo/service/KafkaProducerService.java`


ℹ Gemini CLI update available! 0.1.17 → 0.1.18
Installed via Homebrew. Please update with "brew upgrade".
1 package com.example.demo.service;
2
3 import org.slf4j.Logger;
4 import org.slf4j.LoggerFactory;
5 import
org.springframework.beans.factory.annotation.Autowired;
6 import org.springframework.beans.factory.annotation.Value;
7 import org.springframework.kafka.core.KafkaTemplate;
8 import org.springframework.stereotype.Service;
9
10 @Service
11 public class KafkaProducerService {
12
13     private static final Logger LOGGER =
LoggerFactory.getLogger(KafkaProducerService.class);
14
15     @Value("${kafka.topic.name}")
16     private String topicName;
17
18     @Autowired
19     private KafkaTemplate<String, String> kafkaTemplate;
20
21     public void sendMessage(String message) {
22         LOGGER.info(String.format("Mesaj göndərilir -> %s",
message));
23         kafkaTemplate.send(topicName, message);
24     }
25 }

Kafka Consumer (Qəbul edən) Yaradılması

Bu servis, @KafkaListener annotasiyası vasitəsilə müəyyən bir
"topic"-ə gələn mesajları dinləyir.

`src/main/java/com/example/demo/service/KafkaConsumerService.java`

    1 package com.example.demo.service;
    2 
    3 import org.slf4j.Logger;
    4 import org.slf4j.LoggerFactory;
    5 import org.springframework.kafka.annotation.KafkaListener;
    6 import org.springframework.stereotype.Service;
    7 
    8 @Service
    9 public class KafkaConsumerService {
10
11     private static final Logger LOGGER =
LoggerFactory.getLogger(KafkaConsumerService.class);
12
13     @KafkaListener(topics = "${kafka.topic.name}", groupId =
"${spring.kafka.consumer.group-id}")
14     public void listen(String message) {
15         LOGGER.info(String.format("Mesaj qəbul edildi -> %s"
, message));
16     }
17 }

Test üçün Rest Controller

Mesaj göndərmə prosesini asanlıqla test etmək üçün sadə bir REST
endpoint yaradaq.

`src/main/java/com/example/demo/controller/KafkaController.java`

    1 package com.example.demo.controller;
    2 
    3 import com.example.demo.service.KafkaProducerService;
    4 import
      org.springframework.beans.factory.annotation.Autowired;
    5 import org.springframework.web.bind.annotation.GetMapping;
    6 import org.springframework.web.bind.annotation.RequestParam;
    7 import
      org.springframework.web.bind.annotation.RestController;
    8 
    9 @RestController
10 public class KafkaController {
11
12     @Autowired
13     private KafkaProducerService producerService;
14
15     @GetMapping("/send")
16     public String sendMessage(@RequestParam("message")
String message) {
17         producerService.sendMessage(message);
18         return "Mesaj uğurla Kafka topic-inə göndərildi: " +
message;
19     }
20 }

İstifadə Qaydası

1. docker-compose up -d əmri ilə Kafka-nı başladın.
2. Spring Boot tətbiqini işə salın.
3. Brauzerdə və ya curl ilə aşağıdakı kimi bir sorğu göndərin:

1 curl -X GET "http://localhost:8080/send?message=Salam+Kafka"

Sorğunu göndərdikdən sonra tətbiqin konsol loqlarına baxın. Aşağıdakı
kimi çıxışlar görməlisiniz:

Producer loqu:
1 INFO  com.example.demo.service.KafkaProducerService : Mesaj g
öndərilir -> Salam Kafka

Consumer loqu:

1 INFO  com.example.demo.service.KafkaConsumerService : Mesaj q
əbul edildi -> Salam Kafka

Bu, mesajın uğurla göndərildiyini və qəbul edildiyini göstərir.
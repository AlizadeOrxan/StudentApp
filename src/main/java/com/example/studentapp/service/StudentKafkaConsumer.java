package com.example.studentapp.service;

import com.example.studentapp.dto.StudentDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StudentKafkaConsumer {

    private final EmailService emailService;
private final SimpMessagingTemplate messagingTemplate;

    public StudentKafkaConsumer(EmailService emailService, SimpMessagingTemplate messagingTemplate) {
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

//    @KafkaListener(topics = "student-created-topic", groupId = "student-group")
//    public void listenStudentCreated(StudentDto studentDto) {
//        log.info("Group-0 Kafka dan yeni yaradilan telebe haqqinda melumat qebul olundu : {} - {}", studentDto.getFirstName(), studentDto.getLastName());
//
//
//        String subject = "Melumat xarakterli mesaj";
//        String body = " Hormetli " + studentDto.getFirstName() + " " + studentDto.getLastName() + " sizin qeydiyyatiniz tamamlanmishdir ";
//
//        emailService.sendEmail(studentDto.getEmail(), subject, body);
//        log.info("Group - 0 Email sent successfully {}", studentDto.getEmail());
//
//    }
//
//        @KafkaListener(topics = "student-created-topic", groupId = "student-group1")
//        public void listenStudentCreated(ConsumerRecord<String, StudentDto> record) {
//        int partition = record.partition();
//        long offset = record.offset();
//
//        log.info("Group-1 Received record Kafka for partition {} offset {}", partition, offset);
//
//        String subject = "Melumat xarakterli mesaj";
//        String body = " Hormetli " + record.value().getFirstName() + " " + record.value().getLastName() + " sizin qeydiyyatiniz tamamlanmishdir ";
//
//        emailService.sendEmail(record.value().getEmail(), subject, body);
//        log.info("Group-1 Email sent successfully {}", record.value().getEmail());
//
//        }


    @KafkaListener(topics = "student-created-topic", groupId = "student-group-2")
    public void listenStudentCreatedGroup2(ConsumerRecord<String, StudentDto> record) {
        int partition = record.partition();
        long offset = record.offset();
        StudentDto studentDto = record.value();

        log.info("Group-2: Received record Kafka for partition {} offset {}", partition, offset);

        String subject = "Yeni qeydiyyat mesaji";
        String body = "Hormetli " + record.value().getFirstName() + " " + record.value().getLastName() + " sizin qeydiyyatiniz tamamlanmishdir ";

        emailService.sendEmail(record.value().getEmail(), subject, body);
        log.info("Group-2: Email sent successfully {}", record.value().getEmail());

        messagingTemplate.convertAndSend("/topic/students/updates",studentDto);
    }


    @KafkaListener(topics = "student-deleted-topic", groupId = "student-group")
    public void listenStudentDeleted(StudentDto studentDto) {
        log.info("Kafka dan yeni yaradilan telebe haqqinda melumatin silinmesi  qebul olundu {}",studentDto.getFirstName());

           String subject = " Xeberdarliq mesaji ";
                String body = " Hormetli  " + studentDto.getFirstName() + " " + studentDto.getLastName() + " Sizin profiliniz silinmishdir";

                emailService.sendEmail(studentDto.getEmail(), subject, body);
                log.warn("Email sent successfully with deletion {}", studentDto.getEmail());

    }
}

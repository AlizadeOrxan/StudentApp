package com.example.studentapp.controller;

import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketStudentController {

    private final StudentService studentService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketStudentController(StudentService studentService, SimpMessagingTemplate messagingTemplate) {
        this.studentService = studentService;
        this.messagingTemplate = messagingTemplate;
    }

    // Bu metod WebSocket ile "/app/students" endpoint-ie gonderilen msj-lari idare edecek
    @MessageMapping("/students")
    public void handleStudentMessage(StudentDto studentDto) {
        // Məlumatı qəbul edir və loga yazır.
        log.info("Veb səhifədən WebSocket vasitəsilə yeni Student məlumatı gəldi: {}", studentDto);

        // datani bazaya yaziriq
        studentService.createStudent(studentDto);

        // mesajlari qoshulan client-lara geri gondereceyik
        messagingTemplate.convertAndSend("/topic/students/updates", studentDto);
    }
}

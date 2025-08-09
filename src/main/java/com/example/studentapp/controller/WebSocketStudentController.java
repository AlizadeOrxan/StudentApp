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

    // Bu metod WebSocket  "/app/students" endpoint-e gonderilen mesajklari idare edecek
    @MessageMapping("/students")
    public void handleStudentMessage(StudentDto studentDto) {

        log.info("Veb səhifədən WebSocket vasitəsilə yeni Student məlumatı gəldi: {}", studentDto);

        // datalari bazaya yazmaq uchun cavab alinarken
        studentService.createStudent(studentDto);

        // Mesaji qoshulmush klientlere gonderiirk
        messagingTemplate.convertAndSend("/topic/students/updates", studentDto);
    }
}

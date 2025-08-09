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

    // Bu metod WebSocket vasitəsilə "/app/students" endpoint-inə göndərilən mesajları idarə edir.
    @MessageMapping("/students")
    public void handleStudentMessage(StudentDto studentDto) {
        // Məlumatı qəbul edir və loga yazır.
        log.info("Veb səhifədən WebSocket vasitəsilə yeni Student məlumatı gəldi: {}", studentDto);

        // Məlumatı verilənlər bazasına yazmaq üçün service layer-ə göndəririk.
        studentService.createStudent(studentDto);

        // Nümunə: Mesajı bütün qoşulmuş klientlərə geri göndərmək.
        messagingTemplate.convertAndSend("/topic/students/updates", studentDto);
    }
}

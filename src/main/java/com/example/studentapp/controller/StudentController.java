package com.example.studentapp.controller;


import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/students")
@Slf4j
public class StudentController {


    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @PostMapping
    public StudentDto createStudent(@Validated @RequestBody StudentDto studentDto) {
        return studentService.createStudent(studentDto);
    }

    @GetMapping
    public List<StudentDto> getAllStudents() {
        log.info("Fetching all students");
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentDto getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public StudentDto updateStudent(@PathVariable Long id, @RequestBody StudentDto studentDto) {
        return studentService.updateStudent(id, studentDto);
    }


    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

}



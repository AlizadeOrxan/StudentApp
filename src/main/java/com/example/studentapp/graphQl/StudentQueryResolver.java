package com.example.studentapp.graphQl;

import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.service.StudentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StudentQueryResolver {
    private final StudentService studentService;

    public StudentQueryResolver(StudentService studentService) {
        this.studentService = studentService;
    }


    @QueryMapping
    public List<StudentDto> getAllStudents() {
        return studentService.getAllStudents();
    }


    @QueryMapping
    public StudentDto getStudentById(@Argument Long id) {
        return studentService.getStudentById(id);
    }

}

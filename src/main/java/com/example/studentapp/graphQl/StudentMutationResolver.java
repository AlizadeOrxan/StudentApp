package com.example.studentapp.graphQl;

import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.service.StudentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class StudentMutationResolver {

    private final StudentService studentService;

    public StudentMutationResolver(StudentService studentService) {
        this.studentService = studentService;
    }


    public static class StudentInput {
        private String firstName;
        private String lastName;
        private int age;
        private String email;


        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @MutationMapping
    public StudentDto createStudent(@Argument StudentInput studentInput) {
        StudentDto studentDto = new StudentDto(null, studentInput.getFirstName(), studentInput.getLastName(), studentInput.getAge(), studentInput.getEmail());
        return studentService.createStudent(studentDto);
    }


    @MutationMapping
    public StudentDto updateStudent(@Argument Long id ,@Argument StudentInput studentInput) {
        StudentDto studentDto = new StudentDto(null, studentInput.getFirstName(), studentInput.getLastName(), studentInput.getAge(), studentInput.getEmail());
        return studentService.updateStudent(id, studentDto);
    }

    @MutationMapping
    public Boolean deleteStudent(@Argument Long id) {
        studentService.deleteStudent(id);
        return true;
    }





}

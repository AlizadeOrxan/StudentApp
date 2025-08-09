package com.example.studentapp.service.impl;


import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.entity.Student;
import com.example.studentapp.exception.StudentNotFoundException;
import com.example.studentapp.repos.StduentRepo;
import com.example.studentapp.service.StudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);


        private final StduentRepo studentRepo;
        private final KafkaTemplate<String , StudentDto> kafkaTemplate;

    public StudentServiceImpl(StduentRepo studentRepo, KafkaTemplate<String, StudentDto> kafkaTemplate) {
        this.studentRepo = studentRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${kafka.topic.student-created}")
    private String studentCreatedTopic;

    @Value("${kafka.topic.student-deleted}")
    private String studentDeletedTopic;

    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        log.info("Student created successfully {}, {}",studentDto.getFirstName(), studentDto.getLastName());

            Student student = new Student();
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setAge(studentDto.getAge());
            student.setEmail(studentDto.getEmail());

            Student saved =  studentRepo.save(student);

            studentDto.setId(saved.getId());

            kafkaTemplate.send(studentCreatedTopic, studentDto);
            log.info("Student created successfully and sent to Kafka  {}, {}",studentDto.getFirstName(), studentDto.getLastName());
            return new StudentDto(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getAge(), saved.getEmail());

    }

    @Override
    public StudentDto getStudentById(Long id) {
        Student student = studentRepo.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student not found"));

       return new StudentDto(id, student.getFirstName(), student.getLastName(), student.getAge(), student.getEmail());
    }


    @Override
    public List<StudentDto> getAllStudents() {
       return studentRepo.findAll().stream()
                .map(student -> new StudentDto(student.getId(), student.getFirstName(), student.getLastName(), student.getAge(), student.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto updateStudent(Long id , StudentDto studentDto) {
        Student exist = studentRepo.findById(id).orElseThrow(()-> new RuntimeException("Student not found"));

        exist.setFirstName(studentDto.getFirstName());
        exist.setLastName(studentDto.getLastName());
        exist.setAge(studentDto.getAge());
        exist.setEmail(studentDto.getEmail());

        Student updated = studentRepo.save(exist);

        return new StudentDto(id, updated.getFirstName(), updated.getLastName(), updated.getAge(), updated.getEmail());

        

    }

    @Override
    public void deleteStudent(Long id) {

        Student student = studentRepo.findById(id).orElseThrow(null);

        studentRepo.deleteById(id);
        log.warn("Student with id {} deleted successfully", id);

     //Melumati Kafkaya gondermeliyik

        StudentDto deleted = new StudentDto(student.getId(), student.getFirstName(), student.getLastName(), student.getAge(), student.getEmail());
        kafkaTemplate.send(studentDeletedTopic, deleted);
        log.info("Silinme haqqinda melumat Kafkaya gonderildi : {} ", id);


    }
}









//            try {
//                emailService.sendEmail(student.getEmail(), subject, body);
//                log.info("Registration email sent to {}", saved.getEmail());
//            }catch (RuntimeException e){
//                log.error("Failed to send registration email to {}: {}", saved.getEmail(), e.getMessage());
//
//            }

package controllerTest;


import com.example.studentapp.StudentAppApplication;
import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.entity.Student;
import com.example.studentapp.repos.StduentRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = StudentAppApplication.class)
@AutoConfigureMockMvc
public class StudentControllerIntegrationTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    StduentRepo studentRepo;



    private Student testStudent;
    private Student testStudent2;


    @BeforeEach
    void setUp(){
        studentRepo.deleteAll();


        testStudent = new Student(null,"Ali","Alizade",23,"a@mail.com");
        testStudent2 = new Student(null,"Murad","Memmedov",33,"m@mail.com");


        testStudent =  studentRepo.save(testStudent);
        testStudent2 =  studentRepo.save(testStudent2);
    }

    @AfterEach
    void cleanUp(){
        studentRepo.deleteAll();
    }



    @Test
    void createStudent_ReturnsCreatedStudent() throws Exception {

        StudentDto newStudent = new StudentDto(null, "Orxan", "Alizade", 24, "orxan@mail.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String studentJsonType = objectMapper.writeValueAsString(newStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJsonType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Orxan"))
                .andExpect(jsonPath("$.lastName").value("Alizade"))
                .andExpect(jsonPath("$.age").value(24))
                .andExpect(jsonPath("$.email").value("orxan@mail.com"));
    }


    @Test
    void getAllStudents_ReturnsAllStudents() throws Exception {

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value(testStudent.getFirstName()))
                .andExpect(jsonPath("$[1].firstName").value(testStudent2.getFirstName()));
    }


    @Test
    void getStudentById_ReturnsStudent() throws Exception {
        mockMvc.perform(get("/api/students/{id}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(testStudent.getFirstName()));
    }



    @Test
    void getStudentById_NotFound() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 999999))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAllStudents_Error() throws Exception {
        mockMvc.perform(get("/api/studentsss"))
                .andExpect(status().isNotFound());
    }


    @Test
    void studentDeleteById() throws Exception {

        mockMvc.perform(delete("/api/students/{id}", testStudent.getId()))
                .andExpect(status().isOk());

    }





















}

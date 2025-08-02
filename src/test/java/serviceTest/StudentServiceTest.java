package serviceTest;

import com.example.studentapp.dto.StudentDto;
import com.example.studentapp.entity.Student;
import com.example.studentapp.repos.StduentRepo;
import com.example.studentapp.service.EmailService;
import com.example.studentapp.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StduentRepo studentRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private StudentServiceImpl studentService;


    @Test
    void createStudents_Success() {

        StudentDto inputDto = new StudentDto(null,"Orxan","Alizade",24,"a@mail.com");

       Student savedStudent = new Student();

       savedStudent.setId(1L);
       savedStudent.setFirstName("Orxan");
       savedStudent.setLastName("Alizade");
       savedStudent.setAge(24);
       savedStudent.setEmail("a@mail.com");

       when(studentRepo.save(any(Student.class))).thenReturn(savedStudent);

       StudentDto result =  studentService.createStudent(inputDto);

       assertNotNull(result);
       assertEquals(inputDto.getFirstName(), result.getFirstName());
       assertEquals(inputDto.getLastName(), result.getLastName());
       assertEquals(inputDto.getAge(), result.getAge());
       assertEquals(inputDto.getEmail(), result.getEmail());

       verify(emailService,times(1)).sendEmail(eq("a@mail.com"),anyString(),anyString());


    }



    @Test
    void getStudents_Success() {
        Long id = 1L;
        Student student = new Student();
        student.setId(id);
        student.setFirstName("Orxan");
        student.setLastName("Alizade");
        student.setAge(24);
        student.setEmail("a@mail.com");

        when(studentRepo.findById(id)).thenReturn(Optional.of(student));

        StudentDto result = studentService.getStudentById(id);

        assertNotNull(result);
        assertEquals("Orxan", result.getFirstName());
        assertEquals("Alizade", result.getLastName());
        assertEquals(24, result.getAge());
        assertEquals("a@mail.com", result.getEmail());
    }


    @Test
    void getStudentById_NotFound_ThrowsException() {
        Long id = 2L;

        when(studentRepo.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,

                () -> studentService.getStudentById(id)
        );

        assertEquals("Student not found", exception.getMessage());
    }



    @Test
    void getAllStudents_Error() {

        when(studentRepo.findAll()).thenThrow(new RuntimeException("Database Error"));

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> {
            studentService.getAllStudents();
        });

        assertEquals("Database Error", thrown.getMessage());

        }



    @Test
    void deleteStudent_WhenStudentDoesNotExist_ShouldThrowException() {

        Long studentId = 100L;
        when(studentRepo.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () -> studentService.deleteStudent(studentId));

        verify(studentRepo).findById(studentId);
        verify(studentRepo, never()).deleteById(anyLong());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    public void UpdateStudent_Success() {
        // Given
        Long id = 1L;
        Student existingStudent = new Student(id, "Murad", "Alizade", 20, "m@mail.com");

        StudentDto newData = new StudentDto(id, "Ali", "Memmedov", 22, "a@mail.com");

        Student updatedStudent = new Student(id, "Senan", "Veliyev", 22, "b@mail.com");

        when(studentRepo.findById(id)).thenReturn(Optional.of(existingStudent));
        when(studentRepo.save(any(Student.class))).thenReturn(updatedStudent);

        // When
        StudentDto result = studentService.updateStudent(id, newData);

        // Then
        assertNotNull(result);
        assertEquals("Senan", result.getFirstName());
        assertEquals("Veliyev", result.getLastName());
        assertEquals(22, result.getAge());
        assertEquals("b@mail.com", result.getEmail());

        verify(studentRepo).findById(id);
        verify(studentRepo).save(existingStudent);
    }



    }



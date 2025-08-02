package com.example.studentapp.repos;

import com.example.studentapp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StduentRepo extends JpaRepository<Student, Long> {

}

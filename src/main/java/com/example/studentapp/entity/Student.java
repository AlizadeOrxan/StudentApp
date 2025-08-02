package com.example.studentapp.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    String firstName;

    String lastName;

    int age ;

    String email;




}

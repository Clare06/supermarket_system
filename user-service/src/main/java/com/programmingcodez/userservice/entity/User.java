package com.programmingcodez.userservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_TABLE")
public class User {


    @Id
    private String userName;
    private String password;
    private String email;
    private String name;
    private int age;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private boolean adminAcc;

    //    @Enumerated(EnumType.STRING)
//    private Role role;


}
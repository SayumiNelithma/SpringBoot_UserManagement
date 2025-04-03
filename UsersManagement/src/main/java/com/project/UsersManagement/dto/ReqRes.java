package com.project.UsersManagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.UsersManagement.entity.OurUsers;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String city;
    private String role;
    private String email;
    private String password;

    // Since OurUsers is already a MongoDB document, no change is needed for this field
    private OurUsers ourUsers; 

    // List of OurUsers can be returned in responses
    private List<OurUsers> ourUsersList;

}

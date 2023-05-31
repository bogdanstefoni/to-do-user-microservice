package com.bogdan.todouser.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TaskDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("taskDescription")
    private String taskDescription;


    @JsonProperty("userId")
    private Long userId;


}

package com.malugu.springboot_starter_project.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Response<T> {

    private Boolean status;
    private Integer code;
    private String message;
    private T data;
    private List<T> dataList;
    private List<String> errors;

    public Response() {
    }

    // Main constructor (all others delegate to this)
    public Response(Boolean status, Integer code, String message, T data,
                    List<T> dataList, List<String> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.dataList = dataList;
        this.errors = errors;
    }

    public Response(Boolean status, Integer code, String message, T data) {
        this(status, code, message, data, null, null);
    }

    public Response(Boolean status, Integer code, T data) {
        this(status, code, null, data, null, null);
    }

    public Response(Integer code) {
        this(false, code, null, null, null, null);
    }

    public Response(Integer code, Boolean status, T data) {
        this(status, code, null, data, null, null);
    }

    public Response(Integer code, Boolean status, String message, List<T> dataList) {
        this(status, code, message, null, dataList, null);
    }

    public Response(Integer code, String message) {
        this(false, code, message, null, null, null);
    }

    public Response(Integer code, String message, List<String> errors) {
        this(false, code, message, null, null, errors);
    }
}

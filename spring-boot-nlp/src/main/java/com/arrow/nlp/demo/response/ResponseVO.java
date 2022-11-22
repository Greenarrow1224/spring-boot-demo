package com.arrow.nlp.demo.response;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-11-18 15:54
 **/
@EqualsAndHashCode
public class ResponseVO<T> implements Serializable {

    private Integer code;


    private Boolean success;


    private String message;


    private T data;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return "ResponseVO(code=" + getCode() + ", success=" + getSuccess() + ", message=" + getMessage() + ", data=" + getData() + ")";
    }

    public Integer getCode() {
        return this.code;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public ResponseVO() {
        this.code = Integer.valueOf(HttpStatus.OK.value());
        this.success = Boolean.valueOf(true);
        this.message = "";
    }

    public ResponseVO(T data) {
        this.code = Integer.valueOf(HttpStatus.OK.value());
        this.success = Boolean.valueOf(true);
        this.message = "";
        this.data = data;
    }

    public ResponseVO(String message, T data) {
        this.code = Integer.valueOf(HttpStatus.OK.value());
        this.success = Boolean.valueOf(true);
        this.message = message;
        this.data = data;
    }

    public ResponseVO(HttpStatus status, boolean success, String message) {
        this.code = Integer.valueOf(status.value());
        this.success = Boolean.valueOf(success);
        this.message = message;
    }

    public ResponseVO(HttpStatus status, boolean success, String message, T data) {
        this.code = Integer.valueOf(status.value());
        this.success = Boolean.valueOf(success);
        this.message = message;
        this.data = data;
    }

    public ResponseVO(Integer code, boolean success, String message) {
        this.code = code;
        this.success = Boolean.valueOf(success);
        this.message = message;
    }

    public ResponseVO(Integer code, boolean success, String message, T data) {
        this.code = code;
        this.success = Boolean.valueOf(success);
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(Integer.valueOf(200), true, "success", data);
    }

    public static <T> ResponseVO<T> fail(Integer code, String message) {
        return new ResponseVO<>(code, false, message);
    }
}
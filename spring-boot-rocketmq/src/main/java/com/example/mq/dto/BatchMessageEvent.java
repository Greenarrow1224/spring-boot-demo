package com.example.mq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description
 * @date 2022-11-01 9:51
 **/
@Data
public class BatchMessageEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String message;
}

package com.arrow.nlp.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-12-02 17:38
 **/
public class ReadBodyUtil {


    /**
     * 读取HttpServletRequest的请求体
     *
     * @param httpServletRequest 请求
     * @return String
     */
    public static String readBodyString(HttpServletRequest httpServletRequest) {
        BufferedReader reader = null;
        String body = null;
        try {
            httpServletRequest.setCharacterEncoding("utf8");
            reader = httpServletRequest.getReader();
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            body = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return body;
    }
}

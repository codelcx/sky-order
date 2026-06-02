package com.sky.result;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.code = 1;
        r.msg = "success";
        return r;
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 1;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.code = 0;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

}

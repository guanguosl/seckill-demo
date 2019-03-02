package com.imooc.gsl.access;

import com.alibaba.fastjson.JSON;
import com.imooc.gsl.result.CodeMsg;
import com.imooc.gsl.result.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class WebUtils {
    public static void render(HttpServletResponse response, CodeMsg serverError) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            OutputStream outputStream= response.getOutputStream();
            Result<String> result = Result.error(serverError);
            String resultString= JSON.toJSONString(result);
            outputStream.write(resultString.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            // FIXME: 2019/3/3 todo
        }
    }
}

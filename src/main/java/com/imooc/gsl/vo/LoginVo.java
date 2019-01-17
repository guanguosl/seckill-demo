package com.imooc.gsl.vo;

import javax.validation.constraints.NotNull;

import com.imooc.gsl.validator.IsMobile;
import org.hibernate.validator.constraints.Length;




import java.io.Serializable;

public class LoginVo implements Serializable {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min=32)
    private String password;

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "LoginVo [mobile=" + mobile + ", password=" + password + "]";
    }
}

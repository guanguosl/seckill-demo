package com.imooc.gsl.component;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Component {
    public String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static final String salt = "1a2b3c4d";

    /**
     * 明文密码做一次MD5
     * @param inputPass
     *        文明密码
     * @return
     *        加密后密码
     */
    public String inputPassToFormPass(String inputPass) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 指定加密
     * @param inputPass
     * @param salt
     * @return
     */
    public String inputPassToFormPass(String inputPass, String salt) {
        String str = salt.charAt(0) + salt.charAt(1) + inputPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    public String formPassToDBPass(String formPass, String salt) {
        String str = salt.charAt(0) + salt.charAt(1) + formPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    public String inputPassToDBPass(String inputPass, String salt) {
        String formPass = this.inputPassToFormPass(inputPass);
        String dbPass = this.formPassToDBPass(formPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        MD5Component md5Component = new MD5Component();
        System.out.println(md5Component.inputPassToFormPass("123456"));
        System.out.println(md5Component.formPassToDBPass(md5Component.inputPassToFormPass("123456"), "1a2b3c4d"));
    }
}

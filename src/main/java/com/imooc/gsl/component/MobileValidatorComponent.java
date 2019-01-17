package com.imooc.gsl.component;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class MobileValidatorComponent {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public boolean isMobile(String mobileNumber) {
        if (StringUtils.isEmpty(mobileNumber)) {
            return false;
        }
        Matcher matcher = MOBILE_PATTERN.matcher(mobileNumber);
        return matcher.matches();
    }

    // FIXME: 2019/1/16 自测是非常必要的，要严谨

    @Test
    public void isMobileTest(){
        System.out.println(isMobile("15088695596"));
        System.out.println(isMobile("2508869559"));
    }


}

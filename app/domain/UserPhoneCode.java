package domain;

import play.data.validation.Constraints;

import java.io.Serializable;

/**
 * 发送手机验证码
 * Created by Sunny Wu on 16/3/14.
 * kakao china.
 */
public class UserPhoneCode implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String phone;

    public UserPhoneCode() {
    }

    @Override
    public String toString() {
        return "UserPhoneCode{" +
                "phone='" + phone + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserPhoneCode(String phone) {
        this.phone = phone;
    }
}

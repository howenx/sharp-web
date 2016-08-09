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

    //发送短信接口，增加了一个字断smsType(String类型)-> 值：register(注册)，reset(重置密码)，comm(其他)，不传时默认此字断值为comm
    private String smsType;

    public UserPhoneCode() {
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    @Override
    public String toString() {
        return "UserPhoneCode{" +
                "phone='" + phone + '\'' +
                ", smsType='" + smsType + '\'' +
                '}';
    }
}

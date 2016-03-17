package domain;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny Wu on 16/3/14.
 * kakao china.
 */
public class UserPhoneCode implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String phone;

    @Constraints.Required
    protected  String msg;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if ("".equals(msg)) {
            errors.add(new ValidationError("code", "This code is wrong"));
        }
        return errors.isEmpty() ? null : errors;
    }

    public UserPhoneCode() {
    }

    @Override
    public String toString() {
        return "UserPhoneCode{" +
                "phone='" + phone + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserPhoneCode(String phone, String msg) {

        this.phone = phone;
        this.msg = msg;
    }
}

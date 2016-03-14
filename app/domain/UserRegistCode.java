package domain;

import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny Wu on 16/3/14.
 * kakao china.
 */
public class UserRegistCode implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String phone;

    @Constraints.Required
    protected  String msg;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        Logger.error("msg:"+msg);
        if ("".equals(msg)) {
            errors.add(new ValidationError("msg", "This msg is wrong"));
        }
        return errors.isEmpty() ? null : errors;
    }

    public UserRegistCode() {
    }

    @Override
    public String toString() {
        return "UserRegistCode{" +
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

    public UserRegistCode(String phone, String msg) {

        this.phone = phone;
        this.msg = msg;
    }
}

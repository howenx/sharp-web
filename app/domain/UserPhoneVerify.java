package domain;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户手机号检测(用户注册,找回密码,绑定手机号)
 * Created by Sunny Wu on 16/3/15.
 * kakao china.
 */
public class UserPhoneVerify implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String phone;

    @Constraints.Required
    protected  String code;


    public List<ValidationError> validate() {
        String codeReg = "^[0-9a-zA-Z]{4}$";
        List<ValidationError> errors = new ArrayList<>();
        if (code.equals("-1") || code.matches(codeReg)) {
        }else errors.add(new ValidationError("code", "This code is wrong"));
        return errors.isEmpty() ? null : errors;
    }

    public UserPhoneVerify() {
    }

    public UserPhoneVerify(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserPhoneVerify{" +
                "phone='" + phone + '\'' +
                "code='" + code + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

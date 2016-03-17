package domain;

import play.data.validation.Constraints;

import java.io.Serializable;

/**
 * Created by Sunny Wu on 16/3/15.
 * kakao china.
 */
public class UserPhoneVerify implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String name;

    @Constraints.Required
    protected  String code;


//    public List<ValidationError> validate() {
//        List<ValidationError> errors = new ArrayList<>();
//        if (!code.equals("-1")) {
//            errors.add(new ValidationError("code", "This code is wrong"));
//        }
//        return errors.isEmpty() ? null : errors;
//    }

    public UserPhoneVerify() {
    }

    public UserPhoneVerify(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserPhoneVerify{" +
                "name='" + name + '\'' +
                "code='" + code + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

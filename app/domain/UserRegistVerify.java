package domain;

import play.data.validation.Constraints;

import java.io.Serializable;

/**
 * Created by Sunny Wu on 16/3/15.
 * kakao china.
 */
public class UserRegistVerify implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String phone;

    public UserRegistVerify() {

    }

    @Override
    public String toString() {
        return "UserRegistVerify{" +
                "phone='" + phone + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRegistVerify(String phone) {
        this.phone = phone;
    }
}

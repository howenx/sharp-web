package domain;

import play.data.validation.Constraints;

import java.io.Serializable;

/**
 * Created by Sunny Wu on 16/3/11.
 * kakao china.
 */
public class UserRegistInfo implements Serializable {

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String name;

    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    protected String phone;

    @Constraints.MaxLength(12)
    @Constraints.MinLength(6)
    @Constraints.Pattern("^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]{6,12}")
    protected String password;


    @Constraints.MaxLength(6)
    @Constraints.MinLength(6)
    @Constraints.Pattern("[0-9]{6}")
    protected  String code;

    public UserRegistInfo() {
    }

    public UserRegistInfo(String name, String phone, String password, String code) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserRegistInfo{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

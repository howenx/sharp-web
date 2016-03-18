package domain;

import java.io.Serializable;

/**
 * 用户信息
 * Created by sibyl.sun on 16/3/18.
 */
public class UserDTO implements Serializable {
    private String name;//用户昵称
    private String photo;//头像地址
    private String phoneNum;//手机号码
    private Integer couponsCount;//优惠券可用数量
    private String gender;//性别
    private String realYn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getCouponsCount() {
        return couponsCount;
    }

    public void setCouponsCount(Integer couponsCount) {
        this.couponsCount = couponsCount;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRealYn() {
        return realYn;
    }

    public void setRealYn(String realYn) {
        this.realYn = realYn;
    }
}

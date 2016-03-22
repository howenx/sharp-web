package domain;

import play.data.validation.Constraints;

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
    @Constraints.MaxLength(1)
    @Constraints.MinLength(1)
    @Constraints.Pattern("[A-Z]{1}")
    private String gender;//性别
    private String realYn;

    private String photoUrl;//头像字节流
    @Constraints.MaxLength(15)
    @Constraints.MinLength(2)
    @Constraints.Pattern("[a-zA-Z0-9\\u4e00-\\u9fa5]{2,15}")
    private String nickname;//用户昵称

    public UserDTO() {
    }

    public UserDTO(String name, String photo, String phoneNum, Integer couponsCount, String gender, String realYn, String photoUrl, String nickname) {
        this.name = name;
        this.photo = photo;
        this.phoneNum = phoneNum;
        this.couponsCount = couponsCount;
        this.gender = gender;
        this.realYn = realYn;
        this.photoUrl = photoUrl;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", couponsCount=" + couponsCount +
                ", gender='" + gender + '\'' +
                ", realYn='" + realYn + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

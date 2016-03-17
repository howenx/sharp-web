package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import play.data.validation.Constraints;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * 地址表单
 * Created by sibyl.sun on 16/3/16.
 */
public class AddressInfo implements Serializable {
    private Long addId;//用户地址主键
    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    private String tel;//电话
    @Constraints.MaxLength(15)
    @Constraints.MinLength(2)
 //   @Constraints.Pattern("[a-zA-Z0-9\\u4e00-\\u9fa5]")
    private String name;//姓名
    @Constraints.MaxLength(50)
    @Constraints.MinLength(1)
//    @Constraints.Pattern("[a-zA-Z0-9\\u4e00-\\u9fa5]")
    private String deliveryDetail;//配送详细地址
    private String idCardNum; //身份证
    private Boolean orDefault;//是否默认收获地址
    @Constraints.MinLength(1)
    private String province;
    private String city;
    private String area;
    private String area_code;
    private String city_code;
    private String province_code;

    public Long getAddId() {
        return addId;
    }

    public void setAddId(Long addId) {
        this.addId = addId;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDeliveryDetail() {
        return deliveryDetail;
    }

    public void setDeliveryDetail(String deliveryDetail) {
        this.deliveryDetail = deliveryDetail;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public Boolean getOrDefault() {
        return orDefault;
    }

    public void setOrDefault(Boolean orDefault) {
        this.orDefault = orDefault;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getProvince_code() {
        return province_code;
    }

    public void setProvince_code(String province_code) {
        this.province_code = province_code;
    }
    @Override
    public String toString() {
        return "AddressInfo{" +
                "addId="+addId+
                ",name='" + name +
                ", tel='" + tel +
                ", deliveryDetail='" + deliveryDetail +
                ", idCardNum='" + idCardNum +
                ", orDefault='" + orDefault +
                ", province='" + province +
                ", city='" + city +
                ", area='" + area +
                ", province_code='" + province_code +
                ", city_code='" + city_code +
                ", area_code='" + area_code +

                '}';
    }


}

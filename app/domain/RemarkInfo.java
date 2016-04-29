package domain;

import play.data.validation.Constraints;

import java.io.File;
import java.io.Serializable;

/**
 * Created by sibyl.sun on 16/4/28.
 */
public class RemarkInfo implements Serializable {

    private Long orderId;//订单ID
    @Constraints.MaxLength(500)
    @Constraints.MinLength(10)
    private String content;//评价内容
    @Constraints.Required
    private Integer grade;//评分1,2,3,4,5
    @Constraints.Required
    private String skuType;//商品类型
    @Constraints.Required
    private Long skuTypeId;//商品类型ID
    private File img1;//上传图片
    private File img2;//上传图片
    private File img3;//上传图片
    private File img4;//上传图片
    private File img5;//上传图片

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }

    public Long getSkuTypeId() {
        return skuTypeId;
    }

    public void setSkuTypeId(Long skuTypeId) {
        this.skuTypeId = skuTypeId;
    }

    public File getImg1() {
        return img1;
    }

    public void setImg1(File img1) {
        this.img1 = img1;
    }

    public File getImg2() {
        return img2;
    }

    public void setImg2(File img2) {
        this.img2 = img2;
    }

    public File getImg3() {
        return img3;
    }

    public void setImg3(File img3) {
        this.img3 = img3;
    }

    public File getImg4() {
        return img4;
    }

    public void setImg4(File img4) {
        this.img4 = img4;
    }

    public File getImg5() {
        return img5;
    }

    public void setImg5(File img5) {
        this.img5 = img5;
    }
}

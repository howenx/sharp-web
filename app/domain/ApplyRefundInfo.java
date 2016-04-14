package domain;

import play.data.validation.Constraints;

import java.io.File;
import java.io.Serializable;

/**
 * 申请退货
 * Created by sibyl.sun on 16/3/18.
 */
public class ApplyRefundInfo implements Serializable {

    @Constraints.Required
    private Long        orderId;//订单ID
    private Long        splitOrderId;//子订单ID
    @Constraints.Required
    private Long        skuId;//商品ID
    @Constraints.Required
    private String      reason;//申请退款原因
    @Constraints.Required
    private Integer     amount;//申请退款数量
    @Constraints.Required
    private String      contactName;//联系人姓名
    @Constraints.MaxLength(11)
    @Constraints.MinLength(11)
    @Constraints.Pattern("[1][345678]\\d{9}")
    private String      contactTel;//联系人电话
    private File         refundImg1;//退款上传图片
    private File         refundImg2;//退款上传图片
    private File         refundImg3;//退款上传图片

    public ApplyRefundInfo() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSplitOrderId() {
        return splitOrderId;
    }

    public void setSplitOrderId(Long splitOrderId) {
        this.splitOrderId = splitOrderId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public File getRefundImg1() {
        return refundImg1;
    }

    public void setRefundImg1(File refundImg1) {
        this.refundImg1 = refundImg1;
    }

    public File getRefundImg2() {
        return refundImg2;
    }

    public void setRefundImg2(File refundImg2) {
        this.refundImg2 = refundImg2;
    }

    public File getRefundImg3() {
        return refundImg3;
    }

    public void setRefundImg3(File refundImg3) {
        this.refundImg3 = refundImg3;
    }

    public ApplyRefundInfo(Long orderId, Long splitOrderId, Long skuId, String reason, Integer amount, String contactName, String contactTel, File refundImg1, File refundImg2, File refundImg3) {

        this.orderId = orderId;
        this.splitOrderId = splitOrderId;
        this.skuId = skuId;
        this.reason = reason;
        this.amount = amount;
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.refundImg1 = refundImg1;
        this.refundImg2 = refundImg2;
        this.refundImg3 = refundImg3;
    }

    @Override
    public String toString() {
        return "RefundInfo{" +
                "orderId=" + orderId +
                ", splitOrderId=" + splitOrderId +
                ", skuId=" + skuId +
                ", reason='" + reason + '\'' +
                ", amount=" + amount +
                ", contactName='" + contactName + '\'' +
                ", contactTel='" + contactTel + '\'' +
                ", refundImg1=" + refundImg1 +
                ", refundImg2=" + refundImg2 +
                ", refundImg3=" + refundImg3 +
                '}';
    }
}

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
    private String      reason;//申请退款原因

    private String      contactName;//联系人姓名

    private String      contactTel;//联系人电话

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    @Override
    public String toString() {
        return "RefundInfo{" +
                "orderId=" + orderId +
                ", splitOrderId=" + splitOrderId +
                ", reason='" + reason + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactTel='" + contactTel + '\'' +
                '}';
    }
}

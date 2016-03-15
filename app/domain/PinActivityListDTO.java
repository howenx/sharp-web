package domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import util.MoneySerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 用于返回拼购活动的DTO
 * Created by howen on 16/2/17.
 */
public class PinActivityListDTO implements Serializable {

    private Long            pinActiveId;    //拼购活动ID
    private String          pinUrl;         //此团的分享短连接
    @JsonIgnore
    private Long            pinId;          //拼购ID
    @JsonIgnore
    private Long            masterUserId;   //团长用户ID
    private Integer         personNum;      //拼购人数
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal      pinPrice;       //拼购价格
    private Integer         joinPersons;    //已参加活动人数
    @JsonIgnore
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Timestamp       createAt;       //发起时间
    private String          status;         //状态    状态 Y-正常 N－取消 C－完成
    @JsonIgnore
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Timestamp       endAt;          //截止时间
    @JsonIgnore
    private String          pay;//是否新开团,normal,new
    private Long            endCountDown;//结束倒计时

    //拼购商品数据
    private String          pinImg;      //生成后列表图
    private String          pinSkuUrl;   //拼购商品链接
    private String          pinTitle;    //拼购商品标题

    private Integer          orMaster;       //订单支付成功后需要的用户类型,团长: 1,团员:0

    private Long            orderId;//订单ID


    @JsonIgnore
    private List<PinUser>   pinUsers;       //参与拼购活动的用于


    public PinActivityListDTO() {
    }

    public PinActivityListDTO(Long pinActiveId, String pinUrl, Long pinId, Long masterUserId, Integer personNum, BigDecimal pinPrice, Integer joinPersons, Timestamp createAt, String status, Timestamp endAt, String pay, Long endCountDown, String pinImg, String pinSkuUrl, String pinTitle, Integer orMaster, Long orderId, List<PinUser> pinUsers) {
        this.pinActiveId = pinActiveId;
        this.pinUrl = pinUrl;
        this.pinId = pinId;
        this.masterUserId = masterUserId;
        this.personNum = personNum;
        this.pinPrice = pinPrice;
        this.joinPersons = joinPersons;
        this.createAt = createAt;
        this.status = status;
        this.endAt = endAt;
        this.pay = pay;
        this.endCountDown = endCountDown;
        this.pinImg = pinImg;
        this.pinSkuUrl = pinSkuUrl;
        this.pinTitle = pinTitle;
        this.orMaster = orMaster;
        this.orderId = orderId;
        this.pinUsers = pinUsers;
    }

    public Long getPinActiveId() {
        return pinActiveId;
    }

    public void setPinActiveId(Long pinActiveId) {
        this.pinActiveId = pinActiveId;
    }

    public String getPinUrl() {
        return pinUrl;
    }

    public void setPinUrl(String pinUrl) {
        this.pinUrl = pinUrl;
    }

    public Long getPinId() {
        return pinId;
    }

    public void setPinId(Long pinId) {
        this.pinId = pinId;
    }

    public Long getMasterUserId() {
        return masterUserId;
    }

    public void setMasterUserId(Long masterUserId) {
        this.masterUserId = masterUserId;
    }

    public Integer getPersonNum() {
        return personNum;
    }

    public void setPersonNum(Integer personNum) {
        this.personNum = personNum;
    }

    public BigDecimal getPinPrice() {
        return pinPrice;
    }

    public void setPinPrice(BigDecimal pinPrice) {
        this.pinPrice = pinPrice;
    }

    public Integer getJoinPersons() {
        return joinPersons;
    }

    public void setJoinPersons(Integer joinPersons) {
        this.joinPersons = joinPersons;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getEndAt() {
        return endAt;
    }

    public void setEndAt(Timestamp endAt) {
        this.endAt = endAt;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public Long getEndCountDown() {
        return endCountDown;
    }

    public void setEndCountDown(Long endCountDown) {
        this.endCountDown = endCountDown;
    }

    public String getPinImg() {
        return pinImg;
    }

    public void setPinImg(String pinImg) {
        this.pinImg = pinImg;
    }

    public String getPinSkuUrl() {
        return pinSkuUrl;
    }

    public void setPinSkuUrl(String pinSkuUrl) {
        this.pinSkuUrl = pinSkuUrl;
    }

    public String getPinTitle() {
        return pinTitle;
    }

    public void setPinTitle(String pinTitle) {
        this.pinTitle = pinTitle;
    }

    public Integer getOrMaster() {
        return orMaster;
    }

    public void setOrMaster(Integer orMaster) {
        this.orMaster = orMaster;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<PinUser> getPinUsers() {
        return pinUsers;
    }

    public void setPinUsers(List<PinUser> pinUsers) {
        this.pinUsers = pinUsers;
    }

    @Override
    public String toString() {
        return "PinActivityListDTO{" +
                "pinActiveId=" + pinActiveId +
                ", pinUrl='" + pinUrl + '\'' +
                ", pinId=" + pinId +
                ", masterUserId=" + masterUserId +
                ", personNum=" + personNum +
                ", pinPrice=" + pinPrice +
                ", joinPersons=" + joinPersons +
                ", createAt=" + createAt +
                ", status='" + status + '\'' +
                ", endAt=" + endAt +
                ", pay='" + pay + '\'' +
                ", endCountDown=" + endCountDown +
                ", pinImg='" + pinImg + '\'' +
                ", pinSkuUrl='" + pinSkuUrl + '\'' +
                ", pinTitle='" + pinTitle + '\'' +
                ", orMaster=" + orMaster +
                ", orderId=" + orderId +
                ", pinUsers=" + pinUsers +
                '}';
    }
}

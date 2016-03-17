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
public class PinActivityDTO implements Serializable {

    private Long            pinActiveId;    //拼购活动ID
    private String          pinUrl;         //此团的分享短连接
    private Long            pinId;          //拼购ID
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
    private String          pay;//是否新开团,normal,new
    private Long            endCountDown;//结束倒计时

    //拼购商品数据
    private String          pinImg;      //生成后列表图
    private String          pinSkuUrl;   //拼购商品链接
    private String          pinTitle;    //拼购商品标题



    private String          userType;       //订单支付成功后需要的用户类型,团长: master,团员:ordinary

    private Integer         orJoinActivity; //是否参团,0:未参团,1:参团

    @JsonIgnore
    private Integer         orRestrictAmount;//是否超出限购数量,0:可以继续下单,1:当前已经成功购买数量等于限购数量

    //inv
    private     String              invArea;            //库存区域区分：'B'保税区仓库发货，‘Z’韩国直邮
    private     String              invCustoms;         //报关单位
    private     String              invAreaNm;          //仓储地名称
    private     String              postalTaxRate;      //税率,百分比
    private     String              postalStandard;     //关税收费标准
    private     Long                skuId;              //商品ID
    private     String              skuType;            //商品类型
    private     Long                skuTypeId;          //商品类型所对应的ID
    private     Long                pinTieredPriceId;   //拼购价格ID


    private List<PinUser>   pinUsers;       //参与拼购活动的用于


    public PinActivityDTO() {
    }

    public PinActivityDTO(Long pinActiveId, String pinUrl, Long pinId, Long masterUserId, Integer personNum, BigDecimal pinPrice, Integer joinPersons, Timestamp createAt, String status, Timestamp endAt, String pay, Long endCountDown, String pinImg, String pinSkuUrl, String pinTitle, String userType, Integer orJoinActivity, Integer orRestrictAmount, String invArea, String invCustoms, String invAreaNm, String postalTaxRate, String postalStandard, Long skuId, String skuType, Long skuTypeId, Long pinTieredPriceId, List<PinUser> pinUsers) {
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
        this.userType = userType;
        this.orJoinActivity = orJoinActivity;
        this.orRestrictAmount = orRestrictAmount;
        this.invArea = invArea;
        this.invCustoms = invCustoms;
        this.invAreaNm = invAreaNm;
        this.postalTaxRate = postalTaxRate;
        this.postalStandard = postalStandard;
        this.skuId = skuId;
        this.skuType = skuType;
        this.skuTypeId = skuTypeId;
        this.pinTieredPriceId = pinTieredPriceId;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getOrJoinActivity() {
        return orJoinActivity;
    }

    public void setOrJoinActivity(Integer orJoinActivity) {
        this.orJoinActivity = orJoinActivity;
    }

    public Integer getOrRestrictAmount() {
        return orRestrictAmount;
    }

    public void setOrRestrictAmount(Integer orRestrictAmount) {
        this.orRestrictAmount = orRestrictAmount;
    }

    public String getInvArea() {
        return invArea;
    }

    public void setInvArea(String invArea) {
        this.invArea = invArea;
    }

    public String getInvCustoms() {
        return invCustoms;
    }

    public void setInvCustoms(String invCustoms) {
        this.invCustoms = invCustoms;
    }

    public String getInvAreaNm() {
        return invAreaNm;
    }

    public void setInvAreaNm(String invAreaNm) {
        this.invAreaNm = invAreaNm;
    }

    public String getPostalTaxRate() {
        return postalTaxRate;
    }

    public void setPostalTaxRate(String postalTaxRate) {
        this.postalTaxRate = postalTaxRate;
    }

    public String getPostalStandard() {
        return postalStandard;
    }

    public void setPostalStandard(String postalStandard) {
        this.postalStandard = postalStandard;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public Long getPinTieredPriceId() {
        return pinTieredPriceId;
    }

    public void setPinTieredPriceId(Long pinTieredPriceId) {
        this.pinTieredPriceId = pinTieredPriceId;
    }

    public List<PinUser> getPinUsers() {
        return pinUsers;
    }

    public void setPinUsers(List<PinUser> pinUsers) {
        this.pinUsers = pinUsers;
    }

    @Override
    public String toString() {
        return "PinActivityDTO{" +
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
                ", userType='" + userType + '\'' +
                ", orJoinActivity=" + orJoinActivity +
                ", orRestrictAmount=" + orRestrictAmount +
                ", invArea='" + invArea + '\'' +
                ", invCustoms='" + invCustoms + '\'' +
                ", invAreaNm='" + invAreaNm + '\'' +
                ", postalTaxRate='" + postalTaxRate + '\'' +
                ", postalStandard='" + postalStandard + '\'' +
                ", skuId=" + skuId +
                ", skuType='" + skuType + '\'' +
                ", skuTypeId=" + skuTypeId +
                ", pinTieredPriceId=" + pinTieredPriceId +
                ", pinUsers=" + pinUsers +
                '}';
    }
}

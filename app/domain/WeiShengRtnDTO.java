package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 威盛物流
 * Created by sibyl.sun on 16/8/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeiShengRtnDTO {
    @JsonProperty(value = "Status")
    private String status;
    @JsonProperty(value = "Createtime")
    private String createtime;
    @JsonProperty(value = "ExpressNo")
    private String expressNo;
    @JsonProperty(value = "Remark")
    private String remark;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "WeiShengRtnDTO{" +
                "status='" + status + '\'' +
                ", createtime='" + createtime + '\'' +
                ", expressNo='" + expressNo + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

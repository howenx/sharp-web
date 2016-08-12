package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 威盛物流
 * Created by sibyl.sun on 16/8/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeiShengDTO {
    private String rtnCode;
    private String rtnDesc;
    private List<WeiShengRtnDTO> rtnList;

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnDesc() {
        return rtnDesc;
    }

    public void setRtnDesc(String rtnDesc) {
        this.rtnDesc = rtnDesc;
    }

    public List<WeiShengRtnDTO> getRtnList() {
        return rtnList;
    }

    public void setRtnList(List<WeiShengRtnDTO> rtnList) {
        this.rtnList = rtnList;
    }

    @Override
    public String toString() {
        return "WeiShengDTO{" +
                "rtnCode='" + rtnCode + '\'' +
                ", rtnDesc='" + rtnDesc + '\'' +
                ", rtnList=" + rtnList +
                '}';
    }
}

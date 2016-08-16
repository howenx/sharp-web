package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 物流展示数据
 * Created by sibyl.sun on 16/4/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogisticsDTO {
    private String state;
    private List<LogisticsDataDTO> data;
    private String expressName;
    private String expressNum;
    private String message;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<LogisticsDataDTO> getData() {
        return data;
    }

    public void setData(List<LogisticsDataDTO> data) {
        this.data = data;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressNum() {
        return expressNum;
    }

    public void setExpressNum(String expressNum) {
        this.expressNum = expressNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogisticsDTO{" +
                "state='" + state + '\'' +
                ", data=" + data +
                ", expressName='" + expressName + '\'' +
                ", expressNum='" + expressNum + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

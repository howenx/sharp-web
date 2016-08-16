package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 物流展示数据data
 * Created by sibyl.sun on 16/4/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogisticsDataDTO {
    private String  time;
    private String context;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "LogisticsDataDTO{" +
                "time='" + time + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}

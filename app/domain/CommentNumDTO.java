package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by sibyl.sun on 16/4/27.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentNumDTO implements Serializable {
    private Integer remarkCount;//评论数
    private BigDecimal remarkRate;//好评率

    public Integer getRemarkCount() {
        return remarkCount;
    }

    public void setRemarkCount(Integer remarkCount) {
        this.remarkCount = remarkCount;
    }

    public BigDecimal getRemarkRate() {
        return remarkRate;
    }

    public void setRemarkRate(BigDecimal remarkRate) {
        this.remarkRate = remarkRate;
    }
}

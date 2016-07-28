package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * slider
 * Created by howen on 15/10/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SliderNav implements Serializable{

    @JsonIgnore
    private String img;
    private String  itemTarget;
    private String  targetType;
    private String  url;
    private String navText;//导航文字显示

    public String getItemTarget() {
        return itemTarget;
    }

    public void setItemTarget(String itemTarget) {
        this.itemTarget = itemTarget;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNavText() {
        return navText;
    }

    public void setNavText(String navText) {
        this.navText = navText;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "SliderNav{" +
                "itemTarget='" + itemTarget + '\'' +
                ", targetType='" + targetType + '\'' +
                ", url='" + url + '\'' +
                ", navText='" + navText + '\'' +
                '}';
    }
}

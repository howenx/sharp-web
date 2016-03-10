package domain;

import java.io.Serializable;
import java.util.List;

/**
 * 用于映射主页json
 * Created by howen on 16/3/9.
 */
public class IndexMap implements Serializable{

    private Integer msgRemind;
    private Message message;
    private List<Slider> slider;
    private List<Theme> theme;
    private Integer page_count;

    public IndexMap() {
    }

    public IndexMap(Integer msgRemind, Message message, List<Slider> slider, List<Theme> theme, Integer page_count) {
        this.msgRemind = msgRemind;
        this.message = message;
        this.slider = slider;
        this.theme = theme;
        this.page_count = page_count;
    }

    public Integer getMsgRemind() {
        return msgRemind;
    }

    public void setMsgRemind(Integer msgRemind) {
        this.msgRemind = msgRemind;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Slider> getSlider() {
        return slider;
    }

    public void setSlider(List<Slider> slider) {
        this.slider = slider;
    }

    public List<Theme> getTheme() {
        return theme;
    }

    public void setTheme(List<Theme> theme) {
        this.theme = theme;
    }

    public Integer getPage_count() {
        return page_count;
    }

    public void setPage_count(Integer page_count) {
        this.page_count = page_count;
    }


    @Override
    public String toString() {
        return "IndexMap{" +
                "msgRemind=" + msgRemind +
                ", message=" + message +
                ", slider=" + slider +
                ", theme=" + theme +
                ", page_count=" + page_count +
                '}';
    }
}

package domain;

import java.io.Serializable;

/**
 * 用于保存微信用户信息
 * Created by howen on 16/3/30.
 */
public class WechatVo implements Serializable {
    private String openId;
    private String accessToken;

    public WechatVo() {
    }

    public WechatVo(String openId, String accessToken) {
        this.openId = openId;
        this.accessToken = accessToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "WechatVo{" +
                "openId='" + openId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}

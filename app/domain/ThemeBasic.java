package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * 主题基础信息
 * Created by howen on 16/1/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThemeBasic implements Serializable {

    private Long                themeId;   	            //主题ID
    private String              themeImg;               //主题图
    private String              masterItemTag;	        //如果是主打宣传商品，会需要tag json串
    @JsonIgnore
    private String              masterItemTagAndroid;	//如果是主打宣传商品，会需要tag json串
    private List<ThemeItem>     themeItemList;          //主题中的商品数据

    private String              title;                  //主题的标题
    @JsonIgnore
    private List<String>        imgList;                //暂存的所有图片



    public ThemeBasic() {
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public String getThemeImg() {
        return themeImg;
    }

    public void setThemeImg(String themeImg) {
        this.themeImg = themeImg;
    }

    public String getMasterItemTag() {
        return masterItemTag;
    }

    public void setMasterItemTag(String masterItemTag) {
        this.masterItemTag = masterItemTag;
    }

    public String getMasterItemTagAndroid() {
        return masterItemTagAndroid;
    }

    public void setMasterItemTagAndroid(String masterItemTagAndroid) {
        this.masterItemTagAndroid = masterItemTagAndroid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ThemeItem> getThemeItemList() {
        return themeItemList;
    }

    public void setThemeItemList(List<ThemeItem> themeItemList) {
        this.themeItemList = themeItemList;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public ThemeBasic(Long themeId, String themeImg, String masterItemTag, String masterItemTagAndroid, List<ThemeItem> themeItemList) {
        this.themeId = themeId;
        this.themeImg = themeImg;
        this.masterItemTag = masterItemTag;
        this.masterItemTagAndroid = masterItemTagAndroid;
        this.themeItemList = themeItemList;
    }

    @Override
    public String toString() {
        return "ThemeBasic{" +
                "themeId=" + themeId +
                ", themeImg='" + themeImg + '\'' +
                ", masterItemTag='" + masterItemTag + '\'' +
                ", masterItemTagAndroid='" + masterItemTagAndroid + '\'' +
                ", themeItemList=" + themeItemList +
                ", title='" + title + '\'' +
                ", imgList=" + imgList +
                '}';
    }
}

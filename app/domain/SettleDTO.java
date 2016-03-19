package domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sibyl.sun on 16/3/19.
 */
public class SettleDTO implements Serializable {
    private String invCustoms;  //保税区
    private String invArea;//保税区
    private String invAreaNm;//保税区
    private List<CartDto> cartDtos;

    public String getInvCustoms() {
        return invCustoms;
    }

    public void setInvCustoms(String invCustoms) {
        this.invCustoms = invCustoms;
    }

    public String getInvArea() {
        return invArea;
    }

    public void setInvArea(String invArea) {
        this.invArea = invArea;
    }

    public String getInvAreaNm() {
        return invAreaNm;
    }

    public void setInvAreaNm(String invAreaNm) {
        this.invAreaNm = invAreaNm;
    }

    public List<CartDto> getCartDtos() {
        return cartDtos;
    }

    public void setCartDtos(List<CartDto> cartDtos) {
        this.cartDtos = cartDtos;
    }
}

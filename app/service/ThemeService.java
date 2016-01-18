package service;

import com.fasterxml.jackson.databind.JsonNode;
import domain.Slider;
import domain.Theme;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * For homepage theme list display function.
 * Created by howen on 15/10/26.
 */
public interface ThemeService {

    Optional<List<Theme>> getThemes(int pageSize, int offset);

    Optional<JsonNode> getThemeList(Long itemId);

    Optional<List<Slider>> getSlider();

    Optional<Map<String,Object>> getItemDetail(Long id, Long skuId) ;

    Optional<Map<String,Object>> getItemDetailWeb(Long id, Long skuId) ;
}

package controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.util.MapUtils;
import com.google.common.base.Optional;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import controllers.base.BaseController;
import controllers.base.secure.Secure;
import models.Item;
import models.StarItemSetting;
import play.data.validation.Min;
import play.mvc.With;
import vos.ItemSearchResult;
import vos.ItemSearchVo;

/**
 * 
 * 商品列表相关的路由
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月23日 下午3:20:56
 */
@With(Secure.class)
public class ListController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ListController.class);

    /**
     * 
     * 商品搜索页的列表页
     * 
     * @param pageNo
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:20:36
     */
    public static void search(@Min(1) int pageNo) {
        // int ,int ,String kw,float ,float price_end,boolean dp_use,boolean pr_use,long
        // brandId,long cateId
        if (pageNo == 0) {
            pageNo = 1;
        }

        Map<String, String> params = request.params.allSimple();
        // 查询条件归总
        ItemSearchVo vo = ItemSearchVo.newInstance();
        vo.pageNo = pageNo;
        vo.pageSize = MapUtils.getIntValue(params, "pageSize", 20);

        String kw = Optional.fromNullable(params.get("kw")).or("");
        vo.kw(kw);
        vo.price_start = MapUtils.getDoubleValue(params, "price_start", 0);
        vo.price_end = MapUtils.getDoubleValue(params, "price_end", 0);
        vo.brandId = MapUtils.getIntValue(params, "brandId", 0);
        vo.cateId = MapUtils.getIntValue(params, "cateId", 0);
        vo.pr_use = MapUtils.getBooleanValue(params, "pr_use", false);
        vo.dp_use = MapUtils.getBooleanValue(params, "dp_use", false);

        log.info("Search conditions={}", vo.toString());

        StarItemSetting starItem = StarItemSetting.findDefaultByCache();
        renderArgs.put("starItem", starItem);
        ItemSearchResult search_result = Item.findByVoPage(vo);
        renderArgs.put("search_result", search_result);
        // 当前查询条件
        renderArgs.put("vo", vo);
        render();
    }

    /**
     * 
     * 精选商品列表
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:22:17
     */
    public static void best() {
        render();
    }

}
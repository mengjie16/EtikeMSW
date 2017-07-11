package controllers;

import controllers.base.BaseController;
import controllers.base.secure.Secure;
import models.FreightTemp;
import models.Region;
import play.mvc.With;

/**
 * 
 * 帮助中心
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月31日 下午5:14:23
 */
@With(Secure.class)
public class ToolController extends BaseController {

    /**
     * 
     * 动态运费模版计算page show
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月31日 下午5:17:46
     */
    public static void freightCount(long tempId) {
        if(tempId > 0){
            FreightTemp freightTemp =FreightTemp.findById(tempId);
            renderArgs.put("freightTemp", freightTemp);
            renderArgs.put("regionMap", Region.findLevel2ShortIdvsName());
        }
        render();
    }
    
    
    public static void consignTrade(long tradeId){
        render();
    }
}

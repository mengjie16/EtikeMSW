package controllers;

import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.util.MixHelper;

import play.mvc.With;
import controllers.base.BaseController;
import controllers.base.secure.Secure;

/**
 * 
 * 供应商的门户相关的路由
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月23日 下午3:19:06
 */
@With(Secure.class)
public class ShopController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ShopController.class);

    /**
     * 
     * 店铺首页
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:18:55
     */
    public static void home() {
        render();
    }

}
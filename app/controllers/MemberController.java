package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.base.BaseController;
import controllers.base.secure.Secure;
import play.mvc.With;

/**
 * 
 * 会员相关的路由
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月23日 下午3:10:49
 */
@With(Secure.class)
public class MemberController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    /**
     * 
     * 会员等级体系
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:10:34
     */
    public static void grade() {
        render();
    }

}

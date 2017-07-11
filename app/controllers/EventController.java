package controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import models.ActivityPageSetting;
import models.Album;
import models.AlbumItem;
import models.H5ActivityPageSetting;
import models.Item;
import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
import com.aton.util.MixHelper;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.mvc.With;
import vos.ItemVo;
import controllers.base.BaseController;
import controllers.base.secure.Secure;

/**
 * 
 * 营销事件，优惠活动等
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月23日 下午3:05:17
 */
@With(Secure.class)
public class EventController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    /**
     * 
     * 本月优惠
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:09:31
     */
    public static void month() {
        render();
    }

    /**
     * 
     * 活动页
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:09:46
     */
    public static void activity(@Required String id) {
        ActivityPageSetting aps = ActivityPageSetting.findById(id);
        if (aps != null && MixHelper.isNotEmpty(aps.bottomItemIds)) {
            aps.bottomItems = aps.bottomItemIds.stream().map(i -> ItemVo.valueOfBase(i)).filter(v -> v != null).collect(Collectors.toList());
        }
        renderArgs.put("aps", aps);
        render();
    }

    /**
     * 查询H5活动详情
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午4:56:49
     */
    public static void queryH5ActivitySettingDetail(String id) {
        H5ActivityPageSetting h5activityPageSet = H5ActivityPageSetting.findById(id);
        h5activityPageSet.toDetail();
        renderJson(h5activityPageSet);
    }

    /**
     * 
     * 报道页
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:09:46
     */
    public static void report() {
        render();
    }

}

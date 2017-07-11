package controllers;

import java.util.List;
import java.util.Map;

import models.Album;
import models.AlbumItem;
import models.AuthManage;
import models.Brand;
import models.Item;
import models.ItemCate;
import models.ItemPv;
import models.Region;
import models.SupplierFreightTemp;
import models.User;
import models.WechatUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.taobao.api.internal.toplink.embedded.websocket.util.StringUtil;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.With;
import utils.WechatHelper;
import vos.ItemSearchVo;
import vos.ItemVo;
import controllers.annotations.OpenInWeixinRequiredSupport;
import controllers.annotations.WechatSupport;
import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.constants.BizConstants;
import enums.constants.RegexConstants;

/**
 * 
 * 商品相关的路由
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月23日 下午3:10:04
 */
@With(Secure.class)
public class ItemController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    /**
     * 商品详情页
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 上午1:40:01
     */
    public static void detail(@Required @Min(0) long id, long albumId) {
        handleWrongInput(true);
        ItemVo item = Item.itemDetailCacheById(id);
        if (item == null) { // 暂时这样处理
            renderFailedJson(ReturnCode.FAIL);
        }
        // 当前宝贝视图详情
        renderArgs.put("item", item);
        // 专辑宝贝信息
        AlbumItem albumItem = Album.findAbumItemByIdWithAlbumId(albumId, id);
        renderArgs.put("albumItem", albumItem);
        // 右侧关联宝贝
        List<ItemVo> listItem = ItemVo.valueOfItemList(Item.findAlisaItemBySupplierId(item.supplierId));
        renderArgs.put("corrItems", listItem);
        // 所有省份信息
        List<Region> provinces = Region.findLevel2ShortWithCache();
        renderArgs.put("provinces", provinces);
        // 访问量统计
        String cookieKey = Secure.getValidAccessPvCookieKey();
        if (!Strings.isNullOrEmpty(cookieKey)) {
            boolean pvValid = ItemPv.checkIsValidAccessPv(cookieKey + id);
            if (pvValid) {
                ItemPv.incrementPc(id, 1);
                if (albumId > 0) {
                    ItemPv.incrementPcInAlbum(id, 1);
                }
            }
        }
        render("./ItemController/detail.html");
    }

    /**
     * 根据关键字查找宝贝类目信息
     *
     * @param keyWord
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 下午1:50:06
     */
    public static void itemCateFindByKeyWord(
        @Required @MinSize(1) @MaxSize(20) @Match(RegexConstants.ABC_NUM_CN) String keyWord) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 查询宝贝类目信息
        List<ItemCate> list = ItemCate.findByKeyWord(keyWord);
        List<Map> listMap = Lists.transform(list, new Function<ItemCate, Map>() {

            @Override
            public Map apply(ItemCate obj) {
                if (obj != null) {
                    return ImmutableMap.of("id", obj.id, "name", obj.name);
                }
                return null;
            }
        });
        renderJson(listMap);
    }

    /**
     * 条件查询商品信息
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年6月15日 下午5:44:34
     */
    public static void queryItemByVo(@Required ItemSearchVo vo) {
        Page<Item> page = Item.findItemPage(vo);
        renderPageJson(page.items, page.totalCount);
    }

    /**
     * 手机商品详情页
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 上午1:40:01
     */
    @OpenInWeixinRequiredSupport(CheckAuth = true)
    @WechatSupport
    public static void m_detail(@Required @Min(1) long id, @Required @Min(1) long albumId) {
        handleWrongInput(true);
        boolean checkAuth = Optional.fromNullable(renderArgs.get("checkAuth", Boolean.class)).or(true);
        if (checkAuth) {
            String openId = session.get(BizConstants.USER_AUTH_OPENID);
            log.info("user openId :{}", openId);
            // 跳转微信授权
            if (Strings.isNullOrEmpty(openId)) {
                log.info("user will be auth !");
                String redirectUrl = WechatHelper.getAuthToTargetUrl(request.url, null, "snsapi_userinfo");
                redirect(redirectUrl);
            }
            // 无权限
            if (!WechatUser.checkRoleInMoudle("album", albumId + "", openId)) {
                log.info("user no auth role:{},wait manage auth ~", openId);
                renderArgs.put("moudle", "album");
                renderArgs.put("moudleId", albumId + "");
                renderTemplate("Application/loading.html");
            }
        }
        ItemVo item = Item.itemDetailCacheById(id);
        if (item == null) { // 暂时这样处理
            renderFailedJson(ReturnCode.FAIL);
        }
        // 当前宝贝视图详情
        renderArgs.put("item", item);
        // 专辑宝贝信息
        AlbumItem albumItem = Album.findAbumItemByIdWithAlbumId(albumId, id);
        renderArgs.put("albumItem", albumItem);
        // 所有省份信息
        List<Region> provinces = Region.findByParentIdWithCache(1);
        renderArgs.put("provinces", provinces);
        // 访问量统计
        String cookieKey = Secure.getValidAccessPvCookieKey();
        if (!Strings.isNullOrEmpty(cookieKey)) {
            boolean pvValid = ItemPv.checkIsValidAccessPv(cookieKey + id);
            if (pvValid) {
                ItemPv.incrementWireless(id, 1);
                if (albumId > 0) {
                    ItemPv.incrementWirelessInAlbum(id, 1);
                }
            }
        }
        render();
    }

    /**
     * 
     * h5专辑页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月23日 下午3:09:46
     */
    @OpenInWeixinRequiredSupport
    @WechatSupport
    public static void m_album(@Required @Min(1) long id) {
        handleWrongInput(true);
        boolean checkAuth = Optional.fromNullable(renderArgs.get("checkAuth", Boolean.class)).or(true);
        if (checkAuth) {
            String openId = session.get(BizConstants.USER_AUTH_OPENID);
            log.info("user openId :{}", openId);
            // 跳转微信授权
            if (Strings.isNullOrEmpty(openId)) {
                log.info("user will be auth !");
                String redirectUrl = WechatHelper.getAuthToTargetUrl(request.url, null, "snsapi_userinfo");
                redirect(redirectUrl);
            }
            if (!WechatUser.checkRoleInMoudle("album", id + "", openId)) {
                log.info("user no auth role:{},wait manage auth ~", openId);
                renderArgs.put("moudle", "album");
                renderArgs.put("moudleId", id + "");
                renderTemplate("Application/loading.html");
            }
        }

        Album album = Album.findById(id);
        if (album != null) {
            renderArgs.put("albumID", album.id);
            renderArgs.put("albumName", album.name);
            List<Album> albums = Album.findList();
            renderArgs.put("albums", albums);
        } else {
            renderTemplate("errors/404.html");
        }
        render();
    }

    /**
     * 查询专辑商品列表
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 下午10:27:58
     */
    public static void queryAlbumItemsByAlbumId(@Required @Min(0) long id, boolean storeFull,
        @MaxSize(11) @Match("^price_[a,d]|profit_[a,d]$") String sort) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        List<AlbumItem> albumItems = Album.findAlbumItemsWithSortByAlbumID(id, storeFull, sort);
        renderJson(albumItems);
    }

    /**
     * 
     * PC专辑页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年10月4日 下午3:09:46
     */
    public static void album(@Required @Min(0) long id) {
        handleWrongInput(true);
        Album album = Album.findById(id);
        if (album != null && MixHelper.isNotEmpty(album.albumItems)) {
            List<AlbumItem> albumItems = album.parseAlbumItemsForPC();
            if (MixHelper.isNotEmpty(albumItems)) {
                renderArgs.put("albumName", album.name);
                renderArgs.put("albumItems", albumItems);
            }
        }
        render();
    }

    /**
     * 渲染pc端专辑商品信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年10月5日 下午3:53:04
     */
    public static void queryPcAlbumItemsById() {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }

    }
}

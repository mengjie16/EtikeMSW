package controllers;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.AppMode;
import com.aton.config.ReturnCode;
import com.aton.util.MailUtils;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.taobao.api.internal.util.WebUtils;

import controllers.annotations.UploadSupport;
import controllers.annotations.UserLogonSupport;
import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.ItemStatus;
import enums.constants.ErrorCode;
import enums.constants.RegexConstants;
import models.ActivityPageSetting;
import models.Album;
import models.AlbumItem;
import models.Article;
import models.ArticleType;
import models.AuthManage;
import models.Brand;
import models.FreightTemp;
import models.H5ActivityPageSetting;
import models.HomePageSetting;
import models.Item;
import models.ItemCate;
import models.ItemSetting;
import models.Order;
import models.ReferUrl;
import models.Region;
import models.Retailer;
import models.RetailerChannel;
import models.StarItemSetting;
import models.Supplier;
import models.SupplierSendLocationTemp;
import models.Trade;
import models.User;
import models.WechatUser;
import play.data.binding.As;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.With;
import utils.SmsUtil;
import utils.TSFileUtil;
import vos.CateChangeVo;
import vos.FreightSearchVo;
import vos.FreightTempVo;
import vos.ItemSearchVo;
import vos.ItemVo;
import vos.OrderVo;
import vos.PriceRangeVo;
import vos.RetailerSearchVo;
import vos.SupplierSearchVo;
import vos.TradeSearchVo;
import vos.TradeVo;
import vos.UserSearchVo;
import vos.WechatUserVo;

/**
 * 管理员相关路由
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年5月25日 上午11:01:41
 */
@With(Secure.class)
public class ManagerController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ManagerController.class);

    /**
     * 管理员登录
     *
     * @param redirectUrl
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 下午5:48:31
     */
    public static void login(@MaxSize(128) String redirectUrl) {
        if (!Strings.isNullOrEmpty(redirectUrl)) {
            renderArgs.put("rUrl", redirectUrl);
        }
        render();
    }

    /**
     * 系统管理员登录
     *
     * @param account
     * @param password
     * @param rUrl
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 下午6:05:09
     */
    public static void doLogin(@Required String account, @Required @MinSize(6) @MaxSize(20) String password,
        @MaxSize(128) String rUrl) {
        String enterUrl = "/sys/enter";
        if (!Strings.isNullOrEmpty(rUrl)) {
            enterUrl += "?redirectUrl=".concat(WebUtils.encode(rUrl));
        }
        // 参数校验
        if (validation.hasErrors()) {
            flash.error("信息填写错误");
            redirect(enterUrl);
        }
        // 开发状态下无需进行权限验证
        if (AppMode.get().isNotOnline()) {
            User usr = new User();
            Secure.setAdminToContainer(usr);
            if (!Strings.isNullOrEmpty(rUrl)) {
                redirect(rUrl);
            }
            redirect("/sys/user/manage");
        }
        if (!"test".equals(account) || !"notest".equals(password)) {
            flash.error("用户或密码不正确");
            redirect(enterUrl);
        }
        User usr = new User();
        Secure.setAdminToContainer(usr);
        if (!Strings.isNullOrEmpty(rUrl)) {
            redirect(rUrl);
        }
        redirect("/sys/user/manage");
    }

    /**
     * 文章管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void articleManage() {
        render();
    }
    

    
    
    /**
     * 文章编辑
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void articleEdit(long id) {
        Article article = Article.findById(id);
        // 输出文章类型[暂时模拟]
        List<ArticleType> types = ArticleType.defaultTypes();
        renderArgs.put("atypes", types);
        if (article != null) {
            renderArgs.put("article", article);
        }
        render();
    }

    /**
     * 删除品牌
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午5:49:02
     */
    public static void delBrandWithId(@Required @Min(0) long id) {
        handleWrongInput(false);
        int count = Item.checkExsitBrandUse(id);
        if (count > 0) {
            renderFailedJson(ReturnCode.FAIL, "该品牌已被(" + count + ")个商品应用,删除失败!");
        }
        if (Brand.delBrandById(id)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "删除品牌失败!");
    }

    /**
     * 保存更新品牌信息
     *
     * @param brand
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午6:14:38
     */
    public static void brandSave(@Valid @Required Brand brand) {
        handleWrongInput(false);
        if (brand.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "品牌保存失败!");
    }

    /**
     * 品牌管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void brandManage() {
        render();
    }

    /**
     * 类目管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    public static void cateManage(long cateId) {
        // cateId存在，显示页面 二级类目列表
        ItemCate ic = ItemCate.findById(cateId);
        if (ic != null) {
            renderArgs.put("cate", ic);
        }
        render();
    }

    /**
     * 主页管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void homeManage() {
        render();
    }

    /**
     * 查询最新应用的主页设置
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月31日 下午3:46:00
     */
    public static void queryBuildHomePageSetting() {
        HomePageSetting homeSet = HomePageSetting.findBuildSetting();
        if (homeSet != null) {
            renderJson(homeSet);
        }
        renderFailedJson(ReturnCode.FAIL);

    }

    /**
     * 查询主页设置列表
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午1:32:25
     */
    public static void queryHomePageSettingList() {
        List<HomePageSetting> homePageSettingList = HomePageSetting.findHomePageSettingDetailList();
        renderJson(homePageSettingList);
    }

    /**
     * 创建主页配置模板
     *
     * @param name
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午1:53:55
     */
    public static void createHomePageSetting(@Required String name) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        if (HomePageSetting.countName(name) > 0) {
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        long exsit = HomePageSetting.count();
        HomePageSetting hps = new HomePageSetting();
        hps.name = name;
        hps.isUse = exsit <= 0;
        if (hps.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 使用当前主页模板配置
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午5:57:15
     */
    public static void useHomePageSetting(@Required String id) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        HomePageSetting hps = HomePageSetting.findById(id);
        if (hps != null) {
            if (hps.useCurrent()) {
                renderSuccessJson();
            }
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除主页配置模板信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午1:59:13
     */
    public static void deleteHomePageSettingById(@Required String id) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        HomePageSetting hps = HomePageSetting.findById(id);
        if (hps != null && !hps.isUse && hps.delete()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 活动管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void activityManage() {
        render();
    }

    /**
     * 管理员商品编辑
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void itemManaEdit(long id) {
        handleWrongInput(true);
        ItemVo item = Item.itemDetailCacheById(id);
        if (item != null) {
            renderArgs.put("item", item);
            List<ReferUrl> rerferList = ReferUrl.getDefaultList();           
            renderArgs.put("rerferUrls", rerferList);
        }
        
        // 供应商运费模版
        /*List<FreightTemp> freightTemps = FreightTemp.findUserFreightTempAllInCache(item.supplierId);
        if (freightTemps != null) {
            renderArgs.put("supFreightTemps", freightTemps);
        }*/
        render();
    }
    
    

    /**
     * 管理员保存商品
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年7月23日 下午12:18:14
     */
    public static void saveItem(@Valid ItemVo item) {

        String redirectUrl = "/sys/item/edit" + (item.id > 0 ? "?id=" + item.id : "");
        if (validation.hasErrors()) {
            flash.error("保存商品失败！");
            redirect(redirectUrl);
        }
        Item itemObj = item.parseToItem();
/*        if (Item.findById(itemObj.id) == null) { // 直接400
            renderFailedJson(ReturnCode.WRONG_INPUT, "商品不存在");
        }*/
        // 用户地址检查
        if (!itemObj.checkItemLocationRepeat()) {
            flash.error("保存商品失败,商品发货地址重复！！");
            redirect(redirectUrl);
        }
        // 商品保存\并保存地址信息
        if (itemObj.saveWithItemLocation()) {
            redirect("/sys/item/list");
        }
        redirect(redirectUrl);
    }

    /**
     * 根据供应商ID查询供应商发货列表
     *
     * @param itemId
     * @since v1.0
     * @author Calm
     * @created 2016年7月23日 下午12:27:55
     */
    public static void findSendLocationTempList(@Required @Min(0) int supplierId) {
        List<SupplierSendLocationTemp> tempList = SupplierSendLocationTemp.findListBySupplierId(supplierId);
        renderJson(tempList);
    }

    /**
     * 添加供应商发货地址模板
     *
     * @param locationTemp
     * @since v1.0
     * @author Calm
     * @created 2016年7月23日 下午12:36:03
     */
    public static void confirmSendLocationTemp(@Required @Valid SupplierSendLocationTemp locationTemp) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        Supplier supplier = Supplier.findById(locationTemp.supplierId);
        if (supplier == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 检查模板地址是否重复
        List<SupplierSendLocationTemp> lsst = SupplierSendLocationTemp.findListBySupplierId(supplier.id);
        String currentBaseStr = locationTemp.toBaseLocationStr();
        if (MixHelper.isNotEmpty(lsst)) {
            for (SupplierSendLocationTemp sst : lsst) {
                if (Objects.equal(sst.toBaseLocationStr(), currentBaseStr)) {
                    renderFailedJson(ReturnCode.BIZ_LIMIT);
                    break;
                }
            }
        }
        if (locationTemp.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 活动编辑
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void activityEdit(String id) {
        ActivityPageSetting aps = ActivityPageSetting.findById(id);
        if (aps != null) {
            renderArgs.put("aps_id", aps._id);
        }
        render();
    }

    /**
     * 
     * TODO Comment.
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午1:36:43
     */
    @UploadSupport
    public static void h5activityEdit(String id) {
        H5ActivityPageSetting h5aps = H5ActivityPageSetting.findById(id);
        if (h5aps != null) {
            renderArgs.put("h5aps_id", h5aps._id);
        }
        render();
    }

    /**
     * 保存/更新宝贝类目
     *
     * @param cate
     * @since v1.0
     * @author Calm
     * @created 2016年6月6日 下午3:23:26
     */
    public static void cateSave(@Required ItemCate cate) {
        if (cate.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除类目信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年6月13日 下午5:49:35
     */
    public static void cateDelete(@Required long id) {
        handleWrongInput(true);
        ItemCate ic = ItemCate.findByIdMap(id);
        if (ic == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        if (ic.delete()) {
            // 查询本级类目列表(此列表是降序排序的)
            List<ItemCate> currCates = ItemCate.findList(ic.parentCid);
            int startIndex = currCates.size() + 1;
            // 处理剩余正常剩下排序
            for (int i = 0; i < currCates.size(); i++) {
                ItemCate nCate = currCates.get(i);
                nCate.ordinal = startIndex;
                nCate.updateOrdinal();
                startIndex--;
            }
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 更新类目信息
     *
     * @param list
     * @param parentId
     * @since v1.0
     * @author Calm
     * @created 2016年6月13日 下午3:45:51
     */
    public static void changeCateInfo(@Required List<CateChangeVo> list, long parentId) {
        handleWrongInput(true);
        // 查找父级类目
        ItemCate pCate = ItemCate.findByIdMap(parentId);
        if (parentId > 0 && pCate == null) { // 有父级ID确找不到,返回错误
            renderFailedJson(ReturnCode.FAIL, "无法确认上级类目");
        }
        // 当前类目级别
        int level = 1;
        if (pCate != null) {
            level = pCate.level + 1;
        }
        // 检查重复
        for (CateChangeVo catecVo : list) {
            if (!Objects.equal(catecVo.changeType, "del")) {
                if (catecVo.id <= 0 && ItemCate.countName(catecVo.name) > 0) {
                    renderFailedJson(ReturnCode.FAIL, "类目名称:(" + catecVo.name + "),已存在");
                }
            }
        }
        // 逻辑处理后列表
        List<CateChangeVo> copyVol = Lists.newArrayList();
        for (CateChangeVo catecVo : list) {
            if (Objects.equal(catecVo.changeType, "del")) {
                ItemCate.delete(catecVo.id);
                continue;
            }
            // 无论如何更新或保存一次
            ItemCate cateObj = new ItemCate();
            cateObj.name = catecVo.name;
            cateObj.id = catecVo.id;
            cateObj.level = level;
            cateObj.parentCid = parentId;
            cateObj.saveCurrent();
            catecVo.id = cateObj.id;
            copyVol.add(catecVo);
        }
        // 如果当前有父级类目，并且父级类目不为isParent
        if (pCate != null && !pCate.isParent) {
            pCate.isParent = true;
            pCate.updateIsParent();
        }
        // 查询本级类目列表(此列表是降序排序的)
        List<ItemCate> currCates = ItemCate.findList(parentId);
        int startIndex = currCates.size() + 1;
        int endIndex = 1;
        List<Long> ids = Lists.newArrayList();
        // 已经处理过的
        List<Long> cIds = Lists.newArrayList();
        // 处理置顶和置底
        for (CateChangeVo ic : copyVol) {
            // 处理置顶
            if (Objects.equal(ic.changeType, "top")) {
                ItemCate cateObj = new ItemCate();
                cateObj.id = ic.id;
                cateObj.ordinal = startIndex;
                cateObj.updateOrdinal();
                cIds.add(cateObj.id);
                startIndex--;
            }
            // 处理置底
            else if (Objects.equal(ic.changeType, "bottom")) {
                ItemCate cateObj = new ItemCate();
                cateObj.id = ic.id;
                cateObj.ordinal = endIndex;
                cateObj.updateOrdinal();
                cIds.add(cateObj.id);
                endIndex++;
            } else {
                ids.add(ic.id);
            }
        }
        boolean doOther = false;
        // 处理剩余正常剩下排序
        for (int i = 0; i < currCates.size(); i++) {
            ItemCate nCate = currCates.get(i);
            // 已处理
            if (cIds.contains(nCate.id)) {
                continue;
            }
            // 存在需要处理的
            if (ids.contains(nCate.id)) {
                if (!doOther) {
                    for (Long id : ids) {
                        ItemCate cate = new ItemCate();
                        cate.id = id;
                        cate.ordinal = startIndex;
                        cate.updateOrdinal();
                        startIndex--;
                    }
                    doOther = true;
                }
            } else {
                nCate.ordinal = startIndex;
                nCate.updateOrdinal();
                startIndex--;
            }
        }
        renderSuccessJson();
    }

    /**
     * 注册用户管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void userManage() {
        render();
    }

    /**
     * 供应商管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void supplierManage() {
        // 加载区域信息
        List<Region> proRegion = Region.findByParentId(0);
        renderArgs.put("countries", proRegion);

        render();
    }

    /**
     * 零售商管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月25日 下午4:17:02
     */
    @UploadSupport
    public static void retailerManage() {
        // 加载零售商渠道列表
        List<RetailerChannel> rcs = RetailerChannel.getChannelList();
        renderArgs.put("rcs", rcs);

        render();
    }

    /**
     * 条件查询用户分页数据
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 下午3:54:53
     */
    public static void queryUserWithVo(@Valid @Required UserSearchVo vo) {
        handleWrongInput(true);
        Page<User> userPageData = User.findPageByVo(vo);
        renderPageJson(userPageData);
    }

    /**
     * 
     * 条件查询供应商分页数据
     *
     * @param vo
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午3:37:15
     */
    public static void querySupplierWithVo(@Valid @Required SupplierSearchVo vo) {
        handleWrongInput(true);
        Page<Supplier> supplierPageData = Supplier.findPageByVo(vo);
        renderPageJson(supplierPageData);
    }

    /**
     * 
     * 保存供应商信息
     *
     * @param supplier
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午4:16:03
     */
    public static void saveSupplierInfo(@Valid @Required Supplier supplier) {
        handleWrongInput(true);
        // 添加零售商信息
        boolean ret = supplier.save();
        // 注册成功
        if (ret) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "保存失败");
    }

    /**
     * 
     * 删除供应商
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午4:22:59
     */
    public static void removeSupplier(@Required @Min(1) int id) {
        handleWrongInput(true);
        UserSearchVo vo = new UserSearchVo();
        vo.userType = "SUPPLIER";
        vo.userId = id;
        vo.pageSize = 100;

        Page page = User.findPageByVo(vo);
        if (MixHelper.isNotEmpty(page.items)) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "该供应商下有 " + page.totalCount + " 个用户，不能删除");
        }

        Supplier.deleteById(id);
        renderSuccessJson();
    }

    /**
     * 
     * 条件查询零售商分页数据
     *
     * @param vo
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午3:37:27
     */
    public static void queryRetailerWithVo(@Valid @Required RetailerSearchVo vo) {
        handleWrongInput(true);
        Page<Retailer> retailerPageData = Retailer.findPageByVo(vo);
        renderPageJson(retailerPageData);
    }

    /**
     * 
     * 保存零售商信息
     *
     * @param retailer
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午4:16:21
     */
    public static void saveRetailerInfo(@Valid @Required Retailer retailer) {
        handleWrongInput(true);
        // 添加零售商信息
        boolean ret = retailer.save();
        // 注册成功
        if (ret) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "保存失败");
    }

    /**
     * 
     * 删除零售商
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午4:23:34
     */
    public static void removeRetailer(@Required @Min(1) int id) {
        handleWrongInput(true);
        UserSearchVo vo = new UserSearchVo();
        vo.userType = "RETAILER";
        vo.userId = id;
        vo.pageSize = 100;

        Page page = User.findPageByVo(vo);
        if (MixHelper.isNotEmpty(page.items)) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "该零售商下有 " + page.totalCount + " 个用户，不能删除");
        }

        Retailer.deleteById(id);
        renderSuccessJson();
    }

    /**
     * 查询用户注册验证码
     *
     * @param phone
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午12:20:36
     */
    public static void queryUserSmsValidCode(@Required @MaxSize(11) @Match(RegexConstants.PHONE) String phone) {
        handleWrongInput(true);
        String smsCode = SmsUtil.querySmsCode(phone);
        if (!Strings.isNullOrEmpty(smsCode)) {
            renderJson(smsCode);
        }
        renderFailedJson(ReturnCode.BIZ_LIMIT);
    }

    /**
     * 用户密码重置
     *
     * @param userId
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 下午6:27:53
     */
    public static void resetUserPassword(@Required @Min(0) long userId) {
        handleWrongInput(true);
        User user = User.findById(userId);
        if (user == null) {
            renderFailedJson(ReturnCode.FAIL, "用户不存在");
        }
        // 生成随机密码
        user.password = RandomStringUtils.randomAlphanumeric(6);

        // 生成的新密码
        String newPass = user.password;
        if (!user.savePassword()) {
            renderFailedJson(ReturnCode.FAIL, "重置密码失败");
        }
        // 设置了邮件的话，发送一下邮件
        if (!Strings.isNullOrEmpty(user.email)) {
            MailUtils.sendTextMail(user.email, "[吐司宝贝]密码重置成功", "您的新临时密码为:" + newPass + "，请尽快登录并修改密码");
        }
        // 短信通知
        SmsUtil.sendUserModifyPassNotice(user.phone, newPass, "用户");
        renderJson(ImmutableMap.of("newPass", newPass));
    }

    /**
     * 保存用户信息，新增或修改
     *
     * @param user
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 下午6:29:16
     */
    public static void saveUserInfo(@Required @Valid User user) {
        handleWrongInput(true);
        // 用户校验，用户id=0时是新增
        if (user.id > 0) {
            User currentUser = User.findById(user.id);
            if (currentUser == null) {
                renderFailedJson(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.description);
            }
        }
        // 看手机号是不是被占用
        if (User.findByPhone(user.phone) != null) {
            renderFailedJson(ErrorCode.USER_PHONE_DUPLICATE.code, ErrorCode.USER_PHONE_DUPLICATE.description);
        }
        // 邮件被人家用了
        if (User.checkEmailUsedByOthers(user.email, user.id)) {
            renderFailedJson(ErrorCode.USER_EMAIL_DUPLICATE.code, ErrorCode.USER_EMAIL_DUPLICATE.description);
        }
        // 名字被人家用了
        if (User.checkNameUsedByOthers(user.name, user.id)) {
            renderFailedJson(ErrorCode.USER_NAME_DUPLICATE.code, ErrorCode.USER_NAME_DUPLICATE.description);
        }

        if ("RETAILER".equals(user.role)) {
            if (Retailer.findById(user.userId) == null) {
                renderFailedJson(ReturnCode.WRONG_INPUT, "零售商 " + user.userId + " 不存在");
            }
        }
        if ("SUPPLIER".equals(user.role)) {
            if (Supplier.findById(user.userId) == null) {
                renderFailedJson(ReturnCode.WRONG_INPUT, "供应商 " + user.userId + " 不存在");
            }
        }

        user.save();
        renderSuccessJson();
    }

    /**
     * 
     * 删除用户
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月9日 下午10:29:19
     */
    public static void removeUser(@Required @Min(1) long id) {
        handleWrongInput(true);
        if (User.deleteById(id)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.INNER_ERROR);
    }

    /**
     * 更新用户认证状态
     *
     * @param userId
     * @since v1.0
     * @author Calm
     * @created 2016年8月12日 上午11:14:16
     */
    public static void userAuthOne(@Required @Min(1) long userId) {
        handleWrongInput(true);
        User user = User.findById(userId);
        if (user == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        if (user.auth()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 文章删除
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年6月18日 下午1:36:59
     */
    public static void removeArticle(@Required @Min(1) long id) {
        handleWrongInput(true);
        Article article = Article.findById(id);
        if (article == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        if (article.delete()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 文章保存
     *
     * @param article
     * @since v1.0
     * @author Calm
     * @created 2016年6月20日 上午12:32:35
     */
    public static void saveArticle(@Required @Valid Article article) {
        if (validation.hasErrors()) {
            flash.put("error", "保存错误!");
        }
        if (article.save()) {
            redirect("/sys/article");
        }
        flash.error("文章保存错误!");
        renderArgs.put("article", article);
        redirect("/sys/article/edit");
    }

    /**
     * 更新主页设置内容
     *
     * @param moudle
     * @param homePageSet
     * @param index
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 下午1:01:20
     */
    public static void confirmHomePageSetting(@Required String moudle, @Required HomePageSetting homePageSet, int index,
        @Required String modifyType) {
        handleWrongInput(true);
        HomePageSetting homePageSetting = HomePageSetting.findById(homePageSet._id);
        if (homePageSetting == null) {
            renderFailedJson(ReturnCode.FAIL, "保存失败");
        }
        boolean result = false;
        if (Objects.equal("bigBannerSetting", moudle)) {
            if (modifyType.equals("add")) {
                result = HomePageSetting.pushValueToField(homePageSet._id, "big_BannerSettings",
                    homePageSet.bigBannerSetting.toMap());
            } else {
                result = HomePageSetting.updateValueByFieldIndex(homePageSet._id, "big_BannerSettings", index,
                    homePageSet.bigBannerSetting.toMap());
            }
        } else if (Objects.equal("right_SmallBannerSetting", moudle)) {
            if (modifyType.equals("add")) {
                result = HomePageSetting.pushValueToField(homePageSet._id, "right_SmallBannerSettings",
                    homePageSet.right_SmallBannerSettig.toMap());
            } else {
                result = HomePageSetting.updateValueByFieldIndex(homePageSet._id, "right_SmallBannerSettings", index,
                    homePageSet.right_SmallBannerSettig.toMap());
            }
        } else if (Objects.equal("brandSetting", moudle)) {
            if (modifyType.equals("add")) {
                result = HomePageSetting.pushValueToField(homePageSet._id, "brandSettings",
                    homePageSet.brandSetting.toMap());
            } else {
                result = HomePageSetting.updateValueByFieldIndex(homePageSet._id, "brandSettings", index,
                    homePageSet.brandSetting.toMap());
            }
        } else if (Objects.equal("activitySetting", moudle)) {
            if (MixHelper.isNotEmpty(homePageSet.activitySetting.itemSettings)) {
                for (ItemSetting setting : homePageSet.activitySetting.itemSettings) {
                    if (setting.id > 0 && Item.findById(setting.id) == null) {
                        renderFailedJson(ReturnCode.FAIL, "ID:" + setting.id + ",商品不存在！");
                        break;
                    }
                }
            }
            if (modifyType.equals("add")) {
                result = HomePageSetting.pushValueToField(homePageSet._id, "activitySettings",
                    homePageSet.activitySetting.toMap());
            } else {
                result = HomePageSetting.updateValueByFieldIndex(homePageSet._id, "activitySettings", index,
                    homePageSet.activitySetting.toMap());
            }
        }
        if (result) {
            // 更新编辑时间
            HomePageSetting.updateTime(homePageSet._id);
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "保存失败");
    }

    /**
     * 删除主页配置信息
     *
     * @param moudle
     * @param id
     * @param index
     * @since v1.0
     * @author Calm
     * @created 2016年6月29日 下午5:22:50
     */
    public static void deleteHomePageSetting(@Required String moudle, @Required String id, int index) {
        handleWrongInput(true);
        HomePageSetting homePageSetting = HomePageSetting.findById(id);
        if (homePageSetting == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        boolean result = false;
        if (Objects.equal("bigBannerSetting", moudle)) {
            result = HomePageSetting.removeValueByFieldIndex(id, "big_BannerSettings", index);
        } else if (Objects.equal("right_SmallBannerSetting", moudle)) {
            result = HomePageSetting.removeValueByFieldIndex(id, "right_SmallBannerSettings", index);
        } else if (Objects.equal("brandSetting", moudle)) {
            result = HomePageSetting.removeValueByFieldIndex(id, "brandSettings", index);
        } else if (Objects.equal("activitySetting", moudle)) {
            result = HomePageSetting.removeValueByFieldIndex(id, "activitySettings", index);
        }
        if (result) {
            HomePageSetting.removeHomePageDetailCacheById("default");
            renderSuccessJson();
        }
    }

    /**
     * 查询主页配置内容
     *
     * @param sid
     * @since v1.0
     * @author Calm
     * @created 2016年6月27日 下午5:54:54
     */
    public static void queryHomePageSettingById(@Required String sid) {
        HomePageSetting hps = null;
        if (!Strings.isNullOrEmpty(sid)) {
            hps = HomePageSetting.findById(sid);
        }
        hps.parseToDeatil();
        renderJson(hps);
    }

    /**
     * 获取商品信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午12:05:09
     */
    public static void itemData(@Min(1) long id) {
        handleWrongInput(true);
        Item item = Item.findById(id);
        if (item != null) {
            renderJson(item);
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 批量查询商品信息
     *
     * @param ids
     * @since v1.0
     * @author Calm
     * @created 2016年10月9日 下午4:44:27
     */
    public static void itemDataBatch(@Required @As(",") List<Long> ids) {
        if (MixHelper.isEmpty(ids)) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 不存在商品id
        List<Long> unExsit = Lists.newArrayList();
        List<Item> items = Lists.newArrayList();
        for (int i = 0; i < ids.size(); i++) {
            Item item = Item.findById(ids.get(i));
            if (item != null) {
                items.add(item);
            } else {
                unExsit.add(ids.get(i));
            }
        }
        renderJson(ImmutableMap.of("unExsit", unExsit, "items", items));
    }

    /**
     * 查询活动配置信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午12:07:58
     */
    public static void queryActivitySetting(String id) {
        ActivityPageSetting activityPageSet = ActivityPageSetting.findById(id);
        if (MixHelper.isNotEmpty(activityPageSet.bottomItemIds)) {
            activityPageSet.bottomItems = activityPageSet.bottomItemIds.stream().map(i -> ItemVo.valueOfBase(i))
                .filter(v -> v != null).collect(Collectors.toList());
        }
        renderJson(activityPageSet);
    }

    /**
     * 查询h5活动信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午1:06:24
     */
    public static void queryH5ActivitySetting(String id) {
        H5ActivityPageSetting h5activityPageSet = H5ActivityPageSetting.findById(id);
        h5activityPageSet.toDetail();
        renderJson(h5activityPageSet);
    }

    /**
     * 创建活动
     *
     * @param title
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:40:11
     */
    public static void createActivityPageSetting(@Required String title) {
        handleWrongInput(true);
        ActivityPageSetting aps = new ActivityPageSetting();
        aps.title = title;
        if (aps.saveReturnId()) {
            renderJson(aps._id);
        }
        renderFailedJson(ReturnCode.FAIL);

    }

    /**
     * 创建H5活动
     *
     * @param title
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午12:20:19
     */
    public static void createH5ActivityPageSetting(@Required String title) {
        handleWrongInput(true);
        H5ActivityPageSetting aps = new H5ActivityPageSetting();
        aps.title = title;
        if (aps.saveReturnId()) {
            renderJson(aps._id);
        }
        renderFailedJson(ReturnCode.FAIL);

    }

    /**
     * 活动列表查询
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:58:54
     */
    public static void queryActivityList(int pageNumber, int pageSize) {
        int total = ActivityPageSetting.count();
        List<ActivityPageSetting> listSettings = ActivityPageSetting.findList(pageNumber, pageSize);
        renderPageJson(listSettings, total);
    }

    /**
     * H5活动列表查询
     *
     * @param pageNumber
     * @param pageSize
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午12:18:11
     */
    public static void queryH5ActivityList(int pageNumber, int pageSize) {
        int total = H5ActivityPageSetting.count();
        List<H5ActivityPageSetting> listSettings = H5ActivityPageSetting.findList(pageNumber, pageSize);
        renderPageJson(listSettings, total);
    }

    /**
     * 确认保存活动页面模块配置
     * 
     * @param moudle
     * @param activityPageSet
     * @param index
     * @param modifyType
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:37:13
     */
    public static void confirmActivitySetting(@Required String moudle, @Required ActivityPageSetting activityPageSet,
        int index, @Required @Match("add|update") String modifyType) {
        handleWrongInput(true);
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById(activityPageSet._id);
        if (activityPageSetting == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        boolean result = false;
        if (Objects.equal("bannerImg", moudle)) {
            result = ActivityPageSetting.updateValueByField(activityPageSetting._id, "bannerImg",
                activityPageSet.bannerImg);
        } else if (Objects.equal("activityItems", moudle)) {
            if (modifyType.equals("add")) {
                result = ActivityPageSetting.pushValueToField(activityPageSetting._id, "activityItems",
                    activityPageSet.activityItem.toMap());
            } else {
                result = ActivityPageSetting.updateValueByFieldIndex(activityPageSetting._id, "activityItems", index,
                    activityPageSet.activityItem.toMap());
            }
        } else if (Objects.equal("middlePoster", moudle)) {
            result = ActivityPageSetting.updateValueByField(activityPageSetting._id, "middlePoster",
                activityPageSet.middlePoster);
        } else if (Objects.equal("bottomItemIds", moudle)) {
            Item item = Item.findById(activityPageSet.itemId);
            if (item == null) {
                renderFailedJson(ReturnCode.FAIL);
            }
            result = ActivityPageSetting.pushValueToField(activityPageSetting._id, "bottomItemIds",
                activityPageSet.itemId);
            if (result) {
                ItemVo iv = new ItemVo();
                ReflectionUtils.copyBean(item, iv);
                iv.brand = Brand.findBrandWithCacheMap(item.brandId);
                renderJson(iv);
            }
        }
        if (result) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    public static void confirmH5ActivitySetting(@Required String moudle,
        @Required H5ActivityPageSetting activityPageSet, int index, @Required @Match("add|update") String modifyType) {
        handleWrongInput(true);
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById(activityPageSet._id);
        if (activityPageSetting == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        boolean result = false;
        // if(Objects.equal("bannerImg", moudle)){
        // result= ActivityPageSetting.updateValueByField(activityPageSetting._id, "bannerImg",
        // activityPageSet.bannerImg);
        // }
        if (result) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 应用活动设置
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月3日 下午12:51:35
     */
    public static void useActivityPageSetting(@Required String id, @Required boolean use) {
        handleWrongInput(true);
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById(id);
        if (activityPageSetting == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        activityPageSetting.isUse = use;
        if (activityPageSetting.useActivityPageSetting()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 应用H5活动配置
     *
     * @param id
     * @param use
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午3:08:21
     */
    public static void useH5ActivityPageSetting(@Required String id, @Required boolean use) {
        handleWrongInput(true);
        H5ActivityPageSetting activityPageSetting = H5ActivityPageSetting.findById(id);
        if (activityPageSetting == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        activityPageSetting.isUse = use;
        if (activityPageSetting.useActivityPageSetting()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除活动页配置信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月3日 下午2:19:37
     */
    public static void deleteActivityPageSetting(@Required String id) {
        handleWrongInput(true);
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById(id);
        if (activityPageSetting != null && activityPageSetting.delete()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除h5活动配置
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午1:09:08
     */
    public static void deleteH5ActivityPageSetting(@Required String id) {
        handleWrongInput(true);
        H5ActivityPageSetting activityPageSetting = H5ActivityPageSetting.findById(id);
        if (activityPageSetting != null && activityPageSetting.delete()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除活动页面模块设置
     * 
     * @param moudle
     * @param id
     * @param index
     */
    public static void deleteActivityPageSettingMoudle(@Required String moudle, @Required String id, int index) {
        handleWrongInput(true);
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById(id);
        if (activityPageSetting == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        boolean result = false;
        if (Objects.equal("activityItems", moudle)) {
            result = ActivityPageSetting.removeValueByFieldIndex(id, "activityItems", index);
        } else if (Objects.equal("bottomItemIds", moudle)) {
            result = ActivityPageSetting.removeValueByFieldIndex(id, "bottomItemIds", index);
        }
        if (result) {
            renderSuccessJson();
        }
    }

    /**
     * 专辑管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 上午11:27:02
     */
    @UploadSupport
    public static void albumManage() {
        render();
    }

    /**
     * 查询专辑列表
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:17:11
     */
    public static void queryAlbumList() {
        List<Album> albums = Album.findList();
        renderJson(albums);
    }

    /**
     * 根据ID查询专辑信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:18:46
     */
    public static void queryAlbumById(@Required @Min(0) long id) {
        handleWrongInput(true);
        Album album = Album.findById(id);
        renderJson(album);
    }

    /**
     * 查询专辑商品列表(带详情)
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午1:21:26
     */
    public static void queryAlbumItemsByAlbumId(@Required @Min(0) long id) {
        handleWrongInput(true);
        Album album = Album.findById(id);
        if (album != null) {
            album.parseItemDetail();
            renderJson(album.albumItems);
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除专辑商品
     *
     * @param id
     * @param index
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午2:12:37
     */
    public static void deleteAlbumItemByAlbumId(@Required @Min(0) long id, @Min(0) int index) {
        handleWrongInput(true);
        Album album = Album.findById(id);
        if (album != null) {
            if (album.deleteAblumItemByIndex(index)) {
                renderSuccessJson();
            }
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除专辑
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:26:44
     */
    public static void deleteAlbumById(@Required @Min(0) long id) {
        handleWrongInput(true);
        Album album = Album.findById(id);
        if (album != null && album.delete()) {
            AuthManage.deleteByCondition("album", id + "");
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 创建专辑
     *
     * @param name
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:21:47
     */
    public static void createAlbum(@Required @MaxSize(64) String name) {
        handleWrongInput(true);
        if (Album.checkAlbumExsitName(name)) { // 检查是否存在
            renderFailedJson(ReturnCode.FAIL);
        }
        Album album = new Album(name);
        if (album.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 更新专辑
     *
     * @param album
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 下午3:56:07
     */
    public static void updateAlbum(@Required Album album) {
        handleWrongInput(true);
        Album checkAlbum = Album.findById(album.id);
        if (checkAlbum == null || Album.checkAlbumExsitName(album.name) || album.checkExsitAlbumItemInList()) { // 检查是否存在
            renderFailedJson(ReturnCode.FAIL);
        }

        if (album.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 添加专辑商品
     *
     * @param id
     * @param albumItem
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:32:53
     */
    public static void addAlbumItem(@Required @Min(0) long id, @Required @Min(0) long itemId) {
        handleWrongInput(true);
        Album album = Album.findById(id);
        Item item = Item.findBaseInfoById(itemId);
        if (album == null || album.checkExsitAlbumItemByItemId(itemId) || item == null) {
            renderFailedJson(ReturnCode.FAIL);
        }
        AlbumItem albumItem = new AlbumItem();
        albumItem.itemId = itemId;
        albumItem.picUrl = item.picUrl;
        albumItem.title = item.title;
        // leve1
        if (item.distPriceUse && item.distPrice > 0) {
            albumItem.price = item.distPrice;
        } else if (item.priceRangeUse && MixHelper.isNotEmpty(item.priceRanges)) {
            // 排序
            item.priceRanges.sort(new Comparator<PriceRangeVo>() {

                @Override
                public int compare(PriceRangeVo v0, PriceRangeVo v1) {
                    // TODO Auto-generated method stub
                    if (v0.price > v1.price) {
                        return 1;
                    }
                    if (v0.price == v1.price) {
                        return 0;
                    }
                    return -1;
                }
            });
            PriceRangeVo priceVo = item.priceRanges.get(0);
            albumItem.price = priceVo != null ? priceVo.price : 0;
        }
        if (albumItem.price <= 0) {
            albumItem.price = item.retailPrice;
        }
        if (album.addAlbumItem(albumItem)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 管理员商品列表编辑
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月25日 上午1:02:36
     */
    public static void itemList() {
        render();
    }

    /**
     * 查询商品列表
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年7月25日 上午1:41:46
     */
    public static void querySupplierItemByVo(@Required ItemSearchVo vo) {
        Page<Item> page = Item.findItemPage(vo);
        renderPageJson(page);
    }

    /**
     * 批量删除
     *
     * @param itemIds
     * @since v1.0
     * @author Calm
     * @created 2016年7月25日 上午1:43:35
     */
    public static void removeItems(@Required @As(",") List<Long> itemIds) {
        if (MixHelper.isEmpty(itemIds)) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 数据校验
        for (long id : itemIds) {
            Item item = Item.findById(id);
            if (item == null) {
                renderFailedJson(ReturnCode.FAIL);
                break;
            }
        }
        if (Item.batchUpdateStatus(itemIds, ItemStatus.DELETED)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 
     * 商品上架
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午5:41:58
     */
    public static void shelveItem(@Required @Min(1) long id) {
        handleWrongInput(true);

        Item.updateStatus(id, ItemStatus.ONLINE);
        renderSuccessJson();
    }

    /**
     * 
     * 商品下架
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午5:42:05
     */
    public static void unshelveItem(@Required @Min(1) long id) {
        handleWrongInput(true);

        Item.updateStatus(id, ItemStatus.HIDE);
        renderSuccessJson();
    }

    /**
     * 微信用户管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年8月4日 下午4:17:02
     */
    @UploadSupport
    public static void chatManage() {
        render();
    }

    /**
     * 查询微信用户列表
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午5:46:18
     */
    public static void queryWechatUser(@Required WechatUserVo vo) {
        handleWrongInput(true);
        Page<WechatUser> wu = WechatUser.findByVo(vo);
        renderPageJson(wu.items, wu.totalCount);
    }

    /**
     * 根据id查询微信用户信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午6:21:53
     */
    public static void queryWechatUserById(@Required long id) {
        handleWrongInput(true);
        WechatUser wu = WechatUser.findById(id);
        if (wu != null) {
            renderJson(wu);
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 删除微信用户
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午5:57:05
     */
    public static void deleteWechatUser(@Required long id) {
        handleWrongInput(true);
        boolean result = WechatUser.delete(id);
        if (result) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 添加用户到授权管理内
     *
     * @param moudble
     * @param am
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午5:36:36
     */
    public static void saveAuthUserToMoudle(@Required @Valid AuthManage am) {
        handleWrongInput(true);
        if (am.save()) {
            // 更新用户授权时间
            WechatUser.updateAuthTimeBatch(am.users);
            WechatUser.updateLastAuthTimeBatch(am.users);
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 查询模块授权管理内容
     *
     * @param type
     * @param moudleId
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午5:43:28
     */
    public static void queryMoudleAuth(@Required @Match("album") String type, @Required String moudleId) {
        handleWrongInput(true);
        AuthManage am = AuthManage.findByMoubleId(moudleId, type);
        if (am != null) {
            am.parseTodetail();
            renderJson(am);
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 
     * 添加供应商
     *
     * @param supplier
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午7:21:58
     */
    @Deprecated
    public static void addSupplier(@Required @Valid Supplier supplier) {
        handleWrongInput(true);
        // 添加用户信息
        boolean ret = supplier.save();
        // 注册成功
        if (ret) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "添加失败");
    }

    /**
     * 
     * 添加零售商
     *
     * @param retailer
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午7:27:43
     */
    @Deprecated
    public static void addRetailer(@Valid Retailer retailer) {
        handleWrongInput(true);
        // 添加信息
        boolean ret = retailer.save();
        // 注册成功
        if (ret) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "添加失败");
    }

    /**
     * 搜索列表明星产品
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年8月4日 下午4:17:02
     */
    public static void searchShowCase() {
        StarItemSetting sis = StarItemSetting.findFirst();
        if (sis != null) {
            renderArgs.put("starItems", sis);
        }
        render();
    }

    /**
     * 查询明星商品配置信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午2:52:42
     */
    public static void queryStarItemSetting() {
        StarItemSetting sis = StarItemSetting.findFirst();
        renderJson(sis);
    }

    /**
     * 保存明星商品配置
     *
     * @param setting
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午2:52:03
     */
    public static void saveStarItemSetting(@Required StarItemSetting setting) {
        handleWrongInput(true);
        long id = setting.checkItemExsit();
        if (id != 0) {
            renderFailedJson(ReturnCode.FAIL, "商品ID(" + id + ")不存在或已删除,已下架，保存失败");
        }
        id = setting.checkItemRepeat();
        if (id != 0) {
            renderFailedJson(ReturnCode.FAIL, "商品ID(" + id + ")重复配置，保存失败");
        }
        if (setting.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "保存失败");
    }

    /**
     * 管理员模拟用户登录
     *
     * @param phone
     * @since v1.0
     * @author Calm
     * @created 2016年9月12日 下午2:29:10
     */
    public static void mockUser(@Required @Match(RegexConstants.PHONE) String phone) {
        handleWrongInput(true);
        User usr = User.findByPhone(phone);
        if (usr == null || usr.isDelete) {
            handleWrongInput();
        }
        Secure.setUserToContainer(usr);
        redirect("/user/home");
    }

    /**
     * 零售商根据条件检索交易记录
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年9月12日 下午2:34:42
     */
    public static void queryTradeByVo(@Required TradeSearchVo vo) {
        handleWrongInput(true);
        // 用户信息获取
        Page<TradeVo> page = Page.newInstance(vo.pageNo, vo.pageSize, 0);
        List<Trade> trades = Trade.findListWithOrdersByVo(vo);
        if (MixHelper.isEmpty(trades)) {
            renderPageJson(page.items, page.totalCount);
        }
        // 转换为交易视图
        List<TradeVo> vos = trades.stream().map(t -> TradeVo.valueOfTrade(t)).filter(v -> v != null)
            .collect(Collectors.toList());
        if (MixHelper.isNotEmpty(vos)) {
            page = Page.newInstance(vo.pageNo, vo.pageSize, vos.size());
            page.items = vos;
        }
        renderPageJson(page.items, page.totalCount);
    }

    /**
     * 取消交易
     *
     * @param note 取消原因，取消备注
     * @since v1.0
     * @author Calm
     * @created 2016年9月17日 下午1:01:00
     */
    public static void cancelTrade(@Required long tradeId, String note) {
        Trade trade = Trade.findById(tradeId);
        if (trade == null) {
            renderFailedJson(ReturnCode.FAIL, "交易不存在！");
        }
        if (trade.cancelBySystem()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "交易取消失败！");
    }

    /**
     * 运费模板管理
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年9月24日 下午4:17:02
     */
    public static void freightManage() {
        render();
    }

    /**
     * 查询供应商模板
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 下午3:46:54
     */
    public static void queryFreightTemp(FreightSearchVo vo) {
        Page<FreightTemp> temps = FreightTemp.findFreightTempByVo(vo);
        if (temps != null && MixHelper.isNotEmpty(temps.items)) {
            List<FreightTempVo> vos = temps.items.stream().map(t -> FreightTempVo.valueOfItem(t, false))
                .collect(Collectors.toList());
            renderPageJson(vos, vos.size());
        }
        renderPageJson(temps.items, temps.totalCount);
    }

    /**
     * 订单管理页面
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年9月27日 下午4:17:02
     */
    public static void orderList() {
        render();
    }

    /**
     * 根据交易id导出交易子订单excel
     *
     * @param tradeId
     * @since v1.0
     * @author Calm
     * @created 2016年9月14日 上午11:08:02
     */
    public static void exportOrderExcel(@Required long tradeId, @Required @As(",") List<String> columns) {
        if (validation.hasErrors()) {
            renderText("交易ID:" + tradeId + ",导出订单Excel失败!");
        }
        Trade trade = Trade.findWithOrdersById(tradeId);
        if (trade != null) {
            String[] cnHead = OrderVo.parseHeadMap(columns);
            if (cnHead == null || cnHead.length == 0) {
                renderText("交易ID:" + tradeId + ",未选择任何excel文档表头，如(订单ID，商品ID...)");
            }
            List<Map<Integer, Object>> orderDatas = OrderVo.orderVosToExcelData(trade.orders, columns).stream()
                .filter(m -> m != null).collect(Collectors.toList());
            MixHelper.print(orderDatas);
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                if (TSFileUtil.exportExcel(byteOut, cnHead, orderDatas, "订单列表")) {
                    response.setContentTypeIfNotSet("application/msexcel;");
                    response.setHeader("Content-Disposition",
                        new String(("attachment;filename=" + tradeId + ".xls").getBytes("GB2312"), "UTF-8"));
                    response.out.write(byteOut.toByteArray());
                } else {
                    renderText("交易ID:" + tradeId + ",导出订单Excel失败");
                }
                byteOut.close();
            } catch (Exception ex) {
                log.error("tradeId export excel error", ex);
                renderText("交易ID:" + tradeId + ",导出订单Excel失败");
            }
        }
        renderText("交易ID:" + tradeId + ",导出订单Excel失败");
    }

    /**
     * 订单详情页面
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年9月27日 下午4:17:02
     */
    public static void tradeOrders(@Required @Min(1) long tradeId) {
        handleWrongInput(true);
        Trade trade = Trade.findWithOrdersById(tradeId);
        if (trade != null) {
            renderArgs.put("tradeId", tradeId);
            renderArgs.put("totalFee", trade.totalFee);
            renderArgs.put("payment", trade.payment);
            if (MixHelper.isNotEmpty(trade.orders)) {
                List<OrderVo> vos = trade.orders.stream().map(o -> OrderVo.valueOfOrderParseFee(o))
                    .filter(v -> v != null).collect(Collectors.toList());
                renderArgs.put("orders", vos);
            }
        }
        render();
    }

    /**
     * 保存交易订单(添加或更新)
     *
     * @param tradeId
     * @param realityPayment
     * @param orders
     * @since v1.0
     * @author Calm
     * @created 2016年9月29日 下午3:54:02
     */
    public static void saveTradeOrders(@Required long tradeId, @Required List<OrderVo> orders) {
        handleWrongInput(true);
        Trade trade = Trade.findById(tradeId);
        if (trade == null) {
            renderFailedJson(ReturnCode.FAIL, "保存失败,交易不存在");
        }
        List<Order> parseOrders = Lists.newArrayList();
        // 订单生成错误消息集合
        List<String> messages = Lists.newArrayList();
        for (int i = 0; i < orders.size(); i++) {
            OrderVo vo = orders.get(i);
            if (vo == null) {
                continue;
            }
            // 订单删除
            if (vo.isDelete) {
                boolean delete = Order.deleteById(vo.id);
                log.info("delete order, id = {}," + delete, vo.id);
                continue;
            }
            // === 订单视图数据检查
            String checkResult = "";
            // 订单(零售商id,交易ID)
            Order order = new Order(trade.retailerId, tradeId);
            if (vo.id > 0) {
                // 从视图中转换订单
                checkResult = vo.parseExsitOrder(order);
            } else {
                // 多个校验存在，分割
                checkResult = vo.checkValid();
                if (Strings.isNullOrEmpty(checkResult)) {
                    // 从视图中转换订单
                    checkResult = vo.parseToOrder(order);
                } else {
                    checkResult = checkResult.substring(0, checkResult.length() - 1);
                }
            }
            if (!Strings.isNullOrEmpty(checkResult)) {
                checkResult = "<span class='row_num'>行号:" + i + 1 + "</span>" + "<p class='error_desc'>" + checkResult
                    + "</p>";
                messages.add(checkResult);
                continue;
            }
            // 还要检验订单字段是否完整
            parseOrders.add(order);
        }
        // 错误信息输出
        if (MixHelper.isNotEmpty(messages)) {
            renderJson(ReturnCode.BIZ_LIMIT, messages);
        }
        // 重置交易金额
        trade.resetFee();
        // 保存交易
        Iterator<Order> orderIterator = parseOrders.iterator();
        while (orderIterator.hasNext()) {
            Order order = orderIterator.next();
            // 货款
            trade.cargoFee += order.cargoFee;
            // 物流
            trade.shippingFee += order.shippingFee;
            // 实际支付
            trade.payment += order.totalFee;
        }
        // 确认生成交易
        boolean result = trade.calcFee().updateWithOrders(parseOrders);
        if (result) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "订单保存失败");
    }

    /**
     * 修改交易实际付款
     *
     * @param tradeId
     * @param payment
     * @since v1.0
     * @author Calm
     * @created 2016年10月14日 下午2:04:27
     */
    public static void modifyTradePayment(@Required long tradeId, @Required double payment) {
        handleWrongInput(true);
        Trade trade = Trade.findById(tradeId);
        if (trade == null) {
            renderFailedJson(ReturnCode.FAIL, "保存失败,交易不存在");
        }
        // 交易应付金额修改
        int trade_payment = new BigDecimal(payment).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
            .intValue();
        if (payment > 0) {
            trade.payment = trade_payment;
            if (trade.calcFee().updateFee()) {
                renderSuccessJson();
            }
        }
        renderFailedJson(ReturnCode.FAIL, "保存失败");
    }

    /**
     * 保存运费模板
     *
     * @param freightTemp
     * @param index
     * @param type
     * @since v1.0
     * @author Calm
     * @created 2016年8月10日 上午1:12:23
     */
    public static void saveFreightTemp(@Valid FreightTemp freightTemp) {
        handleWrongInput(true);
        FreightTemp checkFreightTemp = FreightTemp.findById(freightTemp._id);
        if (checkFreightTemp == null) {
            renderFailedJson(ReturnCode.FAIL, "保存失败，模板不存在！");
        }
        // 检查模板重复
        if (freightTemp.checkNameDuplicate()) {
            renderFailedJson(ReturnCode.FAIL, "保存失败，名称重复！");
        }
        freightTemp.userId = checkFreightTemp.userId;
        if (freightTemp.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "保存失败");
    }

    /**
     * 删除运费模板
     *
     * @param tempIndex
     * @since v1.0
     * @author Calm
     * @created 2016年8月10日 上午1:12:01
     */
    public static void deleteFreightTemp(@Required long id) {
        handleWrongInput(true);
        FreightTemp sft = FreightTemp.findById(id);
        if (sft == null) {
            renderFailedJson(ReturnCode.FAIL, "模板不存在，删除失败！");
        }
        int count = sft.checkItemUse();
        if (count > 0) {
            renderFailedJson(ReturnCode.FAIL, "该模板正在被(" + count + ")个商品使用，不允许删除！");
        }
        if (sft.delete()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 运费模板管理编辑
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年9月24日 下午4:17:02
     */
    public static void freightEdit(long id) {
        FreightTemp freightTemp = FreightTemp.findById(id);
        if (freightTemp != null) {
            renderArgs.put("freightTemp", freightTemp);
        }
        render();
    }

}
package controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Objects;

import controllers.annotations.UploadSupport;
import controllers.annotations.UserLogonSupport;
import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.ItemStatus;
import models.Brand;
import models.FreightTemp;
import models.Item;
import models.Supplier;
import models.SupplierSendLocationTemp;
import models.User;
import play.data.binding.As;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.With;
import vos.ItemSearchVo;
import vos.ItemVo;
import vos.SupplierVo;

/**
 * 供应商中心
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月20日 下午5:52:00
 */
@With(Secure.class)
public class SupplierController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);

    /**
     * 修改供应商信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年6月20日 下午5:22:44
     */
    @UserLogonSupport
    public static void edit() {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        renderArgs.put("user", user);
        // 供应商信息获取
        Supplier supplier = Supplier.findById(user.userId);
        renderArgs.put("supplier", supplier);
        render();
    }

    /**
     * 信息更新
     *
     * @param supplier
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午1:14:07
     */
    @UserLogonSupport
    public static void infoEdit(SupplierVo supplier) {
        if (Supplier.updateByVo(supplier)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 
     * 编辑或发布商品信息页面
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月7日 上午12:14:30
     */
    @UploadSupport
    @UserLogonSupport
    public static void itemEdit(long id) {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 编辑或发布
        ItemVo item = Item.itemDetailCacheById(id);
        if (item != null) {
            renderArgs.put("item", item);
        }
        // 供应商运费模板
        List<FreightTemp> freightTemps = FreightTemp.findUserFreightTempAllInCache(user.userId);
        if (freightTemps != null) {
            renderArgs.put("supFreightTemps", freightTemps);
        }
        render();
    }

    /**
     * 宝贝保存
     *
     * @param item
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 下午3:23:37
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void itemSave(@Valid ItemVo item) {
        String redirectUrl = "/supplier/item/edit" + (item.id > 0 ? "?id=" + item.id : "");
        if (validation.hasErrors()) {
            flash.error("添加商品失败！");
            redirect(redirectUrl);
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        Item itemObj = item.parseToItem();
        // 用户地址检查
        if (!itemObj.checkItemLocationRepeat()) {
            flash.error("添加商品失败！");
            redirect(redirectUrl);
        }
        Supplier sup = Supplier.findById(user.userId);
        itemObj.supplierId = sup.id;
        itemObj.supplierName = sup.name;
        // 商品保存\并保存地址信息
        if (itemObj.saveWithItemLocation()) {
            redirect("/supplier/item/list");
        }
        redirect(redirectUrl);
    }

    /**
     * 
     * 供应商的商品的列表
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月7日 上午12:09:19
     */
    public static void itemList() {
        // 输送品牌信息
        List<Brand> brands = Brand.findAllList();
        renderArgs.put("brands", brands);
        render();
    }

    /**
     * 批量下架商品
     *
     * @param itemIds
     * @since v1.0
     * @author Calm
     * @created 2016年6月15日 下午6:17:57
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void itemBatchHide(@Required @As(",") List<Long> itemIds) {
        if (MixHelper.isEmpty(itemIds)) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 数据校验
        for (Long id : itemIds) {
            Item item = Item.findById(id);
            if (item == null) {
                renderFailedJson(ReturnCode.FAIL);
                break;
            }
        }
        if (Item.batchUpdateStatus(itemIds, ItemStatus.HIDE)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 批量删除商品
     *
     * @param itemIds
     * @since v1.0
     * @author Calm
     * @created 2016年6月15日 下午6:18:42
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void itemBatchDelete(@Required @As(",") List<Long> itemIds) {
        if (MixHelper.isEmpty(itemIds)) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 数据校验
        for (Long id : itemIds) {
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
     * 批量上架
     *
     * @param itemIds
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午1:31:44
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void itemBatchOnline(@Required @As(",") List<Long> itemIds) {
        if (MixHelper.isEmpty(itemIds)) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 数据校验
        for (Long id : itemIds) {
            Item item = Item.findById(id);
            if (item == null || item.status != ItemStatus.HIDE) {
                renderFailedJson(ReturnCode.FAIL);
                break;
            }
        }
        if (Item.batchUpdateStatus(itemIds, ItemStatus.ONLINE)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 查询
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年6月16日 下午2:48:33
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void querySupplierItemByVo(@Required ItemSearchVo vo) {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        vo.supplierId = user.userId;
        Page<Item> page = Item.findItemPage(vo);
        renderPageJson(page.items, page.totalCount);
    }

    /**
     * 增加发货地址模板
     *
     * @param loactionTemp
     * @since v1.0
     * @author Calm
     * @created 2016年7月18日 下午4:11:33
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void confirmSendLocationTemp(@Required @Valid SupplierSendLocationTemp locationTemp) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }

        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 检查模板地址是否重复
        List<SupplierSendLocationTemp> lsst = SupplierSendLocationTemp.findListBySupplierId(user.userId);
        String currentBaseStr = locationTemp.toBaseLocationStr();
        if (MixHelper.isNotEmpty(lsst)) {
            for (SupplierSendLocationTemp sst : lsst) {
                if (Objects.equal(sst.toBaseLocationStr(), currentBaseStr)) {
                    renderFailedJson(ReturnCode.BIZ_LIMIT);
                    break;
                }
            }
        }
        locationTemp.supplierId = user.userId;
        if (locationTemp.save()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 查找供应商发货地址模板列表
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月18日 下午5:18:39
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void findSendLocationTempList() {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<SupplierSendLocationTemp> tempList = SupplierSendLocationTemp.findListBySupplierId(user.userId);
        renderJson(tempList);
    }

    /**
     * 
     * 供应商的运费模板
     *
     * @since v1.0
     * @author ketty
     * @created 2016年8月9日 上午12:09:19
     */
    public static void freight() {
        render();
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
    @UserLogonSupport(value = "SUPPLIER")
    public static void saveFreightTemp(@Valid FreightTemp freightTemp) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }

        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        freightTemp.userId = user.userId;
        // 检查模板重复
        if (freightTemp.checkNameDuplicate()) {
            renderFailedJson(ReturnCode.FAIL, "模板名称已存在");
        }
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
    @UserLogonSupport(value = "SUPPLIER")
    public static void deleteFreightTemp(@Required long id) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL, "删除失败！");
        }
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
     * 查询供应商
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月10日 下午1:09:28
     */
    @UserLogonSupport(value = "SUPPLIER")
    public static void querySupplierFreightTemp() {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<FreightTemp> supFreightTemp = FreightTemp.findUserFreightTempAllInCache(user.userId);
        renderJson(supFreightTemp);
    }
}

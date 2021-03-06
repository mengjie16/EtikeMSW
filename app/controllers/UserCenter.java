package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.taobao.api.internal.util.WebUtils;

import controllers.annotations.UploadSupport;
import controllers.annotations.UserLogonSupport;
import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.ItemStatus;
import enums.constants.ErrorCode;
import enums.constants.RegexConstants;
import models.Cart;
import models.Favorite;
import models.Item;
import models.RetailerAddress;
import models.User;
import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.mvc.With;
import vos.CartVo;

/**
 * 
 * 个人中心，可能是零售商的用户，也可能是供应商的用户
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2015-4-9 下午4:16:28
 */
@With(Secure.class)
public class UserCenter extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(UserCenter.class);

    /**
     * 
     * 个人中心页面
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:22:21
     */
    public static void index() {
        home();
    }

    /**
     * 
     * 主页
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:22:12
     */
    @UploadSupport
    @UserLogonSupport
    public static void home() {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        if (user == null) {
            redirect("/login?redirectUrl=" + WebUtils.encode(request.url));
        }
        if (Objects.equal(user.role, "RETAILER")) {
            redirect("/retailer/order/list");
        }
        if (Objects.equal(user.role, "SUPPLIER")) {
            redirect("/supplier/item/list");
        }
        render();
    }

    /**
     * 
     * 重设密码页面
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:22:12
     */
    public static void resetPass() {
        render();
    }

    /**
     * 
     * 修改用户密码页面
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月7日 上午12:14:30
     */
    @UploadSupport
    public static void editPass() {
        render();
    }

    /**
     * 
     * 我的收藏页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月7日 上午12:14:30
     */
    @UploadSupport
    public static void storeList() {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<Favorite> favorites = Favorite.findList(user.id);

        // hide offline item.
        List<Favorite> res = new ArrayList<Favorite>();
        for (Favorite favor : favorites) {
            Item item = Item.findBaseInfoById(favor.itemId);
            if (item.status == ItemStatus.ONLINE) {
                res.add(favor);
            }
        }

        renderArgs.put("favoriteList", res);
        render();
    }

    public static void hasSetFavorite(@Required @Valid long itemId) {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        if (user != null && Favorite.findById(itemId, user.id) > 0) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    @UserLogonSupport
    public static void setFavorite(@Required @Valid Favorite favorite) {
        handleWrongInput(true);

        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        favorite.retailerId = (int) user.id;
        if (Favorite.save(favorite)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "收藏失败");
    }

    @UserLogonSupport
    public static void deleteFavorite(@Required @Min(1) long id) {

        if (Favorite.delete(id)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "取消收藏失败");
    }

    /**
     * 
     * 我的足迹页面
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月7日 上午12:14:30
     */
    @UploadSupport
    @UserLogonSupport
    public static void markList() {
        render();
    }

    /**
     * 
     * 店铺授权页面
     *
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月7日 上午12:14:30
     */
    @UploadSupport
    @UserLogonSupport
    public static void authorize() {
        render();
    }

    /**
     * 
     * 我的订单列表
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:22:12
     */
    @UserLogonSupport
    public static void orderList() {
        render();
    }

    /**
     * 
     * 我的购物车
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:22:12
     */
    @UserLogonSupport
    public static void cart() {
        // 用户信息获取
        render();

    }

    /**
     * 
     * 确认订单
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:22:12
     * 
     */

    @UserLogonSupport
    public static void stepTwo(@Required long tradeId, @Required @As(",") @MinSize(1) List<Integer> confirmOrderIds) {
        handleWrongInput(true);

        // 0.用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);

        // 1、收货信息
        RetailerAddress retailerAddress = RetailerAddress.findByDefaultAddress((int) user.id);

        double totalCartPrice = 0;

        List<Cart> cartList = new ArrayList<Cart>();
        Cart cart = null;
        for (Integer s : confirmOrderIds) {
            cart = Cart.findById(s, user.id);
            if (cart != null) {
                cartList.add(cart);
                totalCartPrice = totalCartPrice + cart.cartPrice;
            }
        }

        List<CartVo> cartVos = CartVo.valueOfcartList(cartList);

        renderArgs.put("addr", retailerAddress);
        renderArgs.put("cartList", cartVos);
        renderArgs.put("totalCartPrice", totalCartPrice);
        renderArgs.put("tradeId", tradeId);
        renderArgs.put("confirmOrderIds", confirmOrderIds);

        // 2、支付方式

        // 3、物流方式

        // 4、商品清单

        // 5.删除购物车
        // if ( !MixHelper.isEmpty(cartList)) {
        // for(Cart c : cartList) {
        // cart.delete(c.id);
        // }
        // }

        // 6、结算信息

        render();
    }

    /**
     * 保存用户基本信息
     *
     * @param name
     * @param email
     * @param qq
     * @param weixin
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午12:55:01
     */
    @UserLogonSupport
    public static void saveBaseInfo(@Required @MaxSize(32) String name, @Required @Email String email, String qq,
            String weixin) {
        handleWrongInput(true);
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        User currentUser = User.findById(user.id);
        if (!Strings.isNullOrEmpty(email)) {
            // 用户邮箱是否与其他人重复
            if (User.checkEmailUsedByOthers(email, user.id)) {
                renderFailedJson(ErrorCode.USER_EMAIL_DUPLICATE.code, ErrorCode.USER_EMAIL_DUPLICATE.description);
            }
            currentUser.email = email;
        }
        if (!Strings.isNullOrEmpty(name)) {
            // 检查用户名称是否与其他人重复
            if (User.checkNameUsedByOthers(name, user.id)) {
                renderFailedJson(ErrorCode.USER_NAME_DUPLICATE.code, ErrorCode.USER_NAME_DUPLICATE.description);
            }
            currentUser.name = name;
        }
        currentUser.qq = qq;
        currentUser.weixin = weixin;
        if (currentUser.save()) {
            User.removeCache(currentUser.id);
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 用户修改密码
     *
     * @param pass
     * @param captcha
     * @since v1.0
     * @author Calm
     * @created 2016年7月29日 下午3:07:29
     */
    @UserLogonSupport
    public static void modifyPass(@Required @Match(RegexConstants.PASSWORD) String pass) {
        handleWrongInput(true);
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 校验验证码正确性
        /*
         * boolean matched = SmsUtil.checkSmsCode(user.phone, captcha);
         * if (!matched) {
         * renderFailedJson(ReturnCode.FAIL, "验证码错误");
         * }
         */
        user.password = pass;
        if (user.savePassword()) {
            log.info("User id={} changed password to {} just now", user.id, pass);
            Secure.removeUserByContainer();
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "修改失败");
    }

    /**
     * 修改头像URL
     *
     * @param avatar
     * @since v1.0
     * @author Calm
     * @created 2016年7月26日 上午11:15:51
     */
    @UserLogonSupport
    public static void uploadAvatar(@Required @URL String avatar) {
        handleWrongInput(true);
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        user.avatar = avatar;
        if (user.save()) {
            renderSuccessJson();
        }
    }

    /**
     * 退出登录状态
     *
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午4:27:56
     */
    public static void loginOut() {
        Secure.removeUserByContainer();
        redirect("/login");
    }

    /**
     * 
     * 登录页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月24日 下午4:12:22
     */
    public static void login(@MaxSize(128) String redirectUrl, boolean switchUser) {
        if (!Strings.isNullOrEmpty(redirectUrl)) {
            renderArgs.put("rUrl", redirectUrl);
        }
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        if (user != null && !switchUser) {
            renderArgs.put("quickLogin", true);
        }
        render();
    }

    /**
     * 
     * 快速登录
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月9日 下午5:18:50
     */
    public static void quickLogin() {
        User user = Secure.tryGetUserFromContainer();
        if (user == null) {
            renderFailedJson(ReturnCode.INVALID_PRIVILEGE, "身份已失效");
        }

        renderJson(ImmutableMap.of("uid", user.id, "name", user.name));
    }

    /**
     * 
     * 接受登录请求
     *
     * @param phone
     * @param password
     * @param savePass
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月9日 下午5:20:02
     */
    public static void doLogin(@Required String name,
            @Required @MinSize(6) @MaxSize(20) String password, boolean savePass, @MaxSize(128) String rUrl) {
        handleWrongInput(true);

        User usr = User.findByField("name", name);
        if (usr == null) {
            renderFailedJson(ReturnCode.FAIL, "用户不存在");
            flash.error("用户不存在");
        }
        if (!usr.validate(password)) {
            flash.error("密码不正确");
            renderFailedJson(ReturnCode.INVALID_PRIVILEGE, "密码不正确");
        }

        if (savePass) {
            Secure.setUserToContainer(usr, "300d");
        } else {
            Secure.setUserToContainer(usr);
        }

        usr.updateLastLoginTime(usr);

        renderJson(ImmutableMap.of("uid", usr.id, "name", usr.name));
    }

}

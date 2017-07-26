package controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.aton.config.ReturnCode;
import com.aton.db.handler.JsonArrayTypeHandler;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.vo.AjaxResult;
import com.aton.vo.Page;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;


import controllers.annotations.UserLogonSupport;
import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.AliPayTradeStatus;
import enums.ItemStatus;
import enums.OrderStatus;
import enums.TradeStatus;
import enums.constants.CacheType;
import models.RetailerAddress;
import models.AliPayTrade;
import models.Cart;
import models.Favorite;
import models.Item;
import models.Order;
import models.Retailer;
import models.Supplier;
import models.SupplierSendLocationTemp;
import models.Trade;
import models.User;
import models.mappers.RetailerAddressMapper;
import net.sf.json.JSONArray;
import play.data.binding.As;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.With;
import utils.TSFileUtil;
import vos.AddressVo;
import vos.CartVo;
import vos.ItemVo;
import vos.OrderProductResult;
import vos.OrderVo;
import vos.TradeSearchVo;
import vos.TradeVo;

/**
 * 零售商中心
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月20日 下午5:52:00
 */
@With(Secure.class)
public class RetailerController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(RetailerController.class);

    /**
     * 修改零售商信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年6月20日 下午5:22:20
     */
    @UserLogonSupport(value = "RETAILER")
    public static void edit() {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        renderArgs.put("user", user);
        // 供应商信息获取
        Retailer retailer = Retailer.findById(user.id);
        renderArgs.put("retailer", retailer);
        render();
    }

    /**
     * 零售商购物车
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void cart() {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<Cart> cartItems = Cart.findList(user.id);
        
        //hide offline item.
        List<Cart> res = new ArrayList<Cart>();
        for(Cart cart : cartItems){
            Item item = Item.findBaseInfoById(cart.itemId);
            if(item.status == ItemStatus.ONLINE){
                res.add(cart);
            }
        }
        
        List<CartVo> cartVos = CartVo.valueOfcartList(res);
        
        renderJson(cartVos);
    }

    /**
     * 删除购物车商品
     *
     * @param itemId 商品ID
     * @param count 删除数量
     * @since v1.0
     * @author Calm
     * @created 2016年7月14日 下午3:06:46
     */
    @UserLogonSupport(value = "RETAILER")
    public static void cartRemove(@Required @Min(1) long id) {
        // 移除购物车
        if (Cart.delete(id)) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "删除购物车失败");
    }
    
    @UserLogonSupport(value = "RETAILER")
    public static void cartAdd(@Required @Valid Cart cart) {
        handleWrongInput(true);
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        cart.retailerId = (int) user.id;       
        if(Cart.save(cart)){
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "添加购物车失败");
    }
    
    @UserLogonSupport(value = "RETAILER")
    public static void cartUpdateBatchCount(@Required @Valid String carts) {
       handleWrongInput(true);
      
       List<Cart> catList= (List<Cart>)JSONArray.toList(JSONArray.fromObject(carts), Cart.class);
       
        for(Cart cart : catList) {
            cart.update(cart);
        }
        
        renderSuccessJson();
    }



    /**
     * 付款页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void payFor() {
        render();
    }
    /**
     * 退款页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void refund() {
        render();
    }

    /**
     * 订单详情页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderinfo() {
        render();
    }

    /**
     * 物流详情页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void logistics() {
        render();
    }

    /**
     * 地址管理页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void address() {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<RetailerAddress> list = RetailerAddress.findListByRetailerId((int) user.id);
        renderArgs.put("addressList", list);
        render();
    }
    
    @UserLogonSupport(value = "RETAILER")
    public static void addressList() {
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<RetailerAddress> list = RetailerAddress.findListByRetailerId((int) user.id);
        renderJson(list);
    }
    
    
    @UserLogonSupport(value = "RETAILER")
    public static void addressSave(@Required @Valid RetailerAddress address) {
        handleWrongInput(true);
        
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 检查模板地址是否重复
        List<RetailerAddress> lsst = RetailerAddress.findListByRetailerId((int) user.id);
        String currentBaseStr = address.toBaseLocationStr();
        if (MixHelper.isNotEmpty(lsst)) {
            for (RetailerAddress sst : lsst) {
                if (Objects.equal(sst.toBaseLocationStr(), currentBaseStr)) {
                    renderFailedJson(ReturnCode.BIZ_LIMIT, "模板名称相同，添加失败");
                    break;
                }
            }
        }else{
            address.defaultAddress = true;
        }
        
        address.retailerId = (int) user.id;
              
        boolean ret = RetailerAddress.save(address);
        if (ret) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "添加失败");
    }

    
    
    @UserLogonSupport(value = "RETAILER")
    public static void addressUpdate(@Required @Valid RetailerAddress retailerAddress) {
        handleWrongInput(true);
       
        if ( RetailerAddress.update(retailerAddress) ) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "更新失败");
    }
    
    @UserLogonSupport(value = "RETAILER")
    public static void addressGet(@Required @Valid long id) {
        handleWrongInput(true);
       RetailerAddress retailAddress = null;
       retailAddress = RetailerAddress.findById(id);
        if ( retailAddress != null) {
        	renderArgs.put("retailAddress", retailAddress);
            renderJson(ReturnCode.OK, retailAddress);
        }
        renderFailedJson(ReturnCode.FAIL, "该地址不存在");
    }

    
    @UserLogonSupport(value = "RETAILER")
    public static void addressDelete(@Required @Valid long id) {
        handleWrongInput(true);
       
        if ( RetailerAddress.deleteById(id) ) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "删除失败");
    }
    
    
    @UserLogonSupport(value = "RETAILER")
    public static void addressUpdateDefault(@Required @Valid long id) {
        handleWrongInput(true);
        
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        if ( RetailerAddress.updateDefaultAddress((int)user.id, id)  ) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "更新默认地址失败");
    }


    /**
     * 我的订单页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderList() {
        render();
    }

    /**
     * 零售商支付
     *
     * @param fee
     * @since v1.0
     * @author Calm
     * @created 2016年8月26日 上午11:08:30
     */
    @UserLogonSupport(value = "RETAILER")
    public static void payFee(@Required @Min(0) double fee) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL, "支付失败！");
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 取两位小数
        String totalFeeStr = new java.text.DecimalFormat("0.00").format(fee);
        int totalFee = (int) (Doubles.tryParse(totalFeeStr) * 100);
        // 创建交易
        AliPayTrade trade = new AliPayTrade();
        trade.subject = "支付：" + fee + "元";
        trade.tradeStatus = AliPayTradeStatus.WAIT_BUYER_PAY;
        trade.totalFee = totalFee;
        trade.userId = user.id;
        trade = trade.create();
        if (trade != null) {
            // 放入redirect URL
            // 从缓存中获取支付宝通知URL
            String key = CacheType.PAY_REDIRECT_URL.getKey(trade.id);
            CacheUtils.set(key, "/retailer/pay", CacheType.PAY_REDIRECT_URL.expiredTime);
        }
        // 交给支付中心处理
        PayCenter.alipay_self(trade);
    }

    /**
     * 解析order数据
     *
     * @param blod
     * @since v1.0
     * @author Calm
     * @created 2016年9月3日 下午1:21:12
     */
    @UserLogonSupport(value = "RETAILER")
    public static void parseOrderFromExcel(@Required File uploadExcelFile) {
        if (uploadExcelFile == null) {
            renderHtml(new AjaxResult(ReturnCode.FAIL, "文件不能为空!").toJson());
        }
        File orderFile = uploadExcelFile;
        // 文件格式校验
        String preFix = orderFile.getName().substring(orderFile.getName().lastIndexOf(".") + 1);
        if (!(Objects.equal("xlsx", preFix) || Objects.equal("xls", preFix))) {
            // 删除文件
            orderFile.delete();
            renderHtml(new AjaxResult(ReturnCode.FAIL, "文件格式不正确，请选择(xlsx,xls)格式excel文件").toJson());
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 先清楚之前的订单数据缓存
        user.removeOrderAboutData();
        Table orderData = TSFileUtil.parseExcel(orderFile);
        if (orderData != null) {
            // 删除文件
            orderFile.delete();
            CacheUtils.set(CacheType.RETAILER_ORDER_TABLE_DATA.getKey(user.id), orderData,
                CacheType.RETAILER_ORDER_TABLE_DATA.expiredTime);
            // 首行
            Map<Integer, String> rowData = orderData.row(0);
            // 检查是否包含正常列头信息
            List<String> columns = rowData.values().stream().filter(s -> !Strings.isNullOrEmpty(s))
                .collect(Collectors.toList());
            if (MixHelper.isEmpty(columns)) {
                renderHtml(new AjaxResult(ReturnCode.FAIL, "excel文件未包含列头信息(如：订单编号...)！").toJson());
            }
            renderHtml(new AjaxResult(columns).toJson());
        } else {
            // 删除文件
            orderFile.delete();
            renderHtml(new AjaxResult(ReturnCode.FAIL, "excel文件订单数据解析失败！").toJson());
        }
        // 删除文件
        orderFile.delete();
        renderHtml(new AjaxResult(ReturnCode.FAIL, "上传失败").toJson());
    }

    /**
     * 生成订单视图展现
     *
     * @param orderVo
     * @since v1.0
     * @author Calm
     * @created 2016年9月8日 下午2:30:46
     */
    public static void parseOrderVo(List<Integer> confirmOrder, long id) {
        // 获取读取解析过的数据
//        if (MixHelper.isEmpty(confirmOrder)) {
//            log.info("提交了空的订单");
//        }
        RetailerAddress retailerAddress = RetailerAddress.findByDefaultAddress((int)id);
        
        List<OrderVo> orderVoList = Lists.newArrayList();
        
        // 过滤已删除订单 //
        log.info("开始生成订单....");
        // 订单生成错误消息集合
        List<String> messages = Lists.newArrayList();
        for (int i = 0; i < confirmOrder.size(); i++) {
            OrderVo vo = null;
            // 提交的行列
            int oindex = confirmOrder.get(i);
           
            Cart cart = Cart.findById(oindex, id);
            if(cart ==null){
                 log.info("无订单文件解析数据，或已丢失");
            }
            
            vo.buyerName = retailerAddress.name;
            vo.skuStr = cart.sku();
            vo.contact = retailerAddress.phone;
            vo.productName = cart.title;
            if (Strings.isNullOrEmpty(vo.productName)) {
                vo.productName = "订单商品:" + i;
            }
            vo.province = retailerAddress.province;
            vo.city = retailerAddress.city;
            vo.region = retailerAddress.region;
            vo.address = retailerAddress.address;
            vo.num = cart.cartCount;
            vo.provinceId = retailerAddress.provinceId;
            
            // 关键信息解析
            vo.md5ProductNameSkuStr();
            // 解析地址
            vo.parseAddress();
            // === 订单数据解析后检查
            String checkResult = vo.checkValid();
            if (!Strings.isNullOrEmpty(checkResult)) {
                String message = "<span class='row_num'>行号:" + i + "</span>" + "<p class='error_desc'>"
                    + checkResult.substring(0, checkResult.length() - 1) + "</p>";
                log.warn(message);
                messages.add(message);
                continue;
            }
            // 订单添加
            orderVoList.add(vo);
        }
        
        if (MixHelper.isNotEmpty(messages)) {
            log.error(""+ ReturnCode.BIZ_LIMIT);
        }
        // 缓存订单视图
        String ordervoKey = CacheType.RETAILER_ORDER_VO_DATA.getKey(id);
        CacheUtils.set(ordervoKey, orderVoList, CacheType.RETAILER_ORDER_VO_DATA.expiredTime);

        // 订单信息商品归组
        Map<String, List<Map<String, String>>> productMap = orderVoList.stream().collect(Collectors.groupingBy(
            OrderVo::getMd5ProductName, Collectors.mapping(OrderVo::getProductSkuMap, Collectors.toList())));
        // 映射成商品信息结果集
        List<OrderProductResult> results = OrderVo.parseToOrderProductResult(productMap);
        if (MixHelper.isEmpty(results)) {
            log.error("订单解析失败,商品信息解析，匹配失败！");
        }
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_PRODUCT_DATA.getKey(id);
        CacheUtils.set(pkey, results, CacheType.RETAILER_ORDER_PRODUCT_DATA.expiredTime);
        // 解析成功
    }

    /**
     * 根据规格匹配信息生成订单
     *
     * @param sku
     * @since v1.0
     * @author Calm
     * @created 2016年9月10日 下午5:32:35
     */
    @UserLogonSupport(value = "RETAILER")
    public static void generateOrderVo(@Required Map<String, String> sku, @Required Map<String, Long> productName) {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 获取订单
        String ordervoKey = CacheType.RETAILER_ORDER_VO_DATA.getKey(user.id);
        List<OrderVo> listOrderVo = CacheUtils.get(ordervoKey);
        if (MixHelper.isEmpty(listOrderVo)) {
            renderFailedJson(ReturnCode.FAIL, "订单数据丢失，订单商品匹配失败");
        }
        // 融合订单商品信息
        List<OrderVo> orderVoList = OrderVo.parseVoList(listOrderVo, sku, productName);
        if (MixHelper.isEmpty(orderVoList)) {
            renderFailedJson(ReturnCode.FAIL, "订单商品匹配失败");
        }
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_VO_ALL.getKey(user.id);
        CacheUtils.set(pkey, orderVoList, CacheType.RETAILER_ORDER_VO_ALL.expiredTime);
        renderSuccessJson();
    }

    /**
     * 提交订单第一步页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderStepOne() {
        render();
    }

    /**
     * 提交订单第二步页面(配对订单表头信息)
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderStepTwo() {
        render();
    }

    /**
     * 提交订单第三步页面(解析商品信息)
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderStepThree() {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_PRODUCT_DATA.getKey(user.id);
        List<OrderProductResult> products = CacheUtils.get(pkey);
        renderArgs.put("products", products);
        render();
    }

    /**
     * 提交订单第四步页面(列出订单列表信息)
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderStepFour() {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_VO_ALL.getKey(user.id);
        List<OrderVo> orderVoList = CacheUtils.get(pkey);
        renderArgs.put("orderVoList", orderVoList);
        render();
    }

    /**
     * 
     * 财务流水页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年9月13日 上午1:36:57
     */
    @UserLogonSupport(value = "RETAILER")
    public static void finance() {
        render();
    }

    /**
     * 确认生成订单
     *
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 上午9:40:05
     */
    @UserLogonSupport(value = "RETAILER")
    public static void generateOrder(@Required @As(",") @MinSize(1) List<Integer> confirmOrderIds) {
        if (MixHelper.isEmpty(confirmOrderIds)) {
            log.info("提交了空的订单");
            renderFailedJson(ReturnCode.FAIL, "提交了空的订单");
        }
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
       
        OrderVo.parseOrderVo(confirmOrderIds, user.id);
        
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_VO_DATA.getKey((int)user.id);
        List<OrderVo> orderVoList = CacheUtils.get(pkey);
        if (MixHelper.isEmpty(orderVoList)) {
            log.info("无订单解析数据");
            renderFailedJson(ReturnCode.FAIL, "无订单解析数据");
        }
        log.info("开始生成订单....");
        List<Order> parseOrders = Lists.newArrayList();
        // 订单生成错误消息集合
        List<String> messages = Lists.newArrayList();
        for (int i = 0; i < orderVoList.size(); i++) {
            OrderVo vo = orderVoList.get(i);
            // 非法数据提交
            if (vo == null) {
                log.error("参数提交错误。。");
                continue;
            }
            // 检查订单数据完整性
            String message = vo.checkValid();
            if (!Strings.isNullOrEmpty(message)) {
                message = "<span class='row_num'>行号:" + (i + 1) + "</span>" + "<p class='error_desc'>" + message
                    + "</p>";
                messages.add(message);
                continue;
            }
            // 创建订单
            Order order = new Order((int) user.id);
            // 从视图中转换订单
            message = vo.parseToOrder(order);
            if (!Strings.isNullOrEmpty(message)) {
                message = "<span class='row_num'>行号:" + (i + 1) + "</span>" + "<p class='error_desc'>" + message
                    + "</p>";
                messages.add(message);
                continue;
            }
            parseOrders.add(order);
        }
        if (MixHelper.isNotEmpty(messages)) {
            renderJson(ReturnCode.BIZ_LIMIT, messages);
        }
        // 生成交易
        Trade trade = new Trade((int) user.id).tradeAuiting();
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
        Trade createTrade = trade.calcFee().createWithOrders(parseOrders);
        if (createTrade == null) {
            log.error("交易创建失败..");
            renderFailedJson(ReturnCode.FAIL, "订单提交失败！");
        }
        // -------------- 清除excel订单相关数据缓存
        user.removeOrderAboutData();
        
//        String redirectUrl = "/user/cart/stepTwo"+(trade.id > 0 ? "?tradeId=" + trade.id : "");
//        redirect(redirectUrl);
        
        
        renderJson(trade.id);
    }

 
    /**
     * 零售商根据条件检索交易记录
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年9月12日 下午2:34:42
     */
    @UserLogonSupport(value = "RETAILER")
    public static void queryTradeByVo(@Required TradeSearchVo vo) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL, "查询失败！");
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        vo.retailerId = (int) user.id;
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
    
    
    @UserLogonSupport(value = "RETAILER")
    public static void queryTradeByVoAndTradeStatus(@Required TradeSearchVo vo ,@Required String status) {
        TradeStatus tradeStatus = null;
        switch(status){
            case "TRADE_UNPAIED":
                tradeStatus = TradeStatus.TRADE_UNPAIED;
                break;
            case "TRADE_UNSEND":
                tradeStatus = TradeStatus.TRADE_SETTLEMENT_BEEN;
                break;
            case "TRADE_UNRECIIVED":
                tradeStatus = TradeStatus.TRADE_UNRECIIVED;
                break;            
        }
        
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL, "查询失败！");
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        vo.retailerId = (int) user.id;
        Page<TradeVo> page = Page.newInstance(vo.pageNo, vo.pageSize, 0);
        List<Trade> trades = Trade.selectListWithOrderTradeStatusByVo(vo, tradeStatus);
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
     * 交易订单详情页面
     *
     * @param tradeId
     * @since v1.0
     * @author Calm
     * @created 2016年9月14日 下午1:20:11
     */
    @UserLogonSupport(value = "RETAILER")
    public static void tradeOrders(@Required long tradeId) {
        Trade trade = Trade.findWithOrdersById(tradeId);
        if (trade != null) {
            renderArgs.put("tradeId", tradeId);
            if (MixHelper.isNotEmpty(trade.orders)) {
                List<OrderVo> vos = trade.orders.stream().map(o -> OrderVo.valueOfOrderParseFee(o))
                    .filter(v -> v != null).collect(Collectors.toList());
                renderArgs.put("orders", vos);
            }
        }
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
    @UserLogonSupport(value = "RETAILER")
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
                // TODO Auto-generated catch block
                log.error("tradeId export excel error,{}", ex);
                renderText("交易ID:" + tradeId + ",导出订单Excel失败");
            }
        }
        renderText("交易ID:" + tradeId + ",导出订单Excel失败");
    }

    /**
     * 取消交易
     *
     * @param note 取消原因，取消备注
     * @since v1.0
     * @author Calm
     * @created 2016年9月17日 下午1:01:00
     */
    @UserLogonSupport(value = "RETAILER")
    public static void cancelTrade(@Required long tradeId, String note) {
        Trade trade = Trade.findById(tradeId);
        if (trade == null) {
            renderFailedJson(ReturnCode.FAIL, "交易不存在！");
        }
        if (trade.cancelByUser()) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "交易取消失败！");
    }

    /**
     * 手工下订单页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 上午2:02:31
     */
    @UserLogonSupport(value = "RETAILER")
    public static void manualOrder() {
        render();
    }

    /**
     * 手工录入订单生成
     *
     * @param orders
     * @since v1.0
     * @author Calm
     * @created 2016年10月10日 下午5:23:49
     */
    public static void userGenerateOrder(@Required List<OrderVo> orders) {
        if (validation.hasErrors() || MixHelper.isEmpty(orders)) {
            log.info("订单生成，参数提交错误！");
            renderFailedJson(ReturnCode.FAIL, "订单提交失败！");
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        List<Order> parseOrders = Lists.newArrayList();
        // 过滤已删除订单 //
        log.info("开始生成订单....");
        // 订单生成错误消息集合
        List<String> messages = Lists.newArrayList();
        for (int i = 0; i < orders.size(); i++) {
            OrderVo vo = orders.get(i);
            // 非法数据提交
            if (vo == null) {
                log.error("参数提交错误。。");
                continue;
            }
            // 检查订单数据完整性
            String message = vo.checkValid();
            if (!Strings.isNullOrEmpty(message)) {
                message = "<span class='row_num'>行号:" + (i + 1) + "</span>" + "<p class='error_desc'>" + message
                    + "</p>";
                messages.add(message);
                continue;
            }
            // 创建订单
            Order order = new Order((int) user.id);
            // 从视图中转换订单
            message = vo.parseToOrder(order);
            if (!Strings.isNullOrEmpty(message)) {
                message = "<span class='row_num'>行号:" + (i + 1) + "</span>" + "<p class='error_desc'>" + message
                    + "</p>";
                messages.add(message);
                continue;
            }
            parseOrders.add(order);
        }
        if (MixHelper.isNotEmpty(messages)) {
            renderJson(ReturnCode.BIZ_LIMIT, messages);
        }
        // 生成交易
        Trade trade = new Trade((int) user.id).tradeAuiting();
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
        Trade createTrade = trade.calcFee().createWithOrders(parseOrders);
        if (createTrade == null) {
            log.error("交易创建失败..");
            renderFailedJson(ReturnCode.FAIL, "订单提交失败！");
        }
        // -------------- 清除excel订单相关数据缓存
        user.removeOrderAboutData();
        renderSuccessJson();
    }
    
    /**
     * 保存交易订单(添加或更新)
     *
     * 
     */
    @UserLogonSupport(value = "RETAILER")
    public static void orderUpdate(@Required OrderVo orderVo) {
        handleWrongInput(true);
        Trade trade = Trade.findById(orderVo.tradeId);
        if (trade == null) {
            renderFailedJson(ReturnCode.FAIL, "保存失败,交易不存在");
        }
        List<Order> parseOrders = Order.findListByTradeId(orderVo.tradeId);
      
        // 重置交易金额
        trade.resetFee();
        // 保存交易
        Iterator<Order> orderIterator = parseOrders.iterator();
        while (orderIterator.hasNext()) {
            Order order = orderIterator.next();
            order.status = OrderStatus.TRADE_UNPAIED;
            // 货款
            trade.cargoFee += order.cargoFee;
            // 物流
            trade.shippingFee += order.shippingFee;
            // 实际支付
            trade.payment += order.totalFee;
        }
        
        trade.status = TradeStatus.TRADE_UNPAIED;
        // 确认生成交易
        boolean result = trade.calcFee().updateWithOrders(parseOrders);
        
        if (result) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "订单保存失败");
    }
    
    
    @UserLogonSupport(value = "RETAILER")
    public static void orderDelete(@Required long tradeId){
        handleWrongInput(true);
        Trade trade = Trade.findById(tradeId);
        if (trade == null) {
            renderFailedJson(ReturnCode.FAIL, "删除失败,交易不存在");
        }
       
        boolean result =  trade.deleteWithOrders(tradeId);
        
        if (result) {
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "订单保存失败");
    }
    


}

package controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
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
import enums.constants.CacheType;
import models.AliPayTrade;
import models.Order;
import models.Retailer;
import models.Trade;
import models.User;
import play.data.binding.As;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.With;
import utils.TSFileUtil;
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
        Retailer retailer = Retailer.findById(user.userId);
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
        String key = CacheType.RETAILER_CART_INFO.getKey(user.phone);
        List<ItemVo> cartItems = (List<ItemVo>) CacheUtils.get(key);
        if (MixHelper.isEmpty(cartItems)) {
            cartItems = Lists.newArrayList();
        }
        renderJson(cartItems);
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
    public static void cartRemove(@Required @Min(1) long itemId, int count) {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        String key = CacheType.RETAILER_CART_INFO.getKey(user.phone);
        List<ItemVo> cartItems = (List<ItemVo>) CacheUtils.get(key);
        if (MixHelper.isEmpty(cartItems)) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 移除购物车
        boolean remove = false;
        List<ItemVo> lastCartItems = Lists.newArrayList();
        Iterator<ItemVo> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            ItemVo iv = iterator.next();
            if (iv.id == itemId) {
                if (count > 0) {
                    iv.cartCount -= count;
                } else {
                    iv.cartCount = 0;
                }
                remove = true;
            }
            if (iv.cartCount > 0) {
                lastCartItems.add(iv);
            }
        }
        if (remove) {
            CacheUtils.set(key, lastCartItems, CacheType.RETAILER_CART_INFO.expiredTime);
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL);
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
            CacheUtils.set(CacheType.RETAILER_ORDER_TABLE_DATA.getKey(user.userId), orderData,
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
    @UserLogonSupport(value = "RETAILER")
    public static void parseOrderVo(@Required Map<String, Integer> orderMapper) {
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 获取读取解析过的数据
        String key = CacheType.RETAILER_ORDER_TABLE_DATA.getKey(user.userId);
        Table orderData = CacheUtils.get(key);
        if (orderData == null) {
            log.error("文件未上传，或数据已丢失！");
            renderFailedJson(ReturnCode.FAIL, "无法获取excel文件数据，请检查是否已上传文件！");
        }
        // 未连线检查
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL, "订单解析失败,请检查是否完成连线！");
        }
        List<OrderVo> listOrderVo = Lists.newArrayList();
        // 订单解析错误消息集合
        List<String> messages = Lists.newArrayList();
        try {
            // 查看映射的内容（从第二行解析）
            for (int i = 1; i < orderData.rowMap().size(); i++) {
                // excel行号
                int rowNum = i + 1;
                // 当前行
                Map<Integer, String> mis = orderData.row(i);
                // 当前行是否为空
                boolean match = mis.values().stream().anyMatch(s -> !Strings.isNullOrEmpty(s));
                if (!match) {
                    // 空行过滤
                    continue;
                }
                OrderVo vo = new OrderVo();
                String checkMess = "";
                // 数据检查
                int contact = orderMapper.getOrDefault("contact", -1);
                int buyerName = orderMapper.getOrDefault("buyerName", -1);
                int province = orderMapper.getOrDefault("province", -1);
                int city = orderMapper.getOrDefault("city", -1);
                int region = orderMapper.getOrDefault("region", -1);
                int address = orderMapper.getOrDefault("address", -1);
                int address_detail_pcra = orderMapper.getOrDefault("address_detail_pcra", -1);
                int productNum = orderMapper.getOrDefault("num", -1);
                if (contact == -1) {
                    checkMess += "联系号码，";
                }
                if (buyerName == -1) {
                    checkMess += "顾客名称，";
                }
                // 带省份详细地址未匹配，那么省，市，区，地址都要匹配
                if (address_detail_pcra == -1) {
                    if (province == -1) {
                        checkMess += "顾客所在省，";
                    }
                    if (city == -1) {
                        checkMess += "顾客所在市，";
                    }
                    if (region == -1) {
                        checkMess += "顾客所在区，";
                    }
                    if (address == -1) {
                        checkMess += "顾客详细地址(不包含省市区)，";
                    }
                }
                if (productNum == -1) {
                    checkMess += "商品数量，";
                }
                if (!Strings.isNullOrEmpty(checkMess)) {
                    log.warn("excel表头连线匹配不完整： {}", checkMess);
                    renderFailedJson(ReturnCode.FAIL,
                        "excel表头连线匹配不完整： " + checkMess.substring(0, checkMess.length() - 1) + "未连线匹配");
                }
                vo.buyerName = mis.getOrDefault(buyerName, "");
                vo.skuStr = mis.getOrDefault(orderMapper.getOrDefault("sku_str", -1), "");
                vo.outOrderNo = mis.getOrDefault(orderMapper.getOrDefault("outOrderNo", -1), "");
                vo.contact = mis.getOrDefault(contact, "");
                vo.productName = mis.getOrDefault(orderMapper.getOrDefault("productName", -1), "");
                if (Strings.isNullOrEmpty(vo.productName)) {
                    vo.productName = "订单商品:" + i;
                }
                vo.province = mis.getOrDefault(province, "");
                vo.city = mis.getOrDefault(city, "");
                vo.region = mis.getOrDefault(region, "");
                vo.address = mis.getOrDefault(address, "");
                vo.address_detail_pcra = mis.getOrDefault(address_detail_pcra, "");
                vo.note = mis.getOrDefault(orderMapper.getOrDefault("note", -1), "");
                String timeStr = mis.getOrDefault(orderMapper.getOrDefault("createTime", -1), "");
                if (!Strings.isNullOrEmpty(timeStr)) {
                    try {
                        vo.createTime = DateUtils.parse(timeStr);
                    } catch (Exception ex) {
                        log.warn("parse date error, timeStr={}", timeStr);
                    }
                }
                String num = mis.getOrDefault(productNum, "0");
                try {
                    if (StringUtils.isNumeric(num)) {
                        vo.num = Ints.tryParse(num);
                    }
                } catch (Exception ex) {
                    log.warn("parse num error, num={}", num);
                }
                // 关键信息解析
                vo.md5ProductNameSkuStr();
                // 解析地址
                vo.parseAddress();
                // === 订单数据解析后检查
                String checkResult = vo.checkValid();
                if (!Strings.isNullOrEmpty(checkResult)) {
                    String message = "<span class='row_num'>行号:" + rowNum + "</span>" + "<p class='error_desc'>"
                        + checkResult.substring(0, checkResult.length() - 1) + "</p>";
                    log.warn(message);
                    messages.add(message);
                    continue;
                }
                // 订单添加
                listOrderVo.add(vo);
            }
        } catch (Exception ex) {
            log.error("订单数据从excel解析出错！{}", ex);
            renderFailedJson(ReturnCode.FAIL, "订单数据从excel解析出错,可以尝试检查文件是否正确！");
        }
        if (MixHelper.isNotEmpty(messages)) {
            renderJson(ReturnCode.BIZ_LIMIT, messages);
        }
        // 缓存订单视图
        String ordervoKey = CacheType.RETAILER_ORDER_VO_DATA.getKey(user.userId);
        CacheUtils.set(ordervoKey, listOrderVo, CacheType.RETAILER_ORDER_VO_DATA.expiredTime);

        // 订单信息商品归组
        Map<String, List<Map<String, String>>> productMap = listOrderVo.stream().collect(Collectors.groupingBy(
            OrderVo::getMd5ProductName, Collectors.mapping(OrderVo::getProductSkuMap, Collectors.toList())));
        // 映射成商品信息结果集
        List<OrderProductResult> results = OrderVo.parseToOrderProductResult(productMap);
        if (MixHelper.isEmpty(results)) {
            renderFailedJson(ReturnCode.FAIL, "订单解析失败,商品信息解析，匹配失败！");
        }
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_PRODUCT_DATA.getKey(user.userId);
        CacheUtils.set(pkey, results, CacheType.RETAILER_ORDER_PRODUCT_DATA.expiredTime);
        // 解析成功
        renderSuccessJson();
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
        String ordervoKey = CacheType.RETAILER_ORDER_VO_DATA.getKey(user.userId);
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
        String pkey = CacheType.RETAILER_ORDER_VO_ALL.getKey(user.userId);
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
        String pkey = CacheType.RETAILER_ORDER_PRODUCT_DATA.getKey(user.userId);
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
        String pkey = CacheType.RETAILER_ORDER_VO_ALL.getKey(user.userId);
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
    public static void generateOrder(@Required @As(",") @MinSize(1) List<Integer> confirmOrder) {
        if (MixHelper.isEmpty(confirmOrder)) {
            log.info("提交了空的订单");
            renderFailedJson(ReturnCode.FAIL, "提交了空的订单");
        }
        // 用户信息获取
        User user = renderArgs.get(Secure.FIELD_USER, User.class);
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_VO_ALL.getKey(user.userId);
        List<OrderVo> orderVoList = CacheUtils.get(pkey);
        if (MixHelper.isEmpty(orderVoList)) {
            log.info("无订单excel文件解析数据，或已丢失");
            renderFailedJson(ReturnCode.FAIL, "无订单excel文件解析数据，或已丢失");
        }
        List<Order> orders = Lists.newArrayList();
        // 过滤已删除订单 //
        log.info("开始生成订单....");
        // 订单生成错误消息集合
        List<String> messages = Lists.newArrayList();
        for (int i = 0; i < confirmOrder.size(); i++) {
            OrderVo vo = null;
            // 提交的行列
            int oindex = confirmOrder.get(i);
            // 非法数据提交
            if (oindex >= orderVoList.size() || (vo = orderVoList.get(oindex)) == null) {
                log.error("参数提交错误。。");
                renderFailedJson(ReturnCode.FAIL, "订单提交失败!");
                break;
            }
            // 创建订单
            Order order = new Order(user.userId);
            // 从视图中转换订单
            String message = vo.parseToOrder(order);
            if (!Strings.isNullOrEmpty(message)) {
                message = "<span class='row_num'>行号:" + (i + 1) + "</span>" + "<p class='error_desc'>" + message
                    + "</p>";
                messages.add(message);
                continue;
            }
            orders.add(order);
        }
        if (MixHelper.isNotEmpty(messages)) {
            renderJson(ReturnCode.BIZ_LIMIT, messages);
        }
        // 生成交易
        Trade trade = new Trade(user.userId).tradeAuiting();
        Iterator<Order> orderIterator = orders.iterator();
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
        Trade createTrade = trade.calcFee().createWithOrders(orders);
        if (createTrade == null) {
            log.error("交易创建失败..");
            renderFailedJson(ReturnCode.FAIL, "订单提交失败！");
        }
        // -------------- 清除excel订单相关数据缓存
        user.removeOrderAboutData();
        renderSuccessJson();
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
        vo.retailerId = user.userId;
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
            Order order = new Order(user.userId);
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
        Trade trade = new Trade(user.userId).tradeAuiting();
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

}
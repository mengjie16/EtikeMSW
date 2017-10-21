package vos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.impl.cookie.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.util.RegexUtils;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import controllers.base.secure.Secure;
import enums.OrderStatus;
import enums.constants.CacheType;
import enums.constants.ErrorCode;
import models.BuyerInfo;
import models.Cart;
import models.Item;
import models.Location;
import models.Order;
import models.ProductInfo;
import models.Region;
import models.RetailerAddress;
import models.User;
import play.data.validation.MinSize;
import play.libs.Codec;

/**
 * 订单实体视图
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年9月9日 上午10:53:43
 */
public class OrderVo implements java.io.Serializable {

    private static final Logger log = LoggerFactory.getLogger(OrderVo.class);

    // 商品标题
    public String productName;
    // 商品名称md5，去尾部空格
    public String productMd5Name;
    // 商品ID，平台商品ID
    public long itemId;
    // 外部订单号
    public String outOrderNo;
    // 名字
    public String buyerName;
    // 联系方式
    public String contact;
    // 省
    public String province;
    // 省份id
    public int provinceId;
    // 城市
    public String city;
    // 区域
    public String region;
    // 具体地址
    public String address;
    // 详细信息，不包含省市区
    public String address_detail;
    // 详细地址，包含省市区
    public String address_detail_pcra;
    // 商品数量
    public int num;
    // 商品规格
    public String skuStr;
    // 规格md5，去空格
    public String skuMd5Str;
    // 备注
    public String note;
    // 订单创建时间
    public Date createTime;
    public String createTimeStr;
    // 商品md5+商品名称,规格md5+规格名称
    public Map<String, String> productSkuMap = Maps.newHashMap();

    // 主键 */
    public long id;
    // 所属交易id */
    public long tradeId;
    // 标题 */
    public String caption;
    // 零售商id */
    public int retailerId;
    // 折扣 */
    public int discount;
    // 商品总额 */
    public int cargoFee;
    // 邮费 */
    public int shippingFee;
    // 总金额，单位分
    public int totalFee;
    // 快递公司 */
    public String express;
    // 快递单号
    public String expNo;
    // 支付时间
    public Date payTime;
    // 发货时间
    public Date consignTime;
    // 买家信息
    public BuyerInfo buyerInfo;
    // 商品信息
    public ProductInfo productInfo;
    // 状态中文描述
    public String statusText;
    // 状态英文描述
    public String statusCode;
    // 状态颜色
    public String statusColor;
    // 标记是否删除
    public boolean isDelete;

    public String getMd5ProductName() {
        return this.productMd5Name;
    }

    public String getMd5SkuStr() {
        return this.skuMd5Str;
    }

    /**
     * 获取商品名称，规格信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月22日 上午11:35:22
     */
    public Map<String, String> getProductSkuMap() {
        Map<String, String> mss = Maps.newHashMap();
        mss.put("productName", this.productName);
        mss.put("skuMd5Str", this.skuMd5Str);
        mss.put("skuStr", this.skuStr);
        return mss;
    }

    /**
     * 生成商品名称，规格md5值, 商品名称map, 规格map
     *
     * @since v1.0
     * @author Calm
     * @created 2016年9月10日 下午2:34:10
     */
    public void md5ProductNameSkuStr() {
        if (!Strings.isNullOrEmpty(this.productName)) {
            this.productMd5Name = Codec.hexMD5(StringUtils.trim(this.productName));
        }
        this.skuMd5Str = "";
        if (!Strings.isNullOrEmpty(this.skuStr)) {
            this.skuMd5Str = Codec.hexMD5(StringUtils.trim(this.skuStr));
        }

    }

    /**
     * 解析地址信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月9日 上午10:45:06
     */
    public void parseAddress() {
        // 有省，市，区
        if (!(Strings.isNullOrEmpty(this.province) || Strings.isNullOrEmpty(this.city)
            || Strings.isNullOrEmpty(this.region) || Strings.isNullOrEmpty(this.address))) {
            this.province = StringUtils.trim(this.province);
            this.city = StringUtils.trim(this.city);
            this.region = StringUtils.trim(this.region);
            this.address = StringUtils.trim(this.address);
            parseProvinceId(this.province);
        } else {
            // 地址信息解析不完整
            Location location = parseAddress(this.address_detail_pcra);
            if (location != null) {
                this.province = location.province;
                this.city = location.city;
                this.region = location.region;
                this.address = location.address;
                this.provinceId = location.provinceId;
            }
        }
    }

    /**
     * 解析省份
     *
     * @param provinceStr
     * @since v1.0
     * @author Calm
     * @created 2016年9月21日 下午1:39:02
     */
    public void parseProvinceId(String provinceStr) {
        if (Strings.isNullOrEmpty(provinceStr)) {
            return;
        }
        // 解析出省份id
        List<Region> list = Region.findLevel2ShortWithCache();
        for (Region region : list) {
            if (region.name.indexOf(provinceStr) != -1 || provinceStr.indexOf(region.name) != -1) {
                this.provinceId = region.id;
                break;
            }
        }
    }

    /**
     * 从订单视图解析生成订单
     *
     * @param retailerId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 下午3:19:48
     */
    public String parseToOrder(Order order) {
        order = order == null ? new Order() : order;
        // 等待发货状态
        order.status = OrderStatus.ORDER_CONSIGN_WAIT;
        // ------ 买家信息构建
        order.buyerInfo = new BuyerInfo();
        order.buyerInfo.name = this.buyerName;
        order.buyerInfo.province = this.province;
        order.buyerInfo.city = this.city;
        order.buyerInfo.region = this.region;
        order.buyerInfo.address = this.address;
        order.buyerInfo.contact = this.contact;
        order.buyerInfo.provinceId = this.provinceId;

        // ------ 其他信息构建
        order.outOrderNo = this.outOrderNo;
        order.num = this.num;
        order.note = this.note;
        order.createTime = this.createTime;

        // 查找商品信息
        Item item = Item.findById(this.itemId);
        if (item == null) {
            String result = ErrorCode.ORDER_ITEM_UNEXSIT_ERROR.description;
            log.warn(result + "{}", this.itemId);
            return result;
        }
        // ------ 商品信息构建
        order.productInfo =  this.productInfo;

        // 计算商品价格
        order.cargoFee = this.cargoFee;
        // 计算商品邮费
        // Map<Integer, Integer> shippFee =Maps.newHashMap();
     
       // Map<Integer, Integer> shippFee = item.calculateFreightTempFee(order.num);
        int orderShippFee = 12;
        // 运费模版出错或运费无法计算，当前订单将无法生成
        if (orderShippFee  == -1) {
            String result = ErrorCode.ORDER_SHIPPINGFEE_ERROR.description;
            log.warn(result + ",商品ID{}", this.itemId);
            return result;
        }
        order.shippingFee = orderShippFee;
        order.totalFee = order.cargoFee + order.shippingFee;
        return "";
    }

    /**
     * 平台vo->用来更新或保存order 的转换
     *
     * @param retailerId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月29日 下午4:06:18
     */
    public String parseExsitOrder(Order order) {
        // 查询已存在订单
        Order checkOrder = Order.findById(this.id);
        String result = "";
        if (checkOrder == null) { // 非法提交
            result = String.format(ErrorCode.ORDER_NOT_FOUND.description, order.id);
            log.warn(result + "{}", order.id);
            return result;
        }
        order.id = this.id;
        order.totalFee = checkOrder.totalFee;
        order.productInfo = checkOrder.productInfo != null ? checkOrder.productInfo : new ProductInfo();
        order.cargoFee = checkOrder.cargoFee;
        order.shippingFee = checkOrder.shippingFee;
        order.num = checkOrder.num;
        // ======= 价格处理(小数保留2位，并转换为分)
        this.totalFee = new BigDecimal(this.totalFee).setScale(2, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal(100)).intValue();
        this.shippingFee = new BigDecimal(this.shippingFee).setScale(2, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal(100)).intValue();
        // 状态检查
        if (checkOrder.status != OrderStatus.ORDER_CONSIGN_WAIT) {
            result = ErrorCode.ORDER_STATUS_CANNOT_CHANGE.description;
            log.warn(result + "{}", order.id);
            return result;
        }
        // 外部订单号可修改
        if (!Strings.isNullOrEmpty(this.outOrderNo)) {
            order.outOrderNo = this.outOrderNo;
        }
        // 订单备注修改
        if (!Strings.isNullOrEmpty(this.note)) {
            order.note = this.note;
        }
        // 订单标题修改
        if (!Strings.isNullOrEmpty(this.caption)) {
            order.caption = this.caption;
        }
        // 标记是否计算运费
        boolean calcShippFee = false;
        // 订单数量
        if (calcShippFee = this.num > 0 && this.num != checkOrder.num) {
            order.num = this.num;
            order.cargoFee = order.productInfo.itemPrice * order.num;
        }
        // 下单时间
        if (this.createTime != null) {
            order.createTime = this.createTime;
        }
        // ------ 买家信息构建
        order.buyerInfo = checkOrder.buyerInfo != null ? checkOrder.buyerInfo : new BuyerInfo();
        if (!Strings.isNullOrEmpty(this.buyerName)) {
            order.buyerInfo.name = this.buyerName;
        }
        if (!Strings.isNullOrEmpty(this.contact)) {
            // 4-8位数字，以校验电话号码，不能确定常规填写
            boolean match = RegexUtils.isMatch(this.contact, "\\d{4,8}");
            if (!match) {
                result = ErrorCode.ORDER_BUYER_CONTACT_INVALID.description;
                log.warn(result + "{}", this.itemId);
                return result;
            }
            order.buyerInfo.contact = this.contact;
        }
        // 省 发生变化
        if (this.provinceId > 0 && this.provinceId != checkOrder.buyerInfo.provinceId
            && !Strings.isNullOrEmpty(this.province)) {
            calcShippFee = true;
            order.buyerInfo.province = this.province;
            order.buyerInfo.provinceId = this.provinceId;
        }
        // 市
        if (!Strings.isNullOrEmpty(this.city)) {
            order.buyerInfo.city = this.city;
        }
        // 区
        if (!Strings.isNullOrEmpty(this.region)) {
            order.buyerInfo.region = this.region;
        }
        // 地址
        if (!Strings.isNullOrEmpty(this.address)) {
            order.buyerInfo.address = this.address;
        }
        // ------ 商品信息更新(商品id，规格)
        int num = order.num == 0 ? checkOrder.num : order.num;
        Item item = null;
        if (!Strings.isNullOrEmpty(this.skuStr)) {
            order.productInfo.sku = this.skuStr;
        }
        if (this.itemId > 0 && this.itemId != order.productInfo.itemId) {
            calcShippFee = true;
            // 查找商品信息
            item = Item.findById(order.productInfo.itemId);
            if (item == null) {
                result = ErrorCode.ORDER_ITEM_UNEXSIT_ERROR.description;
                log.warn(result + "{}", this.itemId);
                return result;
            }
            order.productInfo.itemId = this.itemId;
            order.productInfo.itemPrice = item.itemLastFee(num);
            order.cargoFee = order.productInfo.itemPrice * num;
        }
        // 如果提交了物流费用，按照提交保存
        boolean hasShipp = false;
        if (hasShipp = this.shippingFee != checkOrder.shippingFee) {
            // 不需要再计算物流费用
            order.shippingFee = this.shippingFee;
        }
        // 任何涉及费用修改都要重新计算物流费用，但如果有提交物流费用，按照提交的费用计算
        if (calcShippFee && !hasShipp) {
            item = item == null ? Item.findById(order.productInfo.itemId) : item;
            Map<Integer, Integer> shippFee = item.calculateFreightTempFee(num);
            int orderShippFee = -1;
            // 运费模版出错或运费无法计算，当前订单将无法生成
            if (shippFee == null || (orderShippFee = shippFee.getOrDefault(order.buyerInfo.provinceId, -1)) == -1) {
                result = ErrorCode.ORDER_SHIPPINGFEE_ERROR.description;
                log.warn(result + ",商品ID{}", this.itemId);
                return result;
            }
            order.shippingFee = orderShippFee;
        }
        // 任何费用重算的，都要重新计算总金额
        if (calcShippFee) {
            // 订单总金额
            order.totalFee = order.cargoFee + order.shippingFee;
        } else if (this.totalFee > 0 && this.totalFee != checkOrder.totalFee) { // 编辑过总金额，那么按照编辑总价保存(如果编辑的商品总金额大于当前商品总金额那么不符合下单逻辑)
            order.totalFee = this.totalFee;
        }
        return "";
    }

    /**
     * 将订单转换为订单视图(金额将会转换为元)
     *
     * @param order
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午3:58:06
     */
    public static OrderVo valueOfOrderParseFee(Order order) {
        OrderVo vo = new OrderVo();
        if (order == null) {
            return vo;
        }
        vo.id = order.id;
        vo.caption = order.caption;
        vo.cargoFee = order.cargoFee;
        vo.shippingFee = order.shippingFee;
        vo.totalFee = order.totalFee;
        vo.outOrderNo = order.outOrderNo;
        vo.createTime = order.createTime;
        vo.statusCode = order.status.code;
        vo.statusText = order.status.text;
        vo.num = order.num;
        // 状态颜色
        if (order.status == OrderStatus.ORDER_CONSIGN_BEEN || order.status == OrderStatus.ORDER_SUCCESS) {
            vo.statusColor = "green";
        } else if (order.status == OrderStatus.ORDER_REFUNDING || order.status == OrderStatus.ORDER_REFUNDING_FINISH) {
            vo.statusColor = "red";
        }
        if (vo.createTime != null) {
            vo.createTimeStr = DateUtils.formatDate(vo.createTime, "yyyy-MM-dd HH:mm:ss");
        }
        vo.note = order.note;
        // ------ 买家信息
        vo.buyerInfo = order.buyerInfo;
        // 商品信息
        vo.productInfo = order.productInfo;
        vo.express = order.express;
        vo.expNo = order.expNo;
        return vo;
    }

    /**
     * 基本数据转换
     *
     * @param order
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月20日 上午10:58:42
     */
    public static OrderVo valueOfOrderBase(Order order) {
        OrderVo vo = new OrderVo();
        if (order == null) {
            return vo;
        }
        vo.id = order.id;
        vo.caption = order.caption;
        vo.cargoFee = order.cargoFee;
        vo.shippingFee = order.shippingFee;
        vo.totalFee = order.totalFee;
        vo.outOrderNo = order.outOrderNo;
        vo.createTime = order.createTime;
        vo.statusCode = order.status.code;
        vo.statusText = order.status.text;
        vo.note = order.note;
        // ------ 买家信息
        vo.buyerInfo = order.buyerInfo;
        // 商品信息
        vo.productInfo = order.productInfo;
        vo.express = order.express;
        vo.expNo = order.expNo;
        // 状态颜色
        if (order.status == OrderStatus.ORDER_CONSIGN_BEEN || order.status == OrderStatus.ORDER_SUCCESS) {
            vo.statusColor = "green";
        } else if (order.status == OrderStatus.ORDER_REFUNDING || order.status == OrderStatus.ORDER_REFUNDING_FINISH) {
            vo.statusColor = "red";
        }
        if (vo.createTime != null) {
            vo.createTimeStr = DateUtils.formatDate(vo.createTime, "yyyy-MM-dd HH:mm:ss");
        }
        return vo;
    }

    /**
     * 订单列头字段对应的value
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月19日 上午11:31:08
     */
    public Map<String, Object> toMap() {
        Map<String, Object> mio = Maps.newHashMap();
        mio.put("id", this.id);
        mio.put("num", this.num);
        mio.put("cargoFee", new BigDecimal(this.cargoFee).divide(BigDecimal.valueOf(100)).doubleValue());
        mio.put("shippingFee", new BigDecimal(this.shippingFee).divide(BigDecimal.valueOf(100)).doubleValue());
        mio.put("totalFee", new BigDecimal(this.totalFee).divide(BigDecimal.valueOf(100)).doubleValue());
        mio.put("outOrderNo", this.outOrderNo);
        mio.put("expNo", this.expNo);
        mio.put("express", this.express);
        mio.put("createTime", this.createTime);
        mio.put("itemId", this.productInfo.itemId);
        mio.put("sku", this.productInfo.sku);
        mio.put("name", this.buyerInfo.name);
        mio.put("contact", this.buyerInfo.contact);
        mio.put("province", this.buyerInfo.province);
        mio.put("city", this.buyerInfo.city);
        mio.put("region", this.buyerInfo.region);
        mio.put("address", this.buyerInfo.address);
        mio.put("note", this.note);
        return mio;
    }

    /**
     * 订单列头对应中文描述
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月19日 上午11:30:21
     */
    public static Map<String, String> headMap() {
        Map<String, String> mio = Maps.newHashMap();
        mio.put("id", "订单ID");
        mio.put("num", "商品数量");
        mio.put("cargoFee", "商品总金额(元)");
        mio.put("shippingFee", "邮费");
        mio.put("totalFee", "订单总金额(元)");
        mio.put("outOrderNo", "外部订单号");
        mio.put("expNo", "快递单号");
        mio.put("express", "快递公司");
        mio.put("createTime", "创建时间");
        mio.put("itemId", "商品ID");
        mio.put("sku", "商品规格");
        mio.put("name", "客户姓名");
        mio.put("contact", "客户联系方式");
        mio.put("province", "客户地址-省");
        mio.put("city", "客户地址-市");
        mio.put("region", "客户地址-区");
        mio.put("address", "客户-详细地址");
        mio.put("note", "订单备注");
        return mio;
    }

    /**
     * 根据列头字段列表返回中文描述数组
     *
     * @param heads
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月19日 上午11:37:09
     */
    public static String[] parseHeadMap(List<String> columns) {
        if (MixHelper.isEmpty(columns)) {
            return null;
        }
        Map<String, String> mio = headMap();
        String[] cnHead = new String[] {};
        for (int i = 0; i < columns.size(); i++) {
            String str = mio.getOrDefault(columns.get(i), "");
            cnHead = ArrayUtils.add(cnHead, str);
        }
        return cnHead;
    }

    /**
     * 组成excel所需map
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午6:19:19
     */
    public Map<Integer, Object> toExcelMap(List<String> keys) {
        if (MixHelper.isEmpty(keys)) {
            return Maps.newHashMap();
        }
        // 当前视图map
        Map<String, Object> voMap = this.toMap();
        Map<Integer, Object> mio = Maps.newHashMap();
        for (int j = 0; j < keys.size(); j++) {
            mio.put(j, voMap.getOrDefault(keys.get(j), null));
        }
        return mio;
    }

    /**
     * 组合excel所需订单视图表
     *
     * @param orders
     * @param keys 要导出的字段
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午6:20:57
     */
    public static List<Map<Integer, Object>> orderVosToExcelData(List<Order> orders, List<String> keys) {
        if (MixHelper.isEmpty(orders)) {
            return Lists.newArrayList();
        }
        List<Map<Integer, Object>> vos = orders.stream().map(o -> OrderVo.valueOfOrderParseFee(o).toExcelMap(keys))
            .collect(Collectors.toList());
        return vos;
    }

    /**
     * 将最终规格信息载入订单视图
     *
     * @param voList
     * @param skuMap
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月10日 下午9:18:04
     */
    public static List<OrderVo> parseVoList(List<OrderVo> voList, Map<String, String> skuMap,
        Map<String, Long> productName) {

        if (MixHelper.isEmpty(voList) || MixHelper.isEmpty(skuMap)) {
            return voList;
        }
        List<OrderVo> listOrder = Lists.newArrayList();
        for (OrderVo vo : voList) {
            String sku = skuMap.get(vo.productMd5Name + vo.skuMd5Str);
            if (!Strings.isNullOrEmpty(sku)) {
                vo.skuStr = sku;
            }
            long itemId = productName.getOrDefault(vo.productMd5Name, 0L);
            if (itemId > 0) {
                vo.itemId = itemId;
            }
            listOrder.add(vo);
        }
        return listOrder;
    }

    /**
     * 解析地址
     *
     * @param address_deatail_pcra
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月8日 下午3:03:38
     */
    public static Location parseAddress(String addressPcra) {
        if (Strings.isNullOrEmpty(addressPcra)) {
            return null;
        }
        try {
            // 先两边trim
            addressPcra = StringUtils.trim(addressPcra);
            // 省份过滤，替换所有不是中，英文，空格的字符
            addressPcra = org.apache.commons.lang3.StringUtils.replacePattern(addressPcra,
                "[^\\da-zA-Z\u4e00-\u9fa5\\s]", ",");
            // ======= 重组详细地址
            String[] result = org.apache.commons.lang3.StringUtils.split(addressPcra, ",");
            if (result != null) {
                List<String> lStrs = Lists.newArrayList(result);
                // 去空，去重复
                addressPcra = lStrs.stream().filter(s -> !Strings.isNullOrEmpty(s)).distinct().reduce("",
                    (a, b) -> a + b);
            }
            // 基本过滤
            addressPcra = org.apache.commons.lang3.StringUtils.replacePattern(addressPcra, "市辖区", "");
            // 在系统中先匹配省份
            // 地址信息
            Location location = new Location();
            // 解析出省份id
            List<Region> list = Region.findLevel2ShortWithCache();
            for (Region reg : list) {
                if (addressPcra.indexOf(reg.name) == -1) {
                    continue;
                }
                location.province = reg.name;
                location.provinceId = reg.id;
                // 直辖市
                if (Region.zx_region.contains(reg.name)) {
                    location.city = reg.name + "市";
                } else if (Region.tb_region.contains(reg.name)) { // 特别行政区
                    int index = addressPcra.indexOf("特别行政区");
                    if (index != -1) {
                        addressPcra = addressPcra.substring(index + 5);
                    }
                    if (addressPcra.indexOf("香港岛") != -1) {
                        location.city = "香港岛";
                    }
                    if (addressPcra.indexOf("九龙新界") != -1) {
                        location.city = "九龙新界";
                    }
                    if (addressPcra.indexOf("澳门半岛") != -1) {
                        location.city = "澳门半岛";
                    }
                    if (addressPcra.indexOf("离岛") != -1) {
                        location.city = "离岛";
                    }
                } else if (Region.zzq_region.contains(reg.name)) { // 自治区
                    int index = addressPcra.indexOf("自治区");
                    if (index != -1) {
                        addressPcra = addressPcra.substring(index + 3);
                    }
                }
                break;
            }
            // 省份无法解析，就不用解析了
            if (location.provinceId <= 0) {
                return null;
            }
            // 删除省份信息
            String needRemove = location.province + "省|" + location.province;
            // 解析城市
            if (Strings.isNullOrEmpty(location.city)) {
                int index = addressPcra.indexOf("市") + 1;
                if (index > 0) {
                    // 市地址信息删除关于省的信息
                    location.city = org.apache.commons.lang3.StringUtils.replacePattern(addressPcra.substring(0, index),
                        needRemove, "");
                    addressPcra = addressPcra.substring(index);
                }
            }
            if (!Strings.isNullOrEmpty(location.city)) {
                // 市已解析，删除该带“市”的关键词
                addressPcra = addressPcra.replace(location.city, "");
            }

            boolean parse = false;
            // 县级市,区,县 都可能是区域
            char[] imp = new char[] { '市', '区', '县' };
            for (int i = 0; i < imp.length; i++) {
                int index = addressPcra.indexOf(imp[i]) + 1;
                if (index == 0) {
                    continue;
                }
                location.region = addressPcra.substring(0, index);
                if (location.region.length() <= 1) {
                    continue;
                }
                location.region = org.apache.commons.lang3.StringUtils.replacePattern(location.region, needRemove, "");
                if (parse = location.region.length() <= 6) {
                    location.address = addressPcra.substring(index).replace(location.region, "");
                    break;
                }
            }
            // 如果区名太长那么就不做解析
            if (!parse) {
                addressPcra = org.apache.commons.lang3.StringUtils.replacePattern(addressPcra, needRemove, "");
                location.region = "";
                location.address = addressPcra.substring(0);
            }
            return location;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Map<String, String> parseAddressxx(String address_deatail_pcra) {
        /**
         * 情况有好多种：
         * 1. 直辖市
         * 2. 自治区
         * 3. 符号分割详细地址
         */
        if (Strings.isNullOrEmpty(address_deatail_pcra)) {
            return null;
        }
        try {
            // 先两边trim
            address_deatail_pcra = StringUtils.trim(address_deatail_pcra);
            // 省份过滤，替换所有不是中，英文，空格的字符
            address_deatail_pcra = org.apache.commons.lang3.StringUtils.replacePattern(address_deatail_pcra,
                "[^\\da-zA-Z\u4e00-\u9fa5\\s]", ",");
            String[] result = org.apache.commons.lang3.StringUtils.split(address_deatail_pcra, ",");
            String province = "", city = "", region = "", address = "";
            boolean parse = false;
            if (result != null && result.length > 2) {
                List<String> lStrs = Lists.newArrayList(result);
                // 去空，去重复
                lStrs = lStrs.stream().filter(s -> !Strings.isNullOrEmpty(s)).collect(Collectors.toList());
                province = lStrs.get(0);
                if (lStrs.size() > 1) {
                    city = lStrs.get(1);
                }
                if (lStrs.size() > 2) {
                    region = lStrs.get(2);
                }
                if (lStrs.size() > 2) {
                    for (int l = 3; l < lStrs.size(); l++) {
                        address += lStrs.get(l);
                    }
                }
                parse = true;
            }
            if (!parse) {
                // 基本过滤
                address_deatail_pcra = address_deatail_pcra.replace("市辖区", "");
                // 直辖市
                for (int i = 0; i < Region.zx_region.size(); i++) {
                    if (address_deatail_pcra.indexOf(Region.zx_region.get(i)) != -1) {
                        province = Region.zx_region.get(i);
                        city = province + "市";
                        address_deatail_pcra = address_deatail_pcra.replace(province, "");
                        parse = true;
                        break;
                    }
                }
                if (!parse) {
                    // 自治区
                    for (int i = 0; i < Region.zzq_region.size(); i++) {
                        if (address_deatail_pcra.indexOf(Region.zzq_region.get(i)) != -1) {
                            province = Region.zzq_region.get(i);
                            int index = address_deatail_pcra.indexOf("区") + 1;
                            if (index > 0) {
                                province = address_deatail_pcra.substring(0, index);
                                address_deatail_pcra = address_deatail_pcra.substring(province.length());
                            }
                            index = address_deatail_pcra.indexOf("市") + 1;
                            if (index > 0) {
                                city = address_deatail_pcra.substring(0, index);
                                address_deatail_pcra = address_deatail_pcra.substring(city.length());
                            }
                            parse = true;
                            break;
                        }
                    }
                }
                if (!parse) {
                    // 特别行政区
                    for (int i = 0; i < Region.tb_region.size(); i++) {
                        if (address_deatail_pcra.indexOf(Region.tb_region.get(i)) != -1) {
                            province = Region.tb_region.get(i);
                            int index = address_deatail_pcra.indexOf("区") + 1;
                            if (index > 0) {
                                province = address_deatail_pcra.substring(0, index);
                            }
                            //
                            address_deatail_pcra = address_deatail_pcra.substring(province.length());
                            if (address_deatail_pcra.indexOf("香港岛") != -1) {
                                city = "香港岛";
                            }
                            if (address_deatail_pcra.indexOf("九龙新界") != -1) {
                                city = "九龙新界";
                            }
                            if (address_deatail_pcra.indexOf("澳门半岛") != -1) {
                                city = "澳门半岛";
                            }
                            if (address_deatail_pcra.indexOf("离岛") != -1) {
                                city = "离岛";
                            }
                            if (!Strings.isNullOrEmpty(city)) {
                                address_deatail_pcra = address_deatail_pcra.substring(city.length());
                            }
                            parse = true;
                            break;
                        }
                    }
                }
                // 常规省份
                if (!parse) {
                    int index = address_deatail_pcra.indexOf("省") + 1;
                    if (index > 0) {
                        province = address_deatail_pcra.substring(0, index);
                        address_deatail_pcra = address_deatail_pcra.substring(province.length());
                    }
                    index = address_deatail_pcra.indexOf("市") + 1;
                    if (index > 0) {
                        city = address_deatail_pcra.substring(0, index);
                        address_deatail_pcra = address_deatail_pcra.substring(city.length());
                    }
                }
                // 最后处理区和具体地址
                // 如果有‘小区’
                int index = address_deatail_pcra.lastIndexOf("小区");
                String regionAddress = address_deatail_pcra;
                if (index != -1) {
                    regionAddress = address_deatail_pcra.substring(0, index);
                }
                index = regionAddress.lastIndexOf("区");
                if (index == -1) {
                    index = regionAddress.lastIndexOf("市");
                }
                int req = 0;
                if (index > 0) {
                    region = regionAddress.substring(0, index + 1);
                    req = region.length();
                    region = org.apache.commons.lang3.StringUtils.replacePattern(region, "[省|市]", "");
                }
                address = address_deatail_pcra.substring(req);
            }
            return ImmutableMap.of("province", province, "city", city, "region", region, "address", address);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 检查订单是否有效
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月9日 下午3:30:36
     */
    public String checkValid() {
        StringBuilder sb = new StringBuilder("");
        // 联系人，
        if (Strings.isNullOrEmpty(this.buyerName)) {
            sb.append(ErrorCode.ORDER_BUYER_NAME_EMPTY.description + "，");
        }
        // 联系人号码为空
        if (Strings.isNullOrEmpty(this.contact)) {
            sb.append(ErrorCode.ORDER_BUYER_CONTACT_EMPTY.description + "，");
        } else {
            // 4-8位数字，以校验电话号码，不能确定常规填写
            boolean match = RegexUtils.isMatch(this.contact, "\\d{4,8}");
            if (!match) {
                sb.append(ErrorCode.ORDER_BUYER_CONTACT_INVALID.description + "，");
            }
        }
        // 商品数量
        if (this.num <= 0) {
            sb.append(ErrorCode.ORDER_ITEM_COUNT_ERROR.description + "，");
        }
        // 省
        if (Strings.isNullOrEmpty(this.province)) {
            sb.append(ErrorCode.ORDER_BUYER_PROVINCE_EMPTY.description + "，");
        }
        // 市
        if (Strings.isNullOrEmpty(this.city)) {
            sb.append(ErrorCode.ORDER_BUYER_CITY_EMPTY.description + "，");
        }
        // 详细地址
        if (Strings.isNullOrEmpty(this.address)) {
            sb.append(ErrorCode.ORDER_BUYER_ADDRESS_EMPTY.description + "，");
        }
        // 省份id
        if (this.provinceId == 0) {
            sb.append(ErrorCode.ORDER_BUYER_PROVINCE_INVALID.description + "，");
        }
        return sb.toString();
    }

    /**
     * 转换分组后的商品信息结果集合,并过滤sku重复
     *
     * @param params
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月9日 下午4:53:37
     */
    public static List<OrderProductResult> parseToOrderProductResult(Map<String, List<Map<String, String>>> params) {
        List<OrderProductResult> results = Lists.newArrayList();
        if (MixHelper.isEmpty(params)) {
            return results;
        }
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            // 订单视图列表归组后的内容
            Map.Entry<String, List<Map<String, String>>> product = (Map.Entry) it.next();
            // 商品名称md5
            String md5Str = product.getKey();
            // 包含商品名称，商品规格列表的map
            List<Map<String, String>> paramMap = product.getValue();
            // 规格结果集
            List<Map<String, String>> skuStrMap = Lists.newArrayList();
            List<String> skuMd5Strs = Lists.newArrayList();
            String productName = "";
            if (MixHelper.isEmpty(paramMap)) {
                continue;
            }
            for (Map<String, String> mss : paramMap) {
                if (productName == "") {
                    productName = mss.get("productName");
                }
                String skumd5Str = mss.get("skuMd5Str");
                if (!skuMd5Strs.contains(skumd5Str)) {
                    skuMd5Strs.add(skumd5Str);
                    skuStrMap.add(ImmutableMap.of("key", md5Str + skumd5Str, "value", mss.get("skuStr")));
                }
            }
            results.add(new OrderProductResult(skuStrMap, productName, md5Str));
        }
        return results;
    }
    
    
    public static void parseOrderVo(List<Integer> confirmOrder, RetailerAddress retailerAddress, long id) {
        // 获取读取解析过的数据
//        if (MixHelper.isEmpty(confirmOrder)) {
//            log.info("提交了空的订单");
//        }       
        
        List<OrderVo> orderVoList = Lists.newArrayList();
        
        // 过滤已删除订单 //
        log.info("开始生成订单....");
        // 订单生成错误消息集合
        List<String> messages = Lists.newArrayList();
        for (int i = 0; i < confirmOrder.size(); i++) {
            OrderVo vo = new OrderVo();
            // 提交的行列
            int oindex = confirmOrder.get(i);
           
            Cart cart = Cart.findById(oindex, id);
            if(cart == null){
                 log.info("无订单文件解析数据，或已丢失");
            }
            
            
            CartVo cartVo = CartVo.valueOfCart(cart);
            
            vo.cargoFee = cartVo.cartPrice;
            vo.itemId = cartVo.itemId;
            vo.productInfo = new ProductInfo();
            vo.productInfo.itemId = cartVo.itemId;
            vo.productInfo.itemPrice = cartVo.retailPrice;
            vo.productInfo.picUrl = cartVo.picUrl;
            vo.productInfo.brandName = cartVo.brand.name;
            vo.productInfo.color = cartVo.skuColor;
            vo.productInfo.title = cartVo.title;
            vo.productInfo.retailPrice = cartVo.retailPrice;
            vo.skuStr = cart.sku();
            vo.productName = cart.title;
            vo.num = cart.cartCount;
            vo.outOrderNo  = Long.toString(cart.id);
            vo.buyerName = retailerAddress.name;
            vo.contact = retailerAddress.phone;
            if (Strings.isNullOrEmpty(vo.productName)) {
                vo.productName = "订单商品:" + i;
            }
            vo.province = retailerAddress.province;
            vo.city = retailerAddress.city;
            vo.region = retailerAddress.region;
            vo.address = retailerAddress.address;
            vo.provinceId = retailerAddress.provinceId;
            vo.createTime = DateTime.now().toDate();
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
            log.error("订单解析失败,商品信息解析匹配失败！");
        }
        // 缓存当前解析成功的商品信息
        String pkey = CacheType.RETAILER_ORDER_PRODUCT_DATA.getKey(id);
        CacheUtils.set(pkey, results, CacheType.RETAILER_ORDER_PRODUCT_DATA.expiredTime);
        // 解析成功
    }
   
}

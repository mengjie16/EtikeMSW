package enums;

/**
 * 
 * 供应商的种类
 * 
 * @author Tr0j4n
 * @since v1.0
 * @created 2016年4月11日 下午11:07:33
 */
public enum SupplierType {
    // @formatter:off
    FACTORY("FACTORY", "工厂"),
    BRAND("BRAND", "品牌方"),
    RESELLER("RESELLER", "经销商");
    //@formatter:on

    public String code;
    public String name;

    private SupplierType(String _code, String _name) {
        this.code = _code;
        this.name = _name;
    }
}

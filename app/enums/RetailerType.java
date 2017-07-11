package enums;

/**
 * 
 * 店铺的种类
 * 
 * @author Tr0j4n
 * @since  v1.0
 * @created 2016年4月11日 下午11:07:33
 */
public enum RetailerType {
    //天猫商城
    B("B"),
    //集市卖家
    C("C");
    private String _v;

    private RetailerType(String _v) {
        this._v = _v;
    }
}

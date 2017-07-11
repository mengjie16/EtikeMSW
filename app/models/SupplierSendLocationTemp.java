package models;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.google.common.base.Strings;

import models.mappers.SupplierSendLocationTempMapper;

/**
 * 
 * 描述一个物理地址
 * 
 * @author Tr0j4n
 * @since  v1.0
 * @created 2016年4月13日 下午11:45:48
 */
public class SupplierSendLocationTemp extends Location {
    private static final Logger log = LoggerFactory.getLogger(SupplierSendLocationTemp.class);
    
    public static final String TABLE_NAME = "supplier_sendlocation_temp";
    
	/** 地址主键 */
	public long id;
	
    public int supplierId;
	
    /**
     * 创建时间
     */
	public Date createTime;
    /**
     * 参数构造
     * Constructs a <code>SupplierSendLocationTemp</code>
     *
     * @since  v1.0
     */
    public SupplierSendLocationTemp(){
    	
    }
    
    /**
     * 返回地址基本字符串拼接内容(province+city+region+address)
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月18日 下午9:51:46
     */
    public String toBaseLocationStr(){
        return province+city+region+address;
    }
    
    /**
     * 保存当前模板地址
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月18日 下午3:55:40
     */
    public boolean save(){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierSendLocationTempMapper mapper = ss.getMapper(SupplierSendLocationTempMapper.class);
            if(Strings.isNullOrEmpty(country)){
                this.country = "中国";
                this.countryId = 0;
            }
            if(this.id>0){
                mapper.updateById(this);
            }else{
                this.createTime= DateTime.now().toDate();
                mapper.insert(this);
            }
        } finally {
            ss.close();
        }
        return true;
    }
    
    /**
     * 查询供应商发货地址模板列表
     *
     * @param supplierId
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月18日 下午4:07:43
     */
    public static List<SupplierSendLocationTemp>  findListBySupplierId(int supplierId){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierSendLocationTempMapper mapper = ss.getMapper(SupplierSendLocationTempMapper.class);
            return mapper.selectList(supplierId);
        } finally {
            ss.close();
        }
    }
}

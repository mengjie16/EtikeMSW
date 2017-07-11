package vos;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import models.Freight;
import models.FreightTemp;
import models.Supplier;
import play.data.binding.As;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import utils.SearchEndDateBinder;

import com.aton.util.MixHelper;
import com.aton.util.StringUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * 运费模板视图
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年9月28日 下午3:43:54
 */
public class FreightTempVo implements java.io.Serializable {

    public long _id;
    public int userId;
    public String userName;
    public String company;
    public String tempName;
    public String tempType;
    public List<Freight> freights = Lists.newArrayList();
    public Date createTime;

    /**
     * 转换vo
     *
     * @param item
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 下午5:31:00
     */
    public static FreightTempVo valueOfItem(FreightTemp freightTemp, boolean isBase) {
        if (freightTemp == null)
            return null;
        FreightTempVo iv = new FreightTempVo();
        iv._id = freightTemp._id;
        iv.userId = freightTemp.userId;
        // 查询供应商
        if (!isBase) {
            Supplier supplier = Supplier.findById(freightTemp.userId);
            if (supplier != null) {
                iv.userName = supplier.name;
            }
        }
        iv.tempName = freightTemp.tempName;
        iv.tempType = freightTemp.tempType;
        iv.createTime = freightTemp.createTime;
        iv.freights = freightTemp.freights;
        return iv;
    }
}
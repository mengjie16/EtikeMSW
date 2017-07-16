package vos;

import java.util.Date;

import com.aton.vo.Page;

import enums.SupplierType;

/**
 * 供货商检索视图数据结构
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年4月29日 上午1:48:39
 */
public class AddressSearchVo extends Page {

    /** 名字 */
    public String name;
    public Date createTimeStart;
    public Date createTimeEnd;
	
    public static AddressSearchVo newInstance() {
        AddressSearchVo vo = new AddressSearchVo();
        return vo;
    }
}
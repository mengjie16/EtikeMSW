package vos;

import java.util.Date;
import java.util.List;

import models.mappers.ItemCateMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import vos.CateSearchVo;

import com.aton.db.SessionFactory;
import com.aton.vo.Page;

/**
 * 宝贝属性
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月8日 下午12:07:25
 */
public class ItemPropertieVo{
	
	public String key;
	
	public String value;
	
}

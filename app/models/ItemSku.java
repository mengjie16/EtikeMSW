package models;

import java.util.Date;
import java.util.List;

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
 * 商品规格
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月28日 下午4:59:21
 */
public class ItemSku{
	
	public String color;
	
	public String img;
	
	public int quantity;
	
}

package models;

import java.util.Date;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;

import models.mappers.FootMarkMapper;

/**
 * 足迹数据模型
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月1日 下午5:59:25
 */

public class FootMark implements java.io.Serializable {

    public static final String TABLE_NAME = "foot_mark";
    /**
     * 主键
     */
    public long id;
    /**
     * 用户id
     */
    public long userId;
    /**
     *  商品id
     */
    public long itemId;
    /**
     * 访问时间
     */
    public Date createTime;

    public FootMark() {

    }

    /**
     * 保存
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午12:26:35
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FootMarkMapper mapper = ss.getMapper(FootMarkMapper.class);
            this.createTime = DateTime.now().toDate();
            mapper.insert(this);
        } finally {
            ss.close();
        }
        return true;
    }

}

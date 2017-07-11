package models;

import java.util.Date;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.vo.Page;

import enums.constants.CacheType;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;

/**
 * 
 * 文章实体
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月24日 下午7:18:33
 */
public class Article implements java.io.Serializable {

    public static final String TABLE_NAME = "article";

    public long id;
    /** 作者ID **/
    public long authorId;
    /** 作者名称 */
    public String authorName;
    @MinSize(5)
    @MaxSize(30)
    public String title;
    /** 类型名称 */
    public String typeName;
    /** 文章类型ID */
    public String typeId;
    /** HTML格式的内容 */
    public String content;
    /** 是否对外可见 */
    public boolean isPublic;
    /** 是否在文章列表中 */
    public boolean isInList;
    /** 是否标记删除 */
    public boolean isDelete;
    /** 发布时间 */
    public Date createTime;

    /**
     * 保存文章信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月18日 上午3:05:07
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ArticleMapper mapper = ss.getMapper(ArticleMapper.class);
            if (this.id > 0) {
                mapper.updateById(this);
                clearCacheById(this.id);
                return true;
            }
            this.createTime = DateTime.now().toDate();
            mapper.insert(this);
            clearCacheById(this.id);
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 删除文章
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月18日 上午3:13:22
     */
    public boolean delete() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ArticleMapper mapper = ss.getMapper(ArticleMapper.class);
            mapper.delete(this.id);
            clearCacheById(this.id);
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 缓存中查找文章信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 下午3:22:35
     */
    public static Article findByIdWichCache(long id) {
        String key = CacheType.ARTICLE_DATA.getKey(id);
        Article article = CacheUtils.get(key);
        if (article == null) {
            article = findById(id);
            CacheUtils.set(key, article, CacheType.ARTICLE_DATA.expiredTime);
        }
        return article;
    }

    /**
     * 设置当前缓存
     *
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 下午3:52:23
     */
    public void setByIdWithCache() {
        String key = CacheType.ARTICLE_DATA.getKey(this.id);
        if (this != null) {
            CacheUtils.set(key, this, CacheType.ARTICLE_DATA.expiredTime);
        }
    }

    /**
     * 清空缓存
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月28日 下午7:13:47
     */
    public void clearCacheById(long id) {
        String key = CacheType.ARTICLE_DATA.getKey(this.id);
        CacheUtils.remove(key);
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月18日 下午1:35:42
     */
    public static Article findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ArticleMapper mapper = ss.getMapper(ArticleMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 查找上一篇
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 下午2:24:25
     */
    public Article findPrev() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ArticleMapper mapper = ss.getMapper(ArticleMapper.class);
            String createTime = "";
            if (this.createTime != null) {
                createTime = DateTime.now().withMillis(this.createTime.getTime()).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
            }
            return mapper.selectPrev(this.id, createTime);
        } finally {
            ss.close();
        }

    }

    /**
     * 查找下一篇
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 下午2:24:14
     */
    public Article findNext() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ArticleMapper mapper = ss.getMapper(ArticleMapper.class);
            String createTime = "";
            if (this.createTime != null) {
                createTime = DateTime.now().withMillis(this.createTime.getTime()).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
            }
            return mapper.selectNext(this.id, createTime);
        } finally {
            ss.close();
        }

    }

    /**
     * 条件分页查询
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月18日 上午3:08:07
     */
    public static Page<Article> findItemPage(ArticleSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ArticleMapper mapper = ss.getMapper(ArticleMapper.class);
            int totalCount = mapper.countTotalVo(vo);
            Page<Article> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectByVo(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

}

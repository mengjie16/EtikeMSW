package models;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import vos.PriceRangeVo;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.constants.CacheType;

/**
 * 
 * 系统类目
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月27日 下午4:41:04
 */
public class ItemCate implements java.io.Serializable {

    private static final long serialVersionUID = -6938030022907615289L;

    private static final Logger log = LoggerFactory.getLogger(ItemCate.class);

    public static final String TABLE_NAME = "item_cate";

    /** 类目ID */
    public long id;
    
    public long getId(){
        return this.id;
    }
    /** 类目名称 */
    @Required
    @MinSize(0)
    @MaxSize(64)
    // @Match("^[a-zA-Z0-9\u4e00-\u9fa5_/&-]+$")
    public String name;
    public String getName(){
        return this.name;
    }
    public ItemCate getCurrent(){
        return this;
    }
    /** 类目父ID */
    public long parentCid;
    /** 是否父类目 */
    public boolean isParent;
    /** 同级兄弟中自己的展示顺序(排序规则) */
    public int ordinal;
    /** 类目级别[1,2,3,4...] */
    public int level;
    /** 子类目，不过该字段不会由DB直接返回 */
    public List<ItemCate> children;
    /** 创建时间 */
    public Date createTime;

    /**
     * 保存宝贝类目信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 下午12:45:42
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            if (this.id > 0) {
                int row = mapper.updateById(this);
                removeListCache();
                return row > 0;
            }
            this.createTime = DateTime.now().toDate();
            mapper.insert(this);
            removeListCache();
        } finally {
            ss.close();
        }
        return true;
    }
    
    /**
     * 更新是否父级类目
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月24日 下午4:28:46
     */
    public boolean updateIsParent() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            mapper.updateIsParent(this);
            removeListCache();
        } finally {
            ss.close();
        }
        return true;
    }
    
    /**
     * 保存当前并返回主键
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月12日 下午5:55:26
     */

    public ItemCate saveCurrent() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            if (this.id > 0) {
                mapper.updateById(this);
                removeListCache();
                return this;
            }
            this.createTime = DateTime.now().toDate();
            mapper.insert(this);
            removeListCache();
        } finally {
            ss.close();
        }
        return this;
    }

    /**
     * 删除类目信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月13日 上午11:11:23
     */
    public boolean delete() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            mapper.deleteById(this.id);
            removeListCache();
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 删除类目信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月13日 上午11:18:34
     */
    public static boolean delete(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            mapper.deleteById(id);
            removeListCache();
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 批量保存宝贝类目
     *
     * @param list
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 下午12:44:54
     */
    public static void batchSave(List<ItemCate> list) {
        if (MixHelper.isEmpty(list)) {
            return;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            List transList = null;
            mapper.inserts(transList);
            removeListCache();
        } finally {
            ss.close();
        }
    }

    /**
     * 根据关键字查询宝贝类目列表
     *
     * @param keyWord
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 下午1:14:41
     */
    public static List<ItemCate> findByKeyWord(String keyWord) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            List<ItemCate> list = mapper.selectByKeyWord(keyWord);
            return list;
        } finally {
            ss.close();
        }
    }

    /**
     * 查询page cateitem
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月4日 下午7:25:24
     */
    public static Page<ItemCate> findPageByVo(CateSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            int totalCount = mapper.countByVo(vo);
            Page<ItemCate> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectByVo(vo);
            }
            return page;
        } finally {
            ss.close();
        }

    }

    /**
     * 根据id查找类目信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月6日 下午1:58:43
     */
    public static ItemCate findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            ItemCate cate = mapper.selectOne(id);
            return cate;
        } finally {
            ss.close();
        }
    }
    
    /**
     * 检查名字是否重复
     *
     * @param name
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月24日 下午2:41:39
     */
    public static int countName(String name) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            return  mapper.countName(name);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据级别查找类目
     *
     * @param level
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 下午12:18:00
     */
    public static List<ItemCate> findByLevel(int level) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            return mapper.selectWithLevel(level);
        } finally {
            ss.close();
        }
    }

    /**
     * 查询当前类目列表
     *
     * @param parentId(可填写<=0)
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月13日 上午11:27:26
     */
    public static List<ItemCate> findList(long parentId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            List<ItemCate> list = Lists.newArrayList();
            if (parentId >= 0) {
                list = mapper.selectSubCates(parentId);
            } else {
                list = mapper.selectWithLevel(1);
            }
            return list;
        } finally {
            ss.close();
        }
    }
    /**
     * 查找所有类目
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月2日 下午9:12:13
     */
    public static List<ItemCate> findList() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            return mapper.selectList();
        } finally {
            ss.close();
        }
    }
    
    /**
     * 从缓存中查找所有类目信息
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月2日 下午9:18:37
     */
    public static List<ItemCate> findListByCache(){
        String key = CacheType.ITEM_CATE.getKey();
        List<ItemCate> cates = CacheUtils.get(key);
        if(MixHelper.isEmpty(cates)){
            cates = findList();
            if(MixHelper.isNotEmpty(cates)){
                CacheUtils.set(key, cates, CacheType.ITEM_CATE.expiredTime);
            }
        }
        return cates;
    }
    
    /**
     * 在缓存map中查找数据
     *
     * @param id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月23日 上午11:35:35
     */
    public static ItemCate findByIdMap(long id){
        String key_map = CacheType.ITEM_CATE_MAP.getKey();
        Map<Long,ItemCate> cateMap = CacheUtils.get(key_map);
        if (MixHelper.isEmpty(cateMap)) {
            List<ItemCate> cates = findListByCache();
            if(MixHelper.isEmpty(cates)){
                return null;
            }
            cateMap = cates.stream().collect(Collectors.toMap(ItemCate::getId, ItemCate::getCurrent));
            CacheUtils.set(key_map, cateMap, CacheType.BRAND_LIST.expiredTime);
        }
        return cateMap==null?null:cateMap.get(id);
    }
    
    /**
     * 删除list缓存类目信息
     *
     * @param keyStr
     * @since  v1.0
     * @author Calm
     * @created 2016年8月2日 下午9:20:32
     */
    public static void removeListCache(){
        String key_list = CacheType.ITEM_CATE.getKey();
        String key_map = CacheType.ITEM_CATE_MAP.getKey();
        CacheUtils.remove(key_list);
        CacheUtils.remove(key_map);
    }

    /**
     * 更新类目排序级别
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月13日 上午11:57:01
     */
    public boolean updateOrdinal() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            mapper.updateOrdinal(this);
            removeListCache();
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 查找子类目
     *
     * @param cateId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 上午11:41:20
     */
    private List<ItemCate> findSubCates() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemCateMapper mapper = ss.getMapper(ItemCateMapper.class);
            return mapper.selectSubCates(this.id);
        } finally {
            ss.close();
        }
    }

    /**
     * 查找某类目所有父级类目
     *
     * @param cateId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 下午12:20:16
     */
    private static List<ItemCate> findParents(long cateId) {

        List<ItemCate> cates = Lists.newArrayList();
        ItemCate cate = ItemCate.findByIdMap(cateId);
        if (cate == null) {
            return cates;
        }
        cates.add(cate);
        cates.addAll(findParents(cate.parentCid));
        return cates;
    }

    /**
     * 查找类目下所有子类目
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 上午11:46:25
     */
    public void findChilds() {
        if (!isParent)
            return;
        List<ItemCate> subCates = this.findSubCates();
        if (MixHelper.isNotEmpty(subCates)) {
            this.children = Lists.newArrayList();
            for (ItemCate subCate : subCates) {
                subCate.findChilds();
                this.children.add(subCate);
            }
        }
    }

    /**
     * 查找某类目所有父级类目并根据级别排序
     *
     * @param cateId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 下午1:02:18
     */
    public static List<ItemCate> findParentsCateWithSort(long cateId) {
        List<ItemCate> cates = Lists.newArrayList();
        cates = findParents(cateId);
        if (MixHelper.isEmpty(cates)) {
            return cates;
        }
        cates.sort(new Comparator<ItemCate>() {

            @Override
            public int compare(ItemCate c1, ItemCate c2) {
                // TODO Auto-generated method stub
                if (c1.level > c2.level) {
                    return 1;
                }
                if (c1.level == c2.level) {
                    return 0;
                }
                return -1;
            }
        });
        return cates;
    }
    
    /**
     * 根据类目级别查找类目列表(并查找类目中的子类目)
     *
     * @param level
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月22日 下午12:26:32
     */
    public static List<ItemCate> findCatesWithChildrenByLevel(int level) {
        List<ItemCate> cates = ItemCate.findByLevel(level);
        if (MixHelper.isNotEmpty(cates)) {
            return Lists.newArrayList();
        }
        Iterator<ItemCate> iterator = cates.iterator();
        while (iterator.hasNext()) {
            ItemCate cate = iterator.next();
            cate.findChilds();
        }
        return cates;
    }
}

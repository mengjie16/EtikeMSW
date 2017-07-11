package controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.ReturnCode;
import com.aton.util.CacheUtils;
import com.aton.util.Pandora;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.constants.CacheType;
import enums.constants.RegexConstants;
import models.Article;
import models.Brand;
import models.Item;
import models.ItemCate;
import models.Region;
import play.data.validation.Match;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.With;
import utils.QnCloudUtil;
import vos.ArticleSearchVo;
import vos.BrandSearchVo;
import vos.CateSearchVo;

/**
 * 
 * 公开的数据接口提供
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2015-4-9 下午4:16:13
 */
@With(Secure.class)
public class DataPlant extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(DataPlant.class);

    /**
     * 根据id检索商品信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年9月12日 下午2:21:20
     */
    public static void queryItemById(@Required @Min(1) long id) {
        handleWrongInput(true);
        Item item = Item.findById(id);
        if (item != null) {
            renderJson(item);
        }
        renderFailedJson(ReturnCode.FAIL);
    }

    /**
     * 关键字搜索品牌
     *
     * @param keyWord
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午5:12:33
     */
    public static void brandByKeyWord(
        @Required @MinSize(1) @MaxSize(20) @Match(RegexConstants.ABC_NUM_CN) String keyWord) {
        handleWrongInput(true);
        // 查询品牌
        List<Brand> list = Brand.findBykeyWord(keyWord);
        renderJson(list);
    }

    /**
     * 查询品牌列表
     *
     * @param bvo
     * @since v1.0
     * @author Calm
     * @created 2016年6月3日 下午9:59:34
     */
    public static void queryBrandByVo(@Required BrandSearchVo bvo) {
        handleWrongInput(true);
        Page<Brand> page = Brand.findBrandPage(bvo);
        renderPageJson(page.items, page.totalCount);
    }

    /**
     * 查询所有品牌
     *
     * @since v1.0
     * @author Calm
     * @created 2016年6月29日 下午6:18:00
     */
    public static void queryBrandList() {
        List<Brand> listBrand = Brand.findListByCache();
        renderJson(listBrand);
    }

    /**
     * 查询宝贝类目列表
     *
     * @param cvo
     * @since v1.0
     * @author Calm
     * @created 2016年6月4日 下午7:41:33
     */
    public static void queryItemCateByVo(@Required CateSearchVo cvo) {
        handleWrongInput(true);
        Page<ItemCate> page = ItemCate.findPageByVo(cvo);
        renderPageJson(page.items, page.totalCount);
    }

    /**
     * 根据level查询所有宝贝类目
     *
     * @param level
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 下午12:31:35
     */
    public static void queryAllItemCateByLevel(@Required @Min(0) int level) {
        handleWrongInput(true);
        List<ItemCate> cates = ItemCate.findCatesWithChildrenByLevel(level);
        renderJson(cates);
    }

    /**
     * 查询所有类目信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月2日 下午9:23:43
     */
    public static void queryAllItemCate() {
        List<ItemCate> cates = ItemCate.findListByCache();
        renderJson(cates);
    }

    /**
     * 
     * 获取地区信息
     * 
     * @param id
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:21:32
     */
    public static void region(@Required int id) {
        handleWrongInput(true);
        List<Region> list = Region.findByParentIdWithCache(id);
        renderJson(list);
    }

    /**
     * 
     * 简略版的中国省份
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年8月20日 下午7:16:56
     */
    public static void province() {
        List<Region> list = Region.findLevel2ShortWithCache();
        renderJson(list);
    }

    /**
     * 
     * 根据关键字搜索产地
     *
     * @param keyWord
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月9日 下午5:02:06
     */
    public static void queryOriginByKeyword(
        @Required @MinSize(1) @MaxSize(20) @Match(RegexConstants.ABC_NUM_CN) String keyWord) {
        if (validation.hasErrors()) {
            renderFailedJson(ReturnCode.FAIL);
        }
        // 查询宝贝类目信息
        List<Region> list = Region.findRootByKeyWord(keyWord);
        List<Map> listMap = Lists.transform(list, new Function<Region, Map>() {

            @Override
            public Map apply(Region obj) {
                if (obj != null) {
                    return ImmutableMap.of("id", obj.id, "name", obj.name);
                }
                return null;
            }
        });
        renderJson(listMap);
    }

    /**
     * 
     * 获取文件上传凭证.<br/>
     * 
     * 已放入缓存中（3小时），平台所有用户共享使用.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年8月22日 下午2:33:56
     */
    public static void fetchUploadToken(boolean force) {
        // 强制标记：为用户生成一个全新的token，以解决频繁的“上传失败”问题
        if (force) {
            renderJson(QnCloudUtil.generateUploadToken());
        }

        String key = CacheType.FILE_UPTOKEN.getKey();
        String uptoken = CacheUtils.get(key);
        if (Strings.isNullOrEmpty(uptoken)) {
            uptoken = QnCloudUtil.generateUploadToken();
            CacheUtils.set(key, uptoken, CacheType.FILE_UPTOKEN.expiredTime);
        }
        renderJson(uptoken);
    }

    /**
     * 
     * 生成id接口
     *
     * @param bits 位数
     * @since v1.0
     * @author crazyNew
     * @created 2015年8月24日 下午3:21:44
     */
    public static void generateOid(@Required @Max(18) int bits) {
        handleWrongInput(true);
        long rand = Pandora.getInstance().makeId(bits);
        Map result = ImmutableMap.of("code", ReturnCode.OK, "results", String.valueOf(rand));
        renderJSON(result);
    }

    /**
     * vo搜索文章列表查询
     *
     * @param vo
     * @since v1.0
     * @author Calm
     * @created 2016年6月18日 下午1:28:58
     */
    public static void articleQuery(@Required ArticleSearchVo vo) {
        Page<Article> articles = Article.findItemPage(vo);
        renderPageJson(articles.items, articles.totalCount);
    }
}

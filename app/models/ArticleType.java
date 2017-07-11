package models;

import java.util.Date;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * 文章类型
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月18日 上午2:17:55
 */
public class ArticleType implements java.io.Serializable {

    public String _id;
    /** 类型 */
    public String name;
    
    public ArticleType(){
        
    }
    
    public ArticleType(String id,String name){
        this._id = id;
        this.name = name;
    }
    
    /**
     * 获取默认列表
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月19日 上午1:56:10
     */
    public static List<ArticleType> defaultTypes(){
       return ImmutableList.of(new ArticleType("1","FAQ"),
                               new ArticleType("2","指南"),
                               new ArticleType("3","其他"));
    }
}

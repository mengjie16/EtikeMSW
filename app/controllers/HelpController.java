package controllers;

import controllers.base.BaseController;
import controllers.base.secure.Secure;
import models.Article;
import play.data.validation.Min;
import play.mvc.With;

/**
 * 
 * 帮助中心
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月31日 下午5:14:23
 */
@With(Secure.class)
public class HelpController extends BaseController {

    /**
     * 
     * 帮助中心首页
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月31日 下午5:17:46
     */
    public static void index(@Min(0) int id) {
        // 默认文章
        id = id == 0 ? 5 : id;
        Article currentArticle = Article.findByIdWichCache(id);
        if (currentArticle == null || !currentArticle.isPublic) {
            notFound();
        }
        Article prevArticle = currentArticle.findPrev();
        Article nextArticle = currentArticle.findNext();
        renderArgs.put("currentArticle", currentArticle);
        renderArgs.put("prevArticle", prevArticle);
        renderArgs.put("nextArticle", nextArticle);
        render();
    }

}

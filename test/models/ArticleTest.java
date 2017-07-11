package models;

import static org.junit.Assert.*;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.DateUtils;


public class ArticleTest extends UnitTest{

    @Test
    public void test() {
//        Date now = new Date();
//        DateTime.now().withMillis(now.getTime());
//        System.out.println(DateTime.now().withMillis(now.getTime()).toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
//        System.out.println(DateTime.now().withMillis(now.getTime()).toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        Article curr = Article.findById(5);
        if(curr!=null){
            System.out.println(curr.id);
            System.out.println(curr.createTime.getTime());
            Article prevArticle = curr.findPrev();
            Article nextArticle = curr.findNext();
            System.out.println(prevArticle==null);
            System.out.println(nextArticle==null);
        }
    }

}

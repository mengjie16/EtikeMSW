package models;

import static org.junit.Assert.*;

import java.util.List;

import odms.context.HomePageSettingContext;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.google.common.collect.Lists;


public class HomePageSettingTest extends UnitTest {

    @Test
    public void test_createHomeSetting() {
        HomePageSetting hps = new HomePageSetting();
        hps.name = "713母婴大活动";
        hps.isUse = true;
        if(hps.save()){
            System.out.println("保存成功");
        }else{
            System.out.println("保存失败");
        }
    }
    
    @Test
    public void test_findDefaultHomeSetting(){
        HomePageSetting hps = HomePageSetting.findBuildByCache();
        System.out.println(JsonUtil.toJson(hps));
    }
    
    @Test
    public void test_useCurrent(){
        HomePageSetting hps = HomePageSetting.findById("1");
        if(hps!=null){
            if(hps.useCurrent()){
                System.out.println("应用当前主页配置成功");
            }else{
                System.out.println("应用当前主页配置失败");
            }
            
        }
    }
    
    
    @Test
    public void test_findList(){
        List<HomePageSetting> lhs = HomePageSetting.findList();
        if(lhs!=null){
            System.out.println(lhs.size());
        }
    }
    
    @Test
    public void test_count(){
        System.out.println(HomePageSetting.count());
    }
    
    @Test
    public void test_findLastTimeSettingID(){
        HomePageSettingContext content = new HomePageSettingContext();
        String id = content.findLastTimeSettingID();
        System.out.println(id);
    }
    
    @Test
    public void test_pushValue(){
        // 大Banner
        BigBannerSetting bbs =  new BigBannerSetting();
        bbs.title = "大banner更新后了";
        bbs.imgUrl = "https://img.alicdn.com/tps/TB1.GBIKFXXXXcXXpXXXXXXXXXX-520-280.jpg";
        bbs.url = "https://www.taobao.com/markets/promotion/chaowan/xiari-index?spm=a21bo.50862.201862-1.d1.FyDraI&acm=20140506001.1003.2.924430&aldid=U9IyPSpw&scm=1003.2.20140506001.OTHER_1468471533768_924430&pos=1";
        // 右侧小banner
        RightSmallBannerSetting rss =  new RightSmallBannerSetting();
        rss.imgUrl = "https://aecpm.alicdn.com/simba/img/TB1oet7KFXXXXbhXXXXSutbFXXX.jpg";
        rss.title = "右侧小招牌2";
        rss.url = "http://taobao.com";
        // 品牌列表
        BrandSetting bs =  new BrandSetting();
        bs.imgUrl = "http://cdn.tusibaby.com/o_1akblopnf1sol10hjr9oof21u979.jpg";
        bs.title = "帮宝适";
        // 活动列表
        ActivitySetting as = new ActivitySetting();
        as.imgUrl = "https://img.alicdn.com/tps/i4/TB1FtFrKFXXXXXWXFXXSutbFXXX.jpg";
        as.url = "#";
       // as.itemSettings = Lists.newArrayList();
        ItemSetting is1 =new ItemSetting();
        is1.id = 1L;
        is1.price = 256;
        
        ItemSetting is2 =new ItemSetting();
        is2.id = 2L;
        is2.price = 789;
        
        ItemSetting is3 =new ItemSetting();
        is3.id = 3L;
        is3.price = 567;
        as.itemSettings.add(is1);
        as.itemSettings.add(is2);
//        as.itemSettings.add(is3);
        
        if(HomePageSetting.pushValueToField("main_0","big_BannerSettings",bbs.toMap())){
            System.out.println("添加成功");
        }else{
            System.out.println("添加失败");
        }
        
    }
    
    @Test
    public void test_updateFieldValue(){
        ActivitySetting as = new ActivitySetting();
        as.imgUrl = "https://img.alicdn.com/tps/i4/TB1FtFrKFXXXXXWXFXXSutbFXXX.jpg";
        as.url = "#";
       // as.itemSettings = Lists.newArrayList();
        ItemSetting is1 =new ItemSetting();
        is1.id = 1L;
        is1.price = 256;
        
        ItemSetting is2 =new ItemSetting();
        is2.id = 2L;
        is2.price = 789;
        
        ItemSetting is3 =new ItemSetting();
        is3.id = 3L;
        is3.price = 567;
//        as.itemSettings.add(is1);
//        as.itemSettings.add(is2);
        
        BrandSetting bs =  new BrandSetting();
        bs.bid = 4;
        if(HomePageSetting.updateValueByFieldIndex("main_0","brandSettings",0,bs.toMap())){
            System.out.println("更新成功");
        }else{
            System.out.println("更新失败");
        }
    }
    
    @Test
    public void test_removeByIndex(){
        if(HomePageSetting.removeValueByFieldIndex("main_0","brandSettings",0)){
            System.out.println("删除成功");
        }else{
            System.out.println("删除失败");
        } 
    }

}

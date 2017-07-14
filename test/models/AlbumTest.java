package models;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.google.common.collect.Lists;


public class AlbumTest extends UnitTest {

    @Test
    public void test_save() {
        Album album =  new Album();
        album.name = "测试专辑";
        List<AlbumItem> albumItems = Lists.newArrayList();
        albumItems.add(new AlbumItem(2,345));
        albumItems.add(new AlbumItem(3,655));
        album.albumItems = albumItems;
        if(album.save()){
            System.out.println("保存成功");
        }
    }
    
    public void test_update() {
        Album album =  new Album();
        album.name = "测试专辑";
        List<AlbumItem> albumItems = Lists.newArrayList();
        albumItems.add(new AlbumItem(2,345));
        albumItems.add(new AlbumItem(3,655));
        album.albumItems = albumItems;
        if(album.save()){
            System.out.println("保存成功");
        }
    }
    
    @Test
    public void test_findById(){
        Album album =  Album.findById(1);
        if(album!=null){
            System.out.println(JsonUtil.toJson(album));
        }
    }
    
    @Test
    public void test_findList(){
    }
    
    @Test
    public void addAlbumItem(){
        Album album =  Album.findById(1);
        if(album.addAlbumItem(new AlbumItem(7,785,"http://cdn.eitak.com/o_1anhlakdd1pc6h51ph211uv1fsgo.jpg"))){
            System.out.println("添加成功");
        }
    }
    
    @Test
    public void removeAlbumItemByIndex(){
        Album album =  Album.findById(1);
        if(album.deleteAblumItemByIndex(2)){
            System.out.println("添加成功");
        }
    }
    
    @Test
    public void test_checkAlbumItem(){
        Album album =  Album.findById(1);
        if(!album.checkExsitAlbumItemByItemId(7)){
            System.out.println("不存在");
        }else{
            System.out.println("该商品已经存在");
        }
    }
    
    @Test
    public void test_checkAlbumName(){
        if(Album.checkAlbumExsitName("测试专辑")){
            System.out.println("存在");
        }else{
            System.out.println("不存在");
        }
    }
    
    @Test
    public void test_updateAlbumItemByIndex(){
        Album album =  Album.findById(1);
        if(album.updateAlbumItemByIndex(0,new AlbumItem(2,655,"http://cdn.eitak.com/o_1ao180a73t31ce61iqe1cskrstv.jpg"))){
            System.out.println("更新成功");
        }
    }
}

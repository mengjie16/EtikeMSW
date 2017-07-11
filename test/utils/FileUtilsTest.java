package utils;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.aton.util.FileUtils;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.RegexUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;

import models.Item;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.libs.ws.WSAsync;
import play.test.UnitTest;


public class FileUtilsTest extends UnitTest{
    
    @Test
    public void test_readFile() {
       File file = new File("/Users/Calm/Downloads/0826老毕单子.xlsx");
       Table table = utils.TSFileUtil.parseExcel(file);
       MixHelper.print(table.rowKeySet().size());
    }
    
    @Test
    public void test_zipFile() throws URISyntaxException, IOException{
        URL url = new URL("http://icon.nipic.com/BannerPic/20160824/home/20160824153208.jpg");
     
        //InputStream fileStream = request.get().getStream();
        File file = new File("/Users/Calm/Downloads/test_zip.jpg");
        utils.TSFileUtil.zip("/Users/Calm/Downloads/test_zip.zip", ImmutableList.of("/Users/Calm/Downloads/test_zip.jpg"));
        //= new File(url.);
//        if(file!=null){
//            String fileStr = FileUtils.readFileToString(file);
//            System.out.println(file.length());
//        }
    }
    
    @Test
    public void test_matchImgSrc() throws IOException{
        Item item = Item.findById(472);
        if(StringUtils.isEmpty(item.detail)){
            return;
        }
        // 获取商品所有详情img标签
        List<String> imgs = RegexUtils.matches(item.detail, "<(img|IMG)(.*?)(/>|></img>|>)");
        // 捕获所有img src 内容
        if(MixHelper.isNotEmpty(imgs)){
            List<Map<String,String>> matchSrcList = imgs.stream().filter(s->!Strings.isNullOrEmpty(s)).map(s->RegexUtils.match(s, "(src|SRC)=(\"|\')({url}.*)(\"|\')","url")).collect(Collectors.toList());
            if(MixHelper.isEmpty(matchSrcList)){
                return;
            }
            List<String> srcList = matchSrcList.stream().map(m->m.get("url")).filter(s->!Strings.isNullOrEmpty(s)).collect(Collectors.toList());
            if(MixHelper.isEmpty(srcList)){
                return;
            }
            Iterator<String> srcIter = srcList.iterator();
            // 压缩流
            FileOutputStream fileOutputStream = new FileOutputStream("/Users/Calm/Downloads/test_load_zip.zip");
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            int i = 1;
            while(srcIter.hasNext()){
                String src = srcIter.next();
                if(!RegexUtils.isMatch(src, "((?i)http)|((?i)https)")){
                    System.out.println(src);
                    if(RegexUtils.isMatch(src, "^(//).*?$")){
                        src = "http:"+src;
                    }else if(RegexUtils.isMatch(src, "^(:).*?$")){
                        src = "http"+src;
                    }else{
                        src = "http://"+src;
                    }
                }
                
                WSRequest request = WS.url(src);
                HttpResponse respone = request.get();
                if(!respone.success()){
                    continue;
                }
                InputStream stream = respone.getStream();
                try {
                    // 输入流
                    BufferedInputStream bis = new BufferedInputStream(stream);
                    ZipEntry entry = new ZipEntry(i+".jpg");
                    out.putNextEntry(entry);
                    // 复制数据
                    IOUtils.copy(bis, out);
                    // 关闭输入流
                    IOUtils.closeQuietly(bis);
                    stream.close();
                    i++;
                } catch (Exception e) {
                    //throw new RuntimeException("[压缩文件]-压缩文件异常, ZIP[" + zip + "], Files[" + files + "]！", e);
                }
                
            }

            // 完成
            IOUtils.closeQuietly(out);
        }
        
    }
    
    @Test
    public void test_match(){
        String url = "http://icon.nipic.com/BannerPic/20160824/home/20160824153208.jpg";
        System.out.println(StringUtils.substringAfterLast(url, "/"));
    }

}

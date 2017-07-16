package utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;


/**
 * 货币汇率调用示例代码 － 聚合数据
 * 在线接口文档：http://www.juhe.cn/docs/23
 **/

public class ExchangeRateUtil {

    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";


    
    // 外汇汇率
    public static Float getExchangeRate() {
        String result = null, rtn = null;
        String url = "http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=CNYEUR=x";// 请求接口地址
        URL MyURL = null;
        URLConnection con = null;
        InputStreamReader ins = null;
        BufferedReader in = null;
        try {
         MyURL = new URL(url);
         con = MyURL.openConnection();   
         ins = new InputStreamReader(con.getInputStream(),"UTF-8");
         in = new BufferedReader(ins);
         result = in.readLine();
         
         // http response返回的字符串中，日期包含 双引号“ 必须删掉。
         if(result != null) 
             result = result.replace("\"", "");   
             String[] res = result.split(",");
             rtn = res[1];
         
        } catch (SocketException ex) {
         ex.printStackTrace();
        } catch (Exception ex) {
         ex.printStackTrace();
        } finally { 
         if(in != null)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       
        
        return Float.valueOf(rtn);
    }

    public static void main(String[] args) {
        System.out.println(getExchangeRate());        
    }
    


}

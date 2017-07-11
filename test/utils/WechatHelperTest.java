package utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;


public class WechatHelperTest extends UnitTest{

    @Test
    public void test() {
        String jsApi = WechatHelper.getJsapiTicket(false);
        System.out.println(jsApi);
    }

}

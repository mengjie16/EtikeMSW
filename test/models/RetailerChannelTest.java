package models;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;

public class RetailerChannelTest extends UnitTest {

    @Test
    public void test() {
        List<RetailerChannel> lrc = RetailerChannel.getChannelList();
        System.out.println(JsonUtil.toJson(lrc));
    }

}

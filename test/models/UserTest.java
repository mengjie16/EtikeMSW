package models;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import vos.UserSearchVo;

import com.aton.test.UnitTest;
import com.aton.util.DateUtils;
import com.aton.util.JsonUtil;
import com.aton.util.NumberUtils;
import com.aton.util.Pandora;
import com.aton.util.StringUtils;
import com.aton.vo.Page;
import com.google.common.collect.Lists;

import play.libs.Codec;

public class UserTest extends UnitTest {

    @Test
    public void test_other() {
        User user = User.findByPhone("18657157520");
        user.password = "1234567";
        boolean result = user.savePassword();
        System.out.println(result);

    }

    @Test
    public void test_delete() {
        User.deleteById(4);
    }
}

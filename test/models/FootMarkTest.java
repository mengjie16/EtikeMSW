package models;

import static org.junit.Assert.*;

import org.junit.Test;

import play.test.UnitTest;

public class FootMarkTest extends UnitTest {

    @Test
    public void test_save() {
        FootMark fm = new FootMark();
        fm.userId = 6;
        fm.itemId = 4;
        System.out.println("保存结果：" + fm.save());
    }

}

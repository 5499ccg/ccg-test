package org.ccg.test.json;

import org.ccg.test.TestBase;
import org.ccg.test.utils.DateParseUtil;
import org.ccg.test.utils.FileTools;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

public class JsonAssertTest extends TestBase {

    @DataProvider(name = "data_jsonCompare_diff")
    public Object[][] data_jsonCompare_diff() {
        return new Object[][]{
                {"1.json只有一层，数字校验存在差异", "1_json_1floor_int_diff"},
                {"2.json只有一层，字符串存在差异", "2_json_1floor_string_diff"},
                {"3.json只有一层，boolean存在差异", "3_json_1floor_boolean_diff"},
                {"4.json只有一层，actual其中一个key不存在", "4_json_1floor_actual_key_not_exist"},
                {"5.json只有一层，actual其中一个key的值为null", "5_json_1floor_actual_value_isNull"},
                {"6.json只有一层，expected其中一个key不存在", "6_json_1floor_expected_key_not_exist"},
                {"7.json只有一层，expected其中一个key的值为null", "7_json_1floor_expected_value_isNull"},
                {"8.json只有一层，数组元素不一致", "8_json_1floor_array_value_diff"},
                {"9.json只有一层，数组元素长度", "9_json_1floor_array_size_diff"},
                {"10.json三层，各种数据类型都有，第三层有个数据不一样", "10_json_3floor_all_type_3floor_diff"},

        };
    }

    @Test(dataProvider = "data_jsonCompare_diff")
    public void test_jsonCompare_diff(String comment, String testNo) throws Exception {
        logger.info("test_jsonCompare_diff test start: {}, {}.", comment, testNo);
        String expectedJson = FileTools.readAsStringByPathAndStack(testNo + "/expectData.json");
        String actualJson = FileTools.readAsStringByPathAndStack(testNo + "/actualData.json");
        try {
            JsonAssert.assertJsonEquals(expectedJson, actualJson, comment);
        } catch (AssertionError e) {
            String message = e.getMessage();
            logger.error(e);
            String expectMessage = FileTools.readAsStringByPathAndStack(testNo + "/diff_error.txt");
            Assert.assertEquals(message, expectMessage, comment);
//            throw e;
        }
    }


    @DataProvider(name = "data_jsonCompare_pass")
    public Object[][] data_jsonCompare_pass() {
        return new Object[][]{
                {"1.json所有数据类型，校验一致通过", "1_json_all_type_same"},

        };
    }

    @Test(dataProvider = "data_jsonCompare_pass")
    public void test_jsonCompare_pass(String comment, String testNo) throws Exception {
        logger.info("test_jsonCompare_pass test start: {}, {}.", comment, testNo);
        String expectedJson = FileTools.readAsStringByPathAndStack(testNo + "/expectData.json");
        String actualJson = FileTools.readAsStringByPathAndStack(testNo + "/actualData.json");
        JsonAssert.assertJsonEquals(expectedJson, actualJson, comment);
    }


    @DataProvider(name = "data_jsonCompare_checkImpl")
    public Object[][] data_jsonCompare_checkImpl() {
        return new Object[][]{
                {"1.json两层，id存在差异，忽略校验", "1_json_2floor_diff_and_ignore", true},
                {"2.json两层，实际数据的id不存在，忽略校验", "2_json_2floor_actual_isNull_and_ignore", true},
                {"3.json两层，期望的数据的id不存在，忽略校验", "3_json_2floor_expect_isNull_and_ignore", true},
                {"4.json两层，id差异忽略校验，期望数据与当前时间比较", "4_json_2floor_idIgnore_time_compareToNow", true},
                {"5.json两层，id差异,使用正则表达式校验，校验通过", "5_json_2floor_id_regexCheck_pass", true},
                {"6.json两层，id差异,使用正则表达式校验，校验不通过", "6_json_2floor_id_regexCheck_error", false},
                {"7.json两层，id和joinDate差异，一起忽略", "7_json_2floor_id_and_joinDate_ignore", true},
        };
    }

    @Test(dataProvider = "data_jsonCompare_checkImpl")
    public void test_jsonCompare_checkImpl(String comment, String testNo, boolean pass) throws Exception {
        logger.info("test_jsonCompare_checkImpl test start: {}, {}.", comment, testNo);
        Map<String, String> params = new HashMap<>();
        params.put("nowDate", DateParseUtil.formatDateToStandard(new Date()));
        String expectedJson = FileTools.readAsStringByPathAndStack(testNo + "/expectData.json");
        String actualJson = FileTools.readAsStringByPathAndStack(testNo + "/actualData.json", params);
        List<Check> checks = new ArrayList<>();
        // 忽略id
        checks.add(testNo.contains("id_and_joinDate_ignore") ? Check.ignore("datas.id", "datas.joinDate") : Check.ignore("datas.id"));
        if (testNo.contains("compareToNow")) {
            checks.add(Check.timeNow("datas.joinDate"));
        }
        if (testNo.contains("regexCheck")) {
            if (pass) {
                checks.add(Check.regex("^\\d{4}$", "datas.id"));
            } else {
                checks.add(Check.regex("^\\d{5}$", "datas.id"));
            }
        }
        try {
            JsonAssert.assertJsonEquals(expectedJson, actualJson, comment, checks.toArray(new Check[0]));
            if (!pass) {
                Assert.fail(comment);
            }
        } catch (AssertionError e) {
            if (!pass) {
                String message = e.getMessage();
                logger.error(e);
                String expectMessage = FileTools.readAsStringByPathAndStack(testNo + "/diff_error.txt");
                Assert.assertEquals(message, expectMessage, comment);
//            throw e;
            }
        }
    }

}
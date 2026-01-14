package org.ccg.test.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.istack.internal.Nullable;
import org.ccg.test.json.check.ArrayNotOrderCheck;
import org.ccg.test.json.check.IgnoreCheck;
import org.ccg.test.json.check.RegexCheck;
import org.ccg.test.json.check.TimeCheckNow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Check {

    /**
     * 校验两个node是否相等，两个node不会同时为null，但可能存在一个是为null，一个isNull
     * @param expectNode 可能为null，或者值为null
     * @param actualNode 可能为null，或者值为null
     * @return true.校验通过 false.校验不通过
     */
    boolean doCheck(@Nullable JsonNode expectNode, @Nullable JsonNode actualNode);

    /**
     * 校验两个node是否相等，两个node不会同时为null，但可能存在一个是为null，一个isNull
     * @param jsonAssertData json断言数据
     * @param prefixKey key
     * @param expectedNode 节点可能为null，或者值为null
     * @param actualNode 节点可能为null，或者值为null
     */
    default void doCheck(JsonAssertData jsonAssertData, String prefixKey,
                            @Nullable JsonNode expectedNode, @Nullable JsonNode actualNode){
        throw new UnsupportedOperationException("un supported");
    }

    /**
     * 指定字段列表的校验
     * @return 返回作用的key列表
     */
    String[] getKeys();

    static Map<String, List<Check>> toMap(Check[] checks) {
        if (checks == null) {
            return null;
        }
        Map<String, List<Check>> map = new HashMap<>();
        for (Check check : checks) {
            if (check == null) {
                continue;
            }
            for (String key : check.getKeys()) {
                List<Check> keyChecks = map.computeIfAbsent(key, k -> new ArrayList<>());
                keyChecks.add(check);
            }
        }
        return map;
    }

    static Check ignore(String... keys) {
        return new IgnoreCheck(keys);
    }

    static Check timeNow(String... keys) {
        return new TimeCheckNow(keys);
    }

    static Check regex(String regex, String... keys) {
        return new RegexCheck(regex, keys);
    }

    static Check arrayNotOrder(String... keys) {
        return new ArrayNotOrderCheck(keys);
    }
}

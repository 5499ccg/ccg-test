package org.ccg.test.json.check;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.ccg.test.json.Check;
import org.ccg.test.json.CheckFailMessageBuilder;
import org.ccg.test.json.JsonAssert;
import org.ccg.test.json.JsonAssertData;

public class ArrayNotOrderCheck implements Check {

    private final String[] keys;

    public ArrayNotOrderCheck(String... keys) {
        this.keys = keys;
    }

    @Override
    public boolean doCheck(JsonNode expectNode, JsonNode actualNode) {
        throw new UnsupportedOperationException("un supported");
    }

    public void doCheck(JsonAssertData jsonAssertData, String prefixKey,
                           @Nullable JsonNode expectedNode, @Nullable JsonNode actualNode){
        if (jsonAssertData.isFail()) {
            return;
        }
        if (expectedNode == null && actualNode == null) {
            return;
        }
        if (expectedNode == null || actualNode == null) {
            jsonAssertData.toFail(CheckFailMessageBuilder.fail(prefixKey));
            return;
        }
        if (expectedNode.isNull() && actualNode.isNull()) {
            return;
        }
        if (expectedNode.isNull() || actualNode.isNull() || !expectedNode.isArray() || !actualNode.isArray()) {
            jsonAssertData.toFail(CheckFailMessageBuilder.fail(prefixKey));
            return;
        }
        if (expectedNode.size() != actualNode.size()) {
            jsonAssertData.toFail(CheckFailMessageBuilder.failForArraySize(prefixKey, expectedNode, actualNode));
            return;
        }
        // copy一份
        ArrayNode expectArr = expectedNode.deepCopy();
        ArrayNode actualArr = actualNode.deepCopy();
        // 清空
        for (JsonNode expectItem : expectArr) {
            int index = 0;
            for (; index < actualArr.size(); index++) {
                // 如果上个元素校验失败，则重置，继续尝试校验下个元素
                if (jsonAssertData.isFail()) {
                    jsonAssertData.checkReSet();
                }

                // 校验
                JsonNode actualItem = actualArr.get(index);
                JsonAssert.nodesEqual(jsonAssertData, prefixKey, expectItem, actualItem, true);
                if (!jsonAssertData.isFail()){
                    // 校验成功，移除已校验的元素
                    actualArr.remove(index);
                    break;
                }
            }
            if (jsonAssertData.isFail()) {
                return;
            }
        }
    }

    @Override
    public String[] getKeys() {
        return this.keys;
    }
}

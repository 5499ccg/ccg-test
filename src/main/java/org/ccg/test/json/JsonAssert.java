package org.ccg.test.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.ccg.test.json.check.ArrayNotOrderCheck;
import org.ccg.test.json.check.defaults.BooleanCheck;
import org.ccg.test.json.check.defaults.NumberCheck;
import org.ccg.test.json.check.defaults.StringCheck;

import java.util.*;

public class JsonAssert {
    public static final Character OPENING_CHARACTER = '[';
    public static final Character CLOSING_CHARACTER = ']';

    public static final String ASSERT_LEFT = "expected " + OPENING_CHARACTER;
    public static final String ASSERT_LEFT2 = "expected not same " + OPENING_CHARACTER;
    public static final String ASSERT_MIDDLE = CLOSING_CHARACTER + " but found " + OPENING_CHARACTER;
    public static final String ASSERT_RIGHT = Character.toString(CLOSING_CHARACTER);

    // 默认检查器
    private static final Map<JsonNodeType, Check> JSON_NODE_TYPE_CHECK_MAP = new HashMap<>();

    static {
        JSON_NODE_TYPE_CHECK_MAP.put(JsonNodeType.STRING, new StringCheck());
        JSON_NODE_TYPE_CHECK_MAP.put(JsonNodeType.NUMBER, new NumberCheck());
        JSON_NODE_TYPE_CHECK_MAP.put(JsonNodeType.BOOLEAN, new BooleanCheck());
    }

    // 这里public允许客户自己修改一些属性
    public static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        // 时区跟随系统
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.configure(com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.configure(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * JSON比较器
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param checks   自定义校验器
     * @throws Exception 异常
     */
    public static void assertJsonEquals(Object expected, Object actual, Check... checks) throws Exception {
        assertJsonEquals(expected, actual, "", checks);
    }

    /**
     * JSON比较器
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param comment  校验任务描述
     * @param checks   自定义校验器
     * @throws Exception 异常
     */
    public static void assertJsonEquals(Object expected, Object actual, String comment, Check... checks) throws Exception {
        // 从这一步开始，两个对象不会同时为null
        if (Objects.equals(expected, actual)) {
            return;
        }
        String expectJson = expected == null || expected instanceof String ? (String) expected : objectMapper.writeValueAsString(expected);
        String actualJson = actual == null || actual instanceof String ? (String) actual : objectMapper.writeValueAsString(actual);

        JsonAssertData jsonAssertData = new JsonAssertData(expectJson, actualJson, comment, checks);
        // 这里可能会序列化失败异常，那就异常吧
        JsonNode expectedNode = expectJson == null ? null : objectMapper.readTree(expectJson);
        JsonNode actualNode = actualJson == null ? null : objectMapper.readTree(actualJson);

        if (expectedNode == null || actualNode == null) {
            jsonAssertData.toFail("expect or actual is null!");
        } else {
            // 开始递归校验
            nodesEqual(jsonAssertData, "", expectedNode, actualNode, false);
        }

        if (jsonAssertData.isFail()) {
            String message = jsonAssertData.getFailMessage();
            String errorMessage = formatFailMessage(formatJson(expectJson), formatJson(actualJson), message);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * 比较两个JsonNode是否相等，到了这里
     * 首先不存在两个都为null的场景：
     * expectedNode==null && actualNode==null
     * 存在以下的场景的组合：
     * expectedNode==null，expectedNode!=null && expectedNode type isNull，expectedNode is obj
     * actualNode==null，actualNode!=null && actualNode type isNull，actualNode is obj
     * 要明确：
     * 1.== null 和 type is null是不一样的，相当与一个存在key但值为null，一个连key都不存在的，这种场景要进入校验器处理
     * 2.expectedNode和actualNode都is null，也不能直接认为相等，因为有些场景我们使用自定义校验器处理，期望是当前时间
     */
    public static void nodesEqual(JsonAssertData jsonAssertData, String prefixKey,
                                   @Nullable JsonNode expectedNode, @Nullable JsonNode actualNode, boolean preIsArray) {
        // 1.优先走自定义检器，如果存在多个，必须全部过
        List<Check> checkListByKey = jsonAssertData.getCheckListByKey(prefixKey);
        if (checkListByKey != null && !checkListByKey.isEmpty() && !preIsArray) {
            for (Check check : checkListByKey) {
                if (check instanceof ArrayNotOrderCheck) {
                    check.doCheck(jsonAssertData, prefixKey, expectedNode, actualNode);
                    if (jsonAssertData.isFail()) {
                        break;
                    }
                } else if (!check.doCheck(expectedNode, actualNode)) {
                    jsonAssertData.toFail(CheckFailMessageBuilder.fail(prefixKey, expectedNode, actualNode));
                    break;
                }
            }
            // 存在自定义校验器，不使用默认校验器校验
            return;
        }

        // 2.null值处理（包括null和isNull），在这里处理的好处就是，check实现类不需要判空处理
        if (expectedNode == null && actualNode == null) {
            return;
        }
        if (expectedNode == null) {
            // 这里有一种特殊情况：actualNode.isNull(),也认为不一致
            jsonAssertData.toFail(CheckFailMessageBuilder.failForExpectNotExist(prefixKey, expectedNode, actualNode));
            return;
        }
        if (actualNode == null) {
            jsonAssertData.toFail(CheckFailMessageBuilder.failForActualNotExist(prefixKey, expectedNode, actualNode));
            return;
        }
        if (expectedNode.isNull() && actualNode.isNull()) {
            return;
        }
        if (expectedNode.isNull()) {
            jsonAssertData.toFail(CheckFailMessageBuilder.failForExpectNull(prefixKey, expectedNode, actualNode));
            return;
        }
        if (actualNode.isNull()) {
            jsonAssertData.toFail(CheckFailMessageBuilder.failForActualNull(prefixKey, expectedNode, actualNode));
            return;
        }
        // 类型不一致
        if (!expectedNode.getNodeType().equals(actualNode.getNodeType())) {
            jsonAssertData.toFail(CheckFailMessageBuilder.failForType(prefixKey, expectedNode, actualNode));
            return;
        }
        // 默认校验器校验
        nodesEqualForNotNull(jsonAssertData, prefixKey, expectedNode, actualNode);
    }

    private static void nodesEqualForNotNull(JsonAssertData jsonAssertData, String prefixKey,
                                             @NotNull JsonNode expectedNode, @NotNull JsonNode actualNode) {
        //  根据数据类型校验，到了这里expectedNode和actualNode都不会是null或者isNull
        JsonNodeType jsonNodeType = expectedNode.getNodeType();
        switch (jsonNodeType) {
            case OBJECT:
                objectsEqual(jsonAssertData, prefixKey, (ObjectNode) expectedNode, (ObjectNode) actualNode);
                break;
            case ARRAY:
                arraysEqual(jsonAssertData, prefixKey, (ArrayNode) expectedNode, (ArrayNode) actualNode);
                break;
            default:
                // 走默认校验器
                Check check = JSON_NODE_TYPE_CHECK_MAP.get(jsonNodeType);
                if (check != null) {
                    if (!check.doCheck(expectedNode, actualNode)) {
                        jsonAssertData.toFail(CheckFailMessageBuilder.fail(prefixKey, expectedNode, actualNode));
                    }
                    return;
                }
                // 校验器不存在直接报错
                throw new IllegalArgumentException("Unsupported JsonNode type: " + jsonNodeType);
        }
    }

    /**
     * 比较两个对象节点，也就是map
     */
    private static void objectsEqual(JsonAssertData jsonAssertData, String prefixKey,
                                     @NotNull ObjectNode expectON, @NotNull ObjectNode actualON) {
        Set<String> keys = new HashSet<>();
        expectON.fieldNames().forEachRemaining(keys::add);
        actualON.fieldNames().forEachRemaining(keys::add);

        for (String key : keys) {
            JsonNode expect =  expectON.get(key);
            JsonNode actual =  actualON.get(key);
            String newPrefixKey = prefixKeyAdd(prefixKey, key);
            nodesEqual(jsonAssertData, newPrefixKey, expect, actual, false);
            if (jsonAssertData.isFail()) {
                return;
            }
        }
    }

    /**
     * 比较两个数组节点
     */
    private static void arraysEqual(JsonAssertData jsonAssertData, String prefixKey,
                                    @NotNull ArrayNode expectArr, @NotNull ArrayNode actualArr) {
        // 长度一致性校验
        if (expectArr.size() != actualArr.size()) {
            jsonAssertData.toFail(CheckFailMessageBuilder.failForArraySize(prefixKey, expectArr, actualArr));
            return;
        }
        // 数组内元素校验，严格按顺序校验
        for (int i = 0; i < expectArr.size(); i++) {
            nodesEqual(jsonAssertData, prefixKey, expectArr.get(i), actualArr.get(i), true);
            if (jsonAssertData.isFail()) {
                return;
            }
        }
    }

    private static String prefixKeyAdd(String prefixKey, String addKey) {
        return prefixKey.isEmpty() ? addKey : prefixKey + "." + addKey;
    }

    private static String formatJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return json;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            JsonNode sortNode = sortJsonNode(node);
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(sortNode);
        } catch (Exception e) {
            return json; // 如果格式化失败，返回原始字符串
        }
    }


    private static JsonNode sortJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sortedNode = objectMapper.createObjectNode();
            // 获取字段名并排序
            List<String> fieldNames = new ArrayList<>();
            node.fieldNames().forEachRemaining(fieldNames::add);
            Collections.sort(fieldNames);

            for (String fieldName : fieldNames) {
                sortedNode.set(fieldName, sortJsonNode(node.get(fieldName)));
            }
            return sortedNode;
        } else if (node.isArray()) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (JsonNode item : node) {
                arrayNode.add(sortJsonNode(item));
            }
            return arrayNode;
        } else {
            return node;
        }
    }

    private static String formatFailMessage(Object expected, Object actual, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        return formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT;
    }
}

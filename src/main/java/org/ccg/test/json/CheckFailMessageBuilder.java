package org.ccg.test.json;

import com.fasterxml.jackson.databind.JsonNode;

public class CheckFailMessageBuilder {

    public static String fail(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect ")
                .append(expectNode)
                .append(", actual ")
                .append(actualNode);
        return sb.toString();
    }
    public static String fail(String prefixKey) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail");
        return sb.toString();
    }

    public static String failForType(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect type ")
                .append(expectNode.getNodeType())
                .append(", actual type ")
                .append(actualNode.getNodeType());
        return sb.toString();
    }

    public static String failForExpectNotExist(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect not exist, but actual ")
                .append(isObjOrArray(actualNode) ? "exist" : actualNode);
        return sb.toString();
    }

    public static String failForActualNotExist(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect ")
                .append(isObjOrArray(expectNode) ? "exist" : expectNode)
                .append(", but actual not exist");
        return sb.toString();
    }


    public static String failForExpectNull(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect is null, but actual ")
                .append(isObjOrArray(actualNode) ? "not null" : actualNode);
        return sb.toString();
    }

    public static String failForActualNull(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect ")
                .append(isObjOrArray(expectNode) ? "not null" : expectNode)
                .append(", but actual is null");
        return sb.toString();
    }



    public static String failForArraySize(String prefixKey, JsonNode expectNode, JsonNode actualNode) {
        StringBuilder sb = new StringBuilder("check ");
        sb.append(prefixKey)
                .append(" fail: expect array size ")
                .append(expectNode.size())
                .append(", actual array size ")
                .append(actualNode.size());
        return sb.toString();
    }


    private static boolean isObjOrArray(JsonNode node) {
        return node.isObject() || node.isArray();
    }
}

package org.ccg.test.json.check;

import com.fasterxml.jackson.databind.JsonNode;
import org.ccg.test.json.Check;

import java.util.regex.Pattern;

public class RegexCheck implements Check {
    private final String regex;
    private final Pattern pattern;
    private final String[] keys;

    public RegexCheck(String regex, String... keys) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        this.keys = keys;
    }

    @Override
    public boolean doCheck(JsonNode expectNode, JsonNode actualNode) {
        // 检查 actualNode 是否为文本类型
        if (actualNode == null) {
            return false;
        }
        String actualValue = actualNode.toString();
        // 使用预编译的正则模式匹配
        return pattern.matcher(actualValue).matches();
    }

    @Override
    public String[] getKeys() {
        return keys;
    }
}

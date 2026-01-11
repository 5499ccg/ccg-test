package org.ccg.test.json.check.defaults;

import com.fasterxml.jackson.databind.JsonNode;
import org.ccg.test.json.Check;

import java.util.Objects;

public class StringCheck implements Check {
    @Override
    public boolean doCheck(JsonNode expectNode, JsonNode actualNode) {
        return Objects.equals(expectNode.asText(), actualNode.asText());
    }

    @Override
    public String[] getKeys() {
        return null;
    }
}

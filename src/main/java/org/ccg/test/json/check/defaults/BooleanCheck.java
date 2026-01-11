package org.ccg.test.json.check.defaults;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.istack.internal.Nullable;
import org.ccg.test.json.Check;

import java.util.Objects;

public class BooleanCheck implements Check {
    @Override
    public boolean doCheck(@Nullable JsonNode expectNode, @Nullable JsonNode actualNode) {

        return Objects.equals(expectNode.asBoolean(), actualNode.asBoolean());
    }

    @Override
    public String[] getKeys() {
        return null;
    }
}

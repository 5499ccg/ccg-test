package org.ccg.test.json.check;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.ccg.test.json.Check;

import java.util.Arrays;

public class IgnoreCheck implements Check {
    private final String[] keys;

    public IgnoreCheck(String... keys) {
        if (keys != null) {
            boolean b = Arrays.stream(keys).anyMatch(t -> true);
            if (!b) {
                throw new IllegalArgumentException("keys is empty");
            }
        }
        this.keys = keys;
    }

    @Override
    public boolean doCheck(@Nullable JsonNode expectNode,@NotNull JsonNode actualNode) {
        return true;
    }

    @Override
    public String[] getKeys() {
        return keys;
    }
}

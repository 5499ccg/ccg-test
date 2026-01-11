package org.ccg.test.json.check;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.ccg.test.json.Check;
import org.ccg.test.utils.DateParseUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TimeCheck implements Check {
    private final String[] keys;

    public TimeCheck(String... keys) {
        this.keys = keys;
    }

    @Override
    public boolean doCheck(@Nullable JsonNode expectNode,@Nullable JsonNode actualNode) {
        Date expectDate = this.parseDateJsonNode(expectNode);
        Date actualDate = this.parseDateJsonNode(actualNode);
        return this.checkDate(expectDate, actualDate);
    }

    @Override
    public String[] getKeys() {
        return this.keys;
    }

    abstract boolean checkDate(Date expectDate, Date actualDate);

    private Date parseDateJsonNode(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }
        if (jsonNode.isTextual()) {
            return DateParseUtil.parseDateString(jsonNode.asText());
        }
        if (jsonNode.isNumber()) {
            return new Date(jsonNode.asLong());
        }
        return null;
    }
}

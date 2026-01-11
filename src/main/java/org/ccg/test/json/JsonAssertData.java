package org.ccg.test.json;

import java.util.List;
import java.util.Map;

public class JsonAssertData {

    private final String expectJson;
    private final String actualJson;
    private final String comment;
    private boolean isFail;
    private String failMessage;
    private final Map<String, List<Check>> keyCheckListMap;

    public JsonAssertData(String expectJson, String actualJson, String comment, Check... checks) {
        this.expectJson = expectJson;
        this.actualJson = actualJson;
        this.comment = comment;

        keyCheckListMap = Check.toMap(checks);
    }

    public String getComment() {
        return comment;
    }

    public String getExpectJson() {
        return expectJson;
    }

    public String getActualJson() {
        return actualJson;
    }

    public boolean isFail() {
        return isFail;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void toFail(String failMessage) {
        isFail = true;
        this.failMessage = failMessage;
    }

    public List<Check> getCheckListByKey(String key) {
        if (key == null) {
            return null;
        }
        return keyCheckListMap.get(key);
    }
}

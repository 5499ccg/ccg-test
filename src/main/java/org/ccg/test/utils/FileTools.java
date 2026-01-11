package org.ccg.test.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

public class FileTools {


    public static String readAsStringByPathAndStack(String filePath) throws IOException {
        String fullPath = buildTestDataPath(filePath);
        return readFileToString(fullPath);
    }

    public static String readAsStringByPathAndStack(String filePath, Map<String, String> param) throws IOException {
        String fullPath = buildTestDataPath(filePath);
        String s = readFileToString(fullPath);
        for (Map.Entry<String, String> entry : param.entrySet()) {
            s = s.replaceAll("#\\{\\{" + entry.getKey() + "}}", entry.getValue());
        }
        return s;
    }

    /**
     * 构建测试数据路径
     *
     * @param path 路径参数
     * @return 完整的测试数据路径
     */
    protected static String buildTestDataPath(String path) {
        // 获取调用类名
        StackTraceElement callerClass = Thread.currentThread().getStackTrace()[3];
        String className = callerClass.getClassName();
        String methodName = callerClass.getMethodName();

        // 提取类名（不含包名）
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);

        // 构建完整路径：test_data/调用类名/调用方法名/path
        String fullPath = Paths.get("test_data", simpleClassName, methodName, path).toString();

        return fullPath;
    }

    /**
     * 读取文件内容为字符串
     *
     * @param path 相对于测试数据目录的路径
     * @return 文件内容字符串
     * @throws IOException 读取文件时可能抛出的异常
     */
    public static String readFileToString(String path) throws IOException {
        // 使用当前线程的类加载器从类路径读取资源
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                throw new IOException("无法在类路径中找到资源: " + path);
            }

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\r\n");
            }

            // 移除最后多余的换行符
            if (content.length() > 0) {
                content.deleteCharAt(content.length() - 1);
                content.deleteCharAt(content.length() - 1);
            }

            return content.toString();
        }
    }
}

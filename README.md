# JSON 校验工具库 README

## 简介

这是一个功能强大的 JSON 校验工具库，提供了灵活的 JSON 比较和自定义校验功能。
在java的单元测试中，可以轻松地使用这个工具库进行 JSON 数据的校验，并在测试失败时，会显示详细的差异信息，帮助开发人员快速定位问题。

## 核心功能

- **基础类型校验**: 支持字符串、数字、布尔值、数组、对象等基础数据类型比较
- **嵌套结构校验**: 支持多层嵌套 JSON 结构的深度比较
- **自定义校验器**: 提供扩展接口，支持自定义校验逻辑

## 主要特性

### 1. 基础校验
- **数值比较**: 支持整数、浮点数比较
- **字符串比较**: 严格字符串匹配
- **布尔值比较**: 真假值校验
- **数组校验**: 按顺序比较数组元素及长度
- **对象校验**: 递归比较对象属性

### 2. 自定义校验器
- **[IgnoreCheck](file://D:\code\IdeaProjects\testccg\src\main\java\org\ccg\test\json\check\IgnoreCheck.java#L11-L50)**: 忽略指定字段的校验
- **[TimeCheckNow](file://D:\code\IdeaProjects\testccg\src\main\java\org\ccg\test\json\check\TimeCheckNow.java#L11-L36)**: 时间字段与当前时间比较
- **[RegexCheck](file://D:\code\IdeaProjects\testccg\src\main\java\org\ccg\test\json\check\RegexCheck.java#L11-L40)**: 正则表达式校验
- **[TimeCheck](file://D:\code\IdeaProjects\testccg\src\main\java\org\ccg\test\json\check\TimeCheck.java#L12-L64)**: 多格式时间校验

### 3. 使用示例

#### 基础校验
```java
JsonAssert.assertJsonEquals(expectedJson, actualJson, "校验描述");
```


#### 带自定义校验器
```java
Check ignoreId = Check.ignore("datas.id");
JsonAssert.assertJsonEquals(expectedJson, actualJson, "校验描述", ignoreId);
```


#### 时间校验
```java
Check timeNow = Check.timeNow("createTime");
JsonAssert.assertJsonEquals(expectedJson, actualJson, "校验描述", timeNow);
```


#### 正则校验
```java
Check regexCheck = Check.regex("^\\d{4}$", "id");
JsonAssert.assertJsonEquals(expectedJson, actualJson, "校验描述", regexCheck);
```


## 支持的数据类型

- **字符串**: `string`
- **数值**: `integer`, `double`
- **布尔值**: `boolean`
- **数组**: `array`
- **对象**: `object`
- **空值**: `null`

## 错误处理

- **详细错误信息**: 提供清晰的差异对比信息
- **路径定位**: 显示差异字段的完整路径
- **格式化输出**: 自动格式化 JSON 输出便于查看
![img.png](img.png)

## 测试覆盖

- **单层校验**: 基础数据类型比较
- **多层校验**: 嵌套结构比较
- **边界情况**: null 值、缺失字段处理
- **自定义校验**: 各种校验器功能测试

## 优势

1. **灵活性**: 支持自定义校验器扩展
2. **准确性**: 深度比较，精确定位差异
3. **易用性**: 简洁的 API 设计
4. **可维护性**: 清晰的架构设计

## 适用场景

- API 响应数据校验
- JSON 数据一致性检查
- 单元测试中的数据验证
- 数据迁移校验
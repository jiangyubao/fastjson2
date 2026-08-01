# 常见问题

## 概述

### 本精简版和 FASTJSON 2 完整版有什么区别？

本仓库是基于 FASTJSON 2.0.63 裁剪的**纯树模式**版本：

- **只保留** `JSON` / `JSONObject` / `JSONArray` 树模型 API（`JSON.parseObject(String)` → `JSONObject`）
- **已移除** JavaBean 绑定（`parseObject(text, Bean.class)`、`toJavaObject`、`toList`）、注解（`@JSONField` / `@JSONType`）、序列化过滤器、AutoType、`TypeReference`、日期/时间/UUID 支持、`JSONB` / `JSONPath` / `JSON Schema` / `CSV` 等扩展
- 运行时**无反射体系**（`reader/` / `writer/` 等包已删除），安全攻面清零
- 详见 [精简评估报告.md](精简评估报告.md)

### 支持哪些 Java 版本？

- **核心库**: Java 8+（含 `JSONWriterUTF16JDK8` 等 JDK 8 分支）
- **完整功能**: Java 11+（compact string 优化）

## 解析与反序列化

### 如何解析结构未知的 JSON？

```java
// 解析为 JSONObject（JSON 对象）
JSONObject obj = JSON.parseObject(jsonString);

// 解析为 JSONArray（JSON 数组）
JSONArray arr = JSON.parseArray(jsonString);

// 解析为通用 Object（自动检测）
Object result = JSON.parse(jsonString); // JSONObject / JSONArray / String / BigDecimal / Boolean
```

### 如何解析带泛型的 JSON？

本精简版**不支持**泛型 Bean 绑定（`TypeReference` 已移除）。如需处理嵌套结构，直接使用树模型：

```java
JSONObject obj = JSON.parseObject(json);
JSONArray users = obj.getJSONArray("users");      // List<User> 场景
JSONObject first = users.getJSONObject(0);        // 逐层取
```

### 如何读取嵌套对象 / 数组？

```java
JSONArray array = obj.getJSONArray("items");
JSONObject child = obj.getJSONObject("child");
String name = child.getString("name");
```

### 小数会被解析成什么类型？

默认解析为 **`BigDecimal`**（保精度，无精度损失）。需要 double/float 时：

```java
JSONObject obj = JSON.parseObject(json, JSONReader.Feature.UseDoubleForDecimals);
JSONObject obj2 = JSON.parseObject(json, JSONReader.Feature.UseBigDecimalForFloats);
```

### 支持单引号 JSON 吗？

支持。默认允许单引号 key/value（`{'a':1}`），可用 `JSONReader.Feature.DisableSingleQuote` 关闭。

### 解析出错时如何得到 null 而不是异常？

```java
JSONObject obj = JSON.parseObject(json, JSONReader.Feature.NullOnError);
```

## 序列化

### 支持序列化哪些类型？

`JSONObject`、`JSONArray`、`Map`、`List`、`String`、`Number`（含 `BigDecimal` / `BigInteger`）、`Boolean`、`null`。**其他类型**（如 `Date`、自定义 POJO）会抛出 `JSONException`。

### 如何在 JSON 输出中包含 null 字段？

默认跳过 null 值：

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);
```

针对特定 null 值的处理策略：

```java
// null String → ""
JSONWriter.Feature.WriteNullStringAsEmpty

// null List → []
JSONWriter.Feature.WriteNullListAsEmpty

// null Number → 0
JSONWriter.Feature.WriteNullNumberAsZero

// null Boolean → false
JSONWriter.Feature.WriteNullBooleanAsFalse
```

### 如何格式化输出 JSON？

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
String json2 = JSON.toJSONString(obj,
    JSONWriter.Feature.PrettyFormat,
    JSONWriter.Feature.PrettyFormatWith2Space); // 2 空格缩进
```

### 如何处理 JavaScript 中的大 Long 值？

JavaScript 无法处理超过 `Number.MAX_SAFE_INTEGER`（2^53 - 1）的 Java `long` 值。使用 `BrowserCompatible` 或 `WriteLongAsString`：

```java
// 自动检测并将大数字转为字符串
String json = JSON.toJSONString(obj, JSONWriter.Feature.BrowserCompatible);

// 始终将 Long 序列化为 String
String json = JSON.toJSONString(obj, JSONWriter.Feature.WriteLongAsString);
```

### 如何按 Key 排序输出（验签场景）？

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.SortMapEntriesByKeys);
```

### 能序列化 Date 吗？

**不能**。日期/时间支持已随精简移除，序列化 `Date` 会抛出 `JSONException`（`JSONWriterUtilDateAsMillis` Feature 同样不生效）。前端场景建议直接使用字符串/毫秒数。

## 与完整版 / 1.x 的差异

### 为什么 `JSON.parseObject(json, Bean.class)` 编译报错？

该方法已被移除。本库只提供树模型 API，不再支持 JavaBean 绑定。若你的业务需要 POJO 映射，请使用完整版 fastjson2，或自行在树模型上做转换。

### 为什么没有 `JSONPath`、`JSON Schema`？

这些扩展模块已随精简移除，不属于 `JSON` 文本协议的核心功能。

### 精简版安全吗？

是的，而且是**最安全**的形态：AutoType 与整个反射体系（`reader/` / `writer/` 等）已删除，运行时不存在任何 `@type` 反序列化攻击面。

## 性能

### 如何获得最佳解析性能？

1. **尽量使用 byte[] 输入** - `JSON.parseObject(bytes)` 可避免 String 编码开销。
2. **避免不必要的 Feature** - 每个启用的 Feature 会增加少量检查开销。

### 如何获得最佳序列化性能？

1. **使用 byte[] 输出** - `JSON.toJSONBytes(obj)` 比 `JSON.toJSONString(obj)` 更快。
2. **纯 ASCII 内容** - 开启 `JSONWriter.Feature.OptimizedForAscii` 走 UTF8 快速路径。

## 故障排查

### 遇到 `com.alibaba.fastjson2.JSONException`

常见原因：
1. **JSON 格式错误** - 用 JSON 校验工具检查输入。
2. **序列化不支持的类型** - 如 `Date`、自定义 POJO（见上文"支持序列化哪些类型"）。
3. **`not support write value type`** - 序列化遇到了非树模型类型，检查 `Map` / `List` 中是否混入 POJO 实例。
4. **`not support feature`** - 使用了树模式下不支持的 Feature 组合（如 `ReferenceDetection` 与部分特性的组合在子类 write(Map/List) 中被拒绝）。

### 小数精度不对？

默认小数解析为 `BigDecimal`（保精度）。若你观察到浮点误差，检查是否显式开启了 `UseDoubleForDecimals` / `UseBigDecimalForFloats`；序列化端 `WriteBigDecimalAsPlain` 可避免科学计数法。

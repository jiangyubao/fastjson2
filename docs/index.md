# FASTJSON2 精简版

基于 `FASTJSON2 2.0.63` 裁剪的 JSON 库，**仅保留纯 JSON 文本协议与树模型 API**（`JSON` / `JSONObject` / `JSONArray`）。面向需要 `JSON` 解析/序列化的场景（如 Web 前端数据交互、无 Bean 绑定的通用数据处理），已彻底移除反射体系，无 JavaBean 绑定、无注解、无 AutoType。

## 功能范围

- `JSON` 文本协议解析与序列化（`String` / `char[]` / `byte[]` / `InputStream` 输入，UTF-8 / UTF-16 / ASCII）
- `JSONObject` / `JSONArray` 树模型（继承 `LinkedHashMap<String, Object>` / `ArrayList<Object>`）
- 手写递归解析器（`JSONReader.readAny`）与 instanceof 分支序列化（`JSONWriter.writeAny`），不依赖任何 `ObjectReader` / `ObjectWriter` Provider
- 完整 `JSONReader.Feature` / `JSONWriter.Feature` 枚举（对树模型生效的项见 [features_cn.md](features_cn.md)）
- 数字高精度解析：`BigDecimal` / `BigInteger` 精确转换,double/float 走 ED/ED5/EF 查表无精度损失
- 单引号字符串、非引号字段名等宽松语法兼容

已移除：

- `JSONB` 二进制协议、`JSONPath`、`JSON Schema`、`CSV`、`Kotlin` / `Spring` / `Android` 模块、`fastjson1-compatible` 兼容层、JMH benchmark
- **整个反射体系**：`reader/`、`writer/`、`introspect/`、`annotation/`、`codec/`、`modules/`、`filter/`、`function/`、`internal/` 包及 `TypeReference`、`PropertyNamingStrategy`
- **JavaBean 绑定**：`JSON.parseObject(text, Class)`、`toJavaObject`、`toList`、`@JSONField` / `@JSONType` 注解、序列化过滤器（`NameFilter` / `ValueFilter` 等）、自定义 `ObjectReader` / `ObjectWriter`、AutoType
- **日期/时间与 UUID 支持**：`Date` / `LocalDate` / `LocalDateTime` / `UUID` 等序列化（遇到会抛 `JSONException`），`DateUtils` 已删除

## 1. 添加依赖

`Maven`:

```xml
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.63</version>
</dependency>
```

`Gradle`:

```groovy
dependencies {
    implementation 'com.alibaba.fastjson2:fastjson2:2.0.63'
}
```

## 2. 解析

### 2.1 解析为 `JSONObject`

```java
String text = "{\"id\":1,\"name\":\"fastjson2\"}";
JSONObject data = JSON.parseObject(text);
```

`byte[]` 输入：

```java
byte[] bytes = ...;
JSONObject data = JSON.parseObject(bytes);
```

### 2.2 解析为 `JSONArray`

```java
String text = "[1,2,3]";
JSONArray data = JSON.parseArray(text);
```

### 2.3 解析为任意类型（自动识别）

```java
Object value = JSON.parse("{\"id\":1}");       // JSONObject
Object value = JSON.parse("[1,2,3]");          // JSONArray
Object value = JSON.parse("1.5");              // BigDecimal
Object value = JSON.parse("\"str\"");          // String
Object value = JSON.parse("true");             // Boolean
```

> 注意：本精简版**不支持** `JSON.parseObject(text, Bean.class)` 这类 JavaBean 绑定解析。如需结构未知的数据，使用树模型 API。

## 3. 序列化

```java
String text = JSON.toJSONString(obj);          // String
byte[] bytes = JSON.toJSONBytes(obj);          // byte[]
JSON.writeTo(obj, outputStream);               // 写入流
```

`JSONObject` / `JSONArray` 实例方法：

```java
String text = obj.toJSONString();
String text = obj.toJSONString(JSONWriter.Feature.PrettyFormat);
```

序列化支持的类型：`JSONObject`、`JSONArray`、`Map`、`List`、`String`、`Number`（含 `BigDecimal` / `BigInteger`）、`Boolean`、`null`。其他类型（如 `Date`、自定义对象）会抛出 `JSONException`。

## 4. `JSONObject` / `JSONArray` 使用

### 4.1 读取属性

```java
JSONObject obj = JSON.parseObject("{\"id\":2,\"name\":\"fastjson2\",\"enable\":true}");

int id = obj.getIntValue("id");
String name = obj.getString("name");
boolean enable = obj.getBooleanValue("enable");
```

### 4.2 读取嵌套结构

```java
JSONArray array = obj.getJSONArray("items");
JSONObject child = obj.getJSONObject("child");
```

### 4.3 修改

```java
obj.put("key", value);
obj.remove("key");
obj.containsKey("key");
```

### 4.4 `Map` / `List` 语义

`JSONObject` 继承自 `Map<String, Object>`，`JSONArray` 继承自 `List<Object>`，可直接使用集合 API。

## 5. 进阶

| 主题 | 文档 |
|------|------|
| 序列化/反序列化特性 | [features_cn.md](features_cn.md) |
| 性能优化 | [performance_cn.md](performance_cn.md) |
| 常见问题 | [FAQ_cn.md](FAQ_cn.md) |
| 架构设计 | [ARCHITECTURE.md](ARCHITECTURE.md) |
| 精简说明与评估 | [精简评估报告.md](精简评估报告.md) |

##### 语言: [English](README.md) | 中文

# FASTJSON 2 精简版

基于 [FASTJSON 2.0.63](https://github.com/alibaba/fastjson2) 裁剪的 JSON 库,**仅保留纯 JSON 文本协议与树模型 API**(`JSON` / `JSONObject` / `JSONArray`)以及高精度数字核心。已彻底移除反射体系:无 JavaBean 绑定、无注解、无过滤器、无 AutoType。要求 JDK 8 及以上,制品坐标为 `com.alibaba.fastjson2:fastjson2:2.0.63`,同时打包为 OSGi bundle。

## 功能范围

**支持:**

- `JSON` 文本协议解析与序列化(`String` / `char[]` / `byte[]` / `Reader` / `InputStream` 输入,UTF-8 / UTF-16 / ASCII)
- `JSONObject` / `JSONArray` 树模型(继承 `LinkedHashMap<String, Object>` / `ArrayList<Object>`)
- 手写递归解析器(`JSONReader.readAny`)与 `instanceof` 分支序列化(`JSONWriter.writeAny`),不依赖任何 `ObjectReader` / `ObjectWriter` Provider
- `JSONReader.Feature` / `JSONWriter.Feature`(仅对树模型生效的项有实际作用,详见 [features_cn.md](docs/features_cn.md))
- 数字高精度解析:`BigDecimal` / `BigInteger` 精确转换,`double` / `float` 走 ED/ED5/EF 查表无精度损失
- 宽松语法:单引号字符串、非引号字段名等

**已移除:**

- `JSONB` 二进制协议、`JSONPath`、JSON Schema、CSV、Kotlin / Spring / Android 模块、fastjson1-compatible 兼容层、JMH benchmark
- 整个反射体系:`reader/`、`writer/`、`introspect/`、`annotation/`、`codec/`、`modules/`、`filter/`、`function/`、`internal/` 包及 `TypeReference`
- JavaBean 绑定:`JSON.parseObject(text, Class)`、`toJavaObject`、`toList`、`@JSONField` / `@JSONType` 注解、序列化过滤器、自定义 `ObjectReader` / `ObjectWriter`、AutoType
- 日期/时间与 UUID 支持:`Date` / `LocalDate` / `LocalDateTime` / `UUID` 等(遇到会抛 `JSONException`)

## 快速开始

```xml
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.63</version>
</dependency>
```

```java
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

JSONObject obj = JSON.parseObject("{\"id\":1,\"name\":\"fastjson2\"}");  // 解析文本
JSONArray arr  = JSON.parseArray("[1,2,3]");                             // 解析数组

String json  = JSON.toJSONString(obj);   // 序列化为 String
byte[] bytes = JSON.toJSONBytes(obj);    // 序列化为 byte[]
```

## 使用

### 解析

```java
JSONObject obj = JSON.parseObject(text);   // String / char[] / byte[] / Reader / InputStream
JSONArray  arr = JSON.parseArray(text);    // String / char[] / byte[]
Object     any = JSON.parse(text);         // JSONObject / JSONArray / String / Number / Boolean
```

### 序列化

```java
String  text  = JSON.toJSONString(obj);   // String
byte[]  bytes = JSON.toJSONBytes(obj);    // byte[]
JSON.writeTo(obj, outputStream);          // 写入流

String pretty = obj.toJSONString(JSONWriter.Feature.PrettyFormat);
```

序列化支持的类型:`JSONObject`、`JSONArray`、`Map`、`List`、`String`、`Number`(含 `BigDecimal` / `BigInteger`)、`Boolean`、`null`。其他类型(如 `Date`、自定义对象)会抛出 `JSONException`。

### 树模型操作

```java
JSONObject obj = JSON.parseObject("{\"id\":2,\"name\":\"fastjson2\",\"enable\":true}");

int id       = obj.getIntValue("id");
String name  = obj.getString("name");
boolean en   = obj.getBooleanValue("enable");

JSONObject child = obj.getJSONObject("child");   // 嵌套对象
JSONArray  items = obj.getJSONArray("items");    // 嵌套数组

obj.put("key", value);
obj.remove("key");
```

`JSONObject` 继承自 `Map<String, Object>`,`JSONArray` 继承自 `List<Object>`,可直接使用集合 API。

### Feature 配置

```java
String json = JSON.toJSONString(obj,
        JSONWriter.Feature.WriteMapNullValue,     // 输出 null 值(默认跳过)
        JSONWriter.Feature.PrettyFormat);         // 格式化输出

JSONObject obj = JSON.parseObject(text,
        JSONReader.Feature.UseDoubleForDecimals); // 小数解析为 double(默认 BigDecimal)
```

完整列表见 [features_cn.md](docs/features_cn.md)。

## 文档索引

| 文档 | 说明 |
|------|------|
| [index.md](docs/index.md) | 本精简版概述 |
| [features_cn.md](docs/features_cn.md) | `JSONReader` / `JSONWriter` Feature 列表 |
| [performance_cn.md](docs/performance_cn.md) | 性能优化建议 |
| [FAQ_cn.md](docs/FAQ_cn.md) | 常见问题 |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | 内部设计 |
| [fastjson_1_upgrade_cn.md](docs/fastjson_1_upgrade_cn.md) | 从 Fastjson 1.x 升级 |

## 参与贡献

见 [CONTRIBUTING.md](CONTRIBUTING.md) 与 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)。

## 许可证

[Apache License 2.0](LICENSE)。

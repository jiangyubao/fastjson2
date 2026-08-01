# 通过 Features 配置序列化和反序列化的行为

# 1. Feature 介绍

在 fastjson 2.x 中，有两个 Feature，分别用来配置序列化和反序列化的行为。

- `JSONWriter.Feature` 配置序列化的行为
- `JSONReader.Feature` 配置反序列化的行为

> **精简版说明**：本库仅提供树模型 API（`JSON.parseObject(String)` 返回 `JSONObject`，无 `parseObject(text, Bean.class)` 的 JavaBean 绑定）。Feature 枚举完整保留，但**仅针对 `JSONObject` / `JSONArray` 树模型生效**的项有实际作用；面向 JavaBean 字段的 Feature（如 `FieldBased`、`SupportArrayToBean`、`SupportSmartMatch` 等）在纯树模式下不产生效果，表中已标注。

# 2. 在 JSON 的 toJSONString 和 parse 方法中使用 Feature

## 2.1 在 JSON 的 toJSONString 方法中使用 JSONWriter.Feature

```java
JSONObject obj = JSON.parseObject("{\"name\":\"fastjson2\"}");
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat); // 格式化输出
String json2 = JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue); // 输出 null 值
```

## 2.2 在 JSON 的 parse 方法中使用 JSONReader.Feature

```java
String jsonStr = "{\"v\":1.5}";
JSONObject obj = JSON.parseObject(jsonStr, JSONReader.Feature.UseDoubleForDecimals); // 小数读取为 double（默认 BigDecimal）
```

# 3. JSONReader.Feature 介绍

| JSONReader.Feature | 树模型生效 | 介绍 |
|---------------------------------|:---:|-----------------------------------------------------------------------------------------------------|
| FieldBased | — | 基于字段反序列化（仅对 JavaBean 有意义，树模式下无 Bean 字段） |
| IgnoreNoneSerializable | — | 反序列化忽略非 Serializable 类型的字段（Bean 专用） |
| ErrorOnNoneSerializable | — | 反序列化时遇到非 Serializable 类型抛异常（Bean 专用） |
| SupportArrayToBean | — | 支持数组映射到 Bean（Bean 专用） |
| InitStringFieldAsEmpty | — | 初始化 String 字段为空字符串 ""（Bean 专用） |
| SupportSmartMatch | — | 智能识别 camel/upper/pascal/snake/Kebab 五种 case（Bean 专用，树模式键名精确匹配） |
| UseNativeObject | ✅ | 默认使用 JSONObject 和 JSONArray，开启后使用 LinkedHashMap 和 ArrayList |
| SupportClassForName | — | 支持类型为 Class 的字段（Bean 专用） |
| IgnoreSetNullValue | — | 忽略输入为 null 的字段（Bean 专用） |
| UseDefaultConstructorAsPossible | — | 尽可能使用缺省构造函数（Bean 专用） |
| UseBigDecimalForFloats | ✅ | 默认配置会使用 BigDecimal 来 parse 小数，打开后会使用 Float |
| UseBigDecimalForDoubles | ✅ | 默认配置会使用 BigDecimal 来 parse 小数，打开后会使用 Double |
| ErrorOnEnumNotMatch | — | 默认 Enum 的 name 不匹配时会忽略，打开后不匹配会抛异常（Bean 专用） |
| TrimString | ✅ | 对读取到的字符串值做 trim 处理 |
| DuplicateKeyValueAsArray | ✅ | 重复 Key 的 Value 不是替换而是组合成数组 |
| AllowUnQuotedFieldNames | ✅ | 支持不带双引号的字段名 |
| NonStringKeyAsString | ✅ | 非 String 类型的 Key 当做 String 处理 |
| Base64StringAsByteArray | — | 将 Base64 格式的字符串反序列化为 byte[]（Bean 专用） |
| IgnoreCheckClose | — | 反序列化时忽略资源关闭检查 |
| ErrorOnNullForPrimitives | — | 基本类型字段遇到 null 值时抛异常（Bean 专用） |
| NullOnError | ✅ | 反序列化出错时返回 null 而非抛异常 |
| NonZeroNumberCastToBooleanAsTrue | ✅ | 非零数值转为 boolean 时按 true 处理 |
| IgnoreNullPropertyValue | ✅ | 忽略值为 null 的属性 |
| ErrorOnUnknownProperties | — | JSON 中出现目标类不存在的属性时抛异常（Bean 专用） |
| EmptyStringAsNull | ✅ | 将空字符串 "" 转换为 null |
| NonErrorOnNumberOverflow | — | 数字溢出时不抛异常（Bean 专用） |
| UseBigIntegerForInts | ✅ | 未指定具体类型的整数读取为 BigInteger |
| UseLongForInts | ✅ | 未指定具体类型且值在 int 范围内的整数读取为 Long |
| DisableSingleQuote | ✅ | 不允许在 key 和 value 中使用单引号（默认支持单引号） |
| UseDoubleForDecimals | ✅ | 小数数值读取为 double |
| DisableReferenceDetect | ✅ | 不处理 JSON 引用（如 $ref） |
| DisableStringArrayUnwrapping | — | 默认情况下单元素字符串数组会解包为字符串（Bean 专用） |

> 说明：`SupportAutoType`、`ErrorOnNotSupportAutoType`、`IgnoreAutoTypeNotMatch` 等 AutoType 相关 Feature 已随反射体系一起**移除**（枚举中已不存在）。

# 4. JSONWriter.Feature 介绍

| JSONWriter.Feature | 树模型生效 | 介绍 |
|-----------------------------------|:---:|------------------------------------------------------------------------------------------------------------------|
| FieldBased | — | 基于字段序列化（Bean 专用，树模式无 Bean 字段） |
| IgnoreNoneSerializable | — | 序列化忽略非 Serializable 类型的字段（Bean 专用） |
| ErrorOnNoneSerializable | — | 序列化非 Serializable 对象时报错（Bean 专用） |
| BeanToArray | — | 将对象序列为 [101,"XX"] 这样的数组格式（Bean 专用） |
| WriteNulls | ✅ | 序列化输出空值字段（`WriteMapNullValue` 的别名） |
| WriteMapNullValue | ✅ | 序列化输出 Map 中值为 null 的条目（默认跳过 null） |
| BrowserCompatible | ✅ | 在大范围超过 JavaScript 支持的整数，输出为字符串格式 |
| NullAsDefaultValue | ✅ | 将 null 值输出为缺省值（数字→0，String→""，Character→\u0000，集合→[]，其余→{}） |
| WriteBooleanAsNumber | ✅ | 将 true 输出为 1，false 输出为 0 |
| WriteNonStringValueAsString | ✅ | 将非 String 类型的值输出为 String（不含对象和数据类型） |
| WriteClassName | — | 序列化时输出类型信息（树模型无类型可输出） |
| NotWriteRootClassName | — | 同上（Bean 专用） |
| NotWriteHashMapArrayListClassName | — | 同上（Bean 专用） |
| NotWriteDefaultValue | — | 当字段的值为缺省值时，不输出（Bean 专用） |
| WriteEnumsUsingName | ✅ | 序列化 enum 使用 name |
| WriteEnumUsingToString | ✅ | 序列化 enum 使用 toString 方法 |
| IgnoreErrorGetter | — | 忽略 getter 方法的错误（Bean 专用） |
| PrettyFormat | ✅ | 格式化输出（支持对象与数组） |
| ReferenceDetection | ✅ | 打开引用检测（默认关闭；树模型无引用语义，一般无需开启） |
| WriteNameAsSymbol | — | 将字段名按照 symbol 输出（Bean 专用） |
| WriteBigDecimalAsPlain | ✅ | 序列化 BigDecimal 使用 toPlainString，避免科学计数法 |
| UseSingleQuotes | ✅ | 使用单引号 |
| MapSortField | ✅ | 对 Map 中的 KeyValue 按照 Key 做排序后再输出（已弃用，改用 SortMapEntriesByKeys） |
| WriteNullListAsEmpty | ✅ | 将 List 类型字段的空值序列化输出为空数组 "[]" |
| WriteNullStringAsEmpty | ✅ | 将 String 类型字段的空值序列化输出为空字符串 "" |
| WriteNullNumberAsZero | ✅ | 将 Number 类型字段的空值序列化输出为 0 |
| WriteNullBooleanAsFalse | ✅ | 将 Boolean 类型字段的空值序列化输出为 false |
| NotWriteEmptyArray | ✅ | 数组类型字段当 length 为 0 时不输出（已弃用，改用 IgnoreEmpty） |
| IgnoreEmpty | ✅ | 忽略空值字段（空集合、空字符串等） |
| WriteNonStringKeyAsString | ✅ | 将 Map 中的非 String 类型的 Key 当做 String 类型输出 |
| WritePairAsJavaBean | — | 将 Apache Commons 包中的 Pair 对象当做 JavaBean 序列化（Bean 专用） |
| OptimizedForAscii | ✅ | 对纯 ASCII 内容使用优化的序列化路径（使用 UTF8 writer） |
| EscapeNoneAscii | ✅ | 非 ASCII 字符使用转义输出 |
| WriteByteArrayAsBase64 | — | 将 byte[] 序列化为 Base64 字符串（Bean 专用） |
| IgnoreNonFieldGetter | — | 只考虑对应实际字段的 getter 方法（Bean 专用） |
| LargeObject | — | 防止序列化有循环引用对象消耗过大资源的保护措施（Bean 专用） |
| WriteLongAsString | ✅ | 将 Long 序列化为 String |
| BrowserSecure | ✅ | 浏览器安全，将 '<' '>' '(' ')' 字符做转义输出 |
| WriteEnumUsingOrdinal | ✅ | 序列化 Enum 使用 Ordinal，缺省是 name |
| WriteThrowableClassName | — | 序列化 Throwable 时带上类型信息（Bean 专用） |
| UnquoteFieldName | ✅ | 不带引号输出 Key |
| NotWriteSetClassName | — | 同上（Bean 专用） |
| NotWriteNumberClassName | — | 同上（Bean 专用） |
| SortMapEntriesByKeys | ✅ | 序列化 Map 前按 Key 排序，用于验签等需要稳定输出顺序的场景 |
| PrettyFormatWith2Space | ✅ | 格式化输出使用 2 个空格缩进（需同时开启 PrettyFormat） |
| PrettyFormatWith4Space | ✅ | 格式化输出使用 4 个空格缩进（需同时开启 PrettyFormat） |
| WriterUtilDateAsMillis | — | 将 java.util.Date 序列化为毫秒时间戳（日期支持已移除，不生效） |
| WriteFloatSpecialAsString | ✅ | 启用后，NaN/Infinity 将被序列化为 "NaN"、"Infinity"、"-Infinity" |

# 5. 使用示例

## 5.1 序列化示例

```java
// 基本使用
JSONObject obj = new JSONObject();
obj.put("name", "张三");
obj.put("age", 25);
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);

// 输出 null 值
obj.put("remark", null);
String json2 = JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);

// 多个 Feature 组合
String json3 = JSON.toJSONString(obj,
    JSONWriter.Feature.PrettyFormat,
    JSONWriter.Feature.WriteMapNullValue);
```

## 5.2 反序列化示例

```java
// 小数按 double 读取（默认 BigDecimal 保精度）
String json = "{\"pi\":3.14}";
JSONObject obj = JSON.parseObject(json, JSONReader.Feature.UseDoubleForDecimals);
Object pi = obj.get("pi"); // Double

// 大整数按 Long 读取
JSONObject obj2 = JSON.parseObject("{\"id\":123456}", JSONReader.Feature.UseLongForInts);

// 禁用单引号语法
JSON.parseObject("{'a':1}");                    // 默认支持单引号
JSON.parseObject("{'a':1}", JSONReader.Feature.DisableSingleQuote); // 抛异常
```

# 6. 最佳实践建议

1. **小数精度**：默认小数解析为 `BigDecimal`（保精度）。前端场景如无需高精度，可开启 `UseDoubleForDecimals` / `UseBigDecimalForDoubles`。
2. **大整数**：`long` 超过 `2^53-1` 时，前端 JS 会丢失精度。序列化端可用 `BrowserCompatible` 或 `WriteLongAsString` 输出字符串；解析端可用 `UseLongForInts` / `UseBigIntegerForInts` 控制整数类型。
3. **null 值**：默认序列化跳过 null 值。需要输出时用 `WriteMapNullValue`；要输出空串/空数组/0/false 可用 `WriteNullStringAsEmpty` 等。
4. **格式化输出**：`PrettyFormat` 输出带缩进；`PrettyFormatWith2Space` / `PrettyFormatWith4Space` 控制缩进宽度。
5. **稳定输出顺序**：验签场景用 `SortMapEntriesByKeys` 保证 Map 按键排序输出。
6. **安全**：本库已彻底移除 AutoType 与反射体系，不存在 `@type` 反序列化攻击面。

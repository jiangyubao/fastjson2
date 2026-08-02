# 精简版差异说明（相对 FASTJSON 1.x / 完整版 2.x）

本仓库是基于 fastjson2 2.0.63 裁剪的**纯树模式**版本。本文档说明它与 fastjson 1.x 以及完整版 fastjson2 的差异，帮助迁移决策。

## 1. 为什么精简

- 使用场景仅需要 `JSON` 文本协议与 `JSONObject` / `JSONArray` 树模型（如 Web 前端数据交互）
- 删除反射体系后，运行时无 `reader` / `writer` / 注解 / 过滤器 / AutoType，**安全攻面清零**
- 代码量从 83,275 行降至 36,615 行（-56%），jar 约 321KB，便于审计与嵌入

## 2. 与 fastjson 1.x 的差异

### 2.1 包名

`FASTJSON 2` 与 1.x 使用不同的 package（`com.alibaba.fastjson2`），可共存：

```java
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
```

### 2.2 Maven 依赖

```xml
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.63</version>
</dependency>
```

### 2.3 行为差异（精简版）

| 维度 | fastjson 1.x | 精简版 |
|------|-------------|--------|
| 解析入口 | `JSON.parseObject(text, Bean.class)` | **仅树 API**：`JSON.parseObject(text)` → `JSONObject` |
| 序列化入口 | `JSON.toJSONString(Object)` | 同左（仅支持树模型/基础类型） |
| JavaBean 绑定 | 支持 | **已移除**（`toJavaObject` / `toList` / `TypeReference` 均不存在） |
| 注解 | `@JSONField` 等 | **已移除** |
| AutoType | 默认带白名单 | **已移除**（无 `@type` 解析能力，天然免疫） |
| 循环引用检测 | 默认开启 | 默认关闭（`ReferenceDetection` 树模型下一般无需） |
| SmartMatch | 默认开启 | 已移除（键名精确匹配） |
| 日期/时间 | 支持 | **已移除**（序列化 `Date` 抛异常） |
| 序列化过滤器 | `NameFilter` / `ValueFilter` 等 | **已移除** |

### 2.4 从 1.x 迁移到树模式

如果你的代码大量使用 `JSON.parseObject(text, Bean.class)` / `toJavaObject`，**不建议直接替换为本精简版**（会编译失败）。迁移选项：

1. **继续使用完整版 fastjson2** - 保持 Bean 绑定与注解能力
2. **改用树模型** - 重写为：

```java
// 之前
User user = JSON.parseObject(json, User.class);
String name = user.getName();

// 之后（树模式）
JSONObject user = JSON.parseObject(json);
String name = user.getString("name");
```

3. **自己写转换层** - 在树模型之上自行实现 `JSONObject` → POJO 的映射（常见做法：`JSONObject.get*` + 手动装配）

## 3. 与完整版 fastjson2 的差异

| 完整版 2.x | 精简版 |
|-----------|--------|
| `JSON.parseObject(text, Bean.class)`、泛型 `TypeReference` | 已移除 |
| 注解 `@JSONField` / `@JSONType` / `@JSONCreator` / `@JSONBuilder` | 已移除 |
| `ObjectReader` / `ObjectWriter` 自定义与 Provider 注册 | 已移除 |
| 过滤器（`NameFilter` / `ValueFilter` / `PropertyFilter` 等） | 已移除 |
| AutoType（`SupportAutoType` / 白名单 / SafeMode） | 已移除 |
| MixIn 注解注入 | 已移除 |
| 代码生成（ASM / LambdaMetafactory） | 无 Bean 绑定，无需 |
| 日期/时间/UUID（`DateUtils`、`WriterUtilDateAsMillis`） | 已移除 |
| `JSONB` / `JSONPath` / `JSON Schema` / `CSV` / Kotlin / Spring / Android 扩展 | 已移除 |
| `JSONReader.Feature` / `JSONWriter.Feature` 枚举 | **完整保留**（对树模型生效的项见 [features_cn.md](features_cn.md)） |
| `JSON.parse` / `parseObject` / `parseArray` / `toJSONString` / `toJSONBytes` / `writeTo` / `isValid` | **保留**（树 API 全部可用，方法为 public） |

## 4. 常见问题

### 4.1 1.x 的 `SerializerFeature` 如何对应？

1.x 的 `SerializerFeature` → 完整版 2.x 的 `JSONWriter.Feature` → 精简版同（枚举保留，仅部分生效）。常用映射：

| 1.x SerializerFeature | 精简版 JSONWriter.Feature |
|-----------------------|--------------------------|
| `WriteMapNullValue` | `WriteMapNullValue` / `WriteNulls` ✅ |
| `PrettyFormat` | `PrettyFormat` ✅ |
| `SortField` | `SortMapEntriesByKeys` ✅（JSONObject 本身 LinkedHashMap 保序） |
| `WriteLongAsString` | `WriteLongAsString` ✅ |
| `WriteDateUseDateFormat` | 不支持（日期已移除） |
| `BrowserCompatible` | `BrowserCompatible` ✅ |

### 4.2 1.x 的 `ExtraProcessor` / `ObjectDeserializer` 如何对应？

**无对应**。扩展机制（`filter` / `reader` / `writer` 包）已整体移除。树模式下多余字段天然保留在 `JSONObject` 中（`obj.get("extra")`），无需处理器。

### 4.3 1.x 的 AutoType 白名单如何替代？

**无需替代**。精简版已彻底移除 AutoType，不存在 `@type` 反序列化。

### 4.4 1.x 的循环引用检测如何关闭？

1.x 默认开启循环引用检测。精简版默认关闭（`ReferenceDetection` 默认不启用），行为等同于 1.x 开启 `DisableCircularReferenceDetect`。

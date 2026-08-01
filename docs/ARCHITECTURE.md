# Fastjson2 精简版架构

## Overview

本仓库是基于 FASTJSON2 2.0.63 裁剪的 JSON 库（纯树模式），目标是**只提供 `JSON` 文本协议的解析与序列化**，不包含 JavaBean 绑定、反射体系、注解、过滤器与 AutoType。整个代码库仅一个 `core` Maven 模块，共 32 个源文件。

## 架构总览

```
┌────────────────────────────────────────────────────────────┐
│                      API 层                                 │
│   JSON（静态入口） · JSONObject · JSONArray                  │
│   JSONFactory · JSONReader（抽象） · JSONWriter（抽象）      │
│   JSONException · JSONLargeObjectException                  │
├────────────────────────────────────────────────────────────┤
│  ┌──────────────┐          ┌──────────────┐                 │
│  │  Reader 层    │          │  Writer 层    │                 │
│  │ JSONReaderUTF8│          │ JSONWriterUTF8│                │
│  │ JSONReaderUTF16          │ JSONWriterUTF16               │
│  │ JSONReaderASCII          │ (JDK8/9 变体)                 │
│  └──────────────┘          └──────────────┘                 │
│  ┌────────────────────────────────────────┐                 │
│  │          util 层（解析/格式化内核）        │                 │
│  │ IOUtils · NumberUtils · Fnv          │                 │
│  │ JDKUtils · StringUtils              │                 │
│  │ MutableBigInteger · Scientific ·       │                 │
│  │ ED/ED5/EF（数字查表）· SymbolTable      │                 │
│  └────────────────────────────────────────┘                 │
└────────────────────────────────────────────────────────────┘
```

**关键点：没有 Object Mapping 层。** 原版 fastjson2 的 `JSONReader.readAny()` 经由 `ObjectReaderProvider` 动态分派类型解析，`JSONWriter.writeAny()` 经由 `ObjectWriterProvider` 获取类型写入器；本精简版改为：

- `JSONReader.readAny()`：手写 `switch` 按首字符递归解析（`{` → 对象，`[` → 数组，`"` → 字符串，数字/`t`/`f`/`n` → 对应类型），直接构建 `JSONObject` / `JSONArray` / `String` / `BigDecimal` / `Boolean` 等
- `JSONWriter.writeAny()`：`instanceof` 分支写入（`JSONObject` / `JSONArray` / `Map` / `List` / `String` / `Number` / `Boolean`），其余类型抛 `JSONException`

## 1. API 层

- **`JSON`** - 静态入口：`parse` / `parseObject` / `parseArray` / `toJSONString` / `toJSONBytes` / `writeTo` / `isValid` / `isValidObject` / `isValidArray` / `toJSON`，共 30 个 public 方法
- **`JSONObject`** - `LinkedHashMap<String, Object>` 子类，保持插入顺序；提供 `getString` / `getInteger` / `getIntValue` / `getLong` / `getBooleanValue` / `getBigDecimal` / `getJSONObject` / `getJSONArray` 等类型化读取
- **`JSONArray`** - `ArrayList<Object>` 子类；提供对应的按索引类型化读取
- **`JSONFactory`** - 创建 read/write 上下文；持有数字解析缓存（`NAME_CACHE` / `DIGITS2` / `NIBBLES` / `FLOAT_10_POW` / `DOUBLE_10_POW`）、`defaultDecimalMaxScale` 等静态配置；`setDefaultObjectSupplier` / `setDefaultArraySupplier` 可定制树模型容器
- **`JSONReader` / `JSONWriter`**（抽象） - 定义解析与序列化契约；`JSONReader.of(...)` 按输入类型选择具体实现

## 2. Reader 层（解析）

| 类 | 输入 | 说明 |
|-------|-------|-------|
| `JSONReaderUTF8` | UTF-8 `byte[]` | 字节级扫描器，字符分类查表 + `Unsafe` 批量读（性能核心） |
| `JSONReaderUTF16` | UTF-16 `byte[]`、`char[]`、`String` | 文本与 UTF-16 输入 |
| `JSONReaderASCII` | ASCII / ISO-8859-1 `byte[]` | 纯 ASCII 快速路径 |

`JSONReader.of(...)` 按输入选择实现：UTF-8 → `JSONReaderUTF8`，ASCII / ISO-8859-1 → `JSONReaderASCII`，UTF-16 与字符输入 → `JSONReaderUTF16`。

## 3. Writer 层（序列化）

| 类 | 输出 | 说明 |
|-------|--------|-------|
| `JSONWriterUTF8` | UTF-8 `byte[]` | `toJSONBytes` 使用 |
| `JSONWriterUTF16` | UTF-16 `String` | `toJSONString` 使用 |

`JSONWriterUTF16` 按 JDK 版本选择变体：`JSONWriterUTF16JDK8` / `JSONWriterUTF16JDK8UF`（JDK 8），`JSONWriterUTF16` / `JSONWriterUTF16JDK9UF`（JDK 9+，UF 变体使用 `Unsafe` 字段访问）。

## 4. util 层（解析/格式化内核）

| 类 | 用途 |
|-------|---------|
| `IOUtils` | 字符串/数字批量读写、LE/BE 批量读取、Latin1 检测、转义；同时承载原 TypeUtils 中仍被调用的数字/类型转换工具（`toBigDecimal` / `toIntValue` / `toLongValue` / `toDoubleValue` / `parseBigDecimal` / `doubleValue` / `floatValue` / `isInt64` / `isJavaScriptSupport` / `toString` 系列，TypeUtils 类已整体删除） |
| `NumberUtils` | 整数/浮点序列化（`ED`/`ED5`/`EF` 查表）、`Double.toString` 等价实现 |
| `TypeUtils` | 类型映射、数字字符串解析、基础转换（已裁剪掉 Bean/日期相关方法） |
| `Fnv` | FNV-1a 64 位哈希，字段名快速匹配（**保留**：被 JSONReader 三子类与 SymbolTable 调用 37 处，与 AutoType 无关） |
| `MutableBigInteger` / `Scientific` | 精确 double/float 解析（配合 ED/ED5/EF 查表） |
| `JDKUtils` | `Unsafe` 获取、JDK 版本检测、`String.value` 访问 |
| `StringUtils` | 字符串工具（ISO-8859-1 等） |
| `SymbolTable` | 字段名驻留（注意：位于 `com.alibaba.fastjson2` 包，非 util 包） |

## 5. 关键设计决策（与上游差异）

| 原版 | 精简版 |
|------|--------|
| `ObjectReaderProvider` / `ObjectWriterProvider` 类型分派 | 手写 switch / instanceof 分支，无 Provider |
| `JSON.parseObject(text, Bean.class)` 等泛型 API | 仅树 API：`parseObject(String)` 返回 `JSONObject` |
| 注解（`@JSONField` 等）控制序列化行为 | 无注解，行为由 Feature 控制 |
| 过滤器（`NameFilter` / `ValueFilter` 等） | 已删除 |
| AutoType（`SupportAutoType` Feature + 白名单） | 已删除（枚举中已无相关 Feature） |
| 日期/时间/UUID 序列化（`DateUtils` 等） | 已删除，遇到抛 `JSONException` |
| ASM / LambdaMetafactory 代码生成 | 无 Bean 绑定，无需代码生成 |

## 性能优化手段

1. **Unsafe 批量数组读写**：`putLong`/`getLong` 一次读写 8 字节，绕过数组边界检查（`JSONReaderUTF8`、`IOUtils`、`JSONWriterUTF8/UTF16` 核心路径）
2. **编码特化解析器**：UTF-8 / UTF-16 / ASCII 三种实现按输入自动选择
3. **数字查表**：`ED` / `ED5` / `EF` 常量表 + `Fnv` 哈希，避免运行时计算
4. **精确 double 解析**：`Scientific` + ED/ED5/EF 查表 + `MutableBigInteger`，无精度损失

## 线程安全

| 组件 | 线程安全？ | 说明 |
|-----------|:---:|-------|
| `JSON` 静态方法 | 是 | 无共享可变状态 |
| `JSONObject` / `JSONArray` | 否 | 同 `HashMap` / `ArrayList` |
| `JSONReader` / `JSONWriter` | 否 | 每次操作创建，勿跨线程共享 |
| `JSONFactory` 静态配置 | 是（配置后） | 启动时配置，运行期只读 |
| util 层静态方法 | 是 | 无状态（`Unsafe` 只读常量） |

## 构建

- **构建工具**: Maven（`mvnw` wrapper），单模块 `core`
- **Java 基线**: JDK 8（`maven.compiler.source` / `target` = 8）
- **编译参数**: `-XDignore.symbol.file`（允许访问 `sun.misc.Unsafe` 等内部 API）
- **测试**: JUnit 5（130+ 用例，覆盖树 API 全功能与数字解析边界）

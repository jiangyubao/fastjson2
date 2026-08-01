# 性能优化指南

本指南介绍本精简版（纯树模式）的调优策略和最佳实践。

## 性能架构

本库通过以下关键优化实现高性能：

### 1. Unsafe 批量数组读写

在解析/序列化内核中，使用 `Unsafe.putLong` / `getLong` 一次读写 8 个字节，并绕过 JVM 每次 `bytes[i]` 的边界检查。这是 JSON 字节级密集操作（数字格式化、字符串转义、字段名匹配）的核心加速手段，比纯逐字节循环快约 30-60%。

- 分布：`JSONReaderUTF8`（123 处）、`IOUtils`（70 处）、`JSONWriterUTF8` / `JSONWriterUTF16` 等，合计约 259 处
- 注意：`sun.misc.Unsafe` 从 JDK 8 到当前版本一直存在，JDK 内部自身大量使用；IDE 的 "Access restriction" 仅为静态检查，编译（`-XDignore.symbol.file`）与运行均无问题
- 测试代码已全部改为 `IOUtils.getLongLE` / `getIntLE` 等公开方法，不直接触碰 Unsafe

### 2. 编码特化解析器

为不同编码提供专用实现，库根据输入类型自动选择：

- `JSONReaderUTF8` - 针对 UTF-8 字节流优化（字符分类查表）
- `JSONReaderUTF16` - 针对 UTF-16（Java String 内部表示）优化
- `JSONReaderASCII` - 纯 ASCII 内容的快速路径

### 3. 数字查表与精确解析

- `ED` / `ED5` / `EF` 常量表：整数/浮点序列化查表，避免运行时计算
- `MutableBigInteger` / `Scientific`：精确 double/float 解析（配合 ED/ED5/EF 查表），无精度损失
- `Fnv`：FNV-1a 64 位哈希，字段名快速匹配免字符串比较

### 4. 手写递归解析（无反射分派）

`JSONReader.readAny()` 为手写 switch 递归，`JSONWriter.writeAny()` 为 instanceof 分支——不经过任何 Provider / 反射层，调用开销极低。

## 调优策略

### 1. 优先使用 byte[] 而非 String

**影响: 高**

尽量直接使用 `byte[]` 而非 `String`：

```java
// 更快：从 bytes 解析
byte[] bytes = getJsonBytes(); // 来自网络、文件等
JSONObject obj = JSON.parseObject(bytes);

// 更快：序列化为 bytes
byte[] output = JSON.toJSONBytes(obj);
```

这避免了 String 编码/解码的开销，在 HTTP/RPC 场景中尤其有效。

### 2. 最小化 Feature 使用

**影响: 低-中**

每个启用的 Feature 在热路径中增加一个条件检查。仅启用实际需要的 Feature：

```java
// 好：仅启用需要的
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);

// 避免：启用很多"以防万一"的 Feature
String json2 = JSON.toJSONString(obj,
    JSONWriter.Feature.PrettyFormat,        // 不需要就跳过
    JSONWriter.Feature.SortMapEntriesByKeys // 不关心顺序就跳过
);
```

### 3. 纯 ASCII 内容走 UTF8 快速路径

**影响: 中**

内容为纯 ASCII 时，开启 `OptimizedForAscii` 使用 `JSONWriterUTF8` 实现：

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.OptimizedForAscii);
```

### 4. 预分配容器（可选）

大数据量场景下，可通过 `JSONFactory.setDefaultObjectSupplier` / `setDefaultArraySupplier` 定制树模型的容器实现（如预初始化容量的 Map / List 工厂）。

## 线程安全

| 组件 | 线程安全？ | 说明 |
|------|:---:|------|
| `JSON` 静态方法 | 是 | 主入口，无共享可变状态，始终安全 |
| `JSONObject` / `JSONArray` | 否 | 未同步，类似 `HashMap` / `ArrayList` |
| `JSONReader` / `JSONWriter` | 否 | 每次操作创建，不要跨线程共享 |
| `JSONFactory` 静态配置 | 是（配置后） | 启动时配置，运行期只读 |
| util 层静态方法 | 是 | 无状态（Unsafe 只读常量） |

## JVM 调优

### 推荐 JVM 参数

```
# 启用紧凑字符串（JDK 9+，默认开启）
-XX:+CompactStrings
```

### 内存注意事项

- 解析时小数默认按 `BigDecimal` 存储（精度优先）；对内存敏感的大数据场景可考虑 `UseDoubleForDecimals`。
- `JSONObject` 默认 `LinkedHashMap` 保持插入顺序；不需要顺序时可定制容器或自行 `Map` 化。

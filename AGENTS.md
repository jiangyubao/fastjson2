# fastjson2 精简版 — 协作上下文

本文件面向 AI 编码助手与维护者,概述项目现状与关键约定。改动代码前请先阅读本节。

## 项目概述

从 FASTJSON 2.0.63 裁剪的**纯 JSON 树模式**库:

- 仅一个 Maven 模块 `core/`,产物 `fastjson2-2.0.63.jar`(同时是 OSGi bundle,由 `org.apache.felix:maven-bundle-plugin:5.1.9` 生成)
- JDK 8+、纯 Java 服务端场景;**无反射体系**:无 JavaBean 绑定、无注解、无过滤器、无 AutoType
- 能力范围:JSON 文本解析/序列化 + `JSONObject` / `JSONArray` 树模型 + 高精度数字核心

## 源码结构(core/src/main/java/com/alibaba/fastjson2/)

| 文件 | 职责 |
|------|------|
| `JSON.java` | 静态门面:parse / parseObject / parseArray / toJSONString / toJSONBytes / writeTo / toJSON / isValid 系列 |
| `JSONObject.java` / `JSONArray.java` | 树模型,继承 `LinkedHashMap<String, Object>` / `ArrayList<Object>` |
| `JSONReader.java` + `JSONReaderUTF8` / `JSONReaderUTF16` / `JSONReaderASCII` | 解析器,`readAny()` 手写递归 |
| `JSONWriter.java` + `JSONWriterUTF8` / `JSONWriterUTF16`(含 `JSONWriterUTF16JDK8` / `JDK8UF` / `JDK9UF` 变体) | 序列化,`writeAny()` instanceof 分支 |
| `JSONFactory.java` | 工厂 |
| `JSONException.java` / `JSONLargeObjectException.java` | 异常 |
| `util/` | `IOUtils`(数字转换/工具)、`NumberUtils`、`ED` / `ED5` / `EF`(double/float→文本查表)、`Scientific`、`Fnv`(字段名 FNV-1a 哈希)、`JDKUtils`(Unsafe/String)、`StringUtils`、`MutableBigInteger` |

## 关键行为约定(改动前必读)

- **序列化白名单**:`JSONObject` / `JSONArray` / `Map` / `List` / `String` / `Number`(含 `BigDecimal` / `BigInteger`)/ `Boolean` / `null`;其他类型(如 `Date`、POJO)抛 `JSONException`
- **不支持科学计数法**:`1e2` 解析抛 `JSONException("not support exponent number")`
- **小数默认解析为 `BigDecimal`**(保精度);`JSONReader.Feature.UseDoubleForDecimals` 可改为 double
- **默认跳过 null 值**:输出需 `JSONWriter.Feature.WriteMapNullValue` / `WriteNulls`
- **无 Bean / 泛型 API**:不存在 `parseObject(text, Class)`、`toJavaObject`、`toList`、`JSON.register(...)`;不要引用已删除的 `reader/`、`writer/`、`annotation/`、`filter/` 等包
- Feature 枚举完整保留,但**仅树模型相关项生效**(如 `UseNativeObject`、`UseDoubleForDecimals`、`AllowUnQuotedFieldNames`、`PrettyFormat`、`WriteMapNullValue`、`UseSingleQuotes` 等);Bean 专用项(如 `FieldBased`、`SupportSmartMatch`)无实际效果

## 构建与测试

```bash
./mvnw clean package                            # 全量构建(仅 core)
./mvnw -pl core clean package                   # 构建 core
./mvnw -pl core -Dtest=JSONTreeAPITest test     # 运行单个测试类
./mvnw clean package -DskipTests                # 跳过测试
```

- 测试:JUnit 5,位于 `core/src/test/java/com/alibaba/fastjson2/`(`JSONTreeAPITest`、`JSONReaderTest2`、`util/` 下数字/工具测试)
- 编译:Java 8;maven-compiler-plugin 带 `-Xlint:-options -XDignore.symbol.file`;无 checkstyle / modernizer / jacoco 插件
- OSGi:felix `bundle` goal 接管打包;`Import-Package` 含 `sun.misc;resolution:=optional`,勿删

## CI

`.github/workflows/ci.yaml`:JDK 8/11/17/21/25 × ubuntu-24.04 / windows / macos(JDK 25 排除 macOS)。

> 注意:`test-reflect` job 传入的 `-Dfastjson2.creator=reflect` 已无实际作用(反射/ASM 体系已删除),可考虑移除该 job。

## 文档

- 有效文档:`docs/index.md`、`features_cn/en`、`performance_cn/en`、`FAQ_cn/en`、`ARCHITECTURE.md`、`fastjson_1_upgrade_cn/en`、`精简评估报告.md`
- 旧功能文档(`annotations*`、`Filter/*`、`autotype*`、`mixin*`、`register_custom_reader_writer*`、`reader_codegen*`、`jsontype_seealso*`、`design_jsonreader*` / `design_jsonwriter*`)为"已移除"说明页,不要作为使用指南引用

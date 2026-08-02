##### Language: English | [中文](README_cn.md)

# FASTJSON 2 (Trimmed)

A trimmed fork of [FASTJSON 2.0.63](https://github.com/alibaba/fastjson2) reduced to the **pure JSON tree model**: text parsing and serialization via `JSON`, `JSONObject` / `JSONArray`, plus the high-precision number core. The entire reflection system is removed — no JavaBean binding, annotations, filters, or AutoType. Runs on JDK 8+. Artifact: `com.alibaba.fastjson2:fastjson2:2.0.63`, packaged as an OSGi bundle.

## Feature Scope

**Supported:**

- JSON text parsing and serialization (`String` / `char[]` / `byte[]` / `Reader` / `InputStream` input; UTF-8 / UTF-16 / ASCII)
- `JSONObject` / `JSONArray` tree model (subclasses of `LinkedHashMap<String, Object>` / `ArrayList<Object>`)
- Hand-written recursive parser (`JSONReader.readAny`) and `instanceof`-based writer (`JSONWriter.writeAny`) — no `ObjectReader` / `ObjectWriter` providers
- `JSONReader.Feature` / `JSONWriter.Feature` (only the tree-model items take effect; see [features_en.md](docs/features_en.md))
- High-precision numbers: `BigDecimal` / `BigInteger` exact conversion; `double` / `float` text output via ED/ED5/EF lookup tables
- Loose syntax: single-quoted strings, unquoted field names

**Removed:**

- `JSONB`, `JSONPath`, JSON Schema, CSV, Kotlin / Spring / Android modules, fastjson1-compatible layer, JMH benchmarks
- The reflection system: `reader/`, `writer/`, `introspect/`, `annotation/`, `codec/`, `modules/`, `filter/`, `function/`, `internal/` packages, `TypeReference`
- JavaBean binding: `JSON.parseObject(text, Class)`, `toJavaObject`, `toList`, `@JSONField` / `@JSONType`, filters, custom `ObjectReader` / `ObjectWriter`, AutoType
- Date / UUID support: `Date`, `LocalDate`, `LocalDateTime`, `UUID`, etc. throw `JSONException`

## Quick Start

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

JSONObject obj = JSON.parseObject("{\"id\":1,\"name\":\"fastjson2\"}");  // parse text
JSONArray arr  = JSON.parseArray("[1,2,3]");                             // parse array

String json  = JSON.toJSONString(obj);   // serialize to String
byte[] bytes = JSON.toJSONBytes(obj);    // serialize to byte[]
```

## Usage

### Parse

```java
JSONObject obj = JSON.parseObject(text);   // String / char[] / byte[] / Reader / InputStream
JSONArray  arr = JSON.parseArray(text);    // String / char[] / byte[]
Object     any = JSON.parse(text);         // JSONObject / JSONArray / String / Number / Boolean
```

### Serialize

```java
String  text  = JSON.toJSONString(obj);   // String
byte[]  bytes = JSON.toJSONBytes(obj);    // byte[]
JSON.writeTo(obj, outputStream);          // write to a stream

String pretty = obj.toJSONString(JSONWriter.Feature.PrettyFormat);
```

Serializable types: `JSONObject`, `JSONArray`, `Map`, `List`, `String`, `Number` (including `BigDecimal` / `BigInteger`), `Boolean`, `null`. Any other type (e.g. `Date`, POJOs) throws `JSONException`.

### Tree model operations

```java
JSONObject obj = JSON.parseObject("{\"id\":2,\"name\":\"fastjson2\",\"enable\":true}");

int id       = obj.getIntValue("id");
String name  = obj.getString("name");
boolean en   = obj.getBooleanValue("enable");

JSONObject child = obj.getJSONObject("child");   // nested object
JSONArray  items = obj.getJSONArray("items");    // nested array

obj.put("key", value);
obj.remove("key");
```

`JSONObject` extends `Map<String, Object>` and `JSONArray` extends `List<Object>`, so the collection APIs work directly.

### Features

```java
String json = JSON.toJSONString(obj,
        JSONWriter.Feature.WriteMapNullValue,     // write null values (skipped by default)
        JSONWriter.Feature.PrettyFormat);         // pretty print

JSONObject obj = JSON.parseObject(text,
        JSONReader.Feature.UseDoubleForDecimals); // decimals as double (BigDecimal by default)
```

See [features_en.md](docs/features_en.md) for the full list.

## Documentation

| Document | Description |
|----------|-------------|
| [index.md](docs/index.md) | Overview of this trimmed fork |
| [features_en.md](docs/features_en.md) | `JSONReader` / `JSONWriter` features |
| [performance_en.md](docs/performance_en.md) | Tuning tips |
| [FAQ_en.md](docs/FAQ_en.md) | Frequently asked questions |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Internal design |
| [fastjson_1_upgrade_en.md](docs/fastjson_1_upgrade_en.md) | Upgrading from Fastjson 1.x |

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## License

[Apache License 2.0](LICENSE).

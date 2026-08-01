# Frequently Asked Questions

## Overview

### What is the difference between this trimmed build and full FASTJSON 2?

This repository is a **pure tree-model** build trimmed from FASTJSON 2.0.63:

- **Keeps only** the `JSON` / `JSONObject` / `JSONArray` tree API (`JSON.parseObject(String)` → `JSONObject`)
- **Removed**: JavaBean binding (`parseObject(text, Bean.class)`, `toJavaObject`, `toList`), annotations (`@JSONField` / `@JSONType`), serialization filters, AutoType, `TypeReference`, date/time/UUID support, and extensions such as `JSONB` / `JSONPath` / `JSON Schema` / `CSV`
- **No reflection system at runtime** (`reader/`, `writer/` packages deleted) - zero attack surface
- See [精简评估报告.md](精简评估报告.md) for details

### Which Java versions are supported?

- **Core library**: Java 8+ (includes `JSONWriterUTF16JDK8` variants)
- **Full features**: Java 11+ (compact strings)

## Parsing & Deserialization

### How do I parse JSON with unknown structure?

```java
// Parse into JSONObject
JSONObject obj = JSON.parseObject(jsonString);

// Parse into JSONArray
JSONArray arr = JSON.parseArray(jsonString);

// Parse into generic Object (auto-detected)
Object result = JSON.parse(jsonString); // JSONObject / JSONArray / String / BigDecimal / Boolean
```

### How do I parse JSON with generics?

This build does **not** support generic JavaBean binding (`TypeReference` was removed). Use the tree model directly:

```java
JSONObject obj = JSON.parseObject(json);
JSONArray users = obj.getJSONArray("users");
JSONObject first = users.getJSONObject(0);
```

### How do I read nested objects / arrays?

```java
JSONArray array = obj.getJSONArray("items");
JSONObject child = obj.getJSONObject("child");
String name = child.getString("name");
```

### What type are decimals parsed as?

**`BigDecimal`** by default (no precision loss). For double/float:

```java
JSONObject obj = JSON.parseObject(json, JSONReader.Feature.UseDoubleForDecimals);
JSONObject obj2 = JSON.parseObject(json, JSONReader.Feature.UseBigDecimalForFloats);
```

### Is single-quote JSON supported?

Yes. Single-quoted keys/values (`{'a':1}`) are allowed by default; disable with `JSONReader.Feature.DisableSingleQuote`.

### How do I get null instead of an exception on parse errors?

```java
JSONObject obj = JSON.parseObject(json, JSONReader.Feature.NullOnError);
```

## Serialization

### Which types can be serialized?

`JSONObject`, `JSONArray`, `Map`, `List`, `String`, `Number` (incl. `BigDecimal` / `BigInteger`), `Boolean`, `null`. **Any other type** (e.g. `Date`, custom POJOs) throws `JSONException`.

### How do I include null fields in JSON output?

Nulls are skipped by default:

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);
```

Specific null handling:

```java
JSONWriter.Feature.WriteNullStringAsEmpty  // null String → ""
JSONWriter.Feature.WriteNullListAsEmpty    // null List → []
JSONWriter.Feature.WriteNullNumberAsZero   // null Number → 0
JSONWriter.Feature.WriteNullBooleanAsFalse // null Boolean → false
```

### How do I pretty-print JSON?

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
String json2 = JSON.toJSONString(obj,
    JSONWriter.Feature.PrettyFormat,
    JSONWriter.Feature.PrettyFormatWith2Space); // 2-space indent
```

### How do I handle large long values for JavaScript?

JavaScript cannot represent Java `long` values beyond `Number.MAX_SAFE_INTEGER` (2^53 - 1). Use `BrowserCompatible` or `WriteLongAsString`:

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.BrowserCompatible);
String json = JSON.toJSONString(obj, JSONWriter.Feature.WriteLongAsString);
```

### How do I sort Map keys (signing scenarios)?

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.SortMapEntriesByKeys);
```

### Can I serialize Date?

**No.** Date/time support was removed; serializing a `Date` throws `JSONException` (`WriterUtilDateAsMillis` has no effect either). Use strings or millisecond numbers in front-end scenarios.

## Differences from the full build / 1.x

### Why doesn't `JSON.parseObject(json, Bean.class)` compile?

The method was removed. This build only provides the tree-model API and no longer supports JavaBean binding. If you need POJO mapping, use the full fastjson2 or convert on top of the tree model yourself.

### Why is there no JSONPath / JSON Schema?

These extension modules were removed; they are not part of the core `JSON` text protocol.

### Is this build secure?

Yes - the most secure form possible: AutoType and the entire reflection system (`reader/` / `writer/` etc.) are deleted, so there is no `@type` deserialization attack surface at runtime.

## Performance

### How do I get the best parsing performance?

1. **Prefer byte[] input** - `JSON.parseObject(bytes)` avoids String encoding overhead.
2. **Avoid unnecessary Features** - each enabled Feature adds a small check.

### How do I get the best serialization performance?

1. **Prefer byte[] output** - `JSON.toJSONBytes(obj)` is faster than `JSON.toJSONString(obj)`.
2. **Pure ASCII content** - enable `JSONWriter.Feature.OptimizedForAscii` to use the UTF8 fast path.

## Troubleshooting

### I got `com.alibaba.fastjson2.JSONException`

Common causes:
1. **Malformed JSON** - validate the input.
2. **Unsupported serialization type** - e.g. `Date`, custom POJO (see "Which types can be serialized?").
3. **`not support write value type`** - a non-tree type is being serialized; check whether POJO instances slipped into a `Map` / `List`.
4. **`not support feature`** - a Feature combination unsupported in tree mode (e.g. `ReferenceDetection` combined with certain features is rejected in the subclass `write(Map/List)`).

### Decimal precision seems wrong?

Decimals parse as `BigDecimal` by default (no precision loss). If you see float errors, check whether you enabled `UseDoubleForDecimals` / `UseBigDecimalForFloats` explicitly; use `WriteBigDecimalAsPlain` to avoid scientific notation on output.

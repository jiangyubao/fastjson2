# Trimmed-Build Differences (vs FASTJSON 1.x / full 2.x)

This repository is a **pure tree-model** build trimmed from fastjson2 2.0.63. This document explains the differences from fastjson 1.x and the full fastjson2 to help migration decisions.

## 1. Why Trimmed

- The use case only needs the `JSON` text protocol and the `JSONObject` / `JSONArray` tree model (e.g. web front-end data exchange)
- Removing the reflection system leaves no `reader` / `writer` / annotations / filters / AutoType at runtime - **zero attack surface**
- Code shrank from 83,275 to 41,018 lines (-51%); jar ~354KB, easy to audit and embed

## 2. Differences from fastjson 1.x

### 2.1 Package Name

FASTJSON 2 uses a different package (`com.alibaba.fastjson2`), so it can coexist with 1.x:

```java
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
```

### 2.2 Maven Dependency

```xml
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.63</version>
</dependency>
```

### 2.3 Behavioral Differences (Trimmed Build)

| Aspect | fastjson 1.x | Trimmed build |
|------|-------------|--------|
| Parse entry | `JSON.parseObject(text, Bean.class)` | **Tree API only**: `JSON.parseObject(text)` → `JSONObject` |
| Serialize entry | `JSON.toJSONString(Object)` | Same (tree model / basic types only) |
| JavaBean binding | Supported | **Removed** (`toJavaObject` / `toList` / `TypeReference` gone) |
| Annotations | `@JSONField` etc. | **Removed** |
| AutoType | Whitelist by default | **Removed** (no `@type` parsing - immune by design) |
| Circular-reference detection | On by default | Off by default (`ReferenceDetection` rarely needed in tree mode) |
| SmartMatch | On by default | Removed (exact key matching) |
| Date/time | Supported | **Removed** (serializing `Date` throws) |
| Serialization filters | `NameFilter` / `ValueFilter` etc. | **Removed** |

### 2.4 Migrating from 1.x to the Tree Model

If your code heavily uses `JSON.parseObject(text, Bean.class)` / `toJavaObject`, **do not switch to this trimmed build directly** (it will not compile). Options:

1. **Keep using full fastjson2** - retains Bean binding and annotations
2. **Adopt the tree model** - rewrite as:

```java
// Before
User user = JSON.parseObject(json, User.class);
String name = user.getName();

// After (tree mode)
JSONObject user = JSON.parseObject(json);
String name = user.getString("name");
```

3. **Write your own conversion layer** - map `JSONObject` → POJO manually on top of the tree model (`JSONObject.get*` + manual assembly)

## 3. Differences from Full fastjson2

| Full 2.x | Trimmed build |
|-----------|--------|
| `JSON.parseObject(text, Bean.class)`, generic `TypeReference` | Removed |
| Annotations `@JSONField` / `@JSONType` / `@JSONCreator` / `@JSONBuilder` | Removed |
| Custom `ObjectReader` / `ObjectWriter` and Provider registration | Removed |
| Filters (`NameFilter` / `ValueFilter` / `PropertyFilter` etc.) | Removed |
| AutoType (`SupportAutoType` / whitelist / SafeMode) | Removed |
| MixIn annotation injection | Removed |
| Code generation (ASM / LambdaMetafactory) | Not needed (no Bean binding) |
| Date/time/UUID (`DateUtils`, `WriterUtilDateAsMillis`) | Removed |
| `JSONB` / `JSONPath` / `JSON Schema` / `CSV` / Kotlin / Spring / Android extensions | Removed |
| `JSONReader.Feature` / `JSONWriter.Feature` enums | **Fully retained** (tree-effective entries in [features_en.md](features_en.md)) |
| `JSON.parse` / `parseObject` / `parseArray` / `toJSONString` / `toJSONBytes` / `writeTo` / `isValid` | **Retained** (all tree APIs public) |

## 4. FAQ

### 4.1 How does 1.x `SerializerFeature` map?

1.x `SerializerFeature` → full 2.x `JSONWriter.Feature` → trimmed build (enum retained, partially effective). Common mappings:

| 1.x SerializerFeature | Trimmed JSONWriter.Feature |
|-----------------------|--------------------------|
| `WriteMapNullValue` | `WriteMapNullValue` / `WriteNulls` ✅ |
| `PrettyFormat` | `PrettyFormat` ✅ |
| `SortField` | `SortMapEntriesByKeys` ✅ (JSONObject is a LinkedHashMap) |
| `WriteLongAsString` | `WriteLongAsString` ✅ |
| `WriteDateUseDateFormat` | Not supported (date removed) |
| `BrowserCompatible` | `BrowserCompatible` ✅ |

### 4.2 How does 1.x `ExtraProcessor` / `ObjectDeserializer` map?

**No equivalent.** The extension mechanism (`filter` / `reader` / `writer` packages) was removed. In tree mode, extra properties naturally remain in the `JSONObject` (`obj.get("extra")`) - no processor needed.

### 4.3 How does the 1.x AutoType whitelist map?

**Nothing to map.** AutoType was fully removed; there is no `@type` deserialization.

### 4.4 How do I disable circular-reference detection like 1.x?

1.x enables circular-reference detection by default. The trimmed build has it off by default (no `ReferenceDetection`), equivalent to 1.x with `DisableCircularReferenceDetect` enabled.

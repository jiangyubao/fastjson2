# Configuring Serialization and Deserialization Behavior with Features

# 1. Introduction

In fastjson 2.x, there are two Feature enums controlling serialization and deserialization:

- `JSONWriter.Feature` - controls serialization behavior
- `JSONReader.Feature` - controls deserialization behavior

> **Trimmed-edition note**: This build only provides the tree-model API (`JSON.parseObject(String)` returns `JSONObject`; no JavaBean binding like `parseObject(text, Bean.class)`). The Feature enums are fully retained, but only the entries marked **Tree ✅** actually take effect on `JSONObject` / `JSONArray`. JavaBean-oriented features (`FieldBased`, `SupportArrayToBean`, `SupportSmartMatch`, etc.) have no effect in pure tree mode.

# 2. Using Features with toJSONString and parse

```java
JSONObject obj = JSON.parseObject("{\"name\":\"fastjson2\"}");
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
String json2 = JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);

String jsonStr = "{\"v\":1.5}";
JSONObject o = JSON.parseObject(jsonStr, JSONReader.Feature.UseDoubleForDecimals);
```

# 3. JSONReader.Feature

| JSONReader.Feature | Tree | Description |
|---------------------------------|:---:|-----------------------------------------------------------------------------------------------------|
| FieldBased | — | Field-based deserialization (JavaBean only) |
| IgnoreNoneSerializable | — | Ignore non-Serializable fields (JavaBean only) |
| ErrorOnNoneSerializable | — | Throw on non-Serializable fields (JavaBean only) |
| SupportArrayToBean | — | Map array to JavaBean (JavaBean only) |
| InitStringFieldAsEmpty | — | Init String fields as "" (JavaBean only) |
| SupportSmartMatch | — | Smart camel/pascal/snake/kebab matching (JavaBean only) |
| UseNativeObject | ✅ | Use LinkedHashMap/ArrayList instead of JSONObject/JSONArray |
| SupportClassForName | — | Support Class-typed fields (JavaBean only) |
| IgnoreSetNullValue | — | Ignore null field inputs (JavaBean only) |
| UseDefaultConstructorAsPossible | — | Prefer default constructors (JavaBean only) |
| UseBigDecimalForFloats | ✅ | Parse decimals as Float (default is BigDecimal) |
| UseBigDecimalForDoubles | ✅ | Parse decimals as Double (default is BigDecimal) |
| ErrorOnEnumNotMatch | — | Throw on enum name mismatch (JavaBean only) |
| TrimString | ✅ | Trim string values on read |
| DuplicateKeyValueAsArray | ✅ | Merge duplicate keys into an array |
| AllowUnQuotedFieldNames | ✅ | Allow unquoted field names |
| NonStringKeyAsString | ✅ | Treat non-String keys as String |
| Base64StringAsByteArray | — | Parse Base64 strings into byte[] (JavaBean only) |
| IgnoreCheckClose | — | Ignore resource close checks |
| ErrorOnNullForPrimitives | — | Throw when primitive fields see null (JavaBean only) |
| NullOnError | ✅ | Return null on error instead of throwing |
| NonZeroNumberCastToBooleanAsTrue | ✅ | Non-zero numbers cast to boolean as true |
| IgnoreNullPropertyValue | ✅ | Ignore null-valued properties |
| ErrorOnUnknownProperties | — | Throw on unknown properties (JavaBean only) |
| EmptyStringAsNull | ✅ | Convert "" to null |
| NonErrorOnNumberOverflow | — | Do not throw on number overflow (JavaBean only) |
| UseBigIntegerForInts | ✅ | Parse untyped integers as BigInteger |
| UseLongForInts | ✅ | Parse int-range integers as Long |
| DisableSingleQuote | ✅ | Disallow single-quote keys/values (single-quote is supported by default) |
| UseDoubleForDecimals | ✅ | Parse decimals as double |
| DisableReferenceDetect | ✅ | Do not resolve JSON references (e.g. $ref) |
| DisableStringArrayUnwrapping | — | Keep single-element string arrays as arrays (JavaBean only) |

> Note: AutoType-related features (`SupportAutoType`, `ErrorOnNotSupportAutoType`, `IgnoreAutoTypeNotMatch`) have been **removed** together with the reflection system.

# 4. JSONWriter.Feature

| JSONWriter.Feature | Tree | Description |
|-----------------------------------|:---:|------------------------------------------------------------------------------------------------------------------|
| FieldBased | — | Field-based serialization (JavaBean only) |
| IgnoreNoneSerializable | — | Ignore non-Serializable fields (JavaBean only) |
| ErrorOnNoneSerializable | — | Throw on non-Serializable objects (JavaBean only) |
| BeanToArray | — | Serialize object as array `[101,"XX"]` (JavaBean only) |
| WriteNulls | ✅ | Write null-valued fields (alias of WriteMapNullValue) |
| WriteMapNullValue | ✅ | Write null-valued Map entries (nulls skipped by default) |
| BrowserCompatible | ✅ | Output large integers (beyond JS safe range) as strings |
| NullAsDefaultValue | ✅ | Output defaults for null (numbers→0, String→"", collections→[], others→{}) |
| WriteBooleanAsNumber | ✅ | Output true as 1, false as 0 |
| WriteNonStringValueAsString | ✅ | Output non-String scalars as String |
| WriteClassName | — | Output type info (JavaBean only; no type in tree mode) |
| NotWriteRootClassName | — | (JavaBean only) |
| NotWriteHashMapArrayListClassName | — | (JavaBean only) |
| NotWriteDefaultValue | — | Skip default-valued fields (JavaBean only) |
| WriteEnumsUsingName | ✅ | Serialize enums using name |
| WriteEnumUsingToString | ✅ | Serialize enums using toString |
| IgnoreErrorGetter | — | Ignore getter errors (JavaBean only) |
| PrettyFormat | ✅ | Pretty-print output (objects and arrays) |
| ReferenceDetection | ✅ | Enable reference detection (off by default; rarely needed in tree mode) |
| WriteNameAsSymbol | — | Write field names as symbols (JavaBean only) |
| WriteBigDecimalAsPlain | ✅ | Serialize BigDecimal with toPlainString |
| UseSingleQuotes | ✅ | Use single quotes |
| MapSortField | ✅ | Sort Map keys before output (deprecated; use SortMapEntriesByKeys) |
| WriteNullListAsEmpty | ✅ | Output null List as "[]" |
| WriteNullStringAsEmpty | ✅ | Output null String as "" |
| WriteNullNumberAsZero | ✅ | Output null Number as 0 |
| WriteNullBooleanAsFalse | ✅ | Output null Boolean as false |
| NotWriteEmptyArray | ✅ | Skip empty arrays (deprecated; use IgnoreEmpty) |
| IgnoreEmpty | ✅ | Ignore empty fields (empty collections/strings, etc.) |
| WriteNonStringKeyAsString | ✅ | Output non-String Map keys as String |
| WritePairAsJavaBean | — | Serialize Apache Commons Pair as JavaBean (JavaBean only) |
| OptimizedForAscii | ✅ | Use optimized path for pure-ASCII content (UTF8 writer) |
| EscapeNoneAscii | ✅ | Escape non-ASCII characters |
| WriteByteArrayAsBase64 | — | Serialize byte[] as Base64 (JavaBean only) |
| IgnoreNonFieldGetter | — | Only consider getters backed by fields (JavaBean only) |
| LargeObject | — | Guard against cyclic-reference resource exhaustion (JavaBean only) |
| WriteLongAsString | ✅ | Serialize Long as String |
| BrowserSecure | ✅ | Escape '<' '>' '(' ')' for browser safety |
| WriteEnumUsingOrdinal | ✅ | Serialize enums using ordinal (default is name) |
| WriteThrowableClassName | — | Include class name for Throwable (JavaBean only) |
| UnquoteFieldName | ✅ | Output keys without quotes |
| NotWriteSetClassName | — | (JavaBean only) |
| NotWriteNumberClassName | — | (JavaBean only) |
| SortMapEntriesByKeys | ✅ | Sort Map entries by key before output (stable ordering for signing) |
| PrettyFormatWith2Space | ✅ | 2-space indent (requires PrettyFormat) |
| PrettyFormatWith4Space | ✅ | 4-space indent (requires PrettyFormat) |
| WriterUtilDateAsMillis | — | Date support removed; no effect |
| WriteFloatSpecialAsString | ✅ | Serialize NaN/Infinity as "NaN"/"Infinity"/"-Infinity" |

# 5. Best Practices

1. **Decimal precision**: decimals parse as `BigDecimal` by default (no precision loss). Use `UseDoubleForDecimals` / `UseBigDecimalForDoubles` for front-end scenarios.
2. **Large integers**: `long` values beyond `2^53-1` lose precision in JS. Use `BrowserCompatible` / `WriteLongAsString` when serializing; `UseLongForInts` / `UseBigIntegerForInts` when parsing.
3. **Null values**: nulls are skipped by default. Use `WriteMapNullValue` to output them; `WriteNullStringAsEmpty` etc. for specific defaults.
4. **Pretty printing**: `PrettyFormat` indents output; `PrettyFormatWith2Space` / `PrettyFormatWith4Space` control indent width.
5. **Stable ordering**: use `SortMapEntriesByKeys` for signing scenarios.
6. **Security**: AutoType and the reflection system have been fully removed - no `@type` deserialization attack surface remains.

# Serialization Filters (Removed)

> **All filters have been removed in the trimmed build.**

The 19 filter classes (`AfterFilter`, `BeforeFilter`, `NameFilter`, `ValueFilter`, `PropertyFilter`, `LabelFilter`, `PascalNameFilter`, `ContextNameFilter`, `ContextValueFilter`, `ExtraProcessor`, `AutoTypeBeforeHandler` etc.) are deleted; `com.alibaba.fastjson2.filter` does not exist.

## Why it was removed

Filters customize property names/values during JavaBean serialization/deserialization and depend on the reflection system. This build is a pure tree model with no Bean binding - filters have nothing to act on.

## Replacement

- Renaming / ignoring properties: manipulate the `JSONObject` directly (put / remove / keySet iteration)
- Value transformation: iterate `JSONObject.entrySet()` and convert values before put
- Extra properties: in tree mode they naturally remain in the `JSONObject` - no ExtraProcessor needed

## Related Documents

- [Overview](../index.md)
- [Trim & Evaluation Report](../精简评估报告.md)

# NameFilter / ContextNameFilter (Removed)

> **This filter has been removed in the trimmed build.** `com.alibaba.fastjson2.filter` does not exist.

NameFilter / ContextNameFilter modified property names during serialization. It depended on the JavaBean serialization pipeline and the reflection system; this build is a pure tree model with no Bean binding, so it was deleted.

## Replacement

Handle `JSONObject` / `JSONArray` directly in the tree model (iterate `entrySet()` to change keys/values, or put/remove to control output fields).

## Related Documents

- [Filters Overview](index_en.md) (removed)
- [Overview](../index.md)
- [Trim & Evaluation Report](../精简评估报告.md)

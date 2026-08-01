# @JSONType(seeAlso) (Removed)

> **This feature has been removed in the trimmed build.**

Configuring polymorphic types via `@JSONType(seeAlso = ...)` **no longer exists** in this repository - the corresponding code and APIs have been deleted.

## Why it was removed

This build was trimmed to a pure JSON tree model (`JSON` / `JSONObject` / `JSONArray`), with the reflection system (10 packages: `reader/`, `writer/`, `annotation/`, `filter/` etc.) fully deleted. The mechanisms this feature depended on no longer exist.

## Replacement

No replacement needed. Annotations and polymorphic binding were removed; store an explicit type field in JSON and branch on it in the tree model if needed.

## Related Documents

- [Overview](index.md)
- [Serialization/Deserialization Features](features_en.md)
- [Trim & Evaluation Report](精简评估报告.md)

# Annotation System (Removed)

> **This feature has been removed in the trimmed build.**

annotations (`@JSONField`, `@JSONType`, `@JSONCreator`, `@JSONBuilder`, `@JSONCompiled` etc.) **no longer exists** in this repository - the corresponding code and APIs have been deleted.

## Why it was removed

This build was trimmed to a pure JSON tree model (`JSON` / `JSONObject` / `JSONArray`), with the reflection system (10 packages: `reader/`, `writer/`, `annotation/`, `filter/` etc.) fully deleted. The mechanisms this feature depended on no longer exist.

## Replacement

This build has no JavaBean binding; serialization/deserialization behavior is controlled via `JSONReader.Feature` / `JSONWriter.Feature` (see features_en.md). In tree mode, field names are JSON keys - no annotation mapping needed.

## Related Documents

- [Overview](index.md)
- [Serialization/Deserialization Features](features_en.md)
- [Trim & Evaluation Report](精简评估报告.md)

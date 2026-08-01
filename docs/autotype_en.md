# AutoType (Removed)

> **This feature has been removed in the trimmed build.**

AutoType deserialization (instantiating classes named by `@type`, incl. `SupportAutoType` feature, whitelist, SafeMode) **no longer exists** in this repository - the corresponding code and APIs have been deleted.

## Why it was removed

This build was trimmed to a pure JSON tree model (`JSON` / `JSONObject` / `JSONArray`), with the reflection system (10 packages: `reader/`, `writer/`, `annotation/`, `filter/` etc.) fully deleted. The mechanisms this feature depended on no longer exist.

## Replacement

No replacement needed. The reflection system was removed; there is no `@type` deserialization capability at runtime.

## Related Documents

- [Overview](index.md)
- [Serialization/Deserialization Features](features_en.md)
- [Trim & Evaluation Report](精简评估报告.md)

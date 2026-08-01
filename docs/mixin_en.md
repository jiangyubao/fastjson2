# MixIn Annotation Injection (Removed)

> **This feature has been removed in the trimmed build.**

MixIn (injecting annotations onto third-party classes without modifying their source) **no longer exists** in this repository - the corresponding code and APIs have been deleted.

## Why it was removed

This build was trimmed to a pure JSON tree model (`JSON` / `JSONObject` / `JSONArray`), with the reflection system (10 packages: `reader/`, `writer/`, `annotation/`, `filter/` etc.) fully deleted. The mechanisms this feature depended on no longer exist.

## Replacement

No replacement needed. The annotation system was removed; in tree mode all data is JSONObject/JSONArray with no class binding.

## Related Documents

- [Overview](index.md)
- [Serialization/Deserialization Features](features_en.md)
- [Trim & Evaluation Report](精简评估报告.md)

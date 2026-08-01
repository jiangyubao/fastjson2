# Custom ObjectReader / ObjectWriter (Removed)

> **This feature has been removed in the trimmed build.**

Customizing deserialization/serialization of arbitrary types by implementing ObjectReader/ObjectWriter and registering with the Provider **no longer exists** in this repository - the corresponding code and APIs have been deleted.

## Why it was removed

This build was trimmed to a pure JSON tree model (`JSON` / `JSONObject` / `JSONArray`), with the reflection system (10 packages: `reader/`, `writer/`, `annotation/`, `filter/` etc.) fully deleted. The mechanisms this feature depended on no longer exist.

## Replacement

No replacement needed. The reader/writer packages and Provider registration were removed. Only JSONObject/JSONArray/Map/List/String/Number/Boolean are serializable; anything else throws JSONException.

## Related Documents

- [Overview](index.md)
- [Serialization/Deserialization Features](features_en.md)
- [Trim & Evaluation Report](精简评估报告.md)

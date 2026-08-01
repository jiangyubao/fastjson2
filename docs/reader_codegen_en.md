# Reader Code Generation (Removed)

> **This feature has been removed in the trimmed build.**

Code generation of ObjectReaders for JavaBeans (ASM, LambdaMetafactory, ReaderCreator) **no longer exists** in this repository - the corresponding code and APIs have been deleted.

## Why it was removed

This build was trimmed to a pure JSON tree model (`JSON` / `JSONObject` / `JSONArray`), with the reflection system (10 packages: `reader/`, `writer/`, `annotation/`, `filter/` etc.) fully deleted. The mechanisms this feature depended on no longer exist.

## Replacement

No replacement needed. No JavaBean binding means no code generation; parsing uses hand-written switch recursion (JSONReader.readAny).

## Related Documents

- [Overview](index.md)
- [Serialization/Deserialization Features](features_en.md)
- [Trim & Evaluation Report](精简评估报告.md)

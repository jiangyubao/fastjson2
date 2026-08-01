# AutoType（已移除）

> **精简版已移除该功能。**

AutoType 反序列化（通过 `@type` 字段自动实例化指定类，含 `SupportAutoType` Feature、白名单、SafeMode） 在本仓库中**已不存在**，对应代码与 API 已全部删除。

## 为什么移除

本库已裁剪为纯 JSON 树模型（`JSON` / `JSONObject` / `JSONArray`），彻底删除了反射体系（`reader/`、`writer/`、`annotation/`、`filter/` 等 10 个包），该功能依赖的底层机制已不存在。

## 替代方案

无需替代。反射体系已整体移除，运行时不存在 `@type` 反序列化能力，天然免疫该攻击面。

## 相关文档

- [功能总览](index.md)
- [序列化/反序列化特性](features_cn.md)
- [精简说明与评估](精简评估报告.md)

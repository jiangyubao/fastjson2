# @JSONType(seeAlso)（已移除）

> **精简版已移除该功能。**

通过 `@JSONType(seeAlso = ...)` 配置多态类型（序列化时输出类型信息、反序列化时按类型分发） 在本仓库中**已不存在**，对应代码与 API 已全部删除。

## 为什么移除

本库已裁剪为纯 JSON 树模型（`JSON` / `JSONObject` / `JSONArray`），彻底删除了反射体系（`reader/`、`writer/`、`annotation/`、`filter/` 等 10 个包），该功能依赖的底层机制已不存在。

## 替代方案

无需替代。注解与多态绑定已移除；如需区分类型，可在 JSON 中显式保存 type 字段并在树模型上自行判断。

## 相关文档

- [功能总览](index.md)
- [序列化/反序列化特性](features_cn.md)
- [精简说明与评估](精简评估报告.md)

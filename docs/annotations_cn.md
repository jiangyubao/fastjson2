# 注解体系（已移除）

> **精简版已移除该功能。**

注解（`@JSONField`、`@JSONType`、`@JSONCreator`、`@JSONBuilder`、`@JSONCompiled` 等） 在本仓库中**已不存在**，对应代码与 API 已全部删除。

## 为什么移除

本库已裁剪为纯 JSON 树模型（`JSON` / `JSONObject` / `JSONArray`），彻底删除了反射体系（`reader/`、`writer/`、`annotation/`、`filter/` 等 10 个包），该功能依赖的底层机制已不存在。

## 替代方案

本库无 JavaBean 绑定，序列化/反序列化行为通过 `JSONReader.Feature` / `JSONWriter.Feature` 控制（见 features_cn.md）。树模式下字段名即 JSON 键名，无需注解映射。

## 相关文档

- [功能总览](index.md)
- [序列化/反序列化特性](features_cn.md)
- [精简说明与评估](精简评估报告.md)

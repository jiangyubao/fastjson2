# 自定义 ObjectReader / ObjectWriter（已移除）

> **精简版已移除该功能。**

通过实现 ObjectReader/ObjectWriter 接口并注册到 Provider，定制任意类型的反序列化/序列化 在本仓库中**已不存在**，对应代码与 API 已全部删除。

## 为什么移除

本库已裁剪为纯 JSON 树模型（`JSON` / `JSONObject` / `JSONArray`），彻底删除了反射体系（`reader/`、`writer/`、`annotation/`、`filter/` 等 10 个包），该功能依赖的底层机制已不存在。

## 替代方案

无需替代。reader/writer 包与 Provider 注册机制已移除。树模式下仅支持 JSONObject/JSONArray/Map/List/String/Number/Boolean 等基础类型序列化，其余抛 JSONException。

## 相关文档

- [功能总览](index.md)
- [序列化/反序列化特性](features_cn.md)
- [精简说明与评估](精简评估报告.md)

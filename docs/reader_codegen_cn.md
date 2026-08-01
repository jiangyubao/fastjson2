# Reader 代码生成（已移除）

> **精简版已移除该功能。**

为 JavaBean 生成 ObjectReader 字节码/方法句柄的代码生成机制（ASM、LambdaMetafactory、ReaderCreator） 在本仓库中**已不存在**，对应代码与 API 已全部删除。

## 为什么移除

本库已裁剪为纯 JSON 树模型（`JSON` / `JSONObject` / `JSONArray`），彻底删除了反射体系（`reader/`、`writer/`、`annotation/`、`filter/` 等 10 个包），该功能依赖的底层机制已不存在。

## 替代方案

无需替代。无 JavaBean 绑定即无代码生成需求；解析由手写 switch 递归完成（JSONReader.readAny）。

## 相关文档

- [功能总览](index.md)
- [序列化/反序列化特性](features_cn.md)
- [精简说明与评估](精简评估报告.md)

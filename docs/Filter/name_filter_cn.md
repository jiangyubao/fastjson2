# NameFilter / ContextNameFilter（已移除）

> **精简版已移除该过滤器。** `com.alibaba.fastjson2.filter` 包不存在。

NameFilter / ContextNameFilter 用于在序列化时修改属性名。该功能依赖 JavaBean 序列化流程与反射体系，本库为纯树模型，无 Bean 绑定，该功能已删除。

## 替代方案

在树模型上直接处理 `JSONObject` / `JSONArray`（遍历 `entrySet()` 修改键名/键值，或 put/remove 控制输出字段）。

## 相关文档

- [过滤器总览](index_cn.md)（已移除说明）
- [功能总览](../index.md)
- [精简说明与评估](../精简评估报告.md)

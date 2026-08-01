# 序列化过滤器（已移除）

> **精简版已移除全部过滤器。**

`AfterFilter`、`BeforeFilter`、`NameFilter`、`ValueFilter`、`PropertyFilter`、`LabelFilter`、`PascalNameFilter`、`ContextNameFilter`、`ContextValueFilter`、`ExtraProcessor`、`AutoTypeBeforeHandler` 等 19 个过滤器类均已删除，`com.alibaba.fastjson2.filter` 包不存在。

## 为什么移除

过滤器用于定制 JavaBean 序列化/反序列化时的属性名与属性值，依赖反射体系。本库为纯树模型、无 Bean 绑定，过滤器失去作用对象。

## 替代方案

- 属性重命名 / 忽略：在树模型上直接操作 `JSONObject`（put / remove / keySet 遍历）
- 值转换：遍历 `JSONObject.entrySet()` 自行转换后再 put
- 多余字段处理：树模式下多余字段天然保留在 `JSONObject` 中，无需 ExtraProcessor

## 相关文档

- [功能总览](../index.md)
- [精简说明与评估](../精简评估报告.md)

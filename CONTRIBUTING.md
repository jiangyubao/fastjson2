# 参与贡献

感谢你关注 fastjson2 精简版。这是一个**纯 JSON 树模式**的单模块 Java 库,改动请保持简单、克制,并与现有代码风格一致。

## 构建

```bash
./mvnw clean package
```

产物:`core/target/fastjson2-2.0.63.jar`(同时是 OSGi bundle)。

## 测试

```bash
./mvnw test
./mvnw -pl core -Dtest=JSONTreeAPITest test   # 运行单个测试类
```

- 测试框架:JUnit 5,位于 `core/src/test/java/com/alibaba/fastjson2/`
- 新增或修改功能时,请同步补充或更新对应测试

## 代码风格

- Java 8 语法(不使用 `var` 等 Java 8 以上特性)
- 4 空格缩进,无通配符导入,import 按字母序排列
- 与所在文件的现有风格保持一致

## 提交 Pull Request

1. Fork 本仓库并新建功能分支
2. 保持 PR 小而聚焦:一个 PR 只做一件事
3. 包含测试:bug 修复附带回归测试
4. 提交信息说明"为什么改",而非只写"改了什么"
5. 确保 `./mvnw clean package` 通过

## 报告问题

请通过本仓库的 Issues 提交,并尽量包含:

- 使用的版本与 JDK 版本
- 最小可复现代码
- 预期行为与实际行为
- 相关异常堆栈

## 安全

如发现安全问题,请**私下联系维护者**,不要公开披露。

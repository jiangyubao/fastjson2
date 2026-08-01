
JSONWriter是fastjson2序列化的底层实现，针对toJSONString和toJSONBytes两种场景，会使用JSONWriterUTF8和JSONWriterUTF16两种实现。

* JSONWriterUTF16 当使用JSON.toJSONString时，缺省使用JSONWriterUTF16。
* JSONWriterUTF8 当使用JSON.toJSONBytes时，缺省使用JSONWriterUTF8，在使用JSON.toJSONString结合JSONWriter.Feature.OptimizedForAscii使用时，也会用JSONWriterUTF8实现。

```java
class JSONWriter { }

class JSONWriterUTF8 extends JSONWriter { }

class JSONWriterUTF16 extends JSONWriter { }
```

> **精简版说明**：`JSONWriter.writeAny()` 使用 instanceof 分支写入（`JSONObject` / `JSONArray` / `Map` / `List` / `String` / `Number` / `Boolean`），其余类型抛 `JSONException`；不经过任何 `ObjectWriter` Provider。`JSONWriterUTF8` / `JSONWriterUTF16` 及 JDK8/9 变体全部保留。

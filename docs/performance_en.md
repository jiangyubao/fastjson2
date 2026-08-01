# Performance Guide

This guide covers tuning strategies and best practices for this trimmed (pure tree-model) build.

## Performance Architecture

### 1. Unsafe Bulk Array Reads/Writes

The parsing/serialization core uses `Unsafe.putLong` / `getLong` to read/write 8 bytes at a time, bypassing per-element bounds checks on `bytes[i]`. This is the key acceleration for byte-level intensive work (number formatting, string escaping, field-name matching) - roughly 30-60% faster than plain per-byte loops.

- Distribution: `JSONReaderUTF8` (123 sites), `IOUtils` (70), `JSONWriterUTF8` / `JSONWriterUTF16`, ~259 sites in total
- `sun.misc.Unsafe` has existed from JDK 8 through today and is used heavily by the JDK itself; the IDE "Access restriction" is only static checking - compilation (`-XDignore.symbol.file`) and runtime are unaffected
- Test code uses public wrappers (`IOUtils.getLongLE` / `getIntLE`) instead of touching Unsafe directly

### 2. Encoding-Specialized Parsers

Dedicated implementations per encoding, selected automatically by input type:

- `JSONReaderUTF8` - optimized for UTF-8 byte streams (character-classification lookup tables)
- `JSONReaderUTF16` - optimized for UTF-16 (Java String internal representation)
- `JSONReaderASCII` - fast path for pure-ASCII content

### 3. Number Lookup Tables and Exact Parsing

- `ED` / `ED5` / `EF` constant tables: integer/float serialization without runtime computation
- `MutableBigInteger` / `Scientific`: exact double/float parsing (with ED/ED5/EF lookup tables) - no precision loss
- `Fnv`: FNV-1a 64-bit hash for field-name matching without string comparison

### 4. Hand-Written Recursive Parsing (No Reflection Dispatch)

`JSONReader.readAny()` is a hand-written switch recursion; `JSONWriter.writeAny()` uses instanceof branches - no Provider / reflection layer in between, minimal call overhead.

## Tuning Strategies

### 1. Prefer byte[] over String

**Impact: High**

```java
// Faster: parse from bytes
byte[] bytes = getJsonBytes(); // from network, files, etc.
JSONObject obj = JSON.parseObject(bytes);

// Faster: serialize to bytes
byte[] output = JSON.toJSONBytes(obj);
```

This avoids String encoding/decoding overhead and is especially effective in HTTP/RPC scenarios.

### 2. Minimize Feature Usage

**Impact: Low-Medium**

Each enabled Feature adds a conditional check in the hot path. Enable only what you need:

```java
// Good: only what's needed
String json = JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);

// Avoid: enabling many "just in case" Features
String json2 = JSON.toJSONString(obj,
    JSONWriter.Feature.PrettyFormat,        // skip if not needed
    JSONWriter.Feature.SortMapEntriesByKeys // skip if ordering doesn't matter
);
```

### 3. Use the UTF8 Fast Path for Pure ASCII

**Impact: Medium**

For pure-ASCII content, enable `OptimizedForAscii` to use the `JSONWriterUTF8` implementation:

```java
String json = JSON.toJSONString(obj, JSONWriter.Feature.OptimizedForAscii);
```

### 4. Custom Containers (Optional)

For large payloads, `JSONFactory.setDefaultObjectSupplier` / `setDefaultArraySupplier` let you customize the tree-model container implementations (e.g. pre-sized Map/List factories).

## Thread Safety

| Component | Thread-Safe? | Notes |
|------|:---:|------|
| `JSON` static methods | Yes | Main entry; no shared mutable state |
| `JSONObject` / `JSONArray` | No | Un-synchronized, like `HashMap` / `ArrayList` |
| `JSONReader` / `JSONWriter` | No | Create per operation; do not share across threads |
| `JSONFactory` static config | Yes (after config) | Configure at startup; read-only afterwards |
| util static methods | Yes | Stateless (Unsafe read-only constants) |

## JVM Tuning

### Recommended JVM Flags

```
# Enable compact strings (JDK 9+, default on)
-XX:+CompactStrings
```

### Memory Notes

- Decimals are stored as `BigDecimal` by default (precision first); consider `UseDoubleForDecimals` for memory-sensitive large payloads.
- `JSONObject` is a `LinkedHashMap` preserving insertion order; customize containers if ordering is not needed.

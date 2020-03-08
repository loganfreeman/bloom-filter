Usage
---

```java
// Create a new bloom filter optimized for containing 100 elements and using 1024 bits of memory
BloomFilter f = new BloomFilter(100, 1024);

// Add elements to the filter
// it uses Object.hashCode() internally, so you can add objects of any type
f.add("hello");

// Check if an element is in the filter
f.contains("hello"); // true
f.contains("hello, world!"); // false
```
### Usage
#### 1. Define a dependency
```java
JarDependency dependency = new JarDependency(
        "http://repo.exlll.de/configlib-core-2.2.0.jar",
        Paths.get("dependency.jar")
).addDigest("MD5", "4f8245a58ac12c735d0b1f9ca42a0abe")
 .addDigest("SHA-256", "6b598e77d1671...111373f9bcd10d");
```

#### 2. Download the jar

```java
JarLoader.download(dependency);
// or
JarLoader.downloadIfNotExists(dependency);
```

#### 3. Load the jar

```java
URLClassLoader loader = ...
JarLoader.load(dependency, loader);
```
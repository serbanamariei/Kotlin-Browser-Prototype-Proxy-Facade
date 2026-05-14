# 🧒 Kids Browser

A parental-control HTTP browser implemented in Kotlin, built using three design patterns: **Prototype**, **Proxy**, and **Facade**. Developed as part of Lab 8 — Design Patterns in Kotlin.

---

## Project Structure

```
.
└── Main.kt        # All classes and entry point
```

---

## Requirements

- Kotlin (JVM)
- Maven
- khttp library

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.jkcclemens</groupId>
        <artifactId>khttp</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

---

## How to Run

```bash
mvn compile
mvn exec:java -Dexec.mainClass="MainKt"
```

---

## How It Works

The application chains three design patterns together to build a simple browser with parental controls.

### Pattern 1 — Prototype (`GenericRequest`)

The **Prototype** pattern allows creating new objects by cloning an existing one instead of constructing from scratch. Here, `GenericRequest` implements `Cloneable` and overrides `clone()`. This means a base request (URL + optional params) can be reused and cloned to create variations without re-specifying all fields every time.

```kotlin
val model = GenericRequest("https://www.google.com")
val copy = model.clone() // new object, same data
```

### Pattern 2 — Proxy (`CleanGetRequest`)

The **Proxy** pattern wraps a real object and controls access to it. `CleanGetRequest` acts as a proxy around `GetRequest`: before forwarding the HTTP call, it checks whether the requested URL is on the parental control blocklist. If it is, access is denied with an exception. If not, the real `GetRequest` handles it normally.

This separates the concern of content filtering from the actual HTTP logic — `GetRequest` knows nothing about parental controls.

```kotlin
class CleanGetRequest(val getReq: GetRequest, val parentalControlDisallow: List<String>): HTTPGet {
    override fun getResponse(): Response {
        if (parentalControlDisallow.contains(getReq.genericReq.url)) {
            throw Exception("Access denied for ${getReq.genericReq.url}")
        }
        return getReq.getResponse()
    }
}
```

### Pattern 3 — Facade (`KidsBrowser`)

The **Facade** pattern provides a simple, unified interface over a complex subsystem. `KidsBrowser` hides all the internal wiring (GenericRequest → GetRequest → CleanGetRequest) behind a single `start()` method. The caller doesn't need to know about proxies, cloning, or HTTP details — they just call `start()` and get the result.

```kotlin
val browser = KidsBrowser(CleanGetRequest(GetRequest(5000, model), blocklist), null)
browser.start()
```

---

## Example Output

```
Test 1:
Status pagina: 200
Continut: <!doctype html>...

Test 2:
Eroare: acces interzis pentru site ul www.tiktok.com
```

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin (JVM) |
| Build Tool | Maven |
| HTTP Client | khttp 0.1.0 |
| Design Patterns | Prototype, Proxy, Facade |

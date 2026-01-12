# HTTP Chaos Proxy (Fault Injection Gateway)

## Overview

HTTP Chaos Proxy is a lightweight **reverse proxy written in pure Java** that sits in front of an upstream service and transparently forwards HTTP traffic while providing a foundation for **fault injection and resilience testing**.

The project is inspired by chaos engineering principles and is designed as a **learning-focused yet production-inspired system** to understand how real-world network failures affect distributed systems.

At its current stage, the proxy functions as a clean reverse proxy. Chaos behaviors (latency, failures, malformed responses, etc.) will be incrementally introduced in later stages.

---

## Why This Project Exists

Modern backend systems fail not because of bad business logic, but due to:

- Network instability
- Slow or unreliable dependencies
- Partial outages
- Cascading failures

Most applications are tested only under _ideal conditions_.

This project exists to **simulate non-ideal conditions** _outside_ the application, allowing engineers to:

- Test client-side resilience
- Validate retry, timeout, and fallback logic
- Observe failure behavior without modifying application code

---

## Architecture (Current)

- Clients send requests to the **Chaos Proxy**
- The proxy forwards requests to the upstream service
- Responses flow back through the proxy to the client
- Clients are unaware of the upstream server

---

## Current Features

- Accepts incoming HTTP requests
- Forwards method, path, headers, and body to upstream server
- Relays upstream responses back to the client after injecting random faillure and delay to requests
- Delay Injection
- Intentional Failure

## Tech Stack

- **Java 21**
- **Maven**
- `com.sun.net.httpserver.HttpServer`
- `java.net.http.HttpClient`
- Java concurrency (`Executors`, thread pools)

No frameworks (Spring Boot, Netty, etc.) are used to keep the behavior explicit and transparent.

---

## How to Run

### 1. Start the Upstream Server

```bash
Run UpstreamServer.java
Expected output:

Upstream server started on http://localhost:9090

2. Start the Chaos Proxy
Run ChaosProxyServer.java


Expected output:

Chaos proxy running on http://localhost:1234

3. Test via Proxy
curl http://localhost:1234/health
curl http://localhost:1234/echo
curl http://localhost:1234/headers
curl http://localhost:1234/slow



Planned Enhancements:

Header corruption

Percentage-based chaos rules

YAML/JSON-based configuration

Request-level chaos policies

Observability (metrics + logs)

Deterministic vs probabilistic chaos

#Note

Every module has its own doc and readme files you can have a look at those to understand the need of every functionality that is added and a high level idea of how request flows.
```

# DNS Client - ECSE316

A simple DNS client implemented in Java for **ECSE 316 Assignment 1 (Fall 2025)**.
The client constructs DNS query packets, sends them via UDP, and parses responses for A, MX, and NS records.

Implemented by: **Mathis Bélanger (261049961) & Tarek Namani (261085655)**

## Usage

### Compile

From the project root:

```bash
javac -d . src/*.java
```

### Run

```bash
java DnsClient [options] @server name
```

### Arguments

- `-t timeout` : seconds to wait before retransmit (default 5)
- `-r max-retries` : maximum retransmits (default 3)
- `-p port` : UDP port of DNS server (default 53)
- `-mx` or `-ns` : query type (default is A)
- `@server` : IPv4 address of the DNS server (required)
- `name` : domain name to query (required)

### Examples

```bash
java DnsClient @8.8.8.8 www.mcgill.ca
java DnsClient -t 10 -r 2 -mx @8.8.8.8 mcgill.ca
```

---

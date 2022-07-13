# 🕰️ Clocky 
[![Run tests](https://github.com/mthmulders/clocky/workflows/Run%20tests/badge.svg)](https://github.com/mthmulders/clocky/actions/workflows/main.yml?query=branch%3Amaster)
[![SonarCloud quality gate](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_clocky&metric=alert_status)](https://sonarcloud.io/dashboard?id=mthmulders_clocky)
[![SonarCloud vulnerability count](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_clocky&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=mthmulders_clocky)
[![SonarCloud technical debt](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_clocky&metric=sqale_index)](https://sonarcloud.io/dashboard?id=mthmulders_clocky)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=mthmulders/clocky)](https://dependabot.com)
[![Mutation testing badge](https://img.shields.io/endpoint?style=plastic&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmthmulders%2Fclocky%2Fmaster)](https://dashboard.stryker-mutator.io/reports/github.com/mthmulders/clocky/master)
[![Maven Central](https://img.shields.io/maven-central/v/it.mulders.clocky/clocky.svg?color=brightgreen&label=Maven%20Central)](https://search.maven.org/artifact/it.mulders.clocky/clocky)

## 🚀 TL;DR
Clocky is a test stub for the [`java.time.Clock` class](https://docs.oracle.com/javase/8/docs/api/index.html?java/time/Clock.html) introduced with JSR-310 in Java 8.
It lets you control how time flies in your tests.

## 📖 The longer story
Starting with Java 8, Java has a new class: [`java.time.Clock`](https://docs.oracle.com/javase/8/docs/api/index.html?java/time/Clock.html).
This class provides access to the current instant, date and time using a time-zone.

Previously, Java programmers would use [`System.currentTimeInMillis`](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#currentTimeMillis--).
Since that is a static method, it's hard to replace it with a stub for testing purposes.

The `Clock` class solves this by providing an instance method, [`millis`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#millis--).
It is equivalent - and in fact delegates to - calling `System.currentTimeInMillis`.
Apart from `millis()`, the `Clock` class provides other valuable methods such as [`instant()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#instant--) which returns the same value, wrapped in an instance of [`Instant`](https://docs.oracle.com/javase/8/docs/api/index.html?java/time/Instant.html).

Since `millis()` and `instant()` are both instance methods of the `Clock` class, it becomes easier to replace those calls with a test stub.

### 📦️ Default implementations 
The `Clock` class is an abstract class, and Java ships with a few implementations:

* `SystemClock`, returned by [`Clock.system()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#system-java.time.ZoneId-), [`Clock.systemDefaultZone()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#systemDefaultZone--) and [`Clock.systemUTC()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#systemUTC--).
This clock returns the current instant using best available system clock, usually by calling `System.currentTimeInMillis`.
This is the type that you would typically use in your application.
* `FixedClock`, returned by [`Clock.fixed()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#fixed-java.time.Instant-java.time.ZoneId-).
As the name suggests, this clock always returns the same instant.
* `OffsetClock`, returned by [`Clock.offset()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#offset-java.time.Clock-java.time.Duration-).
This clock adds an offset to an underlying clock - which is why you need a second clock to act as the "base" time.
* `TickClock`, returned by [`Clock.tick()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#tick-java.time.Clock-java.time.Duration-), [`Clock.tickMinutes()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#tickMinutes-java.time.ZoneId-) and [`Clock.tickSeconds()`](https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html#tickSeconds-java.time.ZoneId-).
This clock returns instants from the specified clock truncated to the nearest occurrence of the specified duration.

### 🧪 Testing

When it comes to testing, often it doesn't matter what the exact time is during a test.
But there are cases when it matters a lot.
Let's say you have code that measures how long a method invocation takes - useful for monitoring purposes.

```java
final long start = System.currentTimeInMillis();
// ... actual method invocation
final long end = System.currentTimeInMillis();
final long duration = end - start;
```

In such a case, you want to control **exactly** how much time passes between the two invocations of `System.currentTimeInMillis`.

If we add an instance variable of type `Clock`, and make sure to provide for an implementation, we could rewrite that code:

```java
final Instant start = clock.instant();
// ... actual method invocation
final Instant end = clock.instant();
final Duration duration = Duration.between(start, end);
``` 

It's clear to see that this code is way easier to test than the previous version.
Unfortunately, the default implementations of `Clock` do not include a version that is suitable for this scenario.

* A _Fixed Clock_ doesn't progress, so it's unsuitable.
* The _System Clock_ does progress, but it's not controllable. This means you cannot predict the value of `duration`.
* The _Offset Clock_ and the _Tick Clock_ are both based on another clock, so they need either of the above, which are both unsuitable.

**This is where Clocky 🕰️ comes in.**

Clocky gives you a `Clock` that you control very precisely from your tests.

```java
final long base = System.currentTimeMillis(); // could be any value
final AtomicReference<Instant> instant = new AtomicReference<>();
final Clock clock = new ManualClock(instant::get);

// create system under test, passing clock along.

// invoke system under test
instant.set(Instant.ofEpochMilli(base));
// invoke system under test
instant.set(Instant.ofEpochMilli(base + 10));
// invoke system under test

// verify system under test to see the duration is indeed 10 millis
```

## ⚖️ License
Clocky is licensed under the Apache License, version 2.
See [**LICENSE**](./LICENSE) for the full text of the license.

## 🛠️ Contributing
Do you have an idea for Clocky, or want to report a bug?
All contributions are welcome!
Feel free to [file an issue](https://github.com/mthmulders/clocky/issues/new) with your idea, question or whatever it is you want to contribute.

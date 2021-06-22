# Specrunner
[![CI with Maven](https://github.com/thiagolvlsantos/specrunner/actions/workflows/maven.yml/badge.svg)](https://github.com/thiagolvlsantos/specrunner/actions/workflows/maven.yml)
[![CI with Sonar](https://github.com/thiagolvlsantos/specrunner/actions/workflows/sonar.yml/badge.svg)](https://github.com/thiagolvlsantos/specrunner/actions/workflows/sonar.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=thiagolvlsantos_specrunner&metric=alert_status)](https://sonarcloud.io/dashboard?id=thiagolvlsantos_specrunner)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=thiagolvlsantos_specrunner&metric=coverage)](https://sonarcloud.io/dashboard?id=thiagolvlsantos_specrunner)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thiagolvlsantos/specrunner/badge.svg)](https://repo1.maven.org/maven2/io/github/thiagolvlsantos/specrunner/)

## Testing for everyone

For a quick overview checkout this presentation **[Specrunner](https://github.com/thiagolvlsantos/specrunner/blob/master/specrunner/ppts/SpecRunner.pdf)** and also for each module you can access the **[examples projects](https://github.com/thiagolvlsantos/specrunner/tree/master/userguide)** using different modules.

Specrunner is a ATDD testing tool where you can write your acceptance tests using different styles and formats its up to you, **we wont judge you**. For example, you can instrument the same ATTD test to perform on GUI or to perform in bussiness controllers, for the stakeholders it is transparent. 

## Usage

Include latest version [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thiagolvlsantos/specrunner/badge.svg)](https://repo1.maven.org/maven2/io/github/thiagolvlsantos/specrunner/) to your project.

```xml
		<dependency>
			<groupId>io.github.thiagolvlsantos</groupId>
			<artifactId>specrunner-MODULE</artifactId>
			<version>${latestVersion}</version>
		</dependency>
```


## Changelog

### 1.5.18
 - Filtering tests scenarios by using flags: ``-DfilterTests=regression,ui,etc...``
 [#97](https://github.com/thiagolvlsantos/specrunner/issues/97)  

**IMPORTANTE NOTE**: Due to Sonatype changes **the Specrunner groupId had to change to ``io.github.thiagolvlsantos``**. 

To use this new version replace **groupId** by:

```xml
    <groupId>io.github.thiagolvlsantos</groupId>
    <artifactId>specrunner-XXX</artifactId>
    <version>1.5.18</version>
```
Java packages inside the library have not changed! The only expected change to your project is the ``groupId`` reference. I decided to **not change major version** because running code is not affected, I mean, there is a backward compatibility.

See [full changelog](https://thiagolvlsantos.github.io/specrunner/CHANGELOG.md).

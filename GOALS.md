# Goals

The main goal of the project is to design and implement a tool for
evaluating performance unit test results, suitable for integration
into common CI/CD pipelines. In detail:

- Support multiple performance unit test frameworks
    - [OpenJDK JMH](https://github.com/openjdk/jmh)
    - [BenchmarkDotNet](https://github.com/dotnet/BenchmarkDotNet)
    - [Rust Criterion](https://docs.rs/criterion)
    - [Haskell Criterion](https://hackage.haskell.org/package/criterion)
    - [Go testing](https://pkg.go.dev/testing)
    - more ?

- Produce output compatible with other unit test tools for CI/CD pipelines
    - [Open Test Reporting](https://github.com/ota4j-team/open-test-reporting)
    - [JUnit](https://junit.org)
    - [NUnit](https://nunit.org)
    - [TestNG](https://testng.org)
    - [Allure](https://github.com/allure-framework)

- Manage measurement storage
    - Local measurement storage for quick testing (for example on developer machines)
    - Shared measurement storage for cooperative projects (testing on dedicated CI/CD machines)

- Permit interactive browsing of collected measurements

- Use statistical evaluation of measurements
    - Reporting with user defined false positive error rate (possibly family wise)
    - Recognizing insufficient measurements for given effect size (and possibly collecting additional measurements)

# Requirements

- Minimum installation and integration requirements in typical CI/CD environments
- Integration with common source control repositories (perhaps only git ?)

# Scenarios

TODO This should elaborate on specific usage scenarios.

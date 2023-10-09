# Meeting Notes 2023-09-05

General design
- Mere list of timestamps is not enough because it does not keep runs separate
- Not sure if converting throughput to time is always correct (or straightforward)
- Information should mostly flow through arguments (avoid side effects)
- Converting exceptions to (typically boolean) error codes is bad
    - Correct solution is propagating exceptions upwards
    - Lower layers should report situations they cannot resolve
    - Upper layers should react appropriately
    - Maybe even use own checked exceptions ?
- Consider using a logging framework
- Read https://testing.googleblog.com/2008/08/by-miko-hevery-so-you-decided-to.html

Code impression

- `CacheDatabase`
    - Creating queue in constructor not a good idea (why is it done ?)
    - Path manipulation should use `java.nio` libraries instead of string operations
    - Computing paths inside this class is not good (complete paths should be provided from the outside)
    - In general constructors should not do complex work

- `CLIFlags`
    - Use CLI parsing library ?
    - Just remove this and do decent top down information flow
    - Command line should be parsed in one place and never leak into other code
    - As a result having global constants with command line fragments will become mute

- `ComparatorFactory`
    - Move comparators for testing into testing package
    - Avoid type switching in code
    - Avoid enums for types

- `ConfigManager`
    - Make this generic and use standard ini file format
    - Avoid having individual methods for individual configuration options

- `DatabaseItem`
    - Should be able to serialize itself (rather than be serialized from the outside)
    - Is the name expressive ?

- `Main`
    - Consider having class per command ?
    - Think about appropriately placing things based on their abstraction level
    - For example consider moving the test of installation validity

- `MeasurementComparisonResult`
    - Result should be a result and not run a comparison operation
    - Having `getLastComparisonResult` is strange
    - Use a record ?
    - Strange relationship with `IMeasurementComparisonResult` and `ComparisonResult` and `Verdict` ?
    - Suddenly returning `double` instead of `UniversalTimeUnit` (or other suitable metric container) ?

- `PerfEvalInitializer`
    - Use try with resources
    - Propagate exceptions towards top level
    - How would this code be tested when it reads data from global variables ?

- `ResultPrinter`
    - Not sure whether this class hierarchy is reasonable
    - Should use Strategy Pattern (interface plus printing backends)
    - Using hardcoded indices in tables leads to fragile code
    - Use templating engine

- `UniversalTimeUnit`
    - What about using `java.time.Duration` ? (but see below)
    - Should be a data object (no setters, immutable content)
    - No need to convert to nanoseconds (simply remember value and unit)
    - Think what you will need
        - Computations do not need to know units (just check for compatibility)
        - Maybe think about supporting generic metrics (not just time)

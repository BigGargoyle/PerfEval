# General

- use function arguments to pass data from top-level (management) methods
  to bottom-level (worker) methods instead of using globals!
- do not perform work in constructors
- do not convert exceptions to error codes
  - log errors
- associate code with classes instead of type-switching
- separate levels of abstraction and responsibilities
  - do not mix I/O and reporting with computation work
  - simplifies testing
- use proper libraries and modern APIs (CLI parsing, `java.nio.file`)


# Distractions

- code in `org.example` package
- camelCase in package names

- packages used for code organization, not as units of reuse
  - there should be a single package for each artefact

- naming inconsistent with Java conventions
  - drop `I` prefix from interfaces and use descriptive name
    in concrete implementation classes

- formatting
  - if/else legs on single line without block



# Details

## package `.`

### class `Main`

- consider using a CLI library, e.g., `jopt-simple` or similar
  - command line stays in `Main`

- `System.exit()` called from too many places
  - only `main()` should do it to keep control over `System.exit()`

- static methods `*Handle(args)` should really be handler classes
- type switching on `CLICommands`


## package `.evaluation`

### interface `IMeasurementComparisonResult`

- why is there an interface if there is only a single implementation?
- difference between `IMeasurementComparisonResult` and `ComparisonResult`?
  - what purpose does `getComparisonVerdict()` serve then?
- why there are things related to metrics in that interface?
  - is average the only metric to be considered?

- avoid shortened method names `getOldAvg()`


### class `MeasurementComparisonResult`

- **all the work is done in constructor!**
- most methods just return data, the class provides no behavior
- `resolveTestVerdict()`
  - meaning unclear (returns `boolean`)
  - does not receive any arguments


### class `ResultPrinter`

- type switching on `ComparisonResult`
- hard-coded row numbers


## package `.perfevalCLIEvaluator`

### class `PerfEvalEvaluator`

- `evaluateLastResults()`
  - mixed responsibilities/level of detail
  - database operations, checks of all sorts, args processing
  - type-switching on `jsonOutputFlag`

- `setupExitCode()`
  - unclear purpose (it may/may not call `System.exit()`)  
  
- `filter()`
  - unclear purpose (what is being filtered)
  - mixed level of detail (for loop, switch)
  - type-switching


## package `.perfevalConfig`

### class `ConfigManager`

- purpose unclear, what does it manage?
- should use CLI parsing library
- type switching


## package `.perfevalInit`

### class `IniFileData`

- non-final class, public read/write attributes 
- does work (I/O) in constructor!

- constructor doing two different things based on boolean flag!
  - the flag is unreadable at the call site!
  - constructor should only initialize instance variables
  - use static factory method to do work and then create instance with results

- `readDataFromIniFile()` is too complex
  - the name suggests working with .ini files, but that is not the case
  - the method has no arguments and does not return anything
  - the method should get an argument to read from instead of using globals!
  - use `Paths` API to compose paths instead of concatenating strings
    - the caller should is responsible for providing the final path
  - avoid early returns

- avoid signalling through `validConfig` instance variable

- do not convert exceptions to `boolean` error state
  - ideally convert them to subsystem exceptions, chain the original causes
  - handle exceptions in the upper layers responsible for business logic
    - consider using checked exceptions in lower layers
  
  
### class `PerfEvalInitializer`

- method names inconsistent with Java conventions
- methods do not receive arguments and work with globals!
- there should be a class responsible for directory layout
  - avoid creating paths in low-level methods (management concern)
  - separate I/O (file management) and content creation
- split-off filesystem utility functions into separate class

  
## package `.globalVariables`

- there should be no need for a package like this!
  - keep things as close as possible to whoever is using them, unless
    they **really** need to be visible globally
  - pass arguments to methods instead of having them access globals
  
- non-final classes
- classes mostly/only contain strings

- names inconsistent with java conventions
 
### class `CLICommands`

- subject to type-switching in `Main.main()`
- replace with handler classes
- command names should be part of CLI parser setup in `Main.main()`

### class `CLIFlags`

- option names should be part of CLI parser setup in `Main.main()`

### enum `ExitCode`

- consider providing `exit()` method instead of `getExitCode()`
- enum instances should be CamelCased



## package `.measurementFactory`

### interface `IMeasurementParser`

- `getTestsFromFile()`
  - returns `List<Measurement>`, not tests
    - would lazy loading make sense (streaming measurements)?
    - is `List` enough?
  - should the parser be interested in files?
    - if yes, then it's really a "loader"
  - is a single file always going to be enough?

- `GetParserType()` really returns a printable name, not a type


### class `UniversalTimeUnit`

- why store nanoseconds?
  - keep magnitute and `TimeUnit`

- `setTime()`
  - it should be enough to perform conversions on demand
  - `TimeUnit` can convert to other units
  - why recursion?

- it's not really time unit, but duration
  - there might be a suitable Java class (`Duration`)


## package `.measurementFactory.JMHJSON`

### class `JMHJSONParser`

- initialize variables at declaration
  - nest code properly if necessary
- return empty collections instead of `null`
  - use `Optional<T>` if you need to indicate missing result
  

## package `.resultDatabase`

### class `DatabaseItem`

- use name from problem domain
  -  if this was relational DB, there would be table name with rows

### class `CacheDatabase`

- **performs work in constructor!**
- 
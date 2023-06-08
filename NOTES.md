# Assorted Notes

## On Data Storage

- How to preserve data ?
    - Alternatives
        - In original format (e.g. JSON from JMH or Benchmark.NET)
        - In common format
    - Discussion
        - We certainly want to preserve the original format because "what if the user needs it for something else"
        - We might need to _also_ store common format for processing speed
            - This can be done on demand only when the data is needed
            - At this time we do not plan to do this
            - It can be done elegantly as a cache
        - Navigating through data in original format ?
            - Can the original format store
                - multiple benchmarks in one file ?
                - multiple runs in one file ?
                - ...
                - and should we support combinations ?
            - How can we quickly collect metadata (version time and such) of all available outputs ?
                - If the user runs benchmarks using our commands we will update metadata information quickly
                - We should give the user the option of rescanning original files when needed
            - And what metadata is likely missing from the original output ?
                - Version for sure ?
                - Can be stored in directory name instead ?
                - What about reconstructing version from git log ?
                    - Take result file creation timestamp
                    - Assign latest version that existed at that time

- What should be the minimum common data structure ?
    - Measurements are simply sequences of numbers
        - Here a sequence comes from one run (one execution of a benchmark from start to finish)
        - The ordering is importand (for example for warm up filtering)
    - We can have multiple such sequences for one version (multiple runs)
        - Each run can have different number of samples
        - Needed for benchmarks whose performance changes between runs
    - We can collect multiple metrics (e.g. wall clock time vs thread time vs memory usage ...)
    - ...

## Use Cases

### Directory Per Version Storage

User runs benchmarks from his own scripts and stores results in `.performance/<hash>/<benchmark-time>.json` files.
After a while, we have a directory structure that looks something like this:

```
.performance/58d397fd5a0093f9449cdfada9f6f8e2f119df62/2022-01-01T00:00:23Z.json
.performance/58d397fd5a0093f9449cdfada9f6f8e2f119df62/2022-01-01T00:01:23Z.json
.performance/58d397fd5a0093f9449cdfada9f6f8e2f119df62/2022-01-01T00:02:33Z.json
...
```

To work with this structure, we first have to import the existing data, using a command like this:

`my-best-importer .performance --get-version-hash-from-dir-name --get-version-date-from-git-log`

or ?

`my-best-importer --template ".performance/${HASH}/${DATE}.json"`

Afterwards, we want to update the imported metadata with new files.
To avoid rescanning all directories and files, we include an option
that limits the scan to recent directories:

`my-best-importer .performance --get-version-hash-from-dir-name --get-version-date-from-git-log --max-path-age 1w`


## Osobní poznámky
-   kromě JSON formátu jsem uvažoval také XML
-   ukládání dat
    -   metadata souboru se při kopírování mění (není možné využít čas vytvoření souboru pro ukládání verzí)
            datum změnění souboru se ovšem nemění? -> dalo by se využít ...

-   ukládání spoják vs. strom
    
    Nakonec nechci ukládat data do stromu, protože se hůře implementuje a počet vnitřních uzlů je lineárně závislý na počtu koncových uzlů. Proto budu chtít použít jednu složku a cache. Implementaci bude možné kdykoli zlepšit, ale pro začátek vypadá dobře.

-   společná data 
    -   bezrozměrná jednotka (naměřené hodnoty)
    -   jendotka vs. bezrozměrné číslo?
    -   jmeno stroje
    -   verze benchmarkovaciho SW
    -   verze OS
    -   jmeno benchmarku
    -   percentily?


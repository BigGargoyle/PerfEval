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

# PerfEval: Marrying Unit Testing with Performance Evaluation

For more than two decades, unit testing helps maintain software quality in
agile software development processes. Frameworks such as JUnit or NUnit were
introduced to help implement and execute unit tests to assess software
functionality, and continuous integration platforms such as GitHub or GitLab
support executing the tests and reacting to failures.

Similar to functional unit tests, microbenchmarks are used to assess
software performance. Frameworks such as JMH or BenchmarkDotNet help
implement performance microbenchmarks in a manner similar to unit tests,
however, subsequent reporting and evaluation often involves manual
assessment of measurement results.

The goal of the thesis is to implement a tool that will automate reporting
and evaluation of microbenchmark measurement results to the degree that the
overall developer experience, in particular in local development activities
and environments such as GitHub or GitLab, will be analogous to that of
using common unit testing frameworks when evaluating functionality.
The tool should integrate with common microbenchark frameworks.

Po více než dvacet let pomáhají jednotkové testy udržovat kvalitu kódu v průběhu vývoje softwaru. Knihovny jako JUnit nebo NUnit byly vyvinuty, aby pomohly s jednodušší implementací jednotkových textů, jejich spouštěním a s platformami pro kontinuální integraci jako jsou GitHub nebo GitLab, které podporují spouštění testů a umí reagovat na jejich selhání.

Podobné jednotkovým testům jsou knihovny (rámce) určené k posuzování výkonu softwaru. Knihovny jako JMH nebo BenchmarkDotNet pomáhají implementovat výkonostní testy podobným způsobem jako knihovny pro implementaci jednotkových testů. Vyhodnocování výsledků výkonostních testů ale obvykle zahrnuje i ruční zhodnocení naměřených výsledků.

Cílem práce je tedy implementovat nástroj, který umožní automatické hlášení a vyhodnocování výsledků výkonostních testů tak, aby práce s nástrojem byla při lokálním vývoji a v prostředích jako je GitHub nebo GitLab podobná jako běžné požívání knihoven pro práci s jednotkovými testy, když vyhodnocují funkcionalitu softwaru. Nástroj by měl být schopný interagovat s běžnými nástroji pro vyhodnocování výkonu.

[1] JUnit, https://junit.org
[2] NUnit, https://nunit.org
[3] JMH, https://github.com/openjdk/jmh
[4] BenchmarkDotNet, https://github.com/dotnet/BenchmarkDotNet
[5] Criterion, https://docs.rs/criterion/latest/criterion
[6] L. Bulej, T. Bureš, V. Horký, J. Kotrč, L. Marek, T. Trojánek, P. Tůma: Unit Testing Performance with Stochastic Performance Logic, in Automated Software Engineering 24(1), pp. 139-187, 2017, DOI: 10.1007/s10515-015-0188-0
[7] P. Stefan, V. Horký, L. Bulej, P. Tůma: Unit Testing Performance in Java Projects: Are We There Yet?, in Proc. 8th ACM/SPEC Intl. Conf. on Performance Engineering (ICPE), pp. 401–412, 2017,ISBN: 978-1-4503-4404-3, DOI: 10.1145/3030207.3030226

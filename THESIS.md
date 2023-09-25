# PerfEval: Spojení unit testů s vyhodnocováním výkonu

Po více než dvacet let pomáhají unit testy udržovat kvalitu kódu v průběhu vývoje softwaru. Knihovny jako JUnit nebo NUnit byly vyvinuty pro usnadnění implementace a spouštění unit testů pro ověřování funkčnosti softwaru a verzovací nástroje jako GitHub a GitLab podporují spouštění těchto testů a mohou reagovat na případné neúspěchy.

Podobné unit testům jsou prostředí určená k posuzování výkonu softwaru. Prostředí jako JMH nebo BenchmarkDotNet pomáhají implementovat výkonostní testy podobným způsobem jako knihovny pro implementaci unit testů. Vyhodnocování výsledků výkonostních testů ale obvykle zahrnuje i ruční zhodnocení naměřených výsledků.

Cílem práce bude implementovat nástroj, který umožní automatické hlášení a vyhodnocování výsledků výkonostních testů tak, aby práce s nástrojem, konkrétně při lokálním vývoji a požívání prostředí jako je GitHub a GitLab, byla analogická používání běžných unit testovacích prostředí při vyhodnocování funkčnosti softwaru. Nástroj by měl být schopný interagovat s běžnými prostředími pro vyhodnocování výkonu.

[1] JUnit, https://junit.org
[2] NUnit, https://nunit.org
[3] JMH, https://github.com/openjdk/jmh
[4] BenchmarkDotNet, https://github.com/dotnet/BenchmarkDotNet
[5] Criterion, https://docs.rs/criterion/latest/criterion
[6] L. Bulej, T. Bureš, V. Horký, J. Kotrč, L. Marek, T. Trojánek, P. Tůma: Unit Testing Performance with Stochastic Performance Logic, in Automated Software Engineering 24(1), pp. 139-187, 2017, DOI: 10.1007/s10515-015-0188-0
[7] P. Stefan, V. Horký, L. Bulej, P. Tůma: Unit Testing Performance in Java Projects: Are We There Yet?, in Proc. 8th ACM/SPEC Intl. Conf. on Performance Engineering (ICPE), pp. 401–412, 2017,ISBN: 978-1-4503-4404-3, DOI: 10.1145/3030207.3030226

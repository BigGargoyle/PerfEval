# Performance Unit Test Evaluator (PUTE)
## Cíl projektu
Cílem práce je naprogramovat aplikaci, která by umožňovala vyhodnocovat výsledky výkonostních testů, které jsou spouštěny nad kódem uživatelů aplikace. Aplikace je tedy určena pro programátory a má za úkol pomáhat s vývojem jejich aplikací.

## Detailní specifikace
### Hlavní úkol
Aplikace bude umět zpracovávat výsledky testů běžně užívaných benchmarkovacíh frameworků. Základní podporované frameworky budou *OpenJDK JMH* a *BenchmarkDotNET*. Aplikace bude umět převzít výstupy těchto frameworků a porovnat je s předchozí verzí. PUTE bude možné mít zařazen v běžném GitLab CI/CD, takže po každém commitu bude možné, aby v důsledku toho selhala pipeline. Aplikaci bude možné spouštět i z konzole a nechat ji porovnat poslední verze. Dále bude možné spustit aplikaci tak, aby vygenerovala grafický výstup, kde by bylo porovnáno více posledních verzí.

## Používání aplikace
Vývojář softwaru se rozhodne vyvíjet nový software. Naprogramuje nějakou část svého programu a k němu pomocí benchmark frameworku, který je podporován PUTE, naprogramuje výkonnostní testy. Uživatel si stáhne instalační balíček a PUTE pomocí příkazu `./pute-installater.sh` nainstaluje. Bude se jednat o instalančí skript samotného PUTE. V průběhu instalance bude uživatel požádán o zadání příkazu, pomocí kterého spouští své performance testy. Nadále je uživatel bude spouštět příkazem `pute start-tests`.

?? Uživatel si po instalaci bude moci dokonfigurovat konfigurační soubor `pute-config.json` (nějaká forma úpravy příkazu pro spouštění testů)



### 1 programátor

### Větší projekt

## Způsob ukládání dat
TODO: kde a jak ukládat data???, formát??

## Shrnutí
Soubory
-   pute-installer.sh
-   pute-config.json

Příkazy
-   pute start-tests
-   

Podporované benchmarky
-   BenchmarkDotNET
-   OpenJDK JMH
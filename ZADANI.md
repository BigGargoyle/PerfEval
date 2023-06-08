# Performance Unit Test Evaluator (PUTE)
## Cíl projektu
Cílem práce je naprogramovat aplikaci, která by umožňovala vyhodnocovat výsledky výkonostních testů, které jsou spouštěny nad kódem uživatelů aplikace. Aplikace je tedy určena pro programátory a má za úkol pomáhat s vývojem jejich aplikací.

## Detailní specifikace
### Hlavní úkol
Aplikace bude umět zpracovávat výsledky testů běžně užívaných benchmarkovacíh frameworků. Základní podporované frameworky budou *OpenJDK JMH* a *BenchmarkDotNET*. Aplikace bude umět převzít výstupy těchto frameworků a porovnat je s předchozí verzí. PUTE bude možné mít zařazen v běžném GitLab CI/CD, takže po každém commitu bude možné, aby v důsledku toho selhala pipeline. Aplikaci bude možné spouštět i z konzole a nechat ji porovnat poslední verze. Dále bude možné spustit aplikaci tak, aby vygenerovala grafický výstup, kde by bylo porovnáno více posledních verzí.

PUTE bude naprogramovaný v programovacím jazyce JAVA a bude doplněn o podpůrné skripty napsané v bashi. Předpokládám, že zpracování příkazové řádky přenechám bashovskému skriptu, který následně spustí vhodný podprogram napsaný v jazyce JAVA. Je ale možné, že celá implementace bude provedena v programovacím jazyce JAVA.

### Používání aplikace
Vývojář softwaru se rozhodne vyvíjet nový software. Naprogramuje nějakou část svého programu a k němu pomocí benchmark frameworku, který je podporován PUTE, naprogramuje výkonnostní testy. Uživatel si stáhne instalační balíček a PUTE pomocí příkazu `./pute-installater.sh` nainstaluje. Bude se jednat o instalančí skript samotného PUTE. V průběhu instalance bude uživatel požádán o zadání příkazu, pomocí kterého spouští své performance testy. Nadále je uživatel bude spouštět příkazem `pute start-tests`. O výsledky testů se postarají skripty PUTE. Výsledek testu uloží do vnitřní adresářové struktury schované ve složce 
`.performance`. 

?? Uživatel si po instalaci bude moci dokonfigurovat konfigurační soubor `pute-config.json` (nějaká forma úpravy příkazu pro spouštění testů)

Pokud se uživatel rozhodne, že chce sám vidět jak se změnil výkon mezi posledními dvěma verzemi, pak použije příkaz `pute evaluate`. Příkaz spustí porovnání posledních dvou verzí a vypíše výsledek na standardní výstup. Takový výstup může vypadat následovně:

```
TEST_NAME		|	NEW_TIME (ms)	|	LAST_MEASURED_TIME (ms)	|	CHANGE (%)
PERFORMANCE_TEST_1	|	50		|		20		|	-60
PERFORMANCE_TEST_2	|	60		|		80		|	+33
PERFORMANCE_TEST_3	|	20		|		21		|	+5 !!! (Insufficient data)
```

V případě, že se uživatel bude chtít podívat na porovnání více posledních verzí, pak požije příkaz `pute evaluate --graphical`. V kořenovém adresáři (na úrovni složky .performance) bude vyroben soubor `puteval-result.html`, který bude graficky zobrazovat výsledky porovnávání více starších verzí.

### Větší projekt
Při práci na větším projektu se obvykle používá jeden způsob testování výkonu, tudíž se předpokládá jeden testovací framework. Pokud se tedy při práci na větším projektu používá jeden z podporovaných frameworků, pak není problém PUTE použít. Instalace bude stejná jak byla výše uvedena. Bude ale možné nakonfigurovat uživatele (konkrétního testera) a stroj na kterém se testuje. K tomuto budou sloužit příkazy `pute config --set-user the_best_pute_tester_name` a `pute config --set-machine the_best_testing_machine`.

TODO: Chci dovolit aby více testovacích souborů tvořili jeden výstup (resp. jeden celistvý výsledek jednoho benchmarku)?

### Způsob ukládání dat
Výsledky jednotlivých testů budou ukládány na jednom místě, ideálně v jedné složce (`.performance/test-results`), a zároveň několik posledních testů bude uloženo ve složce `.performance/test-cache`, protože procházet při každém požádání o porovnání posledních 50 testů by při procházení všech výsledků testů bylo pomalé.

Podporované benchmarky jsou schopné generovat svůj výstup ve formátu JSON souboru. Program se ale na základě **obsahu/konfigurace?** rozhodne, který benchmark a jaký jeho formát použít. Podle této konfigurace se bude určovat jak se budou parsovat výsledky testů.

### Shrnutí
Soubory
-   .performace - složka
-   pute-installer.sh
-   pute-config.json
-   puteval-result.html

Příkazy
-   pute start-tests
-   pute evaluate (--graphical)
-   pute config --set-user string
-   pute config --set-machine string

Podporované benchmarky
-   BenchmarkDotNET
-   OpenJDK JMH
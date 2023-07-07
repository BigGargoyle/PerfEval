# Performance Unit Test Evaluator (PUTE)

PUTE je nástroj, který vzniká v rámci předmětu Ročníkový projekt při studiu na MFF UK oboru Informatika se specializací Systémové programování.

TODO: změnit název - zkratka je divná (s tím souhlasím, rád přijmu nápady)

## Cíl projektu
Cílem práce je naprogramovat aplikaci, která by umožňovala vyhodnocovat výsledky výkonostních testů, které jsou spouštěny nad kódem uživatelů aplikace. Aplikace je tedy určena pro programátory a má za úkol pomáhat s vývojem jejich aplikací.

## Detailní specifikace
### Hlavní úkol
Aplikace bude umět zpracovávat výsledky testů běžně užívaných benchmarkovacíh frameworků. Základní podporované frameworky budou *OpenJDK JMH* a *BenchmarkDotNET*. Aplikace bude umět převzít výstupy těchto frameworků a porovnat je s předchozí verzí. PUTE bude možné mít zařazen v běžném GitLab CI/CD, takže po každém commitu bude možné, aby v důsledku toho selhala pipeline. Aplikaci bude možné spouštět i z konzole a nechat ji porovnat poslední verze. Dále bude možné spustit aplikaci tak, aby vygenerovala grafický výstup, kde by bylo porovnáno více posledních verzí.

PUTE bude naprogramovaný v programovacím jazyce JAVA a bude doplněn o podpůrné skripty napsané v bashi.

### Používání aplikace

TODO: seznamy kroků u jendnotlivých use cases

> Tuto část by bylo vhodné rozšířit do podoby připomínající standardní use cases.
> Nejde mi ani tak o obrázky (těm bych zatím moc času nevěnoval), jako o typický
> styl zápisu (hlavně stručný a jasný seznam kroků). Namátkové zdroje příkladů:
> https://en.wikipedia.org/wiki/Use_case#Examples
> https://www.usability.gov/how-to-and-tools/methods/use-cases.html

#### Instalace PUTE

Use Case: Instalace

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel bude schopen pomocí instalačního skriptu nainstalovat nástroj PUTE.

Postconditions: Vývojářský nástroj bude úspěšně nainstalovaný a budou se moci používat příkazy s začínající `pute`

Success Guarantees: Uživatel bude obeznámen s tím, že instalace proběhla úspěšně.

Preconditions: Uživatel bude mít v adresáři přítomnou složku s instalačním souborem a celý její původníí obsah, který bude v tomto repozitáři

Triggers: Pomocí příkazu `./pute-installator.sh` spustí instalační skript.

Basic flow:<br>
1. Uživatel si někam stáhne, naklonuje, nebo okopíruje zdrojový adresář
2. V kořenové složce tohoto adresáře bude instalační skript, který spustí
3. O úspěšné instalaci bude obeznámen výpisem do konzole


Vývojář softwaru se rozhodne vyvíjet nový software. Naprogramuje nějakou část svého programu a k němu pomocí benchmark frameworku, který je podporován PUTE, naprogramuje výkonnostní testy. Uživatel si stáhne instalační balíček a PUTE pomocí příkazu `./pute-installater.sh` nainstaluje. Bude se jednat o instalančí skript samotného PUTE. V průběhu instalace bude uživatel požádán zda-li chce provést výchozí instalaci a nastavit pouze povinné parametry, nebo jestli chce provést detailní instalaci a nastavit všechny parametry. Povinným parametrem je pouze volba benchmarku.

Výstupem bude vytvoření složky `.performance` v adresáři, kde se vyskytuje instalační skript a v ní konfigurační soubory systému PUTE.

> Co přesně má instalátor dělat ? Já si to představuji podobně jako co dělá třeba `ng new` v Angular nebo `django-admin startproject` v Django ?

Ano výsledné chování instalátoru bude obdobné jako u django-admin startproject

#### Přidávání testů do PUTE

Use Case: Přidání informací o lokaci testů do PUTE

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel oznámí systému PUTE, kde se nachází výsledky testů

Postconditions: Vývojářský nástroj PUTE si přidá umístění, nebo test mezi adresy ve kterých bude hledat testy

Success Guarantees: Uživatel bude obeznámen s tím, že adresa byla přidaná

Preconditions: Uživatel zadá platnou adresu složky nebo souboru

Triggers: Pomocí příkazu `pute index-new-result <file>`, nebo `pute index-all-results <target-dir>` spustí skript pro přidání adres

Basic flow:

1. Uživatel zadá příkaz s příslušným argumentem
2. V případě nenalezení souboru, nebo složky bude uživatel obeznámen s její neexistencí a bude vrácen nenulový exit kód

#### Porovnání dvou výsledků mezi sebou

Use Case: Porovnání dvou výsledků mezi sebou

Primary Actor: Uživatel nástroje, CI/CD

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel, nebo CI/CD bude porovnávat dva poslední výsledky benchmark testů, které PUTE eviduje. Umožní rozpoznat, pokud došlo k významnému zhoršení výkonu

Postconditions: Vývojářský nástroj bude úspěšně nainstalovaný a budou se moci používat příkazy s začínající `pute`

Success Guarantees: Pokud nedošlo k významnému zhoršení výsledků v žádném z testů, pak bude návratový kód 0.

Preconditions: PUTE eviduje alespoň dva výsledky benchmark testů

Triggers: Pomocí příkazu `pute evaluate` se spustí porovnání posledních dvou testů.

Basic flow:<br>
1. Uživatel spustí benchmark testy nad svým vyvíjeným SW.
2. Uživatel zařadí tento příkaz do svého CI/CD.
3. Pokud program vrátí nenulový exit kód, tak CI/CD může selhat


Pokud se uživatel rozhodne, že chce sám vidět jak se změnil výkon mezi posledními dvěma verzemi, pak použije příkaz `pute evaluate`. Příkaz spustí porovnání posledních dvou verzí a vypíše výsledek na standardní výstup. Takový výstup může vypadat následovně:

```
TEST_NAME		|	NEW_TIME (ms)	|	LAST_MEASURED_TIME (ms)	|	CHANGE (%)
PERFORMANCE_TEST_1	|	50		|		20		|	-60
PERFORMANCE_TEST_2	|	60		|		80		|	+33
PERFORMANCE_TEST_3	|	20		|		21		|	+5 !!! (Insufficient data)
```

#### Porovnání více výsledků mezi sebou 

TODO: doplnit

> Ještě mi tu chybí (třeba jako další use case) příklad výpisu, který by byl vhodný pro integraci do CI/CD (asi něco jako co produkuje JUnit nebo podobné unit test frameworky ?). Také dobré by bylo popsat možnost integrace nástroje do nějakých automatizovaných skriptů, představoval bych si třeba možnost napsat si skript zhruba v tomto stylu:
>
> ```
> run-all-perf-tests
> while true
> do
>     UNDECIDED="$(pute list-undecided --max-time=1h)"
>     [[ -z "${UNDECIDED}" ]] && break
>     run-given-perf-tests ${UNDECIDED}
> done
> ```

#### Strukturovaný výpis (formát JSON)

Use Case: Porovnání dvou výsledků mezi sebou se strukturovaným výpisem

Primary Actor: CI/CD

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Možnost určená pro CI/CD, aby bylo možné provést strukturovaný výpis v rámci CI

Postconditions: Vývojářský nástroj vypíše strukturovaný výpis na standardní výstup.

Success Guarantees: Pokud nedošlo k významnému zhoršení výsledků v žádném z testů, pak bude návratový kód 0.

Preconditions: PUTE eviduje alespoň dva výsledky benchmark testů

Triggers: Pomocí příkazu `pute evaluate --json-output` se spustí porovnání posledních dvou testů.

Basic flow:<br>
1. Uživatel spustí benchmark testy nad svým vyvíjeným SW.
2. Uživatel zařadí tento příkaz do svého CI/CD.
3. Pokud program vrátí nenulový exit kód, tak CI/CD může selhat.
4. Výstupem programu bude strukturovaný výpis ve formátu JSON na standardní výstup.

#### Porovnání více výsledků s grafickým výstupem

V případě, že se uživatel bude chtít podívat na porovnání více posledních verzí, pak požije příkaz `pute evaluate --graphical`. Na výpisu konzole bude zobrazena webová adresa, na které bude možné výstup zobrazit. V případě, že uživatel bude chtít uložit výsledný html soubor, pak zadá příkaz `pute evaluate --graphical <target-dir>`.

### Větší projekt
Při práci na větším projektu se obvykle používá jeden způsob testování výkonu, tudíž se předpokládá jeden testovací framework. Pokud se tedy při práci na větším projektu používá jeden z podporovaných frameworků, pak není problém PUTE použít. Instalace bude stejná jak byla výše uvedena. Bude ale možné nakonfigurovat uživatele (konkrétního testera) a stroj na kterém se testuje. K tomuto budou sloužit příkazy `pute config --set-user` a `pute config --set-machine`. Dále bude možné nakonfigurovat verzi softwaru, která je testovaná, příkazem `pute config --set-version`. Pokud hodnota verze nebude nakonfigurovaná, její výchozí hodnota bude GIT. Hodnota GIT znamená, že se má program pokusit zjistit verzi pomocí statusu git repozitáře, ve kterém se nachází složka `.performance`. Pokud nebude verze nalezena, tak bude ignorována.

> Z textu není jasné, jakou roli hraje složka `.performance`.

Předpokládá se, že složka `.performance` v kořenovém adresáři projektu bude sloužit k režii PUTE. Bude tedy obsahovat cache testů, konfigurační soubory atd.

TODO: Chci dovolit aby více testovacích souborů tvořili jeden výstup (resp. jeden celistvý výsledek jednoho benchmarku)?
        -> případně pokud bych se rozhodl pozdějí, pravděpodobně by to nemělo být těžké doimplementovat.

> Co přesně myslíte tím "více testovacích souborů" ? Napadá mě více výstupů z více spuštění stejného benchmarku ve stejném prostředí, více výstupů z různých benchmarků ve stejném prostředí, nebo více výstupů z různých prostředí. Obecně myslím, že bychom chtěli, aby všechny tyto možnosti byly podporovány.

Je na mysli zda-li chci dovolit více výstupů stejného benchmarků, které ale dohromady budou tvořit jeden výsledek benchmarku nad celým software. Konkrétně jestli je praktické, aby uživatel mohl nějakým způsobem říci tyhle dva soubory tvoří 1 výsledek a při porovnání se tak mají chovat. Mně osobně to praktické nepřijde a myslím, že povolit výsledky v jednom testovacím benchmarku a skládající se pouze z jediného výstupního souboru jsou dostačující.

### Způsob ukládání dat
Výsledky jednotlivých testů budou uloženy v libovolném formátu, který bude schopen PUTE rozpoznat a naformátovat. Jednotlivé testy budou do vyhodnocovače přidávány příkazem `pute index-new-result <file>`. Více testů je možné do vyhodnocovače přidat přidáním celé složky s výsledky pomocí příkazu `pute index-all-results <path>`.

> Následující 3 odstavce se stanou součástí dokumentace

Výsledky jednotlivých testů budou ukládány na jednom místě, ideálně v jedné složce (výchozí nastavení `.performance/test-results`), a zároveň několik posledních testů bude uloženo ve složce `.performance/test-cache`, protože procházet při každém požádání o porovnání posledních 50 testů by při procházení všech výsledků testů bylo pomalé. Pokud bude cache prázdná projde se celý adresář s testya naplní se cache. Poté se začne vyhodnocovat.

Podporované benchmarky jsou schopné generovat svůj výstup ve formátu JSON souboru. Program se ale na základě konfigurace rozhodne, který benchmark a jaký jeho formát použít. Podle této konfigurace se bude určovat jak se budou parsovat výsledky testů.

Pomocí příkazů na konfiguraci bude možné určit složku, kde se nachází všechny výsledky testů, a určit jaký benchmark byl použit. `pute config --benchmark` bude sloužit k určení benchmarku a zároveň při jeho použití bude ověřeno, zda-li je podporován. `pute config --test-directory` bude sloužit k nastavení složky ze které se budou brát výsledky testů.


### Stručné shrnutí
Soubory
-   .performace - složka
-   pute-installer.sh
-   pute-config.json
-   puteval-result.html

Příkazy
-   pute start-tests
-   pute config --set-user <username>
-   pute config --set-machine <machine-name>
-   pute config --benchmark <benchmark-name>
    -   TODO: zamyslet se jestli se bude rozpoznávat, nebo jestli bude zadáván?
        -   pravděpodobně by nemělo být těžké rozeznávat to strojově, jen bych rád názor :-)
-   pute config --test-directory <target-dir>
-   pute config --set-version <version>
-   pute index-new-result <file>
-   pute index-all-results <path>
-   pute evaluate
-   pute evaluate --json-output
-   pute evaluate --graphical
-   pute evaluate --graphical <target-dir>

Podporované benchmarky (prozatím)
-   BenchmarkDotNET
-   OpenJDK JMH

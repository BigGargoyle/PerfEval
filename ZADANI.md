# Performance Unit Test Evaluator (PerfEval)

PerfEval je nástroj, který vzniká v rámci předmětu Ročníkový projekt při studiu na MFF UK oboru Informatika se specializací Systémové programování.

## Cíl projektu
Cílem práce je naprogramovat aplikaci, která by umožňovala vyhodnocovat výsledky výkonostních testů, které jsou spouštěny nad kódem uživatelů aplikace. Aplikace je tedy určena pro programátory a má za úkol pomáhat s vývojem jejich aplikací.

## Detailní specifikace
### Hlavní úkol
Aplikace bude umět zpracovávat výsledky testů běžně užívaných benchmarkovacíh frameworků. Základní podporované frameworky budou *OpenJDK JMH* a *BenchmarkDotNET*. Aplikace bude umět převzít výstupy těchto frameworků a porovnat je s předchozí verzí. PerfEval bude možné mít zařazen v běžném GitLab CI/CD, takže po každém commitu bude možné, aby v důsledku toho selhala pipeline. Aplikaci bude možné spouštět i z konzole a nechat ji porovnat poslední verze. Dále bude možné spustit aplikaci tak, aby vygenerovala grafický výstup, kde by bylo porovnáno více posledních verzí.

PerfEval bude naprogramovaný v programovacím jazyce JAVA a bude doplněn o podpůrné skripty napsané v bashi.

### Používání aplikace

### Konfigurace PerfEval

TODO: Vyřešit verzování databáze a konfiguračního souboru.

#### Inicializace PerfEval

Use Case: Inicializace

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel bude schopen pomocí instalačního skriptu nainstalovat nástroj PerfEval.

Postconditions: Vývojářský nástroj bude úspěšně inicializovaný.

Success Guarantees: Přítomnost složky `.performance` a přítomnost prázdné databáze testů a konfiguračního souboru v ní.

Preconditions: Uživatel bude mít nainstalovaný nástroj PerfEval.

Triggers: Pomocí příkazu `perfeval init` spustí inicializační skript.

Basic flow:<br>
1. Uživatel si nainstaluje nástroj PerfEval (pomocí skriptu `perfeval-installer.sh`)
2. V kořenovém adresáři svého projektu spustí příkaz `perfeval init`
3. Nástroj vytvoří složku `.performance` a v ní potřebné konfigurační soubory pro správnou práci nástroje
    -   prázdná databáze souborů s výsledky testování
    -   režijní soubory knihovny s potřebnými údaji (maximální doba běhu testu)
    -   pomocí `.gitignore` souboru bude databáze souborů ignorovaná, ale konfigurační soubory se budou verzovat
4. Obeznámí uživatele o úspěchu, nebo neúspěchu inicializace výpisem do konzole.

#### Přidávání testů do PerfEval

TODO: import testů na základě formátu a regexu (bude součástí konfigurace i typ benchmarku a formát?)

Motivace: Jednoduché přidávání výsledků testů do systému PerfEval, udržování si základního přehledu o aktuálnosti testů

Use Case: Přidání informací o lokaci testů do PerfEval

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel oznámí systému PerfEval, kde se nachází výsledky testů

Postconditions: Vývojářský nástroj PerfEval si přidá umístění, nebo test mezi adresy ve kterých bude hledat testy

Success Guarantees: Uživatel bude obeznámen s tím, že adresa byla přidaná

Preconditions: Nástroj PerfEval bude inicializovaný. Uživatel zadá platnou adresu složky nebo souboru.

Triggers: Pomocí příkazu `perfeval index-new-result <file>`, nebo `perfeval index-all-results <target-dir>` spustí skript pro přidání adres

Basic flow:

1. Uživatel v adresáři se složkou `.performance` zadá příkaz
    -   PerfEval
 index-new-result <file> pro soubor s měřením
    -   PerfEval
 index-all-results <dir> pro adresář s více soubory měření
2. Systém ověří, zda-li byl zadán uvnitř inicializovaného projektu, tedy ověří existenci složky `.performance`.
3. Systém nalezne všechny testy k importování a do databáze testů uloží jejich umístění a verzi projektu ke které se výsledek vztahuje.

### Vyhodnocení výkonu bez grafického výstupu

#### Porovnání dvou výsledků mezi sebou (formát pro čtení)

Use Case: Porovnání dvou výsledků mezi sebou

Primary Actor: Uživatel nástroje, CI/CD

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel, nebo CI/CD bude porovnávat dva poslední výsledky benchmark testů, které PerfEval eviduje. Umožní rozpoznat, pokud došlo k významnému zhoršení výkonu. Součástí výsledku bude výpis strukturovaný do přehledné tabulky obsahující informace o porovnávaných testech.<br>
O každém testu bude uvedeno:
-   počet měření
-   verze
-   čas, kdy proběhlo měření
-   průměrná hodnota v jednotce odpovídající benchmarkovacímu systému (operace za jednotku časum, sekundy, ...)

O každé porovnávané dvojici výsledků (starší a novější test) bude uvedeno:
-   Absolutní změna (o kolik se změnila průměrná naměřená hodnota)
-   Relativní změna (o kolik procent se změnila průměrná naměřená hodnota)
-   Výsledek statistického testu po porovnání obou testů

Postconditions: Vývojářský nástroj bude úspěšně inicializovaný a budou se moci používat příkazy s začínající `perfeval`

Success Guarantees: Pokud nedošlo k významnému zhoršení výsledků v žádném z testů, pak bude návratový kód 0.

Preconditions: Systém PerfEval je inicializovaný a eviduje alespoň dva výsledky benchmark testů.

Triggers: Pomocí příkazu `perfeval evaluate` se spustí porovnání posledních dvou testů.

Basic flow:<br>
1. Uživatel spustí benchmark testy nad svým vyvíjeným SW.
2. Uživatel, nebo CI/CD spustí příkaz `perfeval evaluate`
3. Výstupem bude přehledný výpis a v případě nějakého zaznamenaného zhoršení i nenulový exit kód.

TODO: Příklad výstupu bude dodán až bude nějaký reálný vyroben. Pravděpodobně bude využita nějaká knihovna pro formátování výpisu (určitě nějaká existuje).

Alternativa:

Triggers: Výstup bude možné filtrovat pomocí úřepínače `--filter` s možnostmi `test-result`, `size-of-change` a `test-id`. Příkaz pak může vypadat následovně `perfeval evaluate --filter test-id` a dojde k přeuspořádání řádků na výstupu

Triggers: Pomocí příkazu `perfeval evaluate --json-output` dojde taktéž k porovnání posledních dvou výsledků testů mezi sebou. Výsledkem ale budou údaje o porovnávání testů ve formátu JSON.

#### Nalezení statisticky nerozhodnutelných výsledků

Use Case: Porovnání dvou výsledků mezi sebou

Primary Actor: Uživatel nástroje, CI/CD

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel, nebo CI/CD zjistí u kterých testů nebylo možné rozhodnout zda došlo, nebo nedošlo ke zlepšení/zhoršení. Uživatel, nebo CI/CD, se z výpisu také dozví kolik měření je u konkrétního testu zapotřebí, aby došlo k vyhodnocení (pokud je to v dostupném čase možné)

Postconditions: Vývojářský nástroj bude úspěšně inicializovaný a budou se moci používat příkazy s začínající `perfeval`

Success Guarantees: Pokud nedošlo k významnému zhoršení výsledků v žádném z testů, pak bude návratový kód 0.

Preconditions: Systém PerfEval je inicializovaný a eviduje alespoň dva výsledky benchmark testů.

Triggers: Pomocí příkazu `perfeval list-undecided` se spustí porovnání posledních dvou měření a pro každé nerozhodné porovnání vypíše název testu a počet vzorků nutný pro porovnání s jasným výsledkem.

Basic flow:<br>
1. Uživatel spustí benchmark testy nad svým vyvíjeným SW.
2. Uživatel, nebo CI/CD spustí příkaz `perfeval list-undecided`
3. Výstupem bude výpis dvojic název a počet vzorků oddělených tabulátorem.

Účelem je mít možnost napsat skript ve tvaru:<br>
```
run-all-perf-tests
while true
do
    UNDECIDED="$(perfeval list-undecided --max-time=1h)"
    [[ -z "${UNDECIDED}" ]] && break
    run-given-perf-tests ${UNDECIDED}
done
```

Alternativa:
Triggers: Pomocí příkazu `perfeval list-undecided --max-time=1h` se spustí porovnání posledních dvou měření a pro každé nerozhodné porovnání vypíše název testu a počet vzorků nutný pro porovnání s jasným výsledkem. Program bude počítat s tím, že na každý jednotlivý test je vyhrazena nejvýše 1 hodina.


### Vyhodnocování výkonu s grafickým výstupem

TODO: Z popisu není jasné, jak bude stránka vypadat. Nutné doplnit až bude existovat reálná představa.

#### Porovnání více výsledků s grafickým výstupem bez ukládání výstupu

Use Case: Zobrazení dlouhodobého testování výkonu

Primary Actor: Uživatel nástroje

Scope: Projekt, kde se bude nástroj užívat

Stručný popis: Program umožní zobrazit si změny výkonu za poslední týden ve webovém prohlížeči.

Postconditions: Výsledek běhu programu nijak neovlivní žádné soubory. V době běhu pouze dočasně vytvoří webovou stránku, kterou si uživatel bude moci zobrazit.

Success Guarantees: Program vypíše webovou adresu, kterou po zadání do webového prohlížeče bude možné zobrazit.

Preconditions: Inicializovaný systém PerfEval. V případě, že nebudou nalezeny žádné testy, pak bude stránka prázdná, nebo budou prázdné detaily o testování.

Triggers: Pomocí příkazu `perfeval evaluate --graphical`.

Basic flow:<br>
1. Uživatel zadá příkaz `perfeval evaluate --graphical`.
2. Uživatel otevře webovou stránku, jejíž adresu vypíše program
3. Uživatel si na webové stránce prohlédne vývoj výkonu své aplikace
4. Uživatel dle instrukcí aplikace ukončí aplikaci

Alternativa:

Motivace: Uživatel si může chtít grafický výstup uložit a podívat se na něj později.

Stručný popis: Program umožní zobrazit a uložit si změny výkonu za poslední týden ve webovém prohlížeči.

Success Guarantees: Program vypíše webovou adresu, kterou po zadání do webového prohlížeče bude možné zobrazit.

Triggers: Pomocí příkazu `perfeval evaluate --graphical <target-dir>`.

Basic flow:<br>
1. Uživatel zadá příkaz `perfeval evaluate --graphical <target-dir>`.
2. Program vypíše název a umístění webové stránky, kterou vytvořil, a poté skončí.
3. Uživatel si ve svém prohlížeči může webovou stránku prohlédnout.

### Způsob ukládání dat
Výsledky jednotlivých testů budou uloženy v libovolném formátu, který bude schopen PerfEval rozpoznat a naformátovat. Jednotlivé testy budou do vyhodnocovače přidávány příkazem `PerfEval index-new-result <file>`. Více testů je možné do vyhodnocovače přidat přidáním celé složky s výsledky pomocí příkazu `perfeval index-all-results <path>`.

> Následující 3 odstavce se stanou součástí dokumentace

Výsledky jednotlivých testů budou ukládány na jednom místě, ideálně v jedné složce (výchozí nastavení `.performance/test-results`), a zároveň několik posledních testů bude uloženo ve složce `.performance/test-cache`, protože procházet při každém požádání o porovnání posledních 50 testů by při procházení všech výsledků testů bylo pomalé. Pokud bude cache prázdná projde se celý adresář s testya naplní se cache. Poté se začne vyhodnocovat.

Podporované benchmarky jsou schopné generovat svůj výstup ve formátu JSON souboru. Program se ale na základě konfigurace rozhodne, který benchmark a jaký jeho formát použít. Podle této konfigurace se bude určovat jak se budou parsovat výsledky testů.

Pomocí příkazů na konfiguraci bude možné určit složku, kde se nachází všechny výsledky testů, a určit jaký benchmark byl použit. `perfeval config --benchmark` bude sloužit k určení benchmarku a zároveň při jeho použití bude ověřeno, zda-li je podporován. `perfeval config --test-directory` bude sloužit k nastavení složky ze které se budou brát výsledky testů.


### Stručné shrnutí
Soubory
-   .performace - složka
-   PerfEval-installer.sh
-   PerfEval-config.json
-   PerfEvalval-result.html

Příkazy
-   PerfEval init
-   PerfEval start-tests
-   ~~PerfEval config --set-user <username>~~
-   ~~PerfEval config --set-machine <machine-name>~~
-   ~~PerfEval config --test-directory <target-dir>~~
-   PerfEval config --set-version <version>
-   PerfEval index-new-result <file>
-   PerfEval index-all-results <path>
-   PerfEval evaluate
-   PerfEval evaluate --json-output
-   PerfEval evaluate --graphical
-   PerfEval evaluate --graphical <target-dir>

Podporované benchmarky (prozatím)
-   BenchmarkDotNET
-   OpenJDK JMH
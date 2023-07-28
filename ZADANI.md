# Performance Unit Test Evaluator (PUTE)

PUTE je nástroj, který vzniká v rámci předmětu Ročníkový projekt při studiu na MFF UK oboru Informatika se specializací Systémové programování.

TODO: změnit název - zkratka je divná (s tím souhlasím, rád přijmu nápady)
-   možná PerfEval?

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

### Konfigurace PUTE

TODO: Vyřešit verzování databáze a konfiguračního souboru.

#### Inicializace PUTE

Use Case: Inicializace

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel bude schopen pomocí instalačního skriptu nainstalovat nástroj PUTE.

Postconditions: Vývojářský nástroj bude úspěšně inicializovaný.

Success Guarantees: Přítomnost složky `.performance` a přítomnost prázdné databáze testů a konfiguračního souboru v ní.

Preconditions: Uživatel bude mít nainstalovaný nástroj PUTE.

Triggers: Pomocí příkazu `pute init` spustí inicializační skript.

Basic flow:<br>
1. Uživatel si nainstaluje nástroj PUTE (pomocí skriptu `pute-installer.sh`)
2. V kořenovém adresáři svého projektu spustí příkaz `pute init`
3. Nástroj vytvoří složku `.performance` a v ní potřebné konfigurační soubory pro správnou práci nástroje
4. Obeznámí uživatele o úspěchu, nebo neúspěchu inicializace výpisem do konzole.

#### Přidávání testů do PUTE

TODO: import testů na základě formátu a regexu (bude součástí konfigurace i typ benchmarku a formát?)

Motivace: Jednoduché přidávání výsledků testů do systému PUTE

Use Case: Přidání informací o lokaci testů do PUTE

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel oznámí systému PUTE, kde se nachází výsledky testů

Postconditions: Vývojářský nástroj PUTE si přidá umístění, nebo test mezi adresy ve kterých bude hledat testy

Success Guarantees: Uživatel bude obeznámen s tím, že adresa byla přidaná

Preconditions: Nástroj PUTE bude inicializovaný. Uživatel zadá platnou adresu složky nebo souboru.

Triggers: Pomocí příkazu `pute index-new-result <file>`, nebo `pute index-all-results <target-dir>` spustí skript pro přidání adres

Basic flow:

1. Uživatel v adresáři se složkou `.performance` zadá příkaz
    -   pute index-new-result <file> pro soubor s měřením
    -   pute index-all-results <dir> pro adresář s více soubory měření
2. Systém ověří, zda-li byl zadán uvnitř inicializovaného projektu, tedy ověří existenci složky `.performance`.
3. Systém nalezne všechny testy k importování a do databáze testů uloží jejich umístění a verzi projektu ke které se výsledek vztahuje.

#### Větší projekt (možnosti vlastní konfigurace PUTE)

TODO: Pravděpodobně není informace o tom, kdo a kde spustil vyhodnocování (resp. kdo a na jakém stroji měřil), protože tato informace bude součástí commitu, o který se stará verzovací nástroj (např. Git) pravděpodobně tedy není jméno stroje a testera důležité. Možná je vhodné tento use case úplně zrušit. Původní záměr bylo, že nástroje typu Git tuto konfiguraci umožňují, ale protože se tento nástroj bude používat v kombinaci s verzovacím nástrojem, pak toto pravděpodobně není nutné.
Ze všech `pute config` příkazů bych nechal pouze `pute config --set-version` pro případ, že by ji chtěl uživatel zadávat sám.

Use Case: Nastavení testera a stroje pro případ práce na více strojích a při více testerech

Primary Actor: uživatel

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Možnost nastavit jméno testera a stroje

Postconditions: Změna konfigurace nástroje podle uživatele

Success Guarantees: Uživatel bude výpisem informován o úspěchu, pak bude návratový kód 0.

Preconditions: Inicializovaný nástroj PUTE.

Triggers: Pomocí příkazu `pute config --set-user <username>` se změní jméno testera v konfiguračním souboru na požadované. Pomocí příkazu `pute config --set-machine <machine-name>` se změní jméno stroje v konfiguračním souboru na požadované.

Basic flow:<br>
1. Uživatel zadá příkaz.

> Z textu není jasné, jakou roli hraje složka `.performance`.

Předpokládá se, že složka `.performance` v kořenovém adresáři projektu bude sloužit k režii PUTE. Bude tedy obsahovat cache testů, konfigurační soubory atd.

### Vyhodnocení výkonu bez grafického výstupu

#### Porovnání dvou výsledků mezi sebou (formát pro čtení)

Use Case: Porovnání dvou výsledků mezi sebou

Primary Actor: Uživatel nástroje, CI/CD

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Uživatel, nebo CI/CD bude porovnávat dva poslední výsledky benchmark testů, které PUTE eviduje. Umožní rozpoznat, pokud došlo k významnému zhoršení výkonu. Součástí výsledku bude výpis strukturovaný do přehledné tabulky obsahující informace o porovnávaných testech.<br>
O každém testu bude uvedeno:
-   počet měření
-   verze
-   čas, kdy proběhlo měření
-   průměrná hodnota v jednotce odpovídající benchmarkovacímu systému (operace za jednotku časum, sekundy, ...)

O každé porovnávané dvojici výsledků (starší a novější test) bude uvedeno:
-   Absolutní změna (o kolik se změnila průměrná naměřená hodnota)
-   Relativní změna (o kolik procent se změnila průměrná naměřená hodnota)
-   Výsledek statistického testu po porovnání obou testů

Postconditions: Vývojářský nástroj bude úspěšně inicializovaný a budou se moci používat příkazy s začínající `pute`

Success Guarantees: Pokud nedošlo k významnému zhoršení výsledků v žádném z testů, pak bude návratový kód 0.

Preconditions: Systém PUTE je inicializovaný a eviduje alespoň dva výsledky benchmark testů.

Triggers: Pomocí příkazu `pute evaluate` se spustí porovnání posledních dvou testů.

Basic flow:<br>
1. Uživatel spustí benchmark testy nad svým vyvíjeným SW.
2. Uživatel, nebo CI/CD spustí příkaz `pute evaluate`
3. Výstupem bude přehledný výpis a v případě nějakého zaznamenaného zhoršení i nenulový exit kód.

TODO: Příklad výstupu bude dodán až bude nějaký reálný vyroben. Pravděpodobně bude využita nějaká knohovna pro formátování výpisu (určitě nějaká existuje).

Alternativa:

Triggers: Výstup bude možné filtrovat pomocí úřepínače `--filter` s možnostmi `test-result`, `size-of-change` a `test-id`. Příkaz pak může vypadat následovně `pute evaluate --filter test-id` a dojde k přeuspořádání řádků na výstupu

Triggers: Pomocí příkazu `pute evaluate --json-output` dojde taktéž k porovnání posledních dvou výsledků testů mezi sebou. Výsledkem ale budou údaje o porovnávání testů ve formátu JSON.

#### Porovnání více výsledků s posledním výsledkem

TODO: doplnit
Také dobré by bylo popsat možnost integrace nástroje do nějakých automatizovaných skriptů, představoval bych si třeba možnost napsat si skript zhruba v tomto stylu:
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

>> Use case doplním později, protože první se musím ujistit, že vím , co se očekávává.

>> Představuji si podle fragmentu kódu výše, že vyberu všechny testy, které jsou mladší než uvedený čas. Tyto testy potom porovnám s nejmladším testem a testy u kterých jsem se nebyl schopen rozhodnout vrátím (jako seznam názvů souborů, nebo absolutní cesty, ...)
Jak ale poznám nerozhodnutelný výsledek?
>>    -   Je blízko hranice kritického oboru hodnot? (p-hodnota je velká, ale dostatečně malá)
>>    -   Nastavený uživatel a stroj se neshodují?
>>    -   Pokud nastane alespoň jedna z výše uvedených možností?

### Vyhodnocování výkonu s grafickým výstupem

TODO: Z popisu není jasné, jak bude stránka vypadat. Nutné doplnit až bude existovat reálná představa.

#### Porovnání více výsledků s grafickým výstupem bez ukládání výstupu

Use Case: Zobrazení dlouhodobého testování výkonu

Primary Actor: Uživatel nástroje

Scope: Projekt, kde se bude nástroj užívat

Stručný popis: Program umožní zobrazit si změny výkonu za poslední týden ve webovém prohlížeči.

Postconditions: Výsledek běhu programu nijak neovlivní žádné soubory. V době běhu pouze dočasně vytvoří webovou stránku, kterou si uživatel bude moci zobrazit.

Success Guarantees: Program vypíše webovou adresu, kterou po zadání do webového prohlížeče bude možné zobrazit.

Preconditions: Inicializovaný systém PUTE. V případě, že nebudou nalezeny žádné testy, pak bude stránka prázdná, nebo budou prázdné detaily o testování.

Triggers: Pomocí příkazu `pute evaluate --graphical`.

Basic flow:<br>
1. Uživatel zadá příkaz `pute evaluate --graphical`.
2. Uživatel otevře webovou stránku, jejíž adresu vypíše program
3. Uživatel si na webové stránce prohlédne vývoj výkonu své aplikace
4. Uživatel dle instrukcí aplikace ukončí aplikaci


Alternativa:

Motivace: Uživatel si může chtít grafický výstup uložit a podívat se na něj později.

Stručný popis: Program umožní zobrazit a uložit si změny výkonu za poslední týden ve webovém prohlížeči.

Success Guarantees: Program vypíše webovou adresu, kterou po zadání do webového prohlížeče bude možné zobrazit.

Triggers: Pomocí příkazu `pute evaluate --graphical <target-dir>`.

Basic flow:<br>
1. Uživatel zadá příkaz `pute evaluate --graphical <target-dir>`.
2. Program vypíše název a umístění webové stránky, kterou vytvořil, a poté skončí.
3. Uživatel si ve svém prohlížeči může webovou stránku prohlédnout.


TODO: Následující 2 use cases budou součástí webového rozhraní aplikace. Nebudou součástí zadávání příkazů na příkazovou řádku.

<del>

#### Porovnání více výsledků s grafickým výstupem se zadáním vlastního relativního časového úseku

Use Case: Uložení dlouhodobého testování výkonu

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Program umožní zobrazit si změny výkonu za poslední uplynulý zadaný časový úsek ve webovém prohlížeči

Postconditions: Výsledek běhu programu nijak neovlivní žádné soubory. V době běhu pouze dočasně vytvoří webovou stránku, kterou si uživatel bude moci zobrazit.

Success Guarantees: Program vypíše webovou adresu, kterou po zadání do webového prohlížeče bude možné zobrazit.

Preconditions: Nutnost mít nainstalovaný systém PUTE. V případě, že nebudou nalezeny žádné testy, pak bude stránka prázdná, nebo budou prázdné detaily o testování.

Triggers: Pomocí příkazu `pute evaluate --graphical --max-time-relative=xxdxxhxxm`, kde místo xx bude doplněn počet dní, hodin, nebo minut. Může být použit i příkaz `pute evaluate --graphical <target-dir> --max-time-relative=xxdxxhxxm`

Basic flow:<br>
1. Uživatel zadá příkaz.
2. Program se bude chovat jako po spuštění příkazu `pute evaluate --graphical`, nebo `pute evaluate --graphical <target-dir>` jediný rozdíl od těchto chování bude počtu testů, které program na webové stránce vyobrazí

#### Porovnání více výsledků s grafickým výstupem se zadáním vlastního absolutního časového úseku

Use Case: Uložení dlouhodobého testování výkonu

Primary Actor: Uživatel nástroje

Scope: projekt, kde se bude nástroj užívat

Stručný popis: Program umožní zobrazit si změny výkonu od zadaného datumu ve webovém prohlížeči

Postconditions: Výsledek běhu programu nijak neovlivní žádné soubory. V době běhu pouze dočasně vytvoří webovou stránku, kterou si uživatel bude moci zobrazit.

Success Guarantees: Program vypíše webovou adresu, kterou po zadání do webového prohlížeče bude možné zobrazit.

Preconditions: Nutnost mít nainstalovaný systém PUTE. V případě, že nebudou nalezeny žádné testy, pak bude stránka prázdná, nebo budou prázdné detaily o testování.

Triggers: Pomocí příkazu `pute evaluate --graphical --max-time-absolute=DD-MM-YYYYTHH:mm:ss`, nebo příkazu `pute evaluate --graphical <target-dir> --max-time-absolute=DD-MM-YYYYTHH:mm:ss`

Basic flow:<br>
1. Uživatel zadá příkaz.
2. Program se bude chovat jako po spuštění příkazu `pute evaluate --graphical`, nebo `pute evaluate --graphical <target-dir>` jediný rozdíl od těchto chování bude počtu testů, které program na webové stránce vyobrazí

</del>

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
-   pute init
-   pute start-tests
-   ~~pute config --set-user <username>~~
-   ~~pute config --set-machine <machine-name>~~
-   ~~pute config --test-directory <target-dir>~~
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
# Systém PerfEval

Systém PerfEval je konzolová aplikace pro vyhodnocování výsledků výkonnostních testů.
Aplikace slouží ke statistickému porovnání výsledků měření výkonu softwaru.
Jedná se o nástroj určený pro programátory k analýze výkonu jejich softwaru.
PerfEval sám o sobě výkon neměří, pro jeho fungování je nutné dodat výsledky měření od podporovaného frameworku a v podporovaném formátu.
Aktuálně je podporovaný formát JSON od frameworků JMH a BenchmarkDotNET.

Aplikace je napsaná v programovacím jazyce Java a byla odzkoušena v systémech typu Linux
s verzemi Javy 17 a 21. Aplikaci je možné spouštět i v operačním systému Windows.
Doporučuje se ji ale používat raději v rámci WSL.

## Obsah adresáře

V tomto adresáři se nachází zdrojový kód aplikace PerfEval, která je součástí odevzdané bakalářské práce.
Tento zdrojový kód se nachází v podadresáři s názvem `PerfEval`.
Dále se zde nachází adresář `clean-test`, který má předvést funkce systému PerfEval.
V podadresáři `crate-micro` se nachází výsledky měření výkonu některých commitů projektu CrateDB.
K měření výkonu byly použité výkonnostní testy frameworku JMH, které jsou součástí tohoto projektu.

### Adresář PerfEval

V tomto adresáři se nacházejí skripty pro kompilaci systému PerfEval
	- gradlew (Linux)
	- gradle.bat (Windows)

Pro úspěšné sestavení systému PerfEval je nutné mít nainstalovanou verzi Javy 17 nebo 21.
Sestavení probíhá pomocí příkazu `./gradlew build` v systémech typu Linux.
Sestavení probíhá pomocí příkazu `gradle.bat build` v systémech typu Windows.

V podadresáři build/libs bude vytvořen soubor PerfEval.jar. Tento soubor lze spouštět
pomocí příkazu `java -jar PerfEval.sh`, nebo pomocí skriptu `perfeval.sh` (`perfeval.bat` pro Windows), který se
nachází přímo v adresáři `PerfEval`. Teńto skript se postará o volání zmíněného příkazu za uživatele.

Pomocí příkazu `bash gradlew javadoc` je možné vygenerovat dokumentaci. Tato dokumentace bude vygenerovaná do poddadresáře `build/docs`

Pozn.: Program byl úspěšně sestaven v laboratořích Rotunda na MFF UK. Byla použitá verze Javy 17 a testovací skript `show_me.sh`
zde také úspěšně doběhl.


### Rychlý start

Rychlý start je ukazuje jak nainstalovat aplikaci PerfEval a porovnat pomocí něho dvě verze softwaru
a je určen pro uživatele Linuxových distribucí. Na platformě Windows je vhodné využít WSL (Windows Subsystem for Linux) nebo jiný nástroj, který umožní spouštění Linuxových příkazů.

1. V adresáři se zdrojovým kódem spusťte příkaz `bash gradlew build`. Tento příkaz zkompiluje a sestaví aplikaci PerfEval.
2. Přejděte do adresáře svého projektu, kde chcete PerfEval používat.
3. Zadejte příkaz `bash perfeval.sh init --benchmark-parser parser-name`.  
   V případě, že nevíte, jaký parser použít, nezadávejte tento argument a systém vám nabídne dostupné parsery. Parser vyberte podle použitého testovacího frameworku a výstupního formátu.  
   Po vybrání parseru zadejte jeho název jako argument parametru `--benchmark-parser`.
4. Přidejte výsledky měření referenční verze pomocí příkazu `bash perfeval.sh index-new-result --path path-to-result-file --version 1.0`.
5. Přidejte výsledky měření nové verze pomocí příkazu `bash perfeval.sh index-new-result --path path-to-result-file --version 2.0`.
6. Porovnejte výsledky měření pomocí příkazu `bash perfeval.sh evaluate`.
7. Na standardní výstup bude vypsána tabulka s výsledky porovnání verzí 1.0 a 2.0. Pokud došlo k zhoršení výkonu, bude program ukončen s nenulovým exit kódem.

### Adresář crate-micro

V tomto adresáři se nachází výsledky měření výkonu projektu CrateDB (https://github.com/crate/crate).
Pomocí sestaveného nástroje Perfeval je možné přidávat do jeho databáze výsledky těchto měření a reprodukovat závěry
bakalářské práce z kapitoly 6.


### Adresář clean-test

Tento adresář obsahuje ukázku průběžné integrace do GitLabového projektu v souboru `.gitlab-ci.yml`
Pomocí skriptů `measure_old_version.sh` a `measure_new_version.sh` je zde simulované měření jednotlivých verzí.
Po změření verzí má průběžná integrace za úkol zinicializovat PerfEval a přidat výsledky těchto měření do jeho databáze.
Tyto výsledky se porovnají a v rámci průběžné integrace je možné s výsledky dále pracovat.


### Adresář show-perfeval

Adresář `show-perfeval` obsahuje skript `show_me.sh`, který má za úkol prezentovat jednotlivé funkce systému PerfEval.
Jedná se o funkce, které mají prezentovat chování a použití jednotlivých příkazů systému PerfEval.

Pro fungování skriptu je nutné první sestavit PerfEval pomocí skriptu `build.sh` v adresáři se
zdrojovým kódem PerfEvalu. Do té doby skript nebude fungovat, protože skript
`perfeval.sh` využívá sestaveného JAR souboru.


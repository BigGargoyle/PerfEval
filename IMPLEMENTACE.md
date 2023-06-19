# Implementace PUTE

## Rozhraní ITest
Rozhraní ITest má za úkol reprezentovat jedno měření jednoho konkrétního testu. Například pokud test testuje metodu foo a naměří 10 hodnot, pak bude mezi hodnotami `Values` 10 hodnot. ITest má také jméno a ID pod kterým se bude nadále prezentovat. Toto ID bude přidělovat ITestParser při jeho vytváření a bude sloužit pro rychlé porovnán, zda-li jsou dva testy stejné. Pokud vezmu dvě různé instance ITest, kde obě reprezuntují stejný test, ale hodnoty pocházejí v různých měření, pak mají stejná ID. Položka Name je zde potom určena pro výpis. Name se bude vypisovat do výsledné tabulky, kde bude sloužit jako identifikátor testu pro čtenáře výsledků hodnocení.

Rozhraní ITest je vytvořeno k tomu, aby si každý ITestParser mohl vytvořit svůj ITest podle svého benchmarkovacího frameworku a do něj si mohl uložit příslušná metadata, která nemusejí mít všechny testy společné. Společná vlastnost všech testů je pouze to, že mají název a, že se mají prezentovat jako sada naměřených hodnot.

TODO: mají mít instance ITest definované ID nebo stačí operátor porovnání a metoda equals?

### Třída BenchmarkDotNetTest
Implementace rozhraní ITest, která reprezentuje výsledek měření jednoho testu pomocí BenchmarkDotNet benchmark frameworku.

### Třída JMHTest
Implementace rozhraní ITest, která reprezentuje výsledek měření jednoho testu pomocí OpenJDK JMH benchmark frameworku.

TODO: Chci mít někde implementace třídy ITest na jednom místě, nebo chci aby každá implementace ITestParseru implementovala i svůj vlástní ITest?

## Rozhraní ITestParser
Rozhraní má za úkol vytvářet z výsledků jednotlivých benchmarků a vytvářet z nich instance typu ITest. Tyto instance později poslouží k jednodušímu zpracování dalšími částmi programu. List<ITest>, který je výstupem jedné z metod, které musí ITestParser implementovat je reprezentací statistického souboru naměřených dat, které se budou dále nějakou zatím blíže nespecifikovanou statistickou metodou zpracovávat.

### Třída BenchmarkDotNetJSONParser
Implementace rozhraní ITestParser, která má za úkol vytvářet z JSON souboru vytvořeného benachmark testem pomocí BenchmarkDotNet frameworku. Ve skutečnosti bude z JSON souboru vytvářet instance třídy BenchmarkDotNetTest, ale na venek je bude prezentovat jako ITest.

### Třída JMHJSONParser
Implementace rozhraní ITestParser, která má za úkol vytvářet z JSON souboru vytvořeného benachmark testem pomocí JMH frameworku. Ve skutečnosti bude z JSON souboru vytvářet instance třídy JMHTest, ale na venek je bude prezentovat jako ITest.

# Implementace PUTE --graphical
Při použití grafické verze aplikace bude použit nějaká šablona webové stránky. Do šablony se pouze dosadí odkazy na spočtená a naformátovaná data pro vykreslení. Pro vykreslení grafů se zlepšením bude použita nějaká knihovna v jazyce JavaScript (TODO: návrhy dodá kamarád, který má zaměřenou bakalářskou práci na porovnávání JavaScriptových pro vykreslování grafů). 

TODO: Detaily ohledně implementace grafické verze budou doplněny po implementování "porovnávací" verze aplikace.
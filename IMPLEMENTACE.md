# Implementace PUTE

> Tady mám vlastně jen jeden velký komentář, totiž zda by nebylo vhodnější psát většinu následujícího jako JavaDoc (viz třeba jak to dělá https://hg.openjdk.org/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/lang/String.java) ?

## Rozhraní ITest
Rozhraní ITest má za úkol reprezentovat jedno měření jednoho konkrétního testu. Například pokud test testuje metodu foo a naměří 10 hodnot, pak bude mezi hodnotami `Values` 10 hodnot. ITest má také jméno a ID pod kterým se bude nadále prezentovat. Toto ID bude přidělovat ITestParser při jeho vytváření a bude sloužit pro rychlé porovnán, zda-li jsou dva testy stejné. Pokud vezmu dvě různé instance ITest, kde obě reprezuntují stejný test, ale hodnoty pocházejí v různých měření, pak mají stejná ID. Položka Name je zde potom určena pro výpis. Name se bude vypisovat do výsledné tabulky, kde bude sloužit jako identifikátor testu pro čtenáře výsledků hodnocení.

> Pokud má rozhraní reprezentovat měření, proč se jmenuje `ITest` a ne `IMeasurement` ?

Rozhraní ITest je vytvořeno k tomu, aby si každý ITestParser mohl vytvořit svůj ITest podle svého benchmarkovacího frameworku a do něj si mohl uložit příslušná metadata, která nemusejí mít všechny testy společné. Společná vlastnost všech testů je pouze to, že mají název a, že se mají prezentovat jako sada naměřených hodnot.

> Možná se bude hodit, aby společná metadata zahrnovala nějakou identifikaci testu, prostředí a času měření (aby šlo nad kolekcí testů udělat třeba timeline všech porovnatelných výsledků) ?

TODO: mají mít instance ITest definované ID nebo stačí operátor porovnání a metoda equals?

> Zatím nevidím, k čemu by bylo ID potřeba. Objekty v Javě mají přirozenou identitu (reference lze porovnat prostým `==`), extra ID by sloužilo jakému účelu ?

### Třída BenchmarkDotNetTest
Implementace rozhraní ITest, která reprezentuje výsledek měření jednoho testu pomocí BenchmarkDotNet benchmark frameworku.

### Třída JMHTest
Implementace rozhraní ITest, která reprezentuje výsledek měření jednoho testu pomocí OpenJDK JMH benchmark frameworku.

TODO: Chci mít někde implementace třídy ITest na jednom místě, nebo chci aby každá implementace ITestParseru implementovala i svůj vlástní ITest?

> Já bych osobně asi preferoval variantu, kdy je všechen kód pro konkrétní benchmark tool nějak pohromadě (jeden source package), ale už bych se nesnažil vyrábět z takového kódu samostatny JAR. Zbytek klasický factory pattern.

## Rozhraní ITestParser
Rozhraní má za úkol vytvářet z výsledků jednotlivých benchmarků a vytvářet z nich instance typu ITest. Tyto instance později poslouží k jednodušímu zpracování dalšími částmi programu. List<ITest>, který je výstupem jedné z metod, které musí ITestParser implementovat je reprezentací statistického souboru naměřených dat, které se budou dále nějakou zatím blíže nespecifikovanou statistickou metodou zpracovávat.

### Třída BenchmarkDotNetJSONParser
Implementace rozhraní ITestParser, která má za úkol vytvářet z JSON souboru vytvořeného benachmark testem pomocí BenchmarkDotNet frameworku. Ve skutečnosti bude z JSON souboru vytvářet instance třídy BenchmarkDotNetTest, ale na venek je bude prezentovat jako ITest.

### Třída JMHJSONParser
Implementace rozhraní ITestParser, která má za úkol vytvářet z JSON souboru vytvořeného benachmark testem pomocí JMH frameworku. Ve skutečnosti bude z JSON souboru vytvářet instance třídy JMHTest, ale na venek je bude prezentovat jako ITest.

## Třídy pojo
Pomocí webového nástroje www.jsonschema2pojo.org byly vytvořeny dvě třídy pro parsování vstupního JSON souboru na instanci třídy, která jej bude v programu reprezentovat. Do programu tedy byly tímto způsobem přidány 2 adresáře se třídami (pojoBenchmarkDotNet a pojoJMH). V adresářích jsou soubory se třídami potřebné k parsování souboru s výslekem testu.

### Použití nástroje jsonschema2pojo
Do nástroje stačilo vložit obsah souboru a nástroj vygeneroval všechny potřebné třídy k naparsování. Tyto třídy pak stačilo přidat do adresáře projektu.

### Implementace třídy BenchmarkDotNetTest
Třída BenchmarkDotNetTest má jeden private konstruktor, který vytváří instanci třídy na základě tříd z adresáře pojoBenchmarkDotNet a to konkrétně ze třídy Benchmark. Třída BenchmarkDotNetTest slouží ke zjednodušení struktury vygenerovaných tříd a jejich zpřehlednění. Zatímco třídy z adresáře mají reprezentovat celý výstupní soubor, tak instance třídy BenchmarkDotNetTest mají reprezentovat jeden měřený test (daný rozhraním ITest).

Z původní instance třídy Benchmark se tedy vezme název testu a naměřené hodnoty z nich se vyrobí instance třídy BenchmarkDotNetTest. Je kladen důraz na to, aby se uživatel této třídy (programátor) nepokoušel objekt konstruovat sám, nebo jinak, a proto je vytvořena public factory metoda ConstructTest, která má vracet zkonstruovaný objekt typu ITest. Není tedy možné bez konverze datového typu nijak sahat přímo na instanci třídy. K instanci je možné přistupovat pouze skrze rozhraní ITest.

### Implementace třídy JMHTest
Třída JMHTest má taktéž pouze jeden private kostruktor, který vytváří instanci třídy JMHTest. Uživatel této třídy by ji neměl kostruovat sám, a tak je k vytvoření instance této třídy vytvořena metoda ConstructTest, která jako má jako parametr instanci třídy BenchmarkJMHJSONBase. Třída BenchmarkJMHJSONBase reprezentuje výstup JMH Benchmarku ve formátu JSON. Třída JMHTest slouží ke zjednodušení a zpřehlednění vygenerované třídy BenchmarkJMHJSONBase.

Z původní instance třídy BenchmarkJMHJSONBase bude tedy vytvořena instance JMHTest, která implementuje rozhraní ITest, a tedy reprezentuje výsledek jednoho testu v rámci celého benchmarku. Ke konstrukci se z původní třídy využije položka `benchmark` a hodnoty z položky `primaryMetric` respektive z její podpoložky `rawData`. Protože položka `rawData` instance `primaryMetric` je list listů objektů typu Double, tak se k vytvoření položky `Values` použije průměr jednotlivých objektů `List<Double>` uvnitř `rawData`. Jako měřená jednotka se předpokládá počet operací za jednotku času a metoda `hasAscendingPerformanceUnit` tedy vrací hodnotu `true`.

# Implementace PUTE --graphical
Při použití grafické verze aplikace bude použit nějaká šablona webové stránky. Do šablony se pouze dosadí odkazy na spočtená a naformátovaná data pro vykreslení. Pro vykreslení grafů se zlepšením bude použita nějaká knihovna v jazyce JavaScript (TODO: návrhy dodá kamarád, který má zaměřenou bakalářskou práci na porovnávání JavaScriptových pro vykreslování grafů).

TODO: Detaily ohledně implementace grafické verze budou doplněny po implementování "porovnávací" verze aplikace.

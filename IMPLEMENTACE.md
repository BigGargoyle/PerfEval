# Implementace PUTE

> Tady mám vlastně jen jeden velký komentář, totiž zda by nebylo vhodnější psát většinu následujícího jako JavaDoc (viz třeba jak to dělá https://hg.openjdk.org/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/lang/String.java) ?

S automatickým generováním dokumentace jsem se setkal relativně nedávno a nejsem jejím velkým fanouškem. Zatím se mi zdá, že pokud si celou dokumentaci udělám sám, tak se mi opticky nerozbíjí kód komentáři ze kterých se "jen" vytvoří dokumentace. Ale jinak s tím asi nemám větší problém. Ve finální dokumentaci programu se pravděpodobně stejně objeví něco jako následující dokumentace kódu.

## Rozhraní IMeasurement
Rozhraní IMeasurement má za úkol reprezentovat jedno měření jednoho konkrétního testu. Například pokud test testuje metodu foo a naměří 10 hodnot, pak bude mezi hodnotami `Values` 10 hodnot. IMeasurement má také jméno a ID pod kterým se bude nadále prezentovat. Toto ID bude přidělovat IMeasurementParser při jeho vytváření a bude sloužit pro rychlé porovnán, zda-li jsou dva testy stejné. Pokud vezmu dvě různé instance IMeasurement, kde obě reprezuntují stejný test, ale hodnoty pocházejí v různých měření, pak mají stejná ID. Položka Name je zde potom určena pro výpis. Name se bude vypisovat do výsledné tabulky, kde bude sloužit jako identifikátor testu pro čtenáře výsledků hodnocení.

Rozhraní IMeasurement je vytvořeno k tomu, aby si každý IMeasurementParser mohl vytvořit svůj IMeasurement podle svého benchmarkovacího frameworku a do něj si mohl uložit příslušná metadata, která nemusejí mít všechny testy společné. Společná vlastnost všech testů je pouze to, že mají název a, že se mají prezentovat jako sada naměřených hodnot.

Jednotlivé implementace tohoto rozhraní využívají factory pattern. To znamená, že se dají dynamicky určovat instance skutečných tříd jejichž implementace se pod rozhraním IMeasurement bude vracet.

> Možná se bude hodit, aby společná metadata zahrnovala nějakou identifikaci testu, prostředí a času měření (aby šlo nad kolekcí testů udělat třeba timeline všech porovnatelných výsledků) ?


### Třída BenchmarkDotNetTest
Implementace rozhraní IMeasurement, která reprezentuje výsledek měření jednoho testu pomocí BenchmarkDotNet benchmark frameworku.

### Třída JMHTest
Implementace rozhraní IMeasurement, která reprezentuje výsledek měření jednoho testu pomocí OpenJDK JMH benchmark frameworku.

## Rozhraní IMeasurementParser
Rozhraní má za úkol vytvářet z výsledků jednotlivých benchmarků a vytvářet z nich instance typu IMeasurement. Tyto instance později poslouží k jednodušímu zpracování dalšími částmi programu. List<IMeasurement>, který je výstupem jedné z metod, které musí IMeasurementParser implementovat je reprezentací statistického souboru naměřených dat, které se budou dále nějakou zatím blíže nespecifikovanou statistickou metodou zpracovávat.

### Třída BenchmarkDotNetJSONParser
Implementace rozhraní IMeasurementParser, která má za úkol vytvářet z JSON souboru vytvořeného benachmark testem pomocí BenchmarkDotNet frameworku. Ve skutečnosti bude z JSON souboru vytvářet instance třídy BenchmarkDotNetTest, ale na venek je bude prezentovat jako IMeasurement.

### Třída JMHJSONParser
Implementace rozhraní IMeasurementParser, která má za úkol vytvářet z JSON souboru vytvořeného benachmark testem pomocí JMH frameworku. Ve skutečnosti bude z JSON souboru vytvářet instance třídy JMHTest, ale na venek je bude prezentovat jako IMeasurement.

## Třídy pojo
Pomocí webového nástroje [jsonschema2pojo](www.jsonschema2pojo.org) byly vytvořeny dvě třídy pro parsování vstupního JSON souboru na instanci třídy, která jej bude v programu reprezentovat. Do programu tedy byly tímto způsobem přidány 2 adresáře se třídami (pojoBenchmarkDotNet a pojoJMH). V adresářích jsou soubory se třídami potřebné k parsování souboru s výslekem testu.

### Použití nástroje jsonschema2pojo
Do nástroje stačilo vložit obsah souboru a nástroj vygeneroval všechny potřebné třídy k naparsování. Tyto třídy pak stačilo přidat do adresáře projektu.

### Implementace třídy BenchmarkDotNetTest
Třída BenchmarkDotNetTest má jeden private konstruktor, který vytváří instanci třídy na základě tříd z adresáře pojoBenchmarkDotNet a to konkrétně ze třídy Benchmark. Třída BenchmarkDotNetTest slouží ke zjednodušení struktury vygenerovaných tříd a jejich zpřehlednění. Zatímco třídy z adresáře mají reprezentovat celý výstupní soubor, tak instance třídy BenchmarkDotNetTest mají reprezentovat jeden měřený test (daný rozhraním IMeasurement).

Z původní instance třídy Benchmark se tedy vezme název testu a naměřené hodnoty z nich se vyrobí instance třídy BenchmarkDotNetTest. Je kladen důraz na to, aby se uživatel této třídy (programátor) nepokoušel objekt konstruovat sám, nebo jinak, a proto je vytvořena public factory metoda ConstructTest, která má vracet zkonstruovaný objekt typu IMeasurement. Není tedy možné bez konverze datového typu nijak sahat přímo na instanci třídy. K instanci je možné přistupovat pouze skrze rozhraní IMeasurement.

### Implementace třídy JMHTest
Třída JMHTest má taktéž pouze jeden private kostruktor, který vytváří instanci třídy JMHTest. Uživatel této třídy by ji neměl kostruovat sám, a tak je k vytvoření instance této třídy vytvořena metoda ConstructTest, která jako má jako parametr instanci třídy BenchmarkJMHJSONBase. Třída BenchmarkJMHJSONBase reprezentuje výstup JMH Benchmarku ve formátu JSON. Třída JMHTest slouží ke zjednodušení a zpřehlednění vygenerované třídy BenchmarkJMHJSONBase.

Z původní instance třídy BenchmarkJMHJSONBase bude tedy vytvořena instance JMHTest, která implementuje rozhraní IMeasurement, a tedy reprezentuje výsledek jednoho testu v rámci celého benchmarku. Ke konstrukci se z původní třídy využije položka `benchmark` a hodnoty z položky `primaryMetric` respektive z její podpoložky `rawData`. Protože položka `rawData` instance `primaryMetric` je list listů objektů typu Double, tak se k vytvoření položky `Values` použije průměr jednotlivých objektů `List<Double>` uvnitř `rawData`. Jako měřená jednotka se předpokládá počet operací za jednotku času a metoda `hasAscendingPerformanceUnit` tedy vrací hodnotu `true`.

# Implementace PUTE --graphical
Při použití grafické verze aplikace bude použit nějaká šablona webové stránky. Do šablony se pouze dosadí odkazy na spočtená a naformátovaná data pro vykreslení. Pro vykreslení grafů se zlepšením bude použita nějaká knihovna v jazyce JavaScript (TODO: návrhy dodá kamarád, který má zaměřenou bakalářskou práci na porovnávání JavaScriptových pro vykreslování grafů).

TODO: Detaily ohledně implementace grafické verze budou doplněny po implementování "porovnávací" verze aplikace.

TODO: Předělat dokumentaci do formátu JavaDoc
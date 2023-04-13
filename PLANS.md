# Specification

The first activities should focus on completing project specification:

- Sketching usage scenarios in as much detail as possible
- Investigating compatibility requirements

## 1 programátor
Uživatel se rozhodne naprogramovat si své vlastní SW dílo.
Rozhodne se, že pro testování bude používat framework, který nepodporuje verzování testů do té míry v jaké by mu vyhovovalo,
a tak bude se rozhodne používat můj SW.
Stáhne si tedy instalační balíček a SW nainstaluje (např. příkaz ```make```)
Během instalace může být požádán o zadání složky ve které se nacházejí soubory s výsledky proběhlých benchmarků a o formát těchto souborů (použitý testovací framework). Možná by tyto údaje bylo možné zadávat i formou nějakých přepínačů při spouštění skriptu.

Uživatel tedy naprogramuje část aplikace s testy ve svém oblíbeném frameworku a rozhodne se, že chce vidět jak s postupem času vylepšoval
své funkce a zefektivňoval svůj kód:
```
performance-evaluate
```
Skript se podívá do složky ve které jsou výsledky testů. Začne je procházet a vyhodnocovat. Výsledek vyhodnocení vypíše na standardní
výstup.

	TEST_NAME		|	NEW_TIME (ms)	|	LAST_MEASURED_TIME (ms)	|	CHANGE (%)
	PERFORMANCE_TEST_1	|	50		|		20		|	-60
	PERFORMANCE_TEST_2	|	60		|		80		|	+33
	PERFORMANCE_TEST_3	|	20		|		21		|	+5 !!! (Insufficient data)

Samozřejmě je také možné použít příkaz, který vyrobí grafičtější formu zpracování výsledků testů než je výpis do konzolové řádky.

```
performance-evaluate --graphical
```
Skript vyrobí soubor typu evaluation-result.html, což bude soubor, který bude umět otevřít každý webový prohlížeč a bude v něm graficky
znázorněné postupné změny ve výkonu (v rychlosti běhu jednotlivých metod). Bude zobrazeno porovnání i se staršími než jen posledními
testovacími výsledky. K vyhodnocení budou použity základní statistické metody (které prozatím neumím blíže specifikovat).


## Větší projekt
Při práci ve větší skupině se obvykle využívá jeden způsob testování výkonu, tudíž jen jeden testovací framework. Za předpokladu, že data z tohoto frameworku umí evaluator vyhodnocovat, pak bude evaluator schopen zpracovávat i data pro větší projekt obdobným způsobem jako u malého.
Instalace a nastavení bude pravděpodobně stejné jako u malého projektu. Možná bude nějaký rozdíl v případě, že bude uživatel chtít zpracovávat výsledky z více souborů najednou.

Pravděpodobně budou moct být zdrojová data pro evaluator ve více souborech, a tedy bude nutné ještě vymyslet způsob jak od uživatele přijmout zdrojová data ve více vstupních souborech. Výsledky testů po zadání příkazu ```performance-evaluate``` by pak vypadaly nějak takhle:

	TEST_NAME			|	NEW_TIME (ms)	|	LAST_MEASURED_TIME (ms)	|	CHANGE (%)
	SET_1-PERFORMANCE_TEST_1	|	50		|	20			|	-60
	SET_1-PERFORMANCE_TEST_2	|	60		|	80			|	+33
	SET_1-PERFORMANCE_TEST_3	|	20		|	21			|	+5 !!! (Insufficient data)
	SET_2-PERFORMANCE_TEST_1	|	50		|	20			|	-60
	SET_2-PERFORMANCE_TEST_2	|	60		|	80			|	+33
	SET_2-PERFORMANCE_TEST_3	|	20		|	21			|	+5 !!! (Insufficient data)

Samozřejmě pro lepší výsledky, by bylo možné použít příkaz ```performance-evaluate --graphical ```, který by vygeneroval jeden soubor 
evaluation-result.html, který by bylo opět možné otevřít v běžných webových prohlížečích. Na stránce by byly lépe graficky znázorněny
(pravděpodobně v podobě grafu) změny ve výkonu u jednotlivých testů i vzhledem k několika starším testům. Opět za použití základních 
statistických metod. 

## Požadavky na kompatibilitu
Každý z testovacích frameworků uvedený v GOALS.md má svůj specifický formát zpracování výstupů měření bude tedy zapotřebí pro každý formát mít specifický způsob zpracovávání dat. Minimálně co se parsování týče. Na data ale bude možné aplikovat stejné statistické metody.

Zpracování dat tedy bude pravděpodobně probíhat tak, že pro každý formát výstupu frameworku bude implementovaná metoda, která data předzpracuje a na data poskytované frameworkem bude aplikováno, co nejvíce statistických metod a různých vyhodnocovacích funkcí. Bude tedy implementovaná, nebo použitá nějaká statistická knihovna z níž se budou na dostupná data volat metody, které budou data vyhodnocovat.

## Způsob ukládání dat
V případě, že v adresáři, ve kterém je skript zavolán (příkaz `performance-evaluate`), nebude konfigurační soubor, tak bude vyhledána složka `benchmarks`. Ve složce se předpokládá existence výstupních souborů jednotlivých benchmarků, které již proběhly. Do konzole se vypíše výsledek vyhodnocení.
V konfiguračním souboru, který bude umístěn v adresáři, ze kterého bude evaluator spouštěný může být umístěn také konfigurační soubor `.performance-evaluator-config.json`, který může vypadat následovně:
```json
{
  "testerName": "tester1",
  "machineName": "testingMachine1",
  "srcDir": "./benchmarks"
  "destDir": "./eval-result",
  "inputFilePattern": [
    "SET1*",
	"SET2*"
  ]
}
```
Předpokládá se, že kód vytvoří z tohoto souboru instanci nějaké konfigurační třídy. Aby nedocházelo v cílové složce k přepisování výsledného souboru bude jeho název generovaný z aktuálního času (je to nutné?, spíš ne -> stačí mi jen jeden výstupní soubor, vždy generovat čerstvý výstup -> mohu defaultně použít destDir = ".").
Do výsledného souboru bude zaznamenán název stroje, jméno testera, výsledky statistického vyhodnocení testů.
```json
{
  "testName": "test1",
  // ... statistická data ...
}
```

## Formát dat
Nejjednodušší způsob pro ukládání dat bude pravděpodobně JSON formát, protože jak JS tak Java mají knihovny umožňující ze stringu v JSON formátu vytvářet objekty. Bude se tedy jednoduše programově zpracovávat jak v Javovském skriptu, který bude vykonávat statistické výpočty a zpracování výstupu benchmarku, tak pomocí JavaScriptu, který bude pomáhat s generováním grafického výstupu aplikace.
Skript v Javě bude generovat z JSON souboru výstup do konzole a ze stejného souboru bude JavaScript generovat webovou stránku, jakožto grafický výstup aplikace. Zároveň s JSON soubory umí pracovat i tooly jiných programátorů, protože se jedná o bežný souborový formát.

## Problémy s formátováním 
Jaká data budu ukládat? (jaké statistické hodnoty)


## Osobní poznámky
	-	kromě JSON formátu jsem uvažoval také XML




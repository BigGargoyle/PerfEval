# Specification

The first activities should focus on completing project specification:

- Sketching usage scenarios in as much detail as possible
- Investigating compatibility requirements

## 1 programátor
Uživatel se rozhodne naprogramovat si své vlastní SW dílo.
Rozhodne se, že pro testování bude používat framework, který nepodporuje verzování testů do té míry v jaké by mu vyhovovalo,
a tak bude se rozhodne používat můj SW.
Stáhne si tedy instalační balíček a SW nainstaluje (např. příkaz ```make```)
Během instalace může být požádán o zadání složky ve které se nacházejí soubory s výsledky proběhlých benchmarků a o formát těchto
souborů (použitý testovací framework). Možná by tyto údaje bylo možné zadávat i formou nějakých přepínačů při spouštění skriptu.

Uživatel tedy naprogramuje část aplikace s testy ve svém oblíbeném frameworku a rozhodne se, že chce vidět jak s postupem času vylepšoval
své funkce a zefektivňoval svůj kód:
```
performance-evaluate
```
Skript se podívá do složky ve které jsou výsledky testů. Začne je procházet a vyhodnocovat. Výsledek vyhodnocení vypíše na standardní
výstup.

	TEST_NAME			|	NEW_TIME (ms)	|	LAST_MEASURED_TIME (ms)	|	CHANGE (%)
	PERFORMANCE_TEST_1	|	50		|		20		|	-60
	PERFORMANCE_TEST_2	|	60		|		80		|	+33
	PERFORMANCE_TEST_3	|	20		|		21		|	+5 !!! (Insufficient data)


```
performance-evaluate --graphical
```
Skript vyrobí soubor typu evaluation-result.html, což bude soubor, který bude umět otevřít každý webový prohlížeč a bude v něm graficky
znázorněné postupné změny ve výkonu (v rychlosti běhu jednotlivých metod). Bude zobrazeno porovnání i se staršími než jen posledními
testovacími výsledky. K vyhodnocení budou použity základní statistické metody (které prozatím neumím blíže specifikovat).


## větší projekt
Při práci ve větší skupině se obvykle využívá jeden způsob testování výkonu, tudíž jen jeden testovací framework. Za předpokladu, že data 
z tohoto frameworku umí evaluator vyhodnocovat, pak bude evaluator schopen zpracovávat i data pro větší projekt obdobným způsobem jako u malého.
Pravděpodobně budou moct být zdrojová data pro evaluator ve více souborech, a tedy bude nutné ještě vymyslet způsob jak od uživatele přijmout
zdrojová data ve více vstupních souborech. Výsledky testů po zadání příkazu ```performance-evaluate``` by pak vypadaly nějak takhle:

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


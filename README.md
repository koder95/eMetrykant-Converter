# eMetrykant Converter
Konwertuje dane zewnętrzne na format programu eMetrykant. Tworzy plik `indices.xml`,
który zawiera dane, które mogą zostać wczytane przez program eMetrykant.

## Pliki CSV
Konwerter posiada wsparcie dla przeróżnych formatów CSV. Każdy plik jest przez
konwerter traktowany jako zestaw danych, które można dołączyć do określonej
księgi. Dostępne są:

* Księga ochrzczonych
* Księga bierzmowanych
* Księga zaślubionych
* Księga zmarłych

Strukturę każdej księgi określa plik szablonów. Według niego należy tworzyć
pliki CSV.

### Paczka plików CSV z przykładowymi danymi
Dostępna jest paczka plików z przykładowymi danymi: `data.zip`;

## Plik szablonów
Oto podstawowy plik szablonów:
```xml
<?xml version="1.0" encoding="utf-8" standalone="no"?>
<templates>
<bt name="Księga ochrzczonych">
    <section header="Ochrzczony">
        <field name="name" index="1" label="Imię" />
        <field name="surname" index="0" label="Nazwisko" />
        <field name="birth-date" index="2" label="Data urodzenia" />
        <field name="birth-place" index="3" label="Miejsce urodzenia" />
        <field name="baptism-date" index="4" label="Data chrztu" />
        <field name="baptism-place" index="5" label="Miejsce chrztu" />
    </section>
</bt>
<bt name="Księga bierzmowanych">
    <section header="Bierzmowany">
        <field name="name" index="1" label="Imię" />
        <field name="surname" index="0" label="Nazwisko" />
    </section>
</bt>
<bt name="Księga zaślubionych">
    <section header="Mąż">
        <field name="husband-name" index="1" label="Imię" />
        <field name="husband-surname" index="0" label="Nazwisko" />
        <field name="husband-confession" index="4" label="Wyznanie" />
        <field name="husband-marital-status" index="5" label="Stan cywilny" />
        <field name="husband-residence" index="6" label="Miejsce zamieszkania" />
        <field name="husband-birth-date" index="7" label="Data urodzenia" />
        <field name="husband-birth-place" index="8" label="Miejsce urodzenia" />
        <field name="husband-baptism-date" index="9" label="Data chrztu" />
        <field name="husband-baptism-place" index="10" label="Miejsce chrztu" />
        <field name="husband-father-name" index="11" label="Imię ojca" />
        <field name="husband-father-surname" index="12" label="Nazwisko ojca" />
        <field name="husband-mother-name" index="13" label="Imię matki" />
        <field name="husband-mother-surname" index="14" label="Nazwisko matki" />
        <field name="husband-mother-maiden-name" index="15" label="Nazwisko panieńskie matki" />
    </section>
    <section header="Żona">
        <field name="wife-name" index="3" label="Imię" />
        <field name="wife-surname" index="2" label="Nazwisko" />
        <field name="wife-confession" index="16" label="Wyznanie" />
        <field name="wife-marital-status" index="17" label="Stan cywilny" />
        <field name="wife-residence" index="18" label="Miejsce zamieszkania" />
        <field name="wife-birth-date" index="19" label="Data urodzenia" />
        <field name="wife-birth-place" index="20" label="Miejsce urodzenia" />
        <field name="wife-baptism-date" index="21" label="Data chrztu" />
        <field name="wife-baptism-place" index="22" label="Miejsce chrztu" />
        <field name="wife-father-name" index="23" label="Imię ojca" />
        <field name="wife-father-surname" index="24" label="Nazwisko ojca" />
        <field name="wife-mother-name" index="25" label="Imię matki" />
        <field name="wife-mother-surname" index="26" label="Nazwisko matki" />
        <field name="wife-mother-maiden-name" index="27" label="Nazwisko panieńskie matki" />
    </section>
</bt>
<bt name="Księga zmarłych">
    <section header="Zmarły">
        <field name="name" index="1" label="Imię" />
        <field name="surname" index="0" label="Nazwisko" />
        <field name="birth-date-age" index="2" label="Data urodzenia albo wiek" />
        <field name="residence" index="3" label="Miejsce zamieszkania" />
        <field name="confession" index="4" label="Wyznanie" />
        <field name="death-date-time" index="5" label="Data i godzina zgonu" />
        <field name="death-place" index="6" label="Miejsce zgonu" />
    </section>
</bt>
</templates>
```
Na jego podstawie można tworzyć swoje własne pliki szablonów. Ważne jest, aby przy
tym zachować warunki:

* Każdy tag `<field>` ma być umieszczony wewnątrz `<section>` i posiadać trzy
  atrybuty:
  - `name` - nazwa systemowa (bez spacji), nie może się powtarzać w ramach księgi
  - `index` - liczba (`0 - n`; `n` - liczba wszystkich pól w księdze), wskaźnik
    pozycji w rekordzie pliku CSV (w jednej księdze nie może być więcej niż
    1 taki sam wskaźnik),
  - `label` - etykieta wyświetlana przed wartością.
* Każdy tag `<section>` ma być umieszczony wewnątrz `<bt` i posiadać atrybut
  `header`, który jest nagłówkiem sekcji.
* Każdy tag `<bt>` ma być umieszczony wewnątrz jedynego nadrzędnego znacznika
  `<templates` i posiadać atrybut `name`, który musi być jedną z nazw dostępnych
  ksiąg.
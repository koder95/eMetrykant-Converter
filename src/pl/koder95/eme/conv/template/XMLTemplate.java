/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.koder95.eme.conv.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import pl.koder95.eme.xml.XMLLoader;
import pl.koder95.eme.xml.XMLRecord;

import javax.xml.parsers.ParserConfigurationException;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.2, 2020-08-18
 * @since 1.0.0
 */
public class XMLTemplate {

    /**
     * Filtr rozszerzeń dla plików *.xml.
     */
    public static final FileChooser.ExtensionFilter XML_FILES
            = new FileChooser.ExtensionFilter("Szablon XML", "*.xml");

    /**
     * Odczytuje i interpretuje plik XML, z którego pozyskuje szablon indeksów.
     *
     * @param f plik szablonów
     * @return szablon indeksów
     *
     * @throws IOException wystąpił błąd czytania pliku
     * @throws SAXException wystąpił błąd parsowania pliku
     * @throws ParserConfigurationException zła konfiguracja parsera
     */
    public static XMLTemplate parse(File f)
            throws ParserConfigurationException, SAXException, IOException {
        XMLTemplate tmpl = new XMLTemplate(f.getName());
        XMLLoader loader = new XMLLoader(f);
        Element template = loader.getDocumentElement();
        if (template.getNodeName().equals("templates")) {
            tmpl.books.addAll(Book.load(template.getElementsByTagName("bt")));
            return tmpl;
        }
        else throw new EOFException("Nie znaleziono znacznika <templates>!");
    }
    
    private final String filename;
    private final ObservableList<Book> books = FXCollections.observableList(new LinkedList<>());
    
    private XMLTemplate(String filename) {
        this.filename = filename;
    }

    /**
     * @return nazwa pliku szablonów
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return lista nazw ksiąg
     */
    public ObservableList<String> getBookNames() {
        return FXCollections.observableList(books.stream().map(Book::getName)
                .collect(Collectors.toList()));
    }
    
    private Book getBook(String name) {
        return books.stream().reduce((result, b) -> b.getName().equals(name)? b:
                result).get();
    }

    /**
     * @param book nazwa księgi
     * @return lista nagłówków sekcji w wybranej księdze
     */
    public ObservableList<String> getSectionHeaders(String book) {
        return FXCollections.observableList(getBook(book).getSections().stream().map(Section::getHeader)
                .collect(Collectors.toList()));
    }
    
    private Section getSection(String book, String header) {
        return getBook(book).getSections().stream()
                .reduce((result, b) -> b.getHeader().equals(header)? b: result)
                .get();
    }

    /**
     * @param book nazwa księgi
     * @return lista nazw pól, które znajdują się w wybranej księdze
     */
    public ObservableList<String> getFieldNames(String book) {
        return FXCollections.observableList(getBook(book).getSections().stream().map(Section::getHeader)
                .collect(Collectors.toList()));
    }
    
    private Field getField(String book, int index) {
        return getBook(book).getSections()
                .stream().filter(
                        (s)->s.getFields()
                                .stream().anyMatch((f)->f.getIndex() == index)
                ).map(Section::getFields).reduce(new LinkedList<>(), (result, current)->{
                    result.addAll(current);
                    return result;
                }).stream().reduce((result, current) -> current.getIndex() == index? current : result).get();
    }

    /**
     * Tworzy nowy rekord XML. Najpierw ustalana jest pozycja roku, który określany jest przez wybór ostatniej
     * kolumny, która nie jest pusta. Następnie pozycja aktu ustalana jest przez odjęcie liczby 1 od pozycji roku.
     * Kolejnym etapem jest odczytanie kolejnych wartości, zamiana znaków {@code '_'} na pojedyncze {@code ' '}
     * i dodanie tych wartości do nowego rekordu XML. Na końcu dodawany jest atrybut {@code an} składający się
     * z aktu i roku, które rozdzielone są znakiem {@literal /}.
     *
     * @param book nazwa księgi
     * @param csv pojedynczy rekord z pliku CSV
     * @return nowy szablon indeksów
     */
    public XMLRecord create(String book, CSVRecord csv) {
        XMLRecord record = new XMLRecord();
        int yearIndex = csv.size()-1;
        String year = csv.get(yearIndex);
        while (year == null || year.isEmpty()) {
            yearIndex--;
            if (yearIndex < 0) {
                return record;
            } else {
                year = csv.get(yearIndex);
            }
        }
        int actNumberIndex = yearIndex-1;
        String actNumber = csv.get(actNumberIndex);
        int i = 0;
        for (String r : csv) {
            if (i == actNumberIndex || i == yearIndex) {
                i++;
                continue;
            } else if (i > yearIndex) break;
            Field f = getField(book, i++);
            r = r.replaceAll("_+", " ");
            record.put(f.getAttr(), r);
        }
        record.put("an", actNumber + "/" + year);
        return record;
    }
}

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

package pl.koder95.eme.conv;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVFormat;
import org.xml.sax.SAXException;
import pl.koder95.eme.conv.template.XMLTemplate;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;

/**
 * Klasa posiada informacje, które mają być dostępne dla konwertera. Środowisko dla konwertera jest tworzone przez
 * usługę konwertera.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 */
public class ConvertContext {

    /**
     * Specyfikacji akceptowana przez eMetrykant.
     */
    public static CSVFormat E_METRYKANT_FORMAT = CSVFormat.EXCEL.withDelimiter(';').withTrim();
    /**
     * Nazwa specyfikacji akceptowanej przez eMetrykant
     */
    public static String E_METRYKANT_FORMAT_NAME = "eMetrykant v0.2.0";
    /**
     * Filtr rozszerzeń dla plików *.csv.
     */
    public static FileChooser.ExtensionFilter CSV_FILES
            = new FileChooser.ExtensionFilter("Pliki CSV", "*.csv");

    /**
     * Zwraca format CSV.
     *
     * @param str nazwa formatu
     * @return format CSV
     */
    public static CSVFormat valueOf(String str) {
        return str.equals(E_METRYKANT_FORMAT_NAME)? E_METRYKANT_FORMAT
                : CSVFormat.valueOf(str);
    }

    private Charset charset;
    private CSVFormat format;
    private XMLTemplate template;
    private File templatesXML;
    private final ObservableList<CSVBook> books = FXCollections.observableList(new LinkedList<>());

    /**
     * Tworzy nowy kontekst konwertowania.
     */
    public ConvertContext() {}

    /**
     * @param spec nazwa specyfikacji CSV
     */
    public void setCSVFormatSpecification(String spec) {
        this.format = valueOf(spec);
    }

    /**
     * @param name nazwa kodowania
     */
    public void setCharset(String name) {
        this.charset = Charset.forName(name);
    }

    /**
     * @param template szablon indeksów
     */
    public void setTemplate(XMLTemplate template) {
        this.template = template;
    }

    /**
     * @param templatesXML plik szablonów
     */
    public void setTemplatesFile(File templatesXML) {
        this.templatesXML = templatesXML;
    }

    /**
     * Wczytuje szablony indeksów z pliku szablonów i ustawia je dla tego kontekstu.
     *
     * @throws IOException wystąpił błąd czytania pliku
     * @throws SAXException wystąpił błąd parsowania pliku
     * @throws ParserConfigurationException zła konfiguracja parsera
     */
    public void loadTemplates() throws IOException, SAXException, ParserConfigurationException {
        setTemplate(XMLTemplate.parse(templatesXML));
    }

    /**
     * @return lista ksiąg CSV
     */
    public ObservableList<CSVBook> getBooks() {
        return books;
    }

    /**
     * @return nazwa kodowania
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @return format CSV
     */
    public CSVFormat getFormat() {
        return format;
    }

    /**
     * @return szablon indeksów XML
     */
    public XMLTemplate getTemplate() {
        return template;
    }
}

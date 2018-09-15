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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Klasa reprezentuje księgę CSV. Przechowuje nazwę księgi, ścieżkę do pliku CSV oraz zbiór rekordów CSV.
 * Utworzenie instancji może odbyć się w każdym wątku. Wczytywanie powinno być jednak wywołane dopiero w wątku
 * aplikacji.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 */
public class CSVBook {

    private final String csvPath;
    private final String bookName;
    private ObservableList<CSVRecord> records;

    /**
     * Tworzy nową instancję zawierającą ścieżkę do plikuCSV i nazwę księgie.
     * @param csvPath ścieżka do pliku CSV
     * @param bookName nazwa księgi
     */
    public CSVBook(String csvPath, String bookName) {
        this.csvPath = csvPath;
        this.bookName = bookName;
        System.out.println(this.toString());
    }

    /**
     * @return nazwa księgi
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Wczytuje dane zapisane w pliku CSV. Jeżeli jest to pierwsze ładowanie do księgi, tworzy nową listę
     * z rekordami. Jeżeli nie, nowe rekordy dodawane są do już istniejącej listy.
     *
     * @param charset kodowanie pliku CSV
     * @param format specyfikacja CSV
     * @throws IOException jeśli problem z odczytaniem pliku
     */
    public void load(Charset charset, CSVFormat format) throws IOException {
        ObservableList<CSVRecord> records = parse(this, charset, format);
        if (this.records == null) this.records = records;
        else this.records.addAll(records);
    }

    /**
     * Czyści listę usuwając wszystkie wczytane rekordy.
     */
    public void clear() {
        if (records != null) this.records.clear();
    }

    /**
     * @return wczytane rekordy, {@code null} - gdy jeśli nie było jeszcze wczytywania
     */
    public ObservableList<CSVRecord> getRecords() {
        return records;
    }

    @Override
    public final String toString() {
        return "CSVBook{" + "csvPath=" + csvPath + ", bookName=" + bookName + '}';
    }

    /**
     * Odczytuje dane z pliku CSV i przyporządkowuje je do określonej księgi.
     *
     * @param book księga CSV
     * @param charset kodowanie pliku CSV
     * @param format specyfikacja formatu CSV
     * @return lista rekordów księgi
     * @throws IOException jeśli wystąpił problem z odczytaniem pliku
     */
    public static ObservableList<CSVRecord> parse(CSVBook book, Charset charset,
            CSVFormat format) throws IOException {
        return FXCollections.observableList(CSVParser.parse(new File(book.csvPath), charset, format)
                .getRecords());
    }
}

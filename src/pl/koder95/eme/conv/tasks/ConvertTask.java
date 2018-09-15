/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.koder95.eme.conv.tasks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import pl.koder95.eme.conv.CSVBook;
import pl.koder95.eme.conv.ConvertContext;
import pl.koder95.eme.xml.XMLRecord;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Klasa rozszerza {@link Task} i tworzy zadanie dla {@link pl.koder95.eme.conv.services.ConvertService ConvertService}.
 * Przed utworzeniem należy najpierw utworzyć kontekst konwertowania ({@link ConvertContext}).
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 */
public class ConvertTask extends Task {

    private final ConvertContext context;

    /**
     * Tworzy nowe zadanie z odpowiednim kontekstem.
     *
     * @param context kontekst konwertowania, używany przez zadanie
     */
    public ConvertTask(ConvertContext context) {
        this.context = context;
    }

    @Override
    protected Object call() throws Exception {
        updateProgress(Double.NaN, Double.NaN);
        long start = System.currentTimeMillis();
        context.loadTemplates();
        saveDocument(createXMLDocument(createBookNameSet()));
        System.out.println("Converting delay: " + (System.currentTimeMillis() - start));
        return null;
    }

    private ObservableSet<String> createBookNameSet() {
        double total = context.getBooks().size();
        double work = 0;
        updateProgress(work, total);
        ObservableSet<String> bookNames = FXCollections.observableSet();
        ObservableList<CSVBook> books = context.getBooks();
        for (CSVBook book : books) {
            try {
                bookNames.add(book.getBookName());
                book.load(context.getCharset(), context.getFormat());
            } catch (IOException ex) {
                System.err.println(ex.getLocalizedMessage());
            }
            updateProgress(++work, total);
        }
        return bookNames;
    }

    private Document createXMLDocument(ObservableSet<String> bookNames) throws ParserConfigurationException {
        updateProgress(Double.NaN, Double.NaN);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node indices = doc.appendChild(doc.createElement("indices"));
        double workDone = 0;
        double totalWork = getNumberOfRecords();
        for (String book: bookNames) {
            Node bookN = doc.createElement("book");
            Attr nameA = doc.createAttribute("name");
            nameA.setValue(book);
            bookN.getAttributes().setNamedItem(nameA);

            ObservableList<CSVRecord> all = collectAll(context.getBooks(), book, workDone, totalWork);
            updateProgress(workDone, totalWork);
            ObservableList<XMLRecord> result;
            {
                ObservableList<Double> workList = FXCollections.observableArrayList(workDone, totalWork);
                ObservableList<XMLRecord> converted = FXCollections.observableList(new LinkedList<>());
                all.forEach((record)->converted.add(convertOne(book, record, workList)));
                result = FXCollections.observableList(new ArrayList<>(converted));
                converted.clear();
            }
            result.forEach((r) -> r.toChild(doc, bookN));
            indices.appendChild(bookN);
        }
        return doc;
    }

    private int getNumberOfRecords() {
        return context.getBooks().stream().map((book) -> book.getRecords().size()).reduce(0, Integer::sum);
    }

    private XMLRecord convertOne(String book, CSVRecord record, ObservableList<Double> workList) {
        workList.set(0, workList.get(0) + 1);
        updateProgress(workList.get(0), workList.get(1));
        return context.getTemplate().create(book, record);
    }

    private ObservableList<CSVRecord> collectAll(ObservableList<CSVBook> books, String bookName, double workDone,
                                                 double totalWork) {
        updateProgress(Double.NaN, Double.NaN);
        ObservableList<ObservableList<CSVRecord>> lists = FXCollections.observableList(books.stream()
                .filter((b) -> b.getBookName().equals(bookName))
                .map(CSVBook::getRecords)
                .collect(Collectors.toList()));
        ObservableList<CSVRecord> all = FXCollections.observableList(new LinkedList<>());
        lists.forEach(all::addAll);
        updateProgress(workDone, totalWork);
        return all;
    }

    private void saveDocument(Document doc) {
        updateProgress(Double.NaN, Double.NaN);
        try {
            TransformerFactory ftrans = TransformerFactory.newInstance();
            Transformer trans = ftrans.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            DOMSource src = new DOMSource(doc);
            trans.transform(src, new StreamResult(new File("./indices.xml")));
        } catch (TransformerException ex) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("");
            a.setContentText("Niemożliwy do naprawienia błąd wystąpił podczas przebiegu transformacji.");
            a.show();
        }
    }

    @Override
    protected void succeeded() {
        updateProgress(0, 1);
    }
}

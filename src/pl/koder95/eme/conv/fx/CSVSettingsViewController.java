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
package pl.koder95.eme.conv.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVFormat;
import pl.koder95.eme.conv.CSVBook;
import pl.koder95.eme.conv.services.ConvertService;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.stream.Collectors;

import static pl.koder95.eme.conv.ConvertContext.CSV_FILES;
import static pl.koder95.eme.conv.ConvertContext.E_METRYKANT_FORMAT_NAME;
import static pl.koder95.eme.conv.template.XMLTemplate.XML_FILES;

/**
 * Klasa tworzy i określa kontroler pliku CSVSettingsView.fxml.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.1, 2020-08-08
 * @since 1.0.0
 */
public class CSVSettingsViewController implements Initializable {

    private static final File INITIAL_DIRECTORY = new File(System.getProperty("user.dir"));

    @FXML
    private VBox filesCSVList;
    @FXML
    private ProgressBar convertProgress;
    @FXML
    private Button convert;
    @FXML
    private ChoiceBox<String> specificationsBox;
    @FXML
    private ChoiceBox<String> charsetBox;
    @FXML
    private Button xmlTemplateSetter;
    @FXML
    private Button csvFileAdder;

    private File xmlTmplFile;

    private final ConvertService service = new ConvertService(this);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (Field field: CSVFormat.class.getFields())
            specificationsBox.getItems().add(field.getName());
        specificationsBox.getItems().add(E_METRYKANT_FORMAT_NAME);
        
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        charsets.keySet().forEach((name) -> charsetBox.getItems().add(name));
        
        specificationsBox.setOnAction((e)->tryEnableConvertButton());
        charsetBox.setOnAction((e)->tryEnableConvertButton());

        resetAll();
    }
    
    private void tryEnableConvertButton() {
        if (
                !specificationsBox.getSelectionModel().isEmpty()
                && !charsetBox.getSelectionModel().isEmpty()
                && xmlTmplFile != null
                )
            convert.setDisable(false);
    }

    /**
     * Akcja przycisku {@code csvFileAdder}.
     */
    @FXML
    public void addNewCSVFile() {
        FileChooser chooser = createFileChooser("Wybieranie plików", CSV_FILES);
        List<File> selected = chooser.showOpenMultipleDialog(null);
        if (selected == null) return;
        selected.forEach((file) -> addNewCSVFile(file.getPath()));
    }

    private FileChooser createFileChooser(String title, FileChooser.ExtensionFilter filter) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.setInitialDirectory(INITIAL_DIRECTORY);
        chooser.getExtensionFilters().add(filter);
        return chooser;
    }

    /**
     * Akcja przycisku {@code convert}.
     */
    @FXML
    public void convert() {
        service.start();
    }

    /**
     * Akcja przycisku {@code xmlTemplateSetter}.
     */
    @FXML
    public void setXMLTemplate() {
        FileChooser chooser = createFileChooser("Wybieranie szablonu XML", XML_FILES);
        File selected = chooser.showOpenDialog(null);
        if (selected == null) {
            xmlTemplateSetter.setText("nie wybrano");
            return;
        }
        xmlTemplateSetter.setText(selected.getName());
        xmlTmplFile = selected;
        tryEnableConvertButton();
    }
    
    private void addNewCSVFile(String path) {
        for (Node hbox: filesCSVList.getChildren()) {
            if (hbox instanceof HBox) {
                HBox parent = (HBox) hbox;
                Label l = null;
                for (Node control: parent.getChildren()) {
                    if (control instanceof Label) {
                        l = (Label) control;
                    }
                }
                if (l == null) continue;
                if (l.getText().equals(path)) {
                    return;
                }
            }
        }
        EventHandler<ActionEvent> removing = (action) -> {
            for (Node hbox: filesCSVList.getChildren()) {
                if (hbox instanceof HBox) {
                    HBox parent = (HBox) hbox;
                    Label l = null;
                    for (Node control: parent.getChildren()) {
                        if (control instanceof Label) {
                            l = (Label) control;
                        }
                    }
                    if (l == null) continue;
                    if (l.getText().equals(path)) {
                        filesCSVList.getChildren().remove(parent);
                        return;
                    }
                }
            }
        };
        HBox hbox = createNewCSVFileRow(path, removing);
        for (Node control: hbox.getChildren()) {
            if (control instanceof ComboBox) {
                ComboBox<String> c = (ComboBox) control;
                c.getSelectionModel().select(0);
                break;
            }
        }
        filesCSVList.getChildren().add(hbox);
    }

    /**
     * Wyłącza wszystkie kontrolki.
     *
     * @param disable jeśli {@code true} kontrolki zostaną wyłączone, jeśli {@code false} - włączone
     */
    public void disableAll(boolean disable) {
        convert.setDisable(disable);
        filesCSVList.getChildren().forEach((n)->n.setDisable(disable));
        specificationsBox.setDisable(disable);
        charsetBox.setDisable(disable);
        xmlTemplateSetter.setDisable(disable);
        csvFileAdder.setDisable(disable);
    }

    /**
     * Resetuje kontrolki pozbawiając zmian dokonanych przez użytkownika.
     */
    public void resetAll() {
        disableAll(false);
        convert.setDisable(true);
        filesCSVList.getChildren().clear();

        specificationsBox.getSelectionModel().select(specificationsBox.getItems().size()-1);
        charsetBox.getSelectionModel().select("UTF-8");
    }
    
    private static HBox createNewCSVFileRow(String path,
            EventHandler<ActionEvent> event) {
        Label p = new Label(path);
        ComboBox<String> cbox = new ComboBox<>();
        cbox.getItems().addAll("Księga ochrzczonych", "Księga bierzmowanych",
                "Księga zaślubionych", "Księga zmarłych");
        Button remove = new Button("-");
        remove.getStyleClass().add("removing-button");
        Font f = remove.getFont();
        remove.setFont(Font.font(f.getFamily(), FontWeight.BOLD, 12));
        remove.setTextFill(Color.WHITE);
        remove.setOnAction(event);
        HBox box = new HBox(p, cbox, remove);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    /**
     * Tworzy listę ksiąg na podstawie dodanych komponentów do VBox'a.
     *
     * @return lista ksiąg, które zostały dodane przez użytkownika
     */
    public ObservableList<CSVBook> createBookList() {
        return FXCollections.observableList(
                filesCSVList.getChildren().stream() // - strumień dzieci VBox'a
                .filter((n) -> n instanceof HBox) // - dzieci, tylko HBox'y
                .map((node) -> (HBox) node) // - strumień HBox'ów
                .filter(
                        (box) -> box.getChildren().stream()
                                .allMatch((n) -> // wszystkie pasujące do:
                                        (n instanceof Label)
                                                || (n instanceof ComboBox)
                                                || (n instanceof Button)
                                )
                ) // - tylko HBox'y z Label'ami, ComboBox'ami lub Button'ami
                .map((row) -> {
                    Optional<Label> csvPath = row.getChildren()
                            .stream() // - strumień dzieci HBox'a
                            .filter((n) -> n instanceof Label)  // - tylko Label'e
                            .map((n) -> (Label) n).findFirst(); // - znajduje pierwszy Label
                    Optional<ComboBox<String>> bookName = row.getChildren()
                            .stream() // - strumień dzieci HBox'a
                            .filter((n) -> n instanceof ComboBox) // - tylko ComboBox'y
                            .map((n) -> (ComboBox<String>) n)
                            .findFirst(); // - znajduje pierwszy ComboBox

                    // Jeśli nie ma ani Label'a, ani ComboBox'a zwraca null:
                    if (!csvPath.isPresent() && !bookName.isPresent()) return null;
                    // W przeciwnym razie tworzy CSVBook'a:
                    return new CSVBook(csvPath.get().getText(), bookName.get().getValue());
                }) // - zamiana HBox'ów na CSVBook'sy
                .collect(Collectors.toList())); // - zamiana strumienia na listę
    }

    /**
     * @see ProgressIndicator#progressProperty()
     * @see javafx.beans.property.DoubleProperty#bind(ObservableValue)
     */
    public void bindProgressProperty(ObservableValue<? extends Number> observable) {
        this.convertProgress.progressProperty().bind(observable);
    }

    /**
     * @return zwraca {@code File} odnoszące się do pliku szablonów
     */
    public File getTemplatesFile() {
        return xmlTmplFile;
    }

    /**
     * @return nazwa kodowania
     */
    public String getCharset() {
        return charsetBox.getValue();
    }

    /**
     * @return nazwa specyfikacji formatu CSV
     */
    public String getSpecification() {
        return specificationsBox.getValue();
    }
}

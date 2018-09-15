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

package pl.koder95.eme.xml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Klasa reprezentuje dane, które mają zostać umieszczone w drzewie dokumentu XML.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 */
public class XMLRecord {

    /**
     * Nazwa tagu w dokumencie XML.
     */
    public static final String TAG_NAME = "index";
    
    private final ObservableMap<String, String> attrs = FXCollections.observableHashMap();

    /**
     * Zamienia na węzeł dokumentu XML.
     *
     * @param doc dokument XML
     * @param parent węzeł-rodzic
     * @return dodany węzeł zawierający informacje rekordu XML
     */
    public Node toChild(Document doc, Node parent) {
        Element tag = doc.createElement(TAG_NAME);
        attrs.keySet().forEach((key) -> tag.setAttribute(key, attrs.get(key)));
        return parent.appendChild(tag);
    }

    /**
     * Dodaje do podanego atrybutu podaną wartość.
     *
     * @param attr nazwa atrybutu
     * @param value wartość
     */
    public void put(String attr, String value) {
        attrs.put(attr, value);
    }
}

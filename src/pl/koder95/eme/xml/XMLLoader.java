/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Ułatwia wczytywanie plików XML. Klasa za pomocą metod przetwarza informacje
 * w pliku XML i w postaci obiektowego modelu dokumentu (OMD, ang. DOM)
 * udostępnia za pomocą interfejsu {@link Document}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 */
public class XMLLoader {

    private static Document loadDOM(File xml)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(xml);
    }
    
    private final Document doc;

    /**
     * Tworzy obiekt przechowujący obiektowy model dokumentu XML i jego typ.
     * 
     * @param xml plik, dokument XML, który zostanie przetworzony na OMD
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public XMLLoader(File xml) throws ParserConfigurationException,
            SAXException, IOException {
        this.doc = loadDOM(xml);
    }
    
    /**
     * @return podstawowy element dokumentu
     */
    public Element getDocumentElement() {
        return doc.getDocumentElement();
    }
    
    /**
     * @param tagname nazwa znacznika
     * @return lista elementów dokumentu, która zawiera znaczniki o podanej nazwie
     */
    public NodeList getElementsByTagName(String tagname) {
        return doc.getElementsByTagName(tagname);
    }
    
    /**
     * @param nodes lista węzłów drzewa dokumentu XML
     * @param tag nazwa znacznika
     * @param attr nazwa atrybutu
     * @param value wartość atrybutu
     * @return węzeł, który jest znacznikiem o określonej nazwie,
     * z określonym atrybutem i wybrany z listy węzłów drzewa dokumentu XML
     */
    public static Node getTagNode(NodeList nodes, String tag, String attr,
            String value) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equalsIgnoreCase(tag)
                    && getAttrV(n, attr).equals(value))
                return n;
        }
        return null;
    }
    
    /**
     * @param doc obiektowy model dokumentu XML
     * @param tag nazwa znacznika XML
     * @param attr nazwa atrybytu znacznika
     * @param value wartość atrybutu znacznika
     * @return węzeł, który jest znacznikiem o określonej nazwie, z określonym atrybutem i pobrany z OMD
     */
    public static Node getTagNode(Document doc, String tag, String attr,
            String value) {
        return getTagNode(doc.getElementsByTagName(tag), tag, attr, value);
    }
    
    /**
     * @param tag węzeł drzewa dokumentu XML, która jest znacznikiem i posiada
     * swoją listę atrybutów
     * @param attr nazwa atrybutu
     * @return węzeł drzewa dokumentu XML, atrybut o podanej nazwie
     */
    public static Node getAttribute(Node tag, String attr)
            throws IllegalArgumentException {
        if (tag.getAttributes() == null) throw new IllegalArgumentException();
        else return tag.getAttributes().getNamedItem(attr);
    }
    
    /**
     * @param tag znacznik
     * @param attr nazwa atrybutu
     * @return wartość atrybutu
     */
    public static String getAttrV(Node tag, String attr) {
        return getAttribute(tag, attr).getTextContent();
    }
}

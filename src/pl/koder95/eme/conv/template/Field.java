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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.koder95.eme.xml.XMLLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 */
class Field {

    public static List<Field> load(NodeList nl) {
        List<Field> fields = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node field = nl.item(i);
            if (!field.getNodeName().equals("field")) continue;
            fields.add(new Field(
                    Integer.parseInt(XMLLoader.getAttrV(field, "index")),
                    XMLLoader.getAttrV(field, "name"),
                    XMLLoader.getAttrV(field, "label")
            ));
        }
        ArrayList<Field> fieldArray = new ArrayList<>(fields);
        fields.clear();
        return fieldArray;
    }

    private final String attr;
    private final int index;
    private final String label;

    public Field(int index, String attr, String label) {
        this.index = index;
        this.attr = attr;
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public String getAttr() {
        return attr;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "#" + index + " (" + attr + "): " + label;
    }

}

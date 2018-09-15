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
class Section {

    public static List<Section> load(NodeList nl) {
        List<Section> sections = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node section = nl.item(i);
            if (!section.getNodeName().equals("section")) continue;
            Section s = new Section(XMLLoader.getAttrV(section, "header"));
            s.fields.addAll(Field.load(section.getChildNodes()));
            sections.add(s);
        }
        ArrayList<Section> sectionArray = new ArrayList<>(sections);
        sections.clear();
        return sectionArray;
    }

    private final String header;
    private final List<Field> fields = new LinkedList<>();

    public Section(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public List<Field> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "/// Sekcja \"" + header + "\": " + fields;
    }
}

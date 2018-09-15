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
class Book {
    
    public static List<Book> load(NodeList nl) {
        List<Book> books = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node bt = nl.item(i);
            if (!bt.getNodeName().equals("bt")) continue;
            Book book = new Book(XMLLoader.getAttrV(bt, "name"));
            book.sections.addAll(Section.load(bt.getChildNodes()));
            books.add(book);
        }
        ArrayList<Book> bts = new ArrayList<>(books);
        books.clear();
        return bts;
    }
    
    private final List<Section> sections = new LinkedList<>();
    private final String name;

    public Book(String name) {
        this.name = name;
    }

    public List<Section> getSections() {
        return sections;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "*** Księga \"" + name + "\": " + sections;
    }

}

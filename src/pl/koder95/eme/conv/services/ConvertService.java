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

package pl.koder95.eme.conv.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.koder95.eme.conv.ConvertContext;
import pl.koder95.eme.conv.fx.CSVSettingsViewController;
import pl.koder95.eme.conv.tasks.ConvertTask;

/**
 * Klasa rozszerza {@link Service} i przygotowuje wszystko co jest potrzebne, aby uruchomić konwertowanie.
 * Wymaga jedynie dostępu do {@link CSVSettingsViewController kontrolera}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 */
public class ConvertService extends Service<Void> {

    private final ConvertContext context = new ConvertContext();
    private final CSVSettingsViewController controller;

    /**
     * Tworzy nowy obiekt usługi konwertowania.
     *
     * @param controller kontroler FX
     */
    public ConvertService(CSVSettingsViewController controller) {
        this.controller = controller;
    }

    @Override
    protected void running() {
        controller.disableAll(true);
    }

    @Override
    protected Task<Void> createTask() {
        context.setCSVFormatSpecification(controller.getSpecification());
        context.setCharset(controller.getCharset());
        context.setTemplatesFile(controller.getTemplatesFile());
        context.getBooks().addAll(controller.createBookList());
        controller.bindProgressProperty(progressProperty());
        return new ConvertTask(context);
    }

    @Override
    protected void succeeded() {
        controller.resetAll();
    }

    @Override
    protected void failed() {
        getException().printStackTrace();
    }
}

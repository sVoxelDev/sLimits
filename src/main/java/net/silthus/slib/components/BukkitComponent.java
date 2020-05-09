/*
 * libcomponents
 * Copyright (C) 2012 zml2008
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.silthus.slib.components;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

/**
 * A component written for a Bukkit server
 */
@TemplateComponent
public abstract class BukkitComponent extends AbstractComponent implements CommandExecutor {

    // TODO: add commands via Advanced Command Framework

    public void setUp() {
    }

    protected final void registerEvents(Listener listener) {
        getPlugin().registerEvents(listener);
    }

    protected final void unregisterEvents(Listener listener) {
        getPlugin().unregisterEvents(listener);
    }
}

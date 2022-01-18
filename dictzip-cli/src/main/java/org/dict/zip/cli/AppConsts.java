/*
 * Copyright (C) 2016 Hiroshi Miura
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.dict.zip.cli;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Application specific constants.
 * @author Hiroshi Miura
 */
public final class AppConsts {
    static final String VERSION = ResourceBundle.getBundle("org/dict/zip/Version")
            .getString("version");
    static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle("org/dict/zip/cli/Bundle", Locale.getDefault());

    static final String NAME = RESOURCE_BUNDLE.getString("application.name");
    static final String BRANDING = "";
    static final String YEAR = "2016-2022";
    static final String AUTHORS = "Hiroshi Miura";

    private static String getString(final String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    static String getNameAndVersion() {
        return MessageFormat.format(getString("app-version-template-pretty"),
                getApplicationName(), VERSION);
    }

    static String getApplicationName() {
        if (BRANDING.isEmpty()) {
            return NAME;
        } else {
            return NAME + " " + BRANDING;
        }
    }

    private AppConsts() {
    }
}

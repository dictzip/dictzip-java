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
public class AppConsts {
    static final String VERSION = ResourceBundle.getBundle("org/dict/zip/Version")
            .getString("version");
    static final String UPDATE = ResourceBundle.getBundle("org/dict/zip/Version")
            .getString("update");
    static final String REVISION = ResourceBundle.getBundle("org/dict/zip/Version")
            .getString("revision");
    
    static final ResourceBundle _messages = ResourceBundle.getBundle("org/dict/zip/cli/Bundle",
            Locale.getDefault());
    
    static final String NAME = _messages.getString("application.name");
    static final String BRANDING = "";    
    static final String YEAR = "2016";
    static final String AUTHORS = "Hiroshi Miura";
    static final String LICENSE = "GPLv2+";

    
    private static String getString(String key) {
        return _messages.getString(key);
    }

    static String getNameAndVersion() {
        if (UPDATE != null && !UPDATE.equals("0")) {
            return MessageFormat.format(getString("app-version-template-pretty-update"),
                    getApplicationName(), VERSION, UPDATE);
        } else {
            return MessageFormat.format(getString("app-version-template-pretty"),
                    getApplicationName(), VERSION);
        }
    }

    static String getApplicationName() {
        return BRANDING.isEmpty() ? NAME : NAME + " " + BRANDING;
    }
    
}

/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GNU General Public License v2.0 or later
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

/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.engine;

import java.util.HashMap;

import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.util.Utils;

/**
 * The base class for settings.
 */
public class SettingsBase {

    private final HashMap<String, String> settings; //存放的是没有h2前缀的默认名称，如DEFAULT_TABLE_ENGINE

    protected SettingsBase(HashMap<String, String> s) {
        this.settings = s;
    }

    /**
     * Get the setting for the given key.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the setting
     */
    protected boolean get(String key, boolean defaultValue) {
        String s = get(key, "" + defaultValue);
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1,
                    e, "key:" + key + " value:" + s);
        }
    }

    /**
     * Get the setting for the given key.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the setting
     */
    protected int get(String key, int defaultValue) {
        String s = get(key, "" + defaultValue);
        try {
            return Integer.decode(s);
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1,
                    e, "key:" + key + " value:" + s);
        }
    }

    /**
     * Get the setting for the given key.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the setting
     */
    protected String get(String key, String defaultValue) {
        StringBuilder buff = new StringBuilder("h2.");
        boolean nextUpper = false;
        for (char c : key.toCharArray()) { //如ALIAS_COLUMN_NAME转成h2.aliasColumnName
            if (c == '_') {
                nextUpper = true;
            } else {
                // Character.toUpperCase / toLowerCase ignores the locale
                buff.append(nextUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                nextUpper = false;
            }
        }
        String sysProperty = buff.toString();
        String v = settings.get(key);
        if (v == null) {
            v = Utils.getProperty(sysProperty, defaultValue);
            settings.put(key, v);
        }
        return v;
    }

    /**
     * Check if the settings contains the given key.
     *
     * @param k the key
     * @return true if they do
     */
    protected boolean containsKey(String k) {
        return settings.containsKey(k);
    }

    /**
     * Get all settings.
     *
     * @return the settings
     */
    public HashMap<String, String> getSettings() {
        return settings;
    }

}

package com.miloFramework.base.util;

import java.net.URL;
import java.util.Properties;

public class UtilProperties {

	
	public static final String module = UtilProperties.class.getName();

    /** Returns the specified resource/properties file
     * @param url The URL to the resource
     * @return The properties file
     */
    public static Properties getProperties(URL url) {
        if (url == null) {
            return null;
        }
        Properties properties = null;
        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(url.openStream());
            } catch (Exception e) {
            }
        }
        if (properties == null) {
            return null;
        }
        return properties;
    }
    /** Returns the value of a split property name from the specified resource/properties file
     * Rather than specifying the property name the value of a name.X property is specified which
     * will correspond to a value.X property whose value will be returned. X is a number from 1 to
     * whatever and all values are checked until a name.X for a certain X is not found.
     * @param url URL object specifying the location of the resource
     * @param name The name of the split property in the properties file
     * @return The value of the split property from the properties file
     */
    public static String getSplitPropertyValue(URL url, String name) {
        if (url == null) return "";
        if (name == null || name.length() <= 0) return "";

        Properties properties = getProperties(url);

        if (properties == null) {
            return null;
        }

        String value = null;

        try {
            int curIdx = 1;
            String curName = null;

            while ((curName = properties.getProperty("name." + curIdx)) != null) {
                if (name.equals(curName)) {
                    value = properties.getProperty("value." + curIdx);
                    break;
                }
                curIdx++;
            }
        } catch (Exception e) {
            //Debug.logInfo(e, module);
        }
        return value == null ? "" : value.trim();
    }
}

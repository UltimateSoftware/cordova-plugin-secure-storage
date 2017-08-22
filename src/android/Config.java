package com.crypho.plugins;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Config {

	private final String PREFERENCE_TAG = "preference";
	private final String TAG = "SecureStorage:Config";

	private final static String PREFERENCE_SECURE_HARDWARE_ONLY = "SecureStorageHardwareOnly";

	private static Config instance;
	private static Map<String, Boolean> configs;

	private Config(Resources res, int configXmlResourceId) {
		configs = getDefaults();
		parseConfigPrefs(configs, res, configXmlResourceId);
	}

	public static Config getInstance(Context context) {
		if (instance == null) {
			Resources res = context.getResources();
			int configXmlResourceId = context.getResources().getIdentifier("config", "xml", context.getPackageName());
			instance = new Config(res, configXmlResourceId);
		}

		return instance;
	}

	private HashMap<String, Boolean> getDefaults() {
		HashMap<String, Boolean> defaultConfigs = new HashMap<>();
		defaultConfigs.put(PREFERENCE_SECURE_HARDWARE_ONLY, false);
		return defaultConfigs;
	}

	private void parseConfigPrefs(Map configs, Resources res, int configXmlResourceId) {
		XmlResourceParser xrp = res.getXml(configXmlResourceId);
		ArrayList<String> preferenceKeys = new ArrayList<>(configs.keySet());

		try {
			xrp.next();
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (PREFERENCE_TAG.equals(xrp.getName())) {
					String key = matchSupportedKeyName(preferenceKeys, xrp.getAttributeValue(null, "name"));

					if (key != null) {
						configs.put(key, Boolean.parseBoolean(xrp.getAttributeValue(null, "value")));
					}
				}
				xrp.next();
			}
		} catch (XmlPullParserException | IOException ex) {
			Log.e(TAG, ex.toString());
		}
	}

	/**
	 * Looks for a match from Supported Preference Keys
	 * @param testKey key to test
	 * @return matching key
	 */
	private String matchSupportedKeyName(ArrayList<String> supportedPrefKeys, String testKey) {
		if (testKey == null) {
			return null;
		}

		for (String realKey : supportedPrefKeys) {
			if (realKey.equalsIgnoreCase(testKey)) {
				return realKey;
			}
		}
		return null;
	}

	public static boolean getSecureHardwareOnly() {
		return configs.get(PREFERENCE_SECURE_HARDWARE_ONLY);
	}
}

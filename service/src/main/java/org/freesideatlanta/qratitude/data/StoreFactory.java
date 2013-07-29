package org.freesideatlanta.qratitude.data;

import java.io.*;
import java.lang.*;
import java.util.*;

public class StoreFactory {

	private static final String DATA_PROPERTIES = "data.properties";
	private static final String DATA_HOST = "data.host";
	private static final String DATA_PORT = "data.port";
	private static final String DATA_DATABASE = "data.database";
	private static final String DATA_PHOTO_BUFFER_SIZE = "data.photo.buffer_size";
	private static final String DATA_PHOTO_BASE_PATH = "data.photo.base_path";
	private static final String DATA_PHOTO_BASE_URL = "data.photo.base_url";

	public static AssetStore getAssetStore() {
		AssetStoreMongo as = null;

		try {
			Properties properties = loadDataProperties();
			String host = properties.getProperty(DATA_HOST);
			String value = properties.getProperty(DATA_PORT);
			int port = Integer.parseInt(value);
			String database = properties.getProperty(DATA_DATABASE);
			
			as = new AssetStoreMongo(host, port, database);
		} catch (NumberFormatException e) {
			// TODO: handle exception better
		}

		return as;
	}

	public static PhotoStore getPhotoStore() {
		PhotoStoreDisk ps = null;

		try {
			Properties properties = loadDataProperties();
			String basePath = properties.getProperty(DATA_PHOTO_BASE_PATH);
			String baseUrl = properties.getProperty(DATA_PHOTO_BASE_URL);
			String value = properties.getProperty(DATA_PHOTO_BUFFER_SIZE);
			int bufferSize = Integer.parseInt(value);

			ps = new PhotoStoreDisk(basePath, baseUrl, bufferSize);
		} catch (NumberFormatException e) {
			// TODO: handle exception better
		}
		
		return ps;
	}

	private static Properties loadDataProperties() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		InputStream is = cl.getResourceAsStream(DATA_PROPERTIES);

		return properties;
	}
}
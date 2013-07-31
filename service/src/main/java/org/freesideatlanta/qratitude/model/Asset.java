package org.freesideatlanta.qratitude.model;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mongodb.*;
import com.mongodb.util.*;
import org.apache.log4j.*;
import org.codehaus.jackson.*;

public class Asset {
	private static Logger log = Logger.getLogger(Asset.class);

	public static String toJson(Collection<Asset> assets) throws IOException {
		StringWriter sw = new StringWriter();
		JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createJsonGenerator(sw);

		g.writeStartObject();

		g.writeFieldName("assets");
		g.writeStartArray();
		for (Asset asset : assets) {
			asset.write(g);
		}
		g.writeEndArray();

		g.writeEndObject();
		g.close();

		String json = sw.toString();

		return json;
	}

	public static Set<URI> getPhotosToRemove(Asset original, Asset updated) {
		Set<URI> toRemove = new HashSet<URI>();

		for (URI uri : original.getPhotos()) {
			if (!updated.getPhotos().contains(uri)) {
				toRemove.add(uri);
			}
		}

		return toRemove;
	}

	private String id;
	private String name;
	private Map<String, String> attributes;
	private Set<URI> photos;

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public Set<URI> getPhotos() {
		return this.photos;
	}

	public Asset() {
		this.attributes = new HashMap<String, String>();
		this.photos = new HashSet<URI>();
	}

	public void addPhoto(String url) throws NullPointerException, IllegalArgumentException {
		URI uri = URI.create(url);
		this.photos.add(uri);
	}

	public void fromDbo(DBObject dbo) {

	}

	public DBObject toDbo() throws IOException {
		String json = this.toJson();
		DBObject dbo = (DBObject)JSON.parse(json);
		return dbo;
	}

	public void fromJson(String json) throws IOException {
		this.attributes.clear();
		this.photos.clear();

		JsonFactory f = new JsonFactory();
		JsonParser p = f.createJsonParser(json);

		while (p.nextToken() != JsonToken.END_OBJECT) {
			String field = p.getCurrentName();
			log.debug(field);
			if ("id".equals(field)) {
				p.nextToken();
				String id = p.getText();
				log.debug(id);
				this.id = id;
			} else if ("name".equals(field)) {
				p.nextToken();
				String name = p.getText();
				log.debug(name);
				this.name = name;
			} else if ("attributes".equals(field)) {
				while (p.nextToken() != JsonToken.END_OBJECT) {
					String attribute = p.getCurrentName();
					p.nextToken();
					String value = p.getText();
					log.debug(attribute);
					log.debug(value);
					this.attributes.put(attribute, value);
				}
			} else if ("photos".equals(field)) {
				p.nextToken(); // [
				while (p.nextToken() != JsonToken.END_ARRAY) {
					String url = p.getText();
					log.debug(url);
					this.addPhoto(url);
				}
			} else {
				log.debug("unmatched field");
				// TODO: throw some kind of error; or ignore
			}
		}
	}

	public String toJson() throws IOException {
		StringWriter sw = new StringWriter();
		JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createJsonGenerator(sw);

		this.write(g);
		g.close();

		String json = sw.toString();
		return json;
	}

	private void write(JsonGenerator g) throws IOException {
		g.writeStartObject();
		g.writeStringField("id", this.id);
		g.writeStringField("name", this.name);

		g.writeObjectFieldStart("attributes");
		for (String key : this.attributes.keySet()) {
			String value = this.attributes.get(key);
			g.writeStringField(key, value);
		}
		g.writeEndObject();

		g.writeFieldName("photos");
		g.writeStartArray();
		for (URI uri : this.photos) {
			String path = uri.toString();
			g.writeString(path);
		}
		g.writeEndArray();

		g.writeEndObject();
	}
}

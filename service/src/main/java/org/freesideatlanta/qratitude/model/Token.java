package org.freesideatlanta.qratitude.model;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.Map.*;

import org.apache.log4j.*;
import org.codehaus.jackson.*;

import org.freesideatlanta.qratitude.data.*;

public class Token {
	private static Logger log = Logger.getLogger(Token.class);

	private String userId;
	private String token;

	// NOTE: this is the token returned to the client
	public String getToken() {
		return this.token;
	}

	public Token() {
	}

	public Token(String userId, String token) {
		log.debug("userId = " + userId);
		log.debug("token = " + token);
		this.userId = userId;
		this.token = token;
	}

	public void generate(User user) throws Exception {
		String random = UUID.randomUUID().toString().toUpperCase();
		String username = user.getUsername();
		this.userId = user.getId();
		log.debug("userId = " + userId);
		
		DateFormat df = getISODateFormat();
		Date date = new Date();
		String creationDate = df.format(date);

		StringBuilder b = new StringBuilder();
		b.append(random).append("|");
		b.append(username).append("|");
		b.append(creationDate);
		String tokenText = b.toString();
		log.debug("tokenText = " + tokenText);

		log.debug("encrypting the token text");
		this.token = CryptUtil.encrypt(tokenText);
		log.debug("this.token = " + this.token);
	}

	public String toJson() throws IOException {
		StringWriter sw = new StringWriter();
		JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createJsonGenerator(sw);

		this.write(g);
		g.close();

		String json = sw.toString();
		log.debug("json = " + json);
		return json;
	}

	private void write(JsonGenerator g) throws IOException {
		g.writeStartObject();
		g.writeStringField("userid", this.userId);
		g.writeStringField("token", this.token);
		g.writeEndObject();
	}

	private DateFormat getISODateFormat() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		return df;
	}
}

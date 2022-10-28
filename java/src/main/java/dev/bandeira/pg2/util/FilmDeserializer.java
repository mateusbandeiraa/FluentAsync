package dev.bandeira.pg2.util;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class FilmDeserializer extends StdDeserializer<Filme> {

	private static final Pattern FILM_ID_PATTERN = Pattern.compile("\\d+(?=\\/$)");

	public FilmDeserializer() {
		this(null);
	}

	public FilmDeserializer(Class<?> vc) {
		super(vc);
	}

	private static final long serialVersionUID = -6209245788472960377L;

	@Override
	public Filme deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.getCodec().readTree(p);
		if (node.getNodeType().equals(JsonNodeType.STRING)) {
			String filmUri = node.asText();

			Matcher matcher = FILM_ID_PATTERN.matcher(filmUri);
			if (matcher.find()) {
				String filmId = matcher.group();
				return new Filme(Integer.valueOf(filmId), null, null, null);
			}
		}
		String title = node.get("title").asText();
		String director = node.get("director").asText();
		LocalDate releaseDate = LocalDate.parse(node.get("release_date").asText());

		return new Filme(null, title, director, releaseDate);
	}

}

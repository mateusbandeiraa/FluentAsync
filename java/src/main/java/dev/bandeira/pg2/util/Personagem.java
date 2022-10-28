package dev.bandeira.pg2.util;

import java.util.List;

public record Personagem(Integer id, String name, Integer height, Integer mass, String hairColor,
		String eyeColor, String birthYear, String gender, List<Filme> films) {
	public Personagem comFilms(List<Filme> films) {
		return new Personagem(id, name, height, mass, hairColor, eyeColor, birthYear, gender, films);
	}
}
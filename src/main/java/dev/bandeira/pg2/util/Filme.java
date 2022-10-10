package dev.bandeira.pg2.util;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FilmDeserializer.class)
public record Filme(Integer id, String title, String director, LocalDate releaseDate) {
}
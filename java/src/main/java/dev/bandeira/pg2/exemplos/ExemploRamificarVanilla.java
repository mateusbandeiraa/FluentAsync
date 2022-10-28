package dev.bandeira.pg2.exemplos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import dev.bandeira.pg2.util.Filme;
import dev.bandeira.pg2.util.Personagem;
import dev.bandeira.pg2.util.ResultadosBusca;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;

public class ExemploRamificarVanilla {
	private static final String URI_BASE = "https://swapi.dev/api";

	private static UnirestInstance http;

	static {
		http = Unirest.spawnInstance();
		http.config().defaultBaseUrl(URI_BASE);
		http.config().setObjectMapper(new JacksonObjectMapper());
	}
	
	public static void main(String[] args) {
		CompletableFuture.supplyAsync(ExemploRamificarVanilla::getAllPeople) //
		.thenApply(resultadosBusca -> {
			var futurePersonagens = new ArrayList<CompletableFuture<Personagem>>();
			for (Personagem personagem : resultadosBusca.results()) {
				var futurePersonagem = CompletableFuture.supplyAsync(() -> {
					var futuresFilmes = new ArrayList<CompletableFuture<Filme>>();
					for (Filme filme : personagem.films()) {
						var futureFilme = CompletableFuture.supplyAsync(() -> {
							Integer idFilme = filme.id();
							return getFilmById(idFilme);
						});
						futuresFilmes.add(futureFilme);
					}
					
					return personagem.comFilms(futuresFilmes.stream().map(CompletableFuture::join).toList());
				});
				futurePersonagens.add(futurePersonagem);
			}
			return futurePersonagens.stream().map(CompletableFuture::join).toList();
		}).thenAccept(ExemploRamificarVanilla::imprimir)
		.join();
	}

	private static ResultadosBusca<Personagem> getAllPeople() {
		var httpResponse = http.get("/people").asObject(new GenericType<ResultadosBusca<Personagem>>() {
		});
		if (httpResponse.getParsingError().isPresent()) {
			throw httpResponse.getParsingError().get();
		}
		return httpResponse.getBody();
	}

	private static Filme getFilmById(Integer idFilme) {
		var httpResponse = http.get("/films/{id}").routeParam("id", String.valueOf(idFilme)).asObject(Filme.class);
		if (httpResponse.getParsingError().isPresent()) {
			throw httpResponse.getParsingError().get();
		}
		return httpResponse.getBody();
	}
	
	private static void imprimir(List<Personagem> resultados) {

		var formato = "| %25s | %-120s |\n";

		System.out.printf(formato, "Nome", "Filmes");

		for (Personagem personagem : resultados) {
			var filmes = personagem.films().stream().map(Filme::title).collect(Collectors.joining("; "));
			System.out.printf(formato, personagem.name(), filmes);
		}
	}
}

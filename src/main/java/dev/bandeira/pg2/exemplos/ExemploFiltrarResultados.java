package dev.bandeira.pg2.exemplos;

import java.util.List;

import dev.bandeira.pg2.api.TarefaAssincrona;
import dev.bandeira.pg2.util.Personagem;
import dev.bandeira.pg2.util.ResultadosBusca;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;

public class ExemploFiltrarResultados {

	private static final String URI_BASE = "https://swapi.dev/api";

	private static UnirestInstance http;

	static {
		http = Unirest.spawnInstance();
		http.config().defaultBaseUrl(URI_BASE);
		http.config().setObjectMapper(new JacksonObjectMapper());
	}

	public static void main(String[] args) {
		new TarefaAssincrona<>(() -> {
			var httpResponse = http.get("/people").asObject(new GenericType<ResultadosBusca<Personagem>>() {
			});
			if (httpResponse.getParsingError().isPresent()) {
				throw httpResponse.getParsingError().get();
			}
			return httpResponse.getBody();
		}) //
		.ramificar(ResultadosBusca::results) //
		.filtrar(personagem -> personagem.films().size() >= 3) //
		.unificar() //
		.consumir(ExemploFiltrarResultados::imprimir) //
		.aguardar();
	}

	private static void imprimir(List<Personagem> resultados) {

		var formato = "| %25s | %10s |\n";
		
		System.out.printf(formato, "Nome", "# filmes");
		
		for (Personagem personagem : resultados) {
			System.out.printf(formato, personagem.name(), personagem.films().size());
		}
	}
}

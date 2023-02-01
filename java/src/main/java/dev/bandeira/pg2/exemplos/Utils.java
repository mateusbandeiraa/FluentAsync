package dev.bandeira.pg2.exemplos;

import java.util.List;
import java.util.stream.Collectors;

import dev.bandeira.pg2.util.Filme;
import dev.bandeira.pg2.util.Personagem;
import dev.bandeira.pg2.util.ResultadosBusca;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;

public class Utils {
  
  private static final String URI_BASE = "https://swapi.dev/api";

  private static UnirestInstance http;

  static {
    http = Unirest.spawnInstance();
    http.config().defaultBaseUrl(URI_BASE);
    http.config().setObjectMapper(new JacksonObjectMapper());
  }
  
  private Utils() {
    
  }
  
  public static ResultadosBusca<Personagem> getAllPeople() {
    var httpResponse = http.get("/people").asObject(new GenericType<ResultadosBusca<Personagem>>() {
    });
    if (httpResponse.getParsingError().isPresent()) {
      throw httpResponse.getParsingError().get();
    }
    return httpResponse.getBody();
  }
  
  public static Filme getFilmById(Integer idFilme) {
    var httpResponse = http.get("/films/{id}").routeParam("id", String.valueOf(idFilme)).asObject(Filme.class);
    if (httpResponse.getParsingError().isPresent()) {
      throw httpResponse.getParsingError().get();
    }
    return httpResponse.getBody();
  }
  
  public static void imprimirPersonagensENumeroFilmes(List<Personagem> resultados) {

    var formato = "| %25s | %10s |\n";

    System.out.printf(formato, "Nome", "# filmes");

    for (Personagem personagem : resultados) {
      System.out.printf(formato, personagem.name(), personagem.films().size());
    }
  }
  
  public static void imprimirPersonagensETitulosFilmes(List<Personagem> resultados) {

    var formato = "| %25s | %-120s |\n";

    System.out.printf(formato, "Nome", "Filmes");

    for (Personagem personagem : resultados) {
      var filmes = personagem.films().stream().map(Filme::title).collect(Collectors.joining("; "));
      System.out.printf(formato, personagem.name(), filmes);
    }
  }

}

package dev.bandeira.pg2.exemplos;

import java.util.concurrent.CompletableFuture;

import dev.bandeira.pg2.api.TarefaAssincrona;
import dev.bandeira.pg2.util.ResultadosBusca;

public class ExemploOrdernarResultados {

public static void executarVanilla() {
 CompletableFuture.supplyAsync(Utils::getAllPeople)
   .thenApply(ResultadosBusca::results)
   .thenApply(personagens -> {
    personagens.sort((p1, p2) -> p2.films().size() - p1.films().size());
    return personagens;
   })
   .thenAccept(Utils::imprimirPersonagensENumeroFilmes)
   .join();
}

public static void executarFluentAsync() {
 new TarefaAssincrona<>(Utils::getAllPeople)
   .transformar(ResultadosBusca::results)
   .transformar(personagens -> {
    personagens.sort((p1, p2) -> p2.films().size() - p1.films().size());
    return personagens;
   })
   .consumir(Utils::imprimirPersonagensENumeroFilmes)
   .aguardar();
}

 public static void main(String[] args) {
  executarVanilla();
  executarFluentAsync();
 }

}

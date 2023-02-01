package dev.bandeira.pg2.exemplos;

import java.util.concurrent.CompletableFuture;

import dev.bandeira.pg2.api.TarefaAssincrona;
import dev.bandeira.pg2.util.ResultadosBusca;

public class ExemploFiltrarResultados {

 public static void executarVanilla() {
  CompletableFuture.supplyAsync(Utils::getAllPeople)
    .thenApply(ResultadosBusca::results)
    .thenApply(personagens -> {
     personagens.removeIf(personagem -> personagem.films().size() < 3);
     return personagens;
    })
    .thenAccept(Utils::imprimirPersonagensENumeroFilmes)
    .join();
 }

 public static void executarFluentAsync() {
  new TarefaAssincrona<>(Utils::getAllPeople)
    .ramificar(ResultadosBusca::results)
    .filtrar(personagem -> personagem.films().size() >= 3)
    .unificar()
    .consumir(Utils::imprimirPersonagensENumeroFilmes)
    .aguardar();
 }

 public static void main(String[] args) {
  executarVanilla();
  executarFluentAsync();
 }
}

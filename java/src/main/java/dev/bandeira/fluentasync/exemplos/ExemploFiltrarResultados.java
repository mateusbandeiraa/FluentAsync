package dev.bandeira.fluentasync.exemplos;

import java.util.concurrent.CompletableFuture;

import dev.bandeira.fluentasync.TarefaAssincrona;
import dev.bandeira.fluentasync.exemplos.util.ResultadosBusca;
import dev.bandeira.fluentasync.exemplos.util.Utils;

public class ExemploFiltrarResultados {

	public static void executarVanilla() {
		CompletableFuture.supplyAsync(Utils::getAllPeople)
				.thenApply(ResultadosBusca::results).thenApply(personagens -> {
					personagens.removeIf(
							personagem -> personagem.films().size() < 3);
					return personagens;
				}).thenAccept(Utils::imprimirPersonagensENumeroFilmes).join();
	}

	public static void executarFluentAsync() {
		new TarefaAssincrona<>(Utils::getAllPeople)
				.ramificar(ResultadosBusca::results)
				.filtrar(personagem -> personagem.films().size() >= 3)
				.unificar().consumir(Utils::imprimirPersonagensENumeroFilmes)
				.aguardar();
	}

	public static void main(String[] args) {
		System.out.println("Vanilla:");
		executarVanilla();
		System.out.println("===========");
		System.out.println("FluentAsync:");
		executarFluentAsync();
	}
}

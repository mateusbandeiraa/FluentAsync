package dev.bandeira.fluentasync.exemplos;

import java.util.concurrent.CompletableFuture;

import dev.bandeira.fluentasync.TarefaAssincrona;
import dev.bandeira.fluentasync.exemplos.util.ResultadosBusca;
import dev.bandeira.fluentasync.exemplos.util.Utils;

public class ExemploOrdernarResultados {

	public static void executarVanilla() {
		CompletableFuture.supplyAsync(Utils::getAllPeople)
				.thenApply(ResultadosBusca::results).thenApply(personagens -> {
					personagens.sort(
							(p1, p2) -> p2.films().size() - p1.films().size());
					return personagens;
				}).thenAccept(Utils::imprimirPersonagensENumeroFilmes).join();
	}

	public static void executarFluentAsync() {
		new TarefaAssincrona<>(Utils::getAllPeople)
				.transformar(ResultadosBusca::results)
				.transformar(personagens -> {
					personagens.sort(
							(p1, p2) -> p2.films().size() - p1.films().size());
					return personagens;
				}).consumir(Utils::imprimirPersonagensENumeroFilmes).aguardar();
	}

	public static void main(String[] args) {
		System.out.println("Vanilla:");
		executarVanilla();
		System.out.println("===========");
		System.out.println("FluentAsync:");
		executarFluentAsync();
	}

}

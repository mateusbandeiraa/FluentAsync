package dev.bandeira.fluentasync.exemplos;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import dev.bandeira.fluentasync.TarefaAssincrona;
import dev.bandeira.fluentasync.exemplos.util.Filme;
import dev.bandeira.fluentasync.exemplos.util.Personagem;
import dev.bandeira.fluentasync.exemplos.util.ResultadosBusca;
import dev.bandeira.fluentasync.exemplos.util.Utils;

public class ExemploRamificar {

	public static void executarVanilla() {
		CompletableFuture.supplyAsync(Utils::getAllPeople)
				.thenApply(resultadosBusca -> {
					var futurePersonagens = new ArrayList<CompletableFuture<Personagem>>();
					for (Personagem personagem : resultadosBusca.results()) {
						var futurePersonagem = CompletableFuture
								.supplyAsync(() -> {
									var futuresFilmes = new ArrayList<CompletableFuture<Filme>>();
									for (Filme filme : personagem.films()) {
										var futureFilme = CompletableFuture
												.supplyAsync(() -> {
													Integer idFilme = filme
															.id();
													return Utils.getFilmById(
															idFilme);
												});
										futuresFilmes.add(futureFilme);
									}
									return personagem.comFilms(futuresFilmes
											.stream()
											.map(CompletableFuture::join)
											.toList());
								});
						futurePersonagens.add(futurePersonagem);
					}
					return futurePersonagens.stream()
							.map(CompletableFuture::join).toList();
				}).thenAccept(Utils::imprimirPersonagensETitulosFilmes).join();
	}

	public static void executarFluentAsync() {
		new TarefaAssincrona<>(Utils::getAllPeople)
				.ramificar(ResultadosBusca::results).transformar(personagem -> {
					var filmes = new TarefaAssincrona<Personagem>(
							() -> personagem).ramificar(Personagem::films)
							.transformar(Filme::id)
							.transformar(Utils::getFilmById).aguardar();
					return personagem.comFilms(filmes);
				}).unificar().consumir(Utils::imprimirPersonagensETitulosFilmes)
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

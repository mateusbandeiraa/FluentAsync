package dev.bandeira.pg2.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TarefaAssincrona<T> {
	protected final CompletableFuture<T> tarefa;

	private TarefaAssincrona(CompletableFuture<T> tarefa) {
		this.tarefa = tarefa;
	}

	public TarefaAssincrona(Supplier<T> tarefa) {
		this(CompletableFuture.supplyAsync(tarefa));
	}

	public T aguardar() {
		return tarefa.join();
	}

	public TarefaAssincrona<Void> consumir(Consumer<T> consumidora) {
		return new TarefaAssincrona<>(tarefa.thenAcceptAsync(consumidora));
	}

	public <U> TarefaAssincrona<U> transformar(Function<T, U> transformadora) {
		return new TarefaAssincrona<>(tarefa.thenApplyAsync(transformadora));
	}

	public <U> TarefaRamificada<U> ramificar(Function<T, List<U>> funcaoRamificadora) {
		return new TarefaRamificada<>(this, funcaoRamificadora);
	}

	public TarefaAssincrona<T> casoErro(Function<Throwable, ? extends T> casoErro) {
		return new TarefaAssincrona<>(tarefa.exceptionally(casoErro));
	}

}

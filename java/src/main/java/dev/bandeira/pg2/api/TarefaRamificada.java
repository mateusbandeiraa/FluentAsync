package dev.bandeira.pg2.api;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TarefaRamificada<R> {
	protected final TarefaAssincrona<List<TarefaAssincrona<R>>> ramos;

	public <T> TarefaRamificada(TarefaAssincrona<T> tarefaOriginal, Function<T, List<R>> funcaoRamificadora) {
		this.ramos = tarefaOriginal.transformar(funcaoRamificadora) //
				.transformar(listaRamos -> listaRamos.stream().map(ramo -> new TarefaAssincrona<>(() -> ramo)) //
						.toList());

	}

	private TarefaRamificada(TarefaAssincrona<List<TarefaAssincrona<R>>> ramos) {
		this.ramos = ramos;
	}

	public <U> TarefaAssincrona<U> unificar(Function<List<R>, U> funcaoUnificadora) {
		return this.unificar().transformar(funcaoUnificadora);
	}

	public TarefaAssincrona<List<R>> unificar() {
		return this.ramos.transformar(listaRamos -> listaRamos.parallelStream() //
				.map(TarefaAssincrona::aguardar) //
				.toList());
	}

	public <U> TarefaRamificada<U> transformar(Function<R, U> transformadora) {
		TarefaAssincrona<List<TarefaAssincrona<U>>> transformar = ramos
				.transformar(r -> r.stream().map(ramo -> ramo.transformar(transformadora)).toList());
		return new TarefaRamificada<>(transformar);
	}

	public TarefaRamificada<Void> consumir(Consumer<R> consumidora) {
		TarefaAssincrona<List<TarefaAssincrona<Void>>> consumir = ramos
				.transformar(r -> r.stream().map(ramo -> ramo.consumir(consumidora)).toList());
		return new TarefaRamificada<>(consumir);
	}

	public TarefaRamificada<R> filtrar(Predicate<R> filtro) {
		TarefaAssincrona<List<TarefaAssincrona<R>>> ramosFiltrados = ramos
				.transformar(r -> r.stream().filter(ramo -> filtro.test(ramo.aguardar())).toList());
		return new TarefaRamificada<>(ramosFiltrados);
	}
	
	public List<R> aguardar() {
		return this.unificar().aguardar();
	}
	
	public <U> U aguardar(Function<List<R>, U> funcaoUnificadora) {
		return this.unificar(funcaoUnificadora).aguardar();
	}
}

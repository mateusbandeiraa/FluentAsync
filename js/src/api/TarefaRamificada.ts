import TarefaAssincrona from "./TarefaAssincrona.js";

export default class TarefaRamificada<R> {
  ramos: TarefaAssincrona<TarefaAssincrona<R>[]>;

  private constructor(ramos: TarefaAssincrona<TarefaAssincrona<R>[]>) {
    this.ramos = ramos;
  }

  static instanciar<U, R>(
    tarefaOriginal: TarefaAssincrona<U>,
    funcaoRamificadora: (a: U) => Array<R>
  ): TarefaRamificada<R> {
    const futurosRamos = tarefaOriginal
      .transformar(funcaoRamificadora)
      .transformar((valores) =>
        valores.map((valor) => new TarefaAssincrona(valor))
      );
    return new TarefaRamificada(futurosRamos);
  }

  obterPromise() {
    return this.ramos.obterPromise();
  }

  transformar<U>(transformadora: (value: R) => U): TarefaRamificada<U> {
    return new TarefaRamificada(
      this.ramos.transformar((ramos) => {
        return ramos.map((ramo) => ramo.transformar(transformadora));
      })
    );
  }

  consumir<U>(consumidora: (value: R) => U): TarefaRamificada<R> {
    return new TarefaRamificada(
      this.ramos.transformar((ramos) => {
        return ramos.map((ramo) => ramo.consumir(consumidora));
      })
    );
  }

  unificar<U>(funcaoUnificadora?: ((valores: R[]) => U)): TarefaAssincrona<R[]> | TarefaAssincrona<U> {
    const tarefaUnificada = new TarefaAssincrona(async () => {
      return await this.ramos
        .transformar((ramos) => ramos.map((ramo) => ramo.obterPromise()))
        .transformar((promises) => Promise.all(promises))
        .obterPromise();
    });
    if(!funcaoUnificadora){
      return tarefaUnificada;
    } else {
      return tarefaUnificada.transformar(funcaoUnificadora);
    }
  }
}

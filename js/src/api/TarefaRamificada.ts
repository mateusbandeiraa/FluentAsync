import TarefaAssincrona from "./TarefaAssincrona.js";

export default class TarefaRamificada<R> {
  futurosRamos: TarefaAssincrona<Array<TarefaAssincrona<R>>>;

  private constructor(
    futurosRamos: TarefaAssincrona<Array<TarefaAssincrona<R>>>
  ) {
    this.futurosRamos = futurosRamos;
  }

  static instanciar<U, R>(
    tarefaOriginal: TarefaAssincrona<U>,
    funcaoRamificadora: (a: U) => Array<R>
  ) {
    const futurosRamos = tarefaOriginal
      .transformar(funcaoRamificadora)
      .transformar((valores) =>
        valores.map((valor) => new TarefaAssincrona(() => valor))
      );
    return new TarefaRamificada(futurosRamos);
  }

  obterPromise() {
    return this.futurosRamos;
  }

  transformar<U>(transformadora: (value: R) => U): TarefaRamificada<U> {
    return new TarefaRamificada(
      this.futurosRamos.transformar((ramos) => {
        return ramos.map((ramo) => ramo.transformar(transformadora));
      })
    );
  }

  consumir<U>(
    consumidora: (value: R) => U
  ): TarefaRamificada<TarefaAssincrona<R>> {
    return new TarefaRamificada(
      this.futurosRamos.transformar((ramos) => {
        return ramos.map((ramo) => ramo.consumir(consumidora));
      })
    );
  }

  unificar<U>(funcaoUnificadora?: (values: Array<R>) => U) {
    if (funcaoUnificadora) {
      return this.futurosRamos
        .transformar(async (ramos) => {
          return await ramos.map(async (ramo) => await ramo.obterPromise());
        })
        .transformar(
          async (ramos) =>
            await Promise.all(ramos).then((results) =>
              funcaoUnificadora(results)
            )
        );
    } else {
      return this.futurosRamos
        .transformar(async (ramos) => {
          return await ramos.map(async (ramo) => await ramo.obterPromise());
        })
        .transformar(
          async (ramos) => await Promise.all(ramos).then((results) => results)
        );
    }
  }
}

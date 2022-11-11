import TarefaAssincrona from "./TarefaAssincrona";

export default class TarefaRamificada<R> {
  futurosRamos: TarefaAssincrona<Array<TarefaAssincrona<R>>>;

  constructor({
    tarefaOriginal,
    funcaoRamificadora,
    futurosRamos,
  }: {
    tarefaOriginal?: TarefaAssincrona<any>;
    funcaoRamificadora?: (a: any) => Array<R>;
    futurosRamos?: TarefaAssincrona<Array<TarefaAssincrona<R>>>;
  }) {
    if (tarefaOriginal && funcaoRamificadora && !futurosRamos) {
      this.futurosRamos = tarefaOriginal
        .transformar(funcaoRamificadora)
        .transformar((valores) =>
          valores.map((valor) => new TarefaAssincrona(() => valor))
        );
    } else if (!tarefaOriginal && !funcaoRamificadora && futurosRamos) {
      this.futurosRamos = futurosRamos;
    } else {
      throw new Error("Utilização incorreta do construtor");
    }
  }

  obterPromise() {
    return this.futurosRamos;
  }

  transformar<U>(transformadora: (value: R) => U): TarefaRamificada<U> {
    return new TarefaRamificada({
      futurosRamos: this.futurosRamos.transformar((ramos) => {
        return ramos.map((ramo) => ramo.transformar(transformadora));
      }),
    });
  }

  consumir<U>(
    consumidora: (value: R) => U
  ): TarefaRamificada<TarefaAssincrona<R>> {
    return new TarefaRamificada({
      futurosRamos: this.futurosRamos.transformar((ramos) => {
        return ramos.map((ramo) => ramo.consumir(consumidora));
      }),
    });
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
        async (ramos) =>
          await Promise.all(ramos).then((results) =>
            results
          )
      );
    }
  }
}

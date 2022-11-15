import TarefaRamificada from "./TarefaRamificada.js";

export default class TarefaAssincrona<T> {
  tarefa: Promise<T>;
  constructor(tarefa: Promise<T> | (() => Promise<T>) | (() => T)) {
    if (tarefa instanceof Promise<T>) {
      this.tarefa = tarefa;
    } else {
      const a = tarefa();
      if (a instanceof Promise) {
        this.tarefa = a;
      } else {
        this.tarefa = new Promise((resolve) => {
          resolve(a);
        });
      }
    }
  }

  obterPromise(): Promise<T> {
    return this.tarefa;
  }

  transformar<U>(transformadora: (value: T) => U | PromiseLike<U>) {
    return new TarefaAssincrona(this.tarefa.then(transformadora));
  }

  consumir(consumidora: (value: T) => void) {
    return new TarefaAssincrona(async () => {
      consumidora(await this.tarefa);
      return this;
    })
  }

  ramificar<U>(funcaoRamificadora: (a: T) => Array<U>): TarefaRamificada<U> {
    return TarefaRamificada.instanciar(this, funcaoRamificadora);
  }
}

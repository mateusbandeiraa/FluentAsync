import TarefaRamificada from "./TarefaRamificada.js";

export default class TarefaAssincrona<T> {
  tarefa: Promise<T>;
  constructor(tarefa: Promise<T> | (() => Promise<T>) | (() => T) | T) {
    if (tarefa instanceof Promise<T>) {
      this.tarefa = tarefa;
    } else if (tarefa instanceof Function) {
      const retorno = tarefa();
      if (retorno instanceof Promise<T>) {
        this.tarefa = retorno;
      } else {
        this.tarefa = new Promise((resolve) => resolve(retorno));
      }
    } else {
      this.tarefa = new Promise((resolve) => resolve(tarefa));
    }
  }

  obterPromise(): Promise<T> {
    return this.tarefa;
  }

  transformar<U>(transformadora: (value: T) => U): TarefaAssincrona<U> {
    return new TarefaAssincrona(this.tarefa.then(transformadora));
  }

  consumir(consumidora: (value: T) => void): TarefaAssincrona<T> {
    return new TarefaAssincrona(
      this.tarefa.then((val) => {
        consumidora(val);
        return val;
      })
    );
  }

  ramificar<U>(funcaoRamificadora: (a: T) => Array<U>): TarefaRamificada<U> {
    return TarefaRamificada.instanciar(this, funcaoRamificadora);
  }
}

import TarefaAssincrona from "./api/TarefaAssincrona.js";

const wait = (ms: number) => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

const tarefa = new TarefaAssincrona(async () => {
  await wait(1000);
  return "Hello World!";
});

async function main(): Promise<void> {
  console.time();
  await tarefa
    .consumir(console.log)
    .ramificar(s => s.split(""))
    .consumir(console.log)
    .unificar(s => s.join(""))
    .consumir(console.log)
    .obterPromise();
    console.timeEnd();
}

main().catch(console.error);

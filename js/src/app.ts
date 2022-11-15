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
    .transformar(async (s) => {
      await wait(1000);
      return s += "😀";
    })
    .consumir(console.log)
    .obterPromise();
    console.timeEnd();
}

main().catch(console.error);

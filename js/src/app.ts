import TarefaAssincrona from "./api/TarefaAssincrona.js";

const wait = (ms: number) => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

const tarefa = new TarefaAssincrona(async () => {
  await wait(1500);
  return "Hello World!";
});

async function main(): Promise<void> {
  await tarefa
    .ramificar((s) => s.split(""))
    .transformar((s) => s.toUpperCase())
    .unificar((chars) => chars.join(""))
    .consumir(console.log)
    .obterPromise();
}

main().catch(console.error);

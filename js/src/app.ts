import fetch from "node-fetch";
import https from "https";
import TarefaAssincrona from "./api/TarefaAssincrona.js";

const wait = (ms: number) => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

const tarefa = new TarefaAssincrona(async () => {
  await wait(1000);
  return "Hello World!";
});

const agent = new https.Agent({ rejectUnauthorized: false });

type Personagem = {
  id: Number;
  name: String;
  films: Filme[] | String;
};

type Filme = {
  id: Number;
  title: String;
};

async function main(): Promise<void> {
  console.time();
  const dados = (await new TarefaAssincrona(async () => {
    return await buscarPersonagens();
  })
    .ramificar((personagens) => personagens)
    .transformar(async (personagem) => {
      const filmesAnterior = personagem.films as unknown as String[];
      const novosFilmes = (await new TarefaAssincrona(filmesAnterior)
        .ramificar((filmes) => filmes)
        .transformar(async (urlFilme) => await buscarFilme(urlFilme))
        .unificar()
        .obterPromise()) as Filme[];

      personagem.films = novosFilmes;

      return personagem;
    })
    .unificar()
    .obterPromise()) as Personagem[];
  console.timeEnd();

  console.table(
    dados.map((personagem) => ({
      nome: personagem.name,
      filmes: (personagem.films as Filme[])
        .map((filme) => filme.title)
        .join("; "),
    }))
  );
}

const buscarPersonagens = (): Promise<Personagem[]> => {
  return fetch("https://swapi.dev/api/people", { agent })
    .then((res) => res.json() as any)
    .then((json) => json.results as Personagem[]);
};

const buscarFilme = (urlFilme: String): Promise<Filme> => {
  return fetch("" + urlFilme, { agent }).then(
    (res) => res.json() as unknown as Filme
  );
};

main().catch(console.error);

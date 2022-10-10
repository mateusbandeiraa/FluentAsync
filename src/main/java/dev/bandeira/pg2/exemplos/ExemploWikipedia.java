package dev.bandeira.pg2.exemplos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.bandeira.pg2.api.TarefaAssincrona;
import kong.unirest.Unirest;

public class ExemploWikipedia {
	public static void main(String[] args) {
		final String WIKI = "https://pt.wikipedia.org/wiki/";

		Unirest.config().defaultBaseUrl(WIKI);

		final String verbeteInicial = "Pirelli";
		final String verbeteFinal = "Ayrton_Senna";

		final Queue<String> fila = new LinkedList<>();
		final Set<String> verbetesVisitados = new HashSet<>();
		fila.offer(verbeteInicial);

		int iteracoes = 1;
		while (!fila.isEmpty()) {
			String verbete = fila.poll();

			if (verbetesVisitados.contains(verbete)) {
				continue;
			} else if (verbete.equalsIgnoreCase(verbeteFinal)) {
				System.out.println("Encontrei o verbete final em %d iterações.".formatted(iteracoes));
				break;
			}

			verbetesVisitados.add(verbete);

			new TarefaAssincrona<>(getVerbete(verbete)) //
					.ramificar(ExemploWikipedia::extrairVerbetes) //
					.filtrar(v -> !verbetesVisitados.contains(v)) //
					.consumir(fila::offer) //
					.unificar() //
					.aguardar(); //

			if(iteracoes == 1000) {
				break;
			}
			iteracoes++;
		}
		System.out.println(verbetesVisitados);
	}

	public static Supplier<String> getVerbete(String verbete) {
		System.out.println(verbete);
		return () -> Unirest.get(verbete).asString().getBody();
	}

	private static List<String> extrairVerbetes(String paginaWiki) {
		final Pattern WIKI_REGEX = Pattern.compile("\\/wiki\\/(\\w+?)\"");

		Matcher matcher = WIKI_REGEX.matcher(paginaWiki);
		HashSet<String> links = new HashSet<>();
		while (matcher.find()) {
			links.add(matcher.group(1));
		}
		return new ArrayList<>(links);
	}
}

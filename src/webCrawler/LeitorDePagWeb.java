package webCrawler;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class LeitorDePagWeb {
	private static ExecutorService threadPool = Executors.newFixedThreadPool(4);
	private static Set<String> LINKS_VISITADOS = new HashSet<>();
	private static Set<String> LINKS_ERRADOS = new HashSet<>();
	public static int NIVEL_ATUAL = 0;
	public static Map<String, Set<String>> indexador = new HashMap<>();
	private static Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(new String[]{"A", "O", "E", "AS", "OS", "UM", "UMA", "UNS", "UMAS"}));
	
	public static String lePagina(String url) {
		try {
			if(!LINKS_VISITADOS.contains(url)) {
				indexarPaginas(url);
				LINKS_VISITADOS.add(url);
				URL u = new URL(url);
				Scanner leitor = new Scanner(u.openStream());
				
				System.out.println("Entrando no link: " + url + " Nível: " + NIVEL_ATUAL);
				
				StringBuilder pagina = new StringBuilder();
				
				while (leitor.hasNextLine()) {
					pagina.append(leitor.nextLine());
				}
				
				leitor.close();
			
				return pagina.toString();
			}
		} catch (Exception ex) {
			if(!LINKS_ERRADOS.contains(url)) {
				LINKS_ERRADOS.add(url);
			}
		}
		return "";
	}
	
	public static String palavraToChave(String palavra) {
		return palavra.toUpperCase();
	}
	
	public static void indexarPaginas(String url) {
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {}
		
		String body = doc.body().text();
		
		String[] palavras = body.split("\\s+");
		
		Arrays.stream(palavras)
		.map((p) -> palavraToChave(p))
		.filter((p) -> !isStopWord(p))
		.forEach((palavra) -> {		
			palavra = palavraToChave(palavra);
			
			Set<String> indicePorPalavra = null;
			
			if (indexador.containsKey(palavra)) {
				indicePorPalavra = indexador.get(palavra);
			} else {
				indicePorPalavra = new HashSet<>();
				indexador.put(palavra, indicePorPalavra);
			}
			
			indicePorPalavra.add(url);
		});
	}
	
	public static boolean isStopWord(String palavra) {
		if (palavra.length() <= 2) {
			return true;
		}
		
		return STOP_WORDS.contains(palavra);
	}
	
	public static Set<String> buscar(String palavra) {
		String chave = palavraToChave(palavra);
		return indexador.get(chave);
	}
	
	public static void acharLinks(String url, int nivel, int nivelDeParada, String palavraBuscar) {
		threadPool.submit(() -> {
			if(NIVEL_ATUAL == nivelDeParada) {
				threadPool.shutdownNow();
				
			}else {
				NIVEL_ATUAL = nivel;
				String pagina = lePagina(url);
				RegexLink.achandoLinks(pagina, nivel, nivelDeParada, palavraBuscar);
				
				System.out.println("Esses foram os links onde foi encontrado a palavra: " + palavraBuscar);
				Set<String> caminhos = buscar(palavraBuscar);
				for (String string : caminhos) {
					System.out.println(string);
				}
			}
		});
	}
}

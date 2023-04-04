package webCrawler;

public class Main {
	public static void main(String[] args) {
		LeitorDePagWeb.acharLinks("https://pt.wikipedia.org/wiki/Campina_Grande", LeitorDePagWeb.NIVEL_ATUAL, 2, "escola");
	}
}

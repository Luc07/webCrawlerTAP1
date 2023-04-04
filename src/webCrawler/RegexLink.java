package webCrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexLink {
	public static final String LINK_REGEX = 
			"<a\\s+(?:[^>]*?\\s+)?href=([\"'])(http.*?)\\1";
	
	public static void achandoLinks(String url, int nivel, int nivelDeParada, String palavra) {
		
		Pattern linkPattern = Pattern.compile(LINK_REGEX);
		Matcher linkMatcher = linkPattern.matcher(url);
		nivel = nivel+1;
		
		while (linkMatcher.find()) {
			LeitorDePagWeb.acharLinks(linkMatcher.group(2), nivel, nivelDeParada, palavra);
		}
		
	}
	
}

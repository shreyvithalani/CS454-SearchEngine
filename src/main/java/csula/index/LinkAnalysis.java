package csula.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkAnalysis {

	public static Long totalDocuments = null;
	public static Map<String, Set<String>> outgoingLinks = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> incomingLinks = new HashMap<String, Set<String>>();

	public static String getPath() {
		return "/Users/dhruvparmar91/downloads/articles";
	}

	public static void main(String[] args) {

		File[] files = new File(getPath()).listFiles();
		totalDocuments = (long) 0;
		processFiles(files);
		System.out.println(totalDocuments);
		PageRank pageRank = new PageRank(totalDocuments, outgoingLinks, incomingLinks);

	}

	public static void processFiles(File[] files) {

		for (File file : files) {
			if (file.isDirectory()) {
				processFiles(file.listFiles());
			} else {
				if (file.isHidden()) {
				} else {
					totalDocuments++;
					getOutgoingLinks(file);
				}
			}
		}

		getIncomingLinks();
	}

	public static void getOutgoingLinks(File file) {

		String[] splits = file.getAbsolutePath().split("articles");
		String fileUrl = splits[1];

		try {
			Document document = Jsoup.parse(file, "UTF-8");
			Elements links = document.select("a[href]");
			Set<String> out = new HashSet<String>();
			for (Element element : links) {
				String url = element.attr("href");
				String[] parts = url.split("articles");
				if (parts.length > 1) {
					out.add(parts[1]);
				}
			}
			outgoingLinks.put(fileUrl, out);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void getIncomingLinks() {

		for (String url : outgoingLinks.keySet()) {
			Set<String> in = new HashSet<String>();

			for (String link : outgoingLinks.keySet()) {
				if (!url.equals(link)) {
					if (outgoingLinks.get(link).contains(url)) {
						in.add(link);
					}
				}
			}
			incomingLinks.put(url, in);

		}

	}

}
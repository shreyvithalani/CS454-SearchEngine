package csula.crawler;

import java.io.IOException;
import java.util.Queue;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

public class MultithredCrawler implements Runnable {
	// private Thread t;
	Queue<Entry> q;
	Integer depth;
	Boolean extractProcess;
	String threadName;
	Set<String> linkset;

	public MultithredCrawler(String threadName, Queue<Entry> q, Integer depth, Boolean extractProcess,
			Set<String> linkset) {
		this.q = q;
		this.depth = depth;
		this.extractProcess = extractProcess;
		this.threadName = threadName;
		this.linkset = linkset;
	}

	@Override
	public void run() {
		System.out.println();
		System.out.println("----------> Running thread: " + threadName);
		System.out.println();
		try {
			runCrawler();
		} catch (SAXException | TikaException e) {
			e.printStackTrace();
		}

	}
	/*
	 * public void start() { System.out.println("Starting " + threadName); if (t
	 * == null) { t = new Thread(this, threadName); t.start(); } }
	 */

	public void runCrawler() throws SAXException, TikaException {

		MongoJdbc mongoJdbc = new MongoJdbc();

		while (!q.isEmpty()) {
			Entry currentLinkEntry = q.remove();
			Integer d = currentLinkEntry.getDepth();

			try {
				Document doc = Jsoup.connect(currentLinkEntry.getLink()).get();
				String title = doc.title();

				org.jsoup.select.Elements extractedLinks = doc.select("a");
				for (int i = 0; i < extractedLinks.size(); i++) {
					if (d < depth) {
						Element currentItem = extractedLinks.get(i);
						String currentLink = currentItem.attr("abs:href");
						int tempDepth = d + 1;
						if (currentLink.startsWith("http://") || currentLink.startsWith("www")
								|| currentLink.startsWith("https://")) {
							if (!linkset.contains(currentLink)) {

								q.add(new Entry(currentLink, tempDepth));
								linkset.add(currentLink);

							}

						}
					} else {
						break;
					}
				}

				mongoJdbc.storeDocument(currentLinkEntry.getLink().toString(), doc, title);

				if (extractProcess) {
					mongoJdbc.StoreExtractedData(currentLinkEntry.getLink().toString(), doc);

				}

			}

			catch (IOException e) {

			}

		}

		mongoJdbc.closeConnection();
		System.out.println();
		System.out.println("----------> Thread " + threadName + " completed task");
		System.out.println();

	}

}

package csula.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

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

	public void savetofile(String title, org.jsoup.nodes.Document doc) throws IOException {
		String folderpath = System.getProperty("user.home") + "/Desktop/crawledfiles/" + title;
		// String filepath = folderpath + title + ".html";
		String filestring = doc.html();
		// System.getProperty ("user.home")
		FileWriter writer = new FileWriter(folderpath);
		BufferedWriter buffWriter = new BufferedWriter(writer);
		buffWriter.write(filestring);
		buffWriter.close();
		writer.close();

	}

	public void runCrawler() throws SAXException, TikaException {

		MongoJdbc mongoJdbc = new MongoJdbc();

		while (!q.isEmpty()) {
			Entry currentLinkEntry = q.remove();
			Integer d = currentLinkEntry.getDepth();
			

			try {
				Document doc = Jsoup.connect(currentLinkEntry.getLink()).get();
				mongoJdbc.storeMD5(doc.title(), currentLinkEntry.getLink().split("/data/")[1]);
				String title = doc.title();

				org.jsoup.select.Elements extractedLinks = doc.select("a");
				for (int i = 0; i < extractedLinks.size(); i++) {
					if (d < depth) {
						Element currentItem = extractedLinks.get(i);
						String currentLink = currentItem.attr("abs:href");
						System.out.println(currentLink);
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
<<<<<<< HEAD

				// mongoJdbc.storeDocument(currentLinkEntry.getLink().toString(),
				// doc, title);
				savetofile(currentLinkEntry.getLink().split("/data/")[1], doc);
=======
				
				savetofile(title,doc);
				//mongoJdbc.storeDocument(currentLinkEntry.getLink().toString(), doc, title);

>>>>>>> origin/master
				if (extractProcess) {
					// mongoJdbc.StoreExtractedData(currentLinkEntry.getLink().toString(),
					// doc);

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
	
	public void savetofile(String title, org.jsoup.nodes.Document doc) throws IOException{
		String folderpath = System.getProperty ("user.home") + "/Desktop/crawleddata/" + title +".html";
		//String filepath = folderpath + title + ".html";
	    String filestring = doc.html();
	    //System.getProperty ("user.home")
	    FileWriter writer = new FileWriter(folderpath);
	    BufferedWriter buffWriter = new BufferedWriter(writer);
	    buffWriter.write(filestring);
	    buffWriter.close();
	    writer.close();
	    
	    
		
	}

}

package csula.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

public class Crawler {

	public void runCrawler(String link, Integer depth, Boolean extractProcess) throws SAXException, TikaException {

		MongoJdbc mongoJdbc = new MongoJdbc();
		Queue<Entry> q = new LinkedList<Entry>();
		Set<String> linkset = new HashSet<String>();
		q.add(new Entry(link, 0));
		linkset.add(link);

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
				savetofile(title,doc);
//				mongoJdbc.storeDocument(currentLinkEntry.getLink().toString(), doc, title);
//
//				if (extractProcess) {
//					mongoJdbc.StoreExtractedData(currentLinkEntry.getLink().toString(), doc);
//
//				}

			}

			catch (IOException e) {

			}

		}

//		mongoJdbc.closeConnection();

	}
	
	public void savetofile(String title, org.jsoup.nodes.Document doc) throws IOException{
		String folderpath = System.getProperty ("user.home") + "/Desktop/crawledfiles/" + title +".html";
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

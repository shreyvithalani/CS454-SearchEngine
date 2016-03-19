package csula.crawler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoJdbc {
	private MongoClient mongoClient;

	public MongoJdbc() {
		this.mongoClient = new MongoClient("localhost");
	}

	public void storeDocument(String url, org.jsoup.nodes.Document doc, String title) throws IOException {

		System.out.println(url);
		
	

		// MongoClient mongoClient = new MongoClient("localhost");
		MongoDatabase db = mongoClient.getDatabase("bigtable");
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("URL", url);
		map.put("content", doc.toString());
		map.put("title", title);

		db.getCollection("crawlerdata").insertOne(new Document(map));
		// mongoClient.close();

	}

	public void StoreExtractedData(String url, org.jsoup.nodes.Document doc)
			throws IOException, SAXException, TikaException {

		// MongoClient mongoClient = new MongoClient("localhost");
		MongoDatabase db = mongoClient.getDatabase("bigtable");
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		java.util.Map<String, Object> map1 = new HashMap<String, Object>();
		java.util.Map<String, Object> map2 = new HashMap<String, Object>();

		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

		for (Element image : images) {
			map2.put("images-src", image.toString());
		}

		BodyContentHandler handler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		InputStream inputstream = new ByteArrayInputStream(doc.toString().getBytes());
		// ContentHandler textHandler = new BodyContentHandler(int writeLimit);
		ParseContext pcontext = new ParseContext();

		// Html parser
		HtmlParser htmlparser = new HtmlParser();
		htmlparser.parse(inputstream, handler, metadata, pcontext);

		map.put("URL", url);
		map.put("content", handler.toString().replaceAll("\\s+", " "));
		map.put("metadata", map1);
		map.put("Images", map2);

		String[] metadataNames = metadata.names();

		for (String name : metadataNames) {
			map1.put(name.replaceAll("[^a-zA-Z0-9_-]", "-"), metadata.get(name));
		}

		db.getCollection("extractordata").insertOne(new Document(map));
		// mongoClient.close();

	}

	public void closeConnection() {
		this.mongoClient.close();
	}
}

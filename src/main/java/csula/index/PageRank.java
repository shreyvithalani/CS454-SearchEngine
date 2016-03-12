package csula.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class PageRank {

	public final Double lambda = 0.15;
	public static Double defaultRank;
	public Long N;
	public Map<String, Set<String>> outgoingLinks;
	public Map<String, Set<String>> incomingLinks;

	public Map<String, Double> defaultMap;
	public Map<String, Double> finalMap;

	public PageRank(Long totalDocuments, Map<String, Set<String>> outgoingLinks,
			Map<String, Set<String>> incomingLinks) {

		this.N = totalDocuments;
		PageRank.defaultRank = (1.0 / totalDocuments);
		this.outgoingLinks = outgoingLinks;
		this.incomingLinks = incomingLinks;
		this.defaultMap = new HashMap<String, Double>();
		this.finalMap = new HashMap<String, Double>();

		this.defaultRanking();
		this.rankingProcess();
		this.storeRanking();

	}

	public void defaultRanking() {

		for (String link : this.incomingLinks.keySet()) {
			this.defaultMap.put(link, defaultRank);
		}

	}

	private void rankingProcess() {

		for (int i = 0; i < 10; i++) {
			for (String link : this.defaultMap.keySet()) {
				Double rank = 0.0;
				for (String incoming : this.incomingLinks.get(link)) {
					Double term = (1.0 - lambda)
							* (this.defaultMap.get(incoming) / this.outgoingLinks.get(incoming).size());
					rank += term;
				}
				rank += lambda / N;
				this.finalMap.put(link, rank);
			}
			for (String link : this.finalMap.keySet()) {
				this.defaultMap.put(link, this.finalMap.get(link));
			}
		}
	}

	private void storeRanking() {
		for (String link : this.finalMap.keySet()) {

			MongoClient mongoClient = new MongoClient("localhost");
			MongoDatabase db = mongoClient.getDatabase("bigtable");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("url",link);
			map.put("rank", this.finalMap.get(link));
			db.getCollection("linkdata").insertOne(new Document(map));
		}

	}

}

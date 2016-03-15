package csula.index;

import java.util.Collection;
import java.util.Collections;
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

	public Map<String, Double> mainMap;
	public Map<String, Double> finalMap;
	public Double maxRank;
	private MongoClient mongoClient;

	public PageRank(Long totalDocuments, Map<String, Set<String>> outgoingLinks,
			Map<String, Set<String>> incomingLinks) {

		this.N = totalDocuments;
		PageRank.defaultRank = (1.0 / totalDocuments);
		this.outgoingLinks = outgoingLinks;
		this.incomingLinks = incomingLinks;
		this.mainMap = new HashMap<String, Double>();
		this.finalMap = new HashMap<String, Double>();

		this.defaultRanking();
		this.rankingProcess();
		this.maxRank = Collections.max(this.finalMap.values());
		this.normalizeValues();
		this.storeRanking();

	}

	private void normalizeValues() {
		for (String key : this.mainMap.keySet()) {
			this.mainMap.put(key, this.mainMap.get(key) / this.maxRank);
		}

	}

	public void defaultRanking() {

		for (String link : this.incomingLinks.keySet()) {
			this.mainMap.put(link, defaultRank);
		}

	}

	private void rankingProcess() {

		for (int i = 0; i < 10; i++) {
			for (String link : this.mainMap.keySet()) {
				Double rank = 0.0;
				for (String incoming : this.incomingLinks.get(link)) {
					if (this.outgoingLinks.get(incoming).size() == 0) {
						Double term = 0.0;
						rank += term;
					} else {
						Double term = (1.0 - lambda)
								* (this.mainMap.get(incoming) / this.outgoingLinks.get(incoming).size());
						rank += term;
					}
				}
				rank += lambda / N;
				this.finalMap.put(link, rank);
			}
			for (String link : this.finalMap.keySet()) {
				this.mainMap.put(link, this.finalMap.get(link));
			}
		}
	}

	private void storeRanking() {
		for (String link : this.mainMap.keySet()) {

			mongoClient = new MongoClient("localhost");
			MongoDatabase db = mongoClient.getDatabase("bigtable");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("url", link);
			map.put("rank", this.mainMap.get(link));
			db.getCollection("linkdata").insertOne(new Document(map));
			mongoClient.close();
		}
		

	}

}

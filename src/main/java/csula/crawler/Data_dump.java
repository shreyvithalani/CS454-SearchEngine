package csula.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Data_dump {

	public static void main(String[] args) {

		MongoClient mongoClient = new MongoClient("localhost");

		MongoDatabase db = mongoClient.getDatabase("bigtable");
		ArrayList<Document> documentList = db.getCollection("extractordata").find().into(new ArrayList<Document>());

		// JSONObject obj1 = new JSONObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		JSONArray jsonArray = new JSONArray();
		for (Document doclist : documentList) {
			JSONObject obj = new JSONObject();
			obj.put("Url", doclist.get("URL"));
			obj.put("Metadata", doclist.get("metadata"));
			jsonArray.put(obj);
			// obj1.put("Dump", obj);
			// System.out.println(doclist.get("URL"));
		}

		// System.out.println(obj);
		String jsonString = gson.toJson(jsonArray);
		// System.out.println(jsonArray);
		mongoClient.close();

		try {
			File file = new File("/Users/dhruvparmar91/Desktop/Data.json");
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);

			fileWriter.write(jsonString);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

package csula.crawler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class PreviousMain {

	public static void main(String[] args) throws SAXException, TikaException, ParseException, InterruptedException {
		String link = null;
		Integer depth = null;
		Boolean extractProcess = null;

		Options options = new Options();
		options.addOption("d", true, "provide depth for crawling");
		options.addOption("u", true, "provide seed url for crawling");
		options.addOption("e", false, "to turn on extraction mode ");
		CommandLineParser parser = new BasicParser();
		org.apache.commons.cli.CommandLine cmd = parser.parse(options, args);
		// Crawler crawl = new Crawler();

		if (cmd.hasOption("d") && cmd.hasOption("u")) {
			depth = Integer.parseInt(cmd.getOptionValue("d"));
			link = cmd.getOptionValue("u");
			if (cmd.hasOption("e")) {
				extractProcess = true;
			} else {
				extractProcess = false;
			}

			Queue<Entry> q = new ConcurrentLinkedQueue<Entry>();
			Set<String> linkset = Collections.synchronizedSet(new HashSet<String>());
			q.add(new Entry(link, 0));
			linkset.add(link);
			MultithredCrawler[] arr = new MultithredCrawler[5];

			for (int i = 0; i < 5; i++) {
				String threadName = "CRAWLER" + String.valueOf(i);
				arr[i] = new MultithredCrawler(threadName, q, depth, extractProcess, linkset);
				// arr[i].start();
				if (arr[i] == arr[0]) {
					Thread.sleep(1000);
				}
			}
			/*
			 * arr[0] = new MultithredCrawler("CRAWLER1", q, depth,
			 * extractProcess, linkset); arr[1] = new
			 * MultithredCrawler("CRAWLER2", q, depth, extractProcess, linkset);
			 * arr[2] = new MultithredCrawler("CRAWLER3", q, depth,
			 * extractProcess, linkset); arr[3] = new
			 * MultithredCrawler("CRAWLER4", q, depth, extractProcess, linkset);
			 * arr[4] = new MultithredCrawler("CRAWLER5", q, depth,
			 * extractProcess, linkset);
			 * 
			 * arr[0].start(); Thread.sleep(3000); arr[1].start();
			 * arr[2].start(); arr[3].start(); arr[4].start();
			 */

			for (int i = 0; i < 5; i++) {
				// arr[i].join();
			}

			// Crawler crawler = new Crawler();
			// crawler.runCrawler("http://web.mit.edu/", 1, true);

		}

	}
}

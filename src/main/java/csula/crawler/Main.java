package csula.crawler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws SAXException, TikaException, ParseException, InterruptedException {

		// Single Thread Crawler Application
		// Crawler crawler = new Crawler();
		// crawler.runCrawler("http://web.mit.edu/", 1, true);

		String link = null;
		Integer depth = null;
		Boolean extractProcess = null;

		// Command Line Options
		Options options = new Options();
		options.addOption("d", true, "provide depth for crawling");
		options.addOption("u", true, "provide seed url for crawling");
		options.addOption("e", false, "to turn on extraction mode ");
		CommandLineParser parser = new BasicParser();
		org.apache.commons.cli.CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("d") && cmd.hasOption("u")) {
			depth = Integer.parseInt(cmd.getOptionValue("d"));
			link = cmd.getOptionValue("u");
			if (cmd.hasOption("e")) {
				extractProcess = true;
			} else {
				extractProcess = false;
			}

			// Multi-threaded Crawler Application
			Queue<Entry> q = new ConcurrentLinkedQueue<Entry>();
			Set<String> linkset = Collections.synchronizedSet(new HashSet<String>());
			q.add(new Entry(link, 0));
			linkset.add(link);
			MultithredCrawler[] arr = new MultithredCrawler[5];

			for (int i = 0; i < 5; i++) {
				String threadName = "CRAWLER" + String.valueOf(i);
				arr[i] = new MultithredCrawler(threadName, q, depth, extractProcess, linkset);

			}

			ExecutorService executor = Executors.newFixedThreadPool(arr.length);
			for (int i = 0; i < 5; i++) {
				executor.execute(arr[i]);
				if (i == 0) {
					Thread.sleep(1000);
				}
				else {
					Thread.sleep(500);
				}
			}
			executor.shutdown();

		} else {
			System.out.println();
			System.out.println("Please provide valid commands for crawler to execute");
			System.out.println();
		}
	}
}

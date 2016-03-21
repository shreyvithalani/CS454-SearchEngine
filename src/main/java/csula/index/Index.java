package csula.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.jsoup.Jsoup;
import org.tartarus.snowball.ext.PorterStemmer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class Index {

	// static MyAnalyzer myAnalyzer = new MyAnalyzer();

	static PorterStemmer stemmer = new PorterStemmer();
	static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35, StandardAnalyzer.STOP_WORDS_SET);

	public static String indexdir = "indexdir";

	public static String dir = "/Users/dhruvparmar91/desktop/crawledfiles";

	// Directory indexDir = FSDirectory.open(new File(indexdir));

	IndexWriterConfig indexconfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);

	public static void contents(File dir, IndexWriter writer) throws SAXException, TikaException {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					// System.out.println("directory:" +
					// file.getCanonicalPath());
					contents(file, writer);
				} else {
					// System.out.println("file:" + file.getCanonicalPath());
					createIndex(file, writer);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createIndex(File file, IndexWriter writer) throws IOException, SAXException, TikaException {
		String path = file.getAbsolutePath();
		Document doc = new Document();

		InputStream fis = new FileInputStream(file);

		int maxStringLength = 10 * 1024 * 1024;
		WriteOutContentHandler handler = new WriteOutContentHandler(maxStringLength);
		ContentHandler contenthandler = new BodyContentHandler(handler);
		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser();
		parser.parse(fis, contenthandler, metadata, new ParseContext());
		String newString = contenthandler.toString().replaceAll("/[^a-zA-Z 0-9]+/g", " ");

		newString.replaceAll("\\s+", " ").trim().replaceAll("[-+$?^:•,@.&{}*/()`_!;%>·<|=#'\"0-9]", "");

		// stem(newString);

		Tokenizer tokenizer = new StandardTokenizer(Version.LUCENE_35, new StringReader(newString.toLowerCase()));
		final StandardFilter standardFilter = new StandardFilter(Version.LUCENE_35, tokenizer);
		final SnowballFilter snowballFilter = new SnowballFilter(standardFilter, stemmer);
		@SuppressWarnings("resource")
		final StopFilter stopFilter = new StopFilter(Version.LUCENE_35, snowballFilter, getStopWords());
		final CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
		stopFilter.reset();
		StringBuilder sb = new StringBuilder();

		while (stopFilter.incrementToken()) {
			final String token = charTermAttribute.toString().toString();
			stemmer.setCurrent(token);
			stemmer.stem();
			String word = stemmer.getCurrent();
			if (!getStopWords().contains(word))
				sb.append(stemmer.getCurrent()).append(System.getProperty("line.separator"));
		}

		Field filepath = new Field("path", path, Field.Store.YES, Field.Index.ANALYZED);
		filepath.setIndexOptions(IndexOptions.DOCS_ONLY);

		doc.add(filepath);
		Reader reader = new FileReader(file);

		doc.add(new Field("contents", reader, Field.TermVector.YES));

		writer.addDocument(doc);
		System.out.println("yes");
		fis.close();

		// IndexWriter index = new IndexWriter();

	}

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		// File currentDir = new File("/Users/shreyvithalani/Desktop/en"); //
		// current directory
		// contents(currentDir);
		Directory indexdir = FSDirectory.open(new File("indexdir"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);

		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		IndexWriter indexwriter = new IndexWriter(indexdir, config);

		contents(new File(dir), indexwriter);

		// indexwriter.optimize();
		indexwriter.close();

	}

	public static Set<String> getStopWords() {
		Set<String> set = new HashSet<String>();

		for (String str : StopWordSet)
			set.add(str);

		return set;
	}

	public static String[] StopWordSet = { "a", "as", "able", "about", "above", "according", "accordingly", "across",
			"actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone",
			"along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any",
			"anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate",
			"appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available",
			"away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before",
			"beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between",
			"beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause",
			"causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning",
			"consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could",
			"couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do",
			"does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight",
			"either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every",
			"everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few",
			"ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth",
			"four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going",
			"gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent",
			"having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit",
			"however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc",
			"indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is",
			"isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows",
			"known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like",
			"liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me",
			"mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my",
			"myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not",
			"nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on",
			"once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours",
			"ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps",
			"placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv",
			"rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively",
			"respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see",
			"seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious",
			"seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some",
			"somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon",
			"sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken",
			"tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their",
			"theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore",
			"therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third",
			"this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
			"together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two",
			"un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used",
			"useful", "uses", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was",
			"wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what",
			"whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby",
			"wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever",
			"whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder",
			"would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours",
			"yourself", "yourselves", "zero" };

	// public static String stem(String string) throws IOException {
	// TokenStream tokenizer = new StandardTokenizer(Version.LUCENE_35, new
	// StringReader(string));
	// tokenizer = new StandardFilter(Version.LUCENE_35, tokenizer);
	// tokenizer = new LowerCaseFilter(Version.LUCENE_35, tokenizer);
	// tokenizer = new PorterStemFilter(tokenizer);
	//
	// CharTermAttribute token =
	// tokenizer.getAttribute(CharTermAttribute.class);
	//
	// tokenizer.reset();
	//
	// StringBuilder stringBuilder = new StringBuilder();
	//
	// while(tokenizer.incrementToken()) {
	// if(stringBuilder.length() > 0 ) {
	// stringBuilder.append(" ");
	// }
	//
	// stringBuilder.append(token.toString());
	// }
	//
	// tokenizer.end();
	// tokenizer.close();
	//
	// return stringBuilder.toString();
	// }

}
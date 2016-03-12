package csula.index;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Index {
	
	
	static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35,StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	
	public static String indexdir = "indexdir";
	
	public static String dir = "/Users/dhruvparmar91/Desktop/en";
	
	//Directory indexDir = FSDirectory.open(new File(indexdir));
	
	IndexWriterConfig indexconfig = new IndexWriterConfig(Version.LUCENE_35,analyzer);	
	
	public static void contents(File dir,IndexWriter writer) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println("directory:" + file.getCanonicalPath());
					contents(file,writer);
				} else {
					//System.out.println("file:" + file.getCanonicalPath());
					createIndex(file,writer);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createIndex(File file,IndexWriter writer) throws IOException{
		String path = file.getCanonicalPath();
		Document doc = new Document();
		doc.add(new Field("path",path,Field.Store.YES,Field.Index.ANALYZED));
		
		Reader reader = new FileReader(file);
		
		doc.add(new Field("contents",reader,Field.TermVector.YES));
		
		writer.addDocument(doc);
		
		
		
		//IndexWriter index = new IndexWriter();
		
		
	}
	
	
	public static void main(String[] args) throws IOException {
		//File currentDir = new File("/Users/shreyvithalani/Desktop/en"); // current directory
		//contents(currentDir);
		Directory indexdir = FSDirectory.open(new File("indexdir"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		
		IndexWriter indexwriter = new IndexWriter(indexdir,config);
		
		contents(new File(dir),indexwriter);
		
		indexwriter.close();
		
		
	}

}
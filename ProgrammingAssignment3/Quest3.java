package team07.assign01;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

/**
 * Implements the lnc.ltn TF-IDF variant
 */
public class quest3  {
	private static final String File = "D:/test200/test200-train/train.pages.cbor-outlines.cbor";
    private static  String DEFAULT_SCORE_FILE = "default1.txt";
    private static final String INDEX_DIR = "index";
     private static final String PARAGRAPH_FILE = "D:/test200/test200-train/train.pages.cbor-paragraphs.cbor";

    private static HashMap<String, String> queries = null;
	
	
    
    
	  private static List<String> value(IndexSearcher searcher) throws IOException {
	        int maxResults = 100;
	        

	        List<String> out= new ArrayList<String>();

	        for (Map.Entry<String, String> que: queries.entrySet()) {
	            String queryId = que.getKey();
	            String query = que.getValue();

	            TopDocs tops = searcher.search(toQuery(query), maxResults);
	            ScoreDoc[] scoreDoc = tops.scoreDocs;

	            int rank = 1;
	            for (ScoreDoc score : scoreDoc) {
	                final Document doc = searcher.doc(score.doc);
	                String q = queryId + " " + "Q0" + " " + doc.getField("paragraphid").stringValue() + " " + rank + " " + score.score + " " + "team7" + "-" + "value";
	                out.add(q);
	                rank += 1;
	            			}  }
	        return out;
	    }
	
	
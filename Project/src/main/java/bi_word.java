
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.HashMap; 
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
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
public class bi_word {
	
	private static final String File = "";
    private static  String DEFAULT_SCORE_FILE ;
    private static final String INDEX_DIR = "index";
    private static final String PARAGRAPH_FILE = "";
    private static HashMap<String, String> queries = null;
	static HashMap<String,List<String>> bi_word =new HashMap<String,List<String>>();

    private static HashMap<String, String> QueriesMap = new HashMap<String, String>();	

    
    
    
    
    private static IndexWriter createWriter() throws IOException {
        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "paragraph.lucene");
        Directory indexDir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        return new IndexWriter(indexDir, config);
    }
    
    public static void buildIndex(String args) throws IOException {
    	String PARAGRAPH_FILE=args;
    	IndexWriter writer = createWriter();
        writer.deleteAll();  // ensure cleaned

        List<Document> documents = new ArrayList<Document>();
        final FileInputStream fileInputStream2 = new FileInputStream(new File(PARAGRAPH_FILE));
        for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2)) {
            String paraId = p.getParaId();
            String textOnly = p.getTextOnly();
            Document document = new Document();
            document.add(new StringField("paragraphid", paraId , Field.Store.YES));
            document.add(new TextField("text", textOnly, Field.Store.YES));
            documents.add(document);
            QueriesMap.put(paraId, textOnly);
        }

        writer.addDocuments(documents);
        writer.commit();
        writer.close();
    }
    
    
    public static List<String> tokenizeString(Analyzer analyzer, String string) {
	   	 List<String> tokens = new ArrayList<>();
	   	  try (TokenStream tokenStream  = analyzer.tokenStream(null, new StringReader(string))) {
	   	    tokenStream.reset();  // required
	   	    while (tokenStream.incrementToken()) {
	   	      tokens.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
	   	    }
	   	  } catch (Exception e) {
	   	    new RuntimeException(e);  // Shouldn't happen...
	   	  }
	   	  return tokens;
	   	
	}
    
    private static List<String> value(IndexSearcher searcher) throws IOException {
		  int maxResults = 1000;
		  

	        List<String> out= new ArrayList<String>();

	        for (Map.Entry<String, List<String>> que: bi_word.entrySet()) {
	            String queryId = que.getKey();
	            int rank = 1;
	            List<String> array=new  ArrayList<String>() ;
	           array = que.getValue();
	           ArrayList<String> Rep=new ArrayList<String>();
	            for (int i=0;i<array.size();i++) {
	            TopDocs tops = searcher.search(toQuery(array.get(i)), maxResults);
	            ScoreDoc[] scoreDoc = tops.scoreDocs;
	            

	           
	            for (ScoreDoc score : scoreDoc) {
	            	
	                final Document doc = searcher.doc(score.doc);
	                
	                if(Rep.contains(doc.getField("paragraphid").stringValue())) {
	                	
	                	
	                }else {
	                Rep.add(doc.getField("paragraphid").stringValue());
	                String q = queryId + " " + "Q0" + " " + doc.getField("paragraphid").stringValue() + " " + rank + " " + score.score + " " + "team7" + "-" + "value";
	                out.add(q);
	                rank += 1;
	            			}  }}
	            
	        
	        }
	        return out;
	    }
    
    public static BooleanQuery toQuery(String queryStr) throws IOException {
    	StandardAnalyzer analyzer = new StandardAnalyzer();
        ArrayList<String> tokens = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(queryStr));
        tokenStream.reset();
        tokens.clear();
        while (tokenStream.incrementToken()) {
            final String token = tokenStream.getAttribute(CharTermAttribute.class).toString();
            tokens.add(token);
        }
        tokenStream.end();
        tokenStream.close();
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        for (String token : tokens) {
            booleanQuery.add(new TermQuery(new Term("text", token)), BooleanClause.Occur.SHOULD);
        }
        return booleanQuery.build();
    }

   
    
    
    public static void bi_word() {							//saperating 
    	Analyzer analyzer=new StandardAnalyzer();
    	
    	for(Entry<String, String> outer : queries.entrySet()) { 
    	String s=	outer.getValue();
    	List<String> strings= new ArrayList<String>();
    	List<String> array= new ArrayList<String>();
    	array=tokenizeString(analyzer, s);
    	if(array.size()==1) {
			strings.add(array.get(0));
		}
    		for(int i=0;i<(array.size()-1);i++) {
    			strings.add(array.get(i)+" "+array.get(i+1));
    			
    		}
    		bi_word.put(outer.getKey(), strings);
    	
    	}
    	
    //	System.out.println(bi_word);
    }
    public static IndexSearcher setupIndexSearcher() throws IOException {
        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "paragraph.lucene");
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
    }
	
	public static void D(String paragraphsFile, String File) throws IOException {
		buildIndex(paragraphsFile);

    	queries = new HashMap<String, String>();

        File file = new File(File);
       
        final FileInputStream fileInputStream = new FileInputStream(file);
        for(Data.Page p: DeserializeData.iterableAnnotations(fileInputStream)) {
            String queryId = p.getPageId();
            String query = p.getPageName();
            queries.put(queryId, query);
    	
        }
        
		bi_word();
	   	
		IndexSearcher searcher = setupIndexSearcher();
		SimilarityBase sb= new SimilarityBase() {

			@Override
			protected float score(BasicStats stats, float freq, float docLen) {
				double idf=(1/Math.sqrt(Math.pow((1 + Math.log(freq)), 2)))*stats.getBoost();
				
				return (float) idf;
			}

			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return "Index";
			}	
			
		};
		searcher.setSimilarity(sb);


		    	
		List<String> content = value(searcher);


		DEFAULT_SCORE_FILE="biword.txt";
		FileWriter writer = new FileWriter(DEFAULT_SCORE_FILE);
		for(String output: content) {
		    writer.write(output + System.lineSeparator());
		}
		
		writer.close();
		
	}
	
	
	
	
	
	
	public static void main(String args[]) throws IOException {
		        

		}
	

}
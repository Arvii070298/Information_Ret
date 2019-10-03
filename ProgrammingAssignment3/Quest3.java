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
    private static  String DEFAULT_SCORE_FILE ;
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
	//--------------------------------------------------------------------------------------------------------
	
	
	  public static void main(String[] args) throws IOException {
	    //	final String File=args[0];
	    	queries = new HashMap<String, String>();

	        File file = new File(File);
	       
	        final FileInputStream fileInputStream = new FileInputStream(file);
	        for(Data.Page p: DeserializeData.iterableAnnotations(fileInputStream)) {
	            String queryId = p.getPageId();
	            String query = p.getPageName();
	            queries.put(queryId, query);
	    	
	        }
	   
	       // final String PARAGRAPH_FILE=args[1];
	        buildIndex(PARAGRAPH_FILE);
	        int x=1;
	        IndexSearcher searcher = setupIndexSearcher();
	        
	        if(x==1) {
	        
	        SimilarityBase sb= new SimilarityBase() {													//lncltn
	        	
                    @Override
                    protected float score(BasicStats stats, float freq, float docLen)
                    {
                    	 float norm = 1;
                         long docFreq = stats.getDocFreq();
                         long numDocs = stats.getNumberOfDocuments();

                         double lnc = (1 + Math.log(freq))*1*(1/Math.sqrt(norm));
                         double ltn = (1 + Math.log(freq))*Math.log(numDocs/(double) docFreq)*1;

                         return (float) (lnc*ltn);

                    }

                    @Override
                    public String toString()
                    {
                        return null;
                    }
                 };
                searcher.setSimilarity(sb);
			
	        
	        
	       
	        List<String> content = value(searcher);
	       
	        
	        DEFAULT_SCORE_FILE="lnc.txt";
	        FileWriter writer = new FileWriter(DEFAULT_SCORE_FILE);
	        for(String output: content) {
	            writer.write(output + System.lineSeparator());
	        }
	        writer.close();
	        x++;
	        }
	        if(x==2) {																	
	        SimilarityBase	sb = new SimilarityBase()
                {																			//bnn-------
                @Override
                protected float score(BasicStats stats, float freq, float docLen)
                {
                    if(freq > 0)
                    {
                        return 1;
                    }
                    return 0;
                }

                @Override
                public String toString() {
                    return null;
                }
                };
                searcher.setSimilarity(sb);
    			
    	        
    	        
     	       
    	        List<String> content = value(searcher);
    	       
    	        DEFAULT_SCORE_FILE="bnn.txt";
    	        FileWriter writer = new FileWriter(DEFAULT_SCORE_FILE);
    	        for(String output: content) {
    	            writer.write(output + System.lineSeparator());
    	        }
    	        writer.close();
    	        x++;
	        	
	        	
	        	
	        	
	        	
	        }if(x==3) {
	        	SimilarityBase sb = new SimilarityBase() {						//anc-----------
                    @Override
                    protected float score(BasicStats stats, float freq, float docLen) {
                    	float norm = 1;
                        long docFreq = stats.getDocFreq();
                        long numDocs = stats.getNumberOfDocuments();
                        double res = (0.5 + ((0.5 * freq)/ stats.getTotalTermFreq()) ) / Math.sqrt(docLen);
                        
                        //apc
                    double apc=  (0.5 + ((0.5 * freq)/ stats.getTotalTermFreq()) )* (Math.max(0,  Math.log(numDocs-docFreq/(double) docFreq)))/ Math.sqrt(docLen);
                        return (float) (apc*res);
                        
                        
                        
                        
                       
                    }

                    @Override
                    public String toString() {
                        return null;
                    }
                };
                searcher.setSimilarity(sb);
                List<String> content = value(searcher);
     	       
    	        
    	        DEFAULT_SCORE_FILE="anc.txt";
    	        FileWriter writer = new FileWriter(DEFAULT_SCORE_FILE);
    	        for(String output: content) {
    	            writer.write(output + System.lineSeparator());
    	        }
    	        writer.close();            
                
	        }
	     
	    
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
	    public static IndexSearcher setupIndexSearcher() throws IOException {
	        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "paragraph.lucene");
	        Directory indexDir = FSDirectory.open(path);
	        IndexReader reader = DirectoryReader.open(indexDir);
	        return new IndexSearcher(reader);
	    }

	   
	    
	   
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
	        }

	        writer.addDocuments(documents);
	        writer.commit();
	        writer.close();
	    }

	    
	    public void writeToFile(String fileName, List<String> content) throws IOException {
	        FileWriter writer = new FileWriter(fileName);
	        for(String output: content) {
	            writer.write(output + System.lineSeparator());
	        }
	        writer.close();
	    }
	    
	    
}



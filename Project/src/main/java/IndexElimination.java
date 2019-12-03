
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.Token;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicModelIF;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.Parser;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
public class IndexElimination {
	
	//private static final String File = "D:/test200/test200-train/train.pages.cbor-outlines.cbor";
    private static  String DEFAULT_SCORE_FILE ;
    private static final String INDEX_DIR = "index";
 //   private static final String PARAGRAPH_FILE = "D:/test200/test200-train/train..cbor-paragraphs.cbor";
    private static HashMap<String, String> QueriesMap = new HashMap<String, String>();
    static HashMap<String,String> idf =new HashMap<String,String>();


    private static HashMap<String,HashMap< String, Integer>> words = new HashMap<String,HashMap< String,Integer>>();				//--------------------------------
   
    private static HashMap<String, String> queries = null;
	
    
    
    public static IndexSearcher setupIndexSearcher() throws IOException {
        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "paragraph.lucene");
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
    }
    
    
    
    

	  private static List<String> value(IndexSearcher searcher) throws IOException {
		  int maxResults = 100;
	        

	        List<String> out= new ArrayList<String>();

	        for (Map.Entry<String, String> que: idf.entrySet()) {
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

	  
	    public static void Highest_idf(HashMap<String, Integer> para) {
	        Analyzer analyzer=new StandardAnalyzer();
	        String fin="";
	        //System.out.println(para);
	        	for(Entry<String, String> inner : queries.entrySet()) { 
	        		List<String> array=new ArrayList<String>();
	        		
	        		String s=inner.getValue();
	        	array=	tokenizeString(analyzer, s);
	        	String q="";double val=0;
	        	int flag=0;double min=100;int position=-1;
	    		List<Double> values=new ArrayList<Double>();
	    		fin="";
	        	for(int i=0;i<array.size();i++) {
	        		
	        		if(array.size()==1) {
	        			idf.put(inner.getValue(),array.get(i));
	        			flag=2;
	        			break;
	        		}
	        		
	        		if(para.containsKey(array.get(i))) {
	        				double freq=  para.get(array.get(i));
	        			val =Math.log10(QueriesMap.size()/freq);
	        				q=q+array.get(i);
	        				values.add(val);
	        				
	        		
	        		}else {
	        			flag=1;
	        			continue;
	        		}
	        		
	        		
	        	}
	        	if(flag==1) {
	        		idf.put(inner.getKey(),q);
	        		
	        	}else if(flag==2)
	        	{
	        	}else
	        	
	        	
	        	{
	        		for(int i=0;i<values.size();i++) {
	        			if(min >values.get(i)) {
	        				min=values.get(i);
	        				position=i;
	        			}
	        		}
	        		
	        		
	        		for(int w=0;w<array.size();w++) {
	        			if(w==position)continue;
	        			fin=fin+array.get(w)+" ";
	        			
	        			
	        		}
	        		int le=fin.length();
	        		  fin=fin.substring(0,(le-1));
	        		idf.put(inner.getKey(), fin);
	        		
	        		
	        	}
	        	}//System.out.println(idf);
	        	
	        	
	        }
	    
	    
	    
	    public static HashMap<String, Integer> idf()
	    {	
	    	
	    	Analyzer analyzer=new StandardAnalyzer();
	    HashMap<String,Integer> para =new HashMap<String,Integer>();
	    	for(Entry<String, String> outer : queries.entrySet()) {
	    		List<String> array=new ArrayList<String>();
	    		array=tokenizeString(analyzer, outer.getValue());
	    		for(int i=0;i<array.size();i++) {
	    			if(para.containsKey(array.get(i))) {
		 				int s=	para.get(array.get(i));
		 				s++;
		 				para.put(array.get(i), s);
		 				//	System.out.println("---"+s);
		 				}else {
		 					para.put(array.get(i), 1);

		 					
		 					
		 				}
		 		
	    		}    	
	    	}
	    	
	    return para;
	    }
	  
    public static void  D(String File) throws IOException {
     	queries = new HashMap<String, String>();

        File file = new File(File);
       
        final FileInputStream fileInputStream = new FileInputStream(file);
        for(Data.Page p: DeserializeData.iterableAnnotations(fileInputStream)) {
            String queryId = p.getPageId();
            String query = p.getPageName();
            //tem.out.println(queryId);
            queries.put(queryId, query);
    	
        }
        IndexSearcher searcher = setupIndexSearcher();
        SimilarityBase sb= new SimilarityBase() {

			@Override
			protected float score(BasicStats stats, float freq, float docLen) {
				double idf=1/Math.sqrt(Math.pow((1 + Math.log(freq)), 2))*stats.getBoost();
				
				return (float) idf;
			}

			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return "Index";
			}	
        	
        };
        searcher.setSimilarity(sb);
        HashMap<String,Integer> para =new HashMap<String,Integer>();

        para=   idf();
        
   Highest_idf(para);    	
        
        
        List<String> content = value(searcher);
	       
        
        DEFAULT_SCORE_FILE="IndexElimination.txt";
        FileWriter writer = new FileWriter(DEFAULT_SCORE_FILE);
        for(String output: content) {
            writer.write(output + System.lineSeparator());
        }
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
    
    
	
	
	
	
	
	

}

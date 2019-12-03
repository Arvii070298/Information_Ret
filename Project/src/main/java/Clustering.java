
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class Clustering {
	
	private static QueryParser parser;
	private static int q=0;
    private static final String INDEX_DIR = "index";
    
    private static HashMap<String, String> queries = null;
    private static final String PARAGRAPH_FILE = "";
    private static HashMap<String, String> QueriesMap = new HashMap<String, String>();	

    private static HashMap<String,HashMap< String, Integer>> words = new HashMap<String,HashMap< String,Integer>>();				//--------------------------------
    private static HashMap<String,HashMap< String, Double>> clusters = new HashMap<String,HashMap< String,Double>>();				//--------------------------------
    private static HashMap<String,HashMap< String, Double>> ord_clust1 = new HashMap<String,HashMap< String,Double>>();				//--------------------------------

    private static IndexWriter createWriter() throws IOException {
        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "paragraph.lucene");
        Directory indexDir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
        return new IndexWriter(indexDir, config);
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
    
    public static void map() {

    //	Iterator<Map.Entry<String, String>> itr = QueriesMap.entrySet().iterator(); //outer loop
        Analyzer analyzer = new StandardAnalyzer();
        
    	 for (Entry<String, String> outer : QueriesMap.entrySet()) {		//iteration thru para
    	        HashMap<String,Integer> para =new HashMap<String,Integer>();

    		 List<String> inner_list = new ArrayList<>();					//for para txt
    		 
    	inner_list=	 tokenizeString(analyzer, outer.getValue());
    				
    	//System.out.println(inner_list);
    		 	for(int i=0;i<inner_list.size();i++) {
    		 				if(para.containsKey(inner_list.get(i))) {
    		 				int s=	para.get(inner_list.get(i));
    		 				s++;
    		 				para.put(inner_list.get(i), s);
    		 				////	System.out.print(inner_list.get(i));
    		 				//	System.out.println("---"+s);
    		 				}else {
    		 					para.put(inner_list.get(i), 1);
    		 				//	System.out.println(inner_list.get(i)+"-----1");

    		 					
    		 					
    		 				}
    		 		
    		 		
    		 	}
       		 
    		 //	System.out.println("------------------------");
    		 	
    		 	
    		 	
    		 	
    		 	words.put(outer.getKey(), para);
    		 
    		 
    		 
    	 }
    	
    }
    
    
    
    
    public static void distance(ArrayList<String> seeds) {
        HashMap<String,Integer> content =new HashMap<String,Integer>();
        HashMap<String,Integer> cl_para =new HashMap<String,Integer>();
       
        HashMap<Integer,Double> min_val =new HashMap<Integer,Double>();
        
        
        
        for(int counter=0;counter<seeds.size();counter++) {
        	
        	
        	 String s1=seeds.get(counter);
             cl_para=words.get(s1);
    	HashMap<String,Double> para =new HashMap<String,Double>();
    	
    	
    	
        for (Entry<String, HashMap<String, Integer>> outer : words.entrySet()) {	//set of all paras
        	double score=0;
        	content=outer.getValue();
        	for(Entry<String, Integer> inner : cl_para.entrySet()) { 			//need iter only to cluster seeds
        	String s=	inner.getKey();
        		if(content.get(s)!=null) {
        			score=score+Math.pow((inner.getValue()-(content.get(s))), 2);
        			
        			
        		}else {
        			
        			score=score+Math.pow(inner.getValue(),2);
        		}
        		
        	}
        	
        	
        	score=Math.sqrt(score);
        	
        	
        	
        	para.put(outer.getKey(), score);
        	
        	
        	
        }
        clusters.put(s1, para);
        }
    }
    
    
    
    public static void clustering(ArrayList<String> seeds) {
    	int p =0;            
    	p++;HashMap<String,Double> clus1 =new HashMap<String,Double>();
		int p_one=0,p_two=0,p_tre=0,p_four=0,p_five=0;

    	for(Entry<String, String> outer : QueriesMap.entrySet()) { 	
    		
    		
p++;HashMap<String,Double> para =new HashMap<String,Double>();
    		double min=100;
    		int pointer=0;
    		
    		
    	for(int i=0;i<seeds.size();i++) {
    		//System.out.println("---");
    		
    	double val=clusters.get(seeds.get(i)).get(outer.getKey());
    	//System.out.println(val);
    		if(val<=min){
    			min=val;
    			pointer=(i);
    			
    		}
    		
    	}
    	if(pointer==0) {
    		p_one=p_one+1;
    	}else if(pointer==1) {
    		p_two=p_two+1;
    	}else if(pointer==2) {
    		p_tre=p_tre+1;
    	}else if(pointer==3) {
    		p_four=p_four+1;
    	}else {
    		p_five=p_five+1;
    	}
    	
    	
    	//System.out.println("pointer is"+pointer);
    	
    	para.put(outer.getKey(), min);
    	if(pointer==0) {
    		clus1.put(outer.getKey(), min);
    		//System.out.println("++++++++++++++++++++++++++");
    		ord_clust1.put(seeds.get(0),clus1 );
    		
    	}
    	
    	
    	}System.out.println(p_one+"--------"+p_two+"----------"+p_tre+"------------"+p_four+"----------"+p_five);
    }
    
    
    public static HashMap<String, Integer> idf()
    {	Analyzer analyzer=new StandardAnalyzer();
    HashMap<String,Integer> para =new HashMap<String,Integer>();
    	for(Entry<String, String> outer : QueriesMap.entrySet()) {
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
    
    public static HashMap<String, String> Highest_idf(HashMap<String, Integer> para) {
    Analyzer analyzer=new StandardAnalyzer();
    HashMap<String,String> idf =new HashMap<String,String>();
    String fin="";
    
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
    	return idf;
    	
    }
    
    public static IndexSearcher setupIndexSearcher() throws IOException {
        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "paragraph.lucene");
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
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
	    public static void D(String string) throws IOException {
	    	buildIndex(string);
	    	
			  map();
			 
			//  System.out.println(words); 
			  ArrayList<String> seeds=new ArrayList<String>();
			  seeds.add("659c22ccd9a238418b606e345ecd821104c471fa");
			  seeds.add("5746ad8f6a2655f1e27fc9af83f6d04218b14c78");
			  seeds.add("6571354668f95a92b5a535c8bc26feffb3120d54");
			seeds.add("52335f4dfd6742c74fb8802fa01dc3a0db433cb2");
			  seeds.add("4d52dc5e80afcb9f1a1f046b8205c30a129ecfa1");
			  seeds.add("4a5f30e8538226f290dd8fe3d5e0387eff10dce2"); distance(seeds);
			  
			  clustering(seeds);
			  
			// System.out.println(clusters); 
			 //System.out.println(ord_clust1);
			 
	    	
	    	
	    	
	    	
	    	
	       

	}
	    }
    
    	

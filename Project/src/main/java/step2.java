import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class step2 {
    ///tf-idf and Unigram done here
	static  String File ;
    private static final String INDEX_DIR = "index";
    static String PARAGRAPH_FILE ;
    static int io=0;
    static HashMap<String, String> queries = null;
    private static HashMap<String, String> paras = null;
	static HashMap<String,List<String>> bi_word =new HashMap<String,List<String>>();

    private static HashMap<String, String> QueriesMap = new HashMap<String, String>();	

	
 public static IndexSearcher setupIndexSearcher() throws IOException {
	    Path path = FileSystems.getDefault().getPath(INDEX_DIR);
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
 
 public static IndexSearcher Similarity(int x) throws IOException {
	 if(x==1&&io==1) {
		 paras=new HashMap<String, String>();
		  final FileInputStream fileInputStream2 = new FileInputStream(new File(PARAGRAPH_FILE));
	        for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2)) {
	            String paraId = p.getParaId();
	            String textOnly = p.getTextOnly();
	            paras.put(paraId, textOnly);
	        }
		 
	 }
	 HashMap<String, HashMap<String,Integer>> map = new HashMap<String, HashMap<String,Integer>>();	
	 queries = new HashMap<String, String>();

	    File file = new File(File);
	   
	    final FileInputStream fileInputStream = new FileInputStream(file);
	    for(Data.Page p: DeserializeData.iterableAnnotations(fileInputStream)) {
	        String queryId = p.getPageId();
	        String query = p.getPageName();
	        queries.put(queryId, query);
		
	    }
	   IndexSearcher searcher =  setupIndexSearcher();
	   SimilarityBase sb=null;
		String val="";
		int vocabSize = getVocabSize(searcher.getIndexReader());

		if(x==1) {val="LNC";
			 sb= new SimilarityBase() {

				@Override
	            protected float score(BasicStats stats, float freq, float docLen)
	            {		
	            	float norm = 1;
	            	
	                 long docFreq = stats.getDocFreq();
	                 long numDocs = stats.getNumberOfDocuments();
	                 double lnc = (1 + Math.log(freq))*1*(1/Math.sqrt(Math.pow((1 + Math.log(freq)), 2))+1);
	                 double ltn = (1 + Math.log(docFreq))*Math.log(numDocs/(double) docFreq)*1;
	                 return (float) (lnc*ltn);

	            }

				@Override
				public String toString() {
					// TODO Auto-generated method stub
					return null;
				}
				
				
				
			};

			
			
			
		}else if(x ==2) {
			val="BNN_L2R";
			 	sb = new SimilarityBase()
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
			
		}else if(x==3){
			val="ANC_L2R";
			 sb = new SimilarityBase() {						//anc-----------
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
		}else if(x==4) {
			val="lap";
			sb = new SimilarityBase() {

				@Override
				protected float score(BasicStats stats, float freq, float docLen) {
// TODO Auto-generated method stub
					float numerator = freq + 1;
					Long vocabS = new Long(stats.getNumberOfFieldTokens());
					float denominator = docLen + vocabS.floatValue();
					return (float) Math.log(numerator / denominator);
//return 0;
				}

				@Override
				public String toString() {
// TODO Auto-generated method stub
					return "Laplace";
				}
			};
			
			
			
			
			
		}else if(x==5) {					//Jm
			val="dir";
			sb = new SimilarityBase() {

				@Override
				protected float score(BasicStats stats, float freq, float docLen) {
// TODO Auto-generated method stub
					float lambda = (float) 0.9;
					float prob = ((lambda * (freq / docLen)) + (1 - lambda) * (stats.getNumberOfFieldTokens()));
					return (float) Math.log(prob);
				}

				@Override
				public String toString() {
// TODO Auto-generated method stub
					return "Jelinker";
				}
			};
				
			
			
			
		}else if(x==6) {			//Dir
			sb = new SimilarityBase() {

				@Override
				protected float score(BasicStats stats, float freq, float docLen) {
// TODO Auto-generated method stub
					float mu = 1000;
					float prob = (float) Math.log((double) (docLen / (docLen + mu))
							+ (mu / (docLen + mu)) * (stats.getNumberOfFieldTokens()));
					return prob;
				}

				@Override
				public String toString() {
// TODO Auto-generated method stub
					return "Dirichlet";
				}
			};
			
			
		}else if(x ==7) {
			sb=new SimilarityBase() {
				
				@Override
				public String toString() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				protected float score(BasicStats stats, float freq, float docLen) {
					// TODO Auto-generated method stub
					return freq;
				}
			};
			
			
		}
		
		
		searcher.setSimilarity(sb);
	     List<String> list= new ArrayList<String>();
		list=	value(searcher,x);
		
		
		 String pp=null;
		 if(x==1) {
			 pp="LNC.txt";
		 }else if(x==2) {
			 pp="BNN.txt";
		 }else if(x==3) {
			 pp="ANC.txt";
		 }else if(x==7) {
			 pp="custom.txt";
		 }else if(x==4) {
			 pp="Lap.txt";
		 }else if(x==5) {
			 pp="Jm.txt";
		 }else if(x==6) {
			 pp="Dir.txt";
		 }
		 if(io==0){
		 FileWriter fw = new FileWriter(pp, true);
			for(String runString:list)
				fw.write(runString+"\n");
			fw.close();}
			return searcher;

	}
 public static int getVocabSize(IndexReader rd){
		int vsize = 0;
		try {
			Fields fields = MultiFields.getFields(rd);
			for (String field:fields)
			{
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator();
				while (termsEnum.next() != null)
					vsize++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vsize;
	}
 
 
 public static double get_cosine(String paraid,String queryid) {
   	 
 String txt=	paras.get(paraid);   
 txt=txt.toLowerCase();
	 String query=queries.get(queryid);
	 StandardAnalyzer analyzer=new StandardAnalyzer();
	 List<String> list=new ArrayList<String>();
	 list=tokenizeString(analyzer, query);   
	 double cos=0;
	 double score=0;
	 for(int i=0;i<list.size();i++) {
		 String q1=list.get(i).toLowerCase();
		 if( txt.contains(q1)){
			
			String q=	list.get(i);
		    int freq= Collections.frequency(list, q);
		    cos=(1 + Math.log(freq))+cos;
			
			
		}
	score=score+cos;
		 
		 
	 }
	 
	return Math.sqrt(score) ;  	 
 }
 
 
 public static double get_cosine_anc(String paraid,String queryid) {
	 
	 String txt=paras.get(paraid);
	 StandardAnalyzer analyzer=new StandardAnalyzer();
	 List<String> list=new ArrayList<String>();
	 String query=queries.get(queryid);
	 list=tokenizeString(analyzer, query);  
	 double cosine=0;
	 
	 for(int i=0;i<list.size();i++) {
		 String q1=list.get(i).toLowerCase();
		 int count=0;
		 if( txt.contains(q1)){
			 String q=	list.get(i);
			    int freq= Collections.frequency(list, q);
			    cosine=cosine+(0.5+(0.5*freq));
			    
			  for  (Map.Entry<String, String> que: paras.entrySet()){
			    	String para_txt=que.getValue();
			    	if( para_txt.contains(q1)){
						 String w=	list.get(i);
						   count=count+ Collections.frequency(list, w);
			    	
			    }
			    	
			 
			 
		 }cosine=cosine/count;
	 }}
	 	 
	 
	 
	 return Math.sqrt(cosine) ;  	 
 }
 
 
 
 
 private static List<String> value(IndexSearcher searcher,int x2) throws IOException {
	    int maxResults = 100;
	    
	    HashMap<String, HashMap<String,Double>> map = new HashMap<String, HashMap<String,Double>>();
	    List<String> out= new ArrayList<String>();

	    for (Map.Entry<String, String> que: queries.entrySet()) {
	        String queryId = que.getKey();
	        String query = que.getValue();
	        TopDocs tops = searcher.search(toQuery(query), maxResults);
	        ScoreDoc[] scoreDoc = tops.scoreDocs;
	      

	        double rank = 1;
	        double q=0;
	        HashMap<String, Integer>para = new HashMap<String, Integer>();	
	        HashMap<String,Double> inner = new HashMap<String,Double>();
	        for (ScoreDoc score : scoreDoc) {
	    	   
	        	
	            final Document doc = searcher.doc(score.doc);
	            double x=0;
	        	
	            if(io==0)
	         x=    get_cosine(doc.getField("id").stringValue(), queryId);
	            if(io==1)
	            	x= get_cosine_anc(doc.getField("id").stringValue(), queryId);
	        
	        
				/*
				 * String q = queryId + " " + "Q0" + " " + doc.getField("id").stringValue() +
				 * " " + rank + " " + score.score + " " + "team7" + "-" + "value"; out.add(q);
				 */
	        if(x2==1 || x2==3)
	          q =score.score/(x);
	        else 
	        q=score.score;
	            inner.put(doc.getField("id").stringValue(), q);
	            
	        			} 
	    
	        map.put(que.getKey(), inner);
	    }
	    
	    
	    
	   List<String> o=new ArrayList<String>();
	 int  count=1;
	    for (Entry<String, HashMap<String, Double>> que: map.entrySet()) {
	    	int rank=1;
	    	 HashMap<String,Double > m=new HashMap<String, Double>();
	    		m=sortByValue(que.getValue());
	    		for (Map.Entry<String, Double> en : m.entrySet()) {
	    			
	    			
	    			String q= que.getKey()+" Q:"+count+" "+en.getKey() +" "+rank+ " "+en.getValue() + " team7-val";
	                o.add(q);
	                rank++;count++;
	            }
	    	
	    }
	    
	    return o;
	}

 
 
 
 
 
 
 public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
 {
     // Create a list from elements of HashMap
     List<Map.Entry<String, Double> > list =
            new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

     // Sort the list
     Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
         public int compare(Map.Entry<String, Double> o1,  
                            Map.Entry<String, Double> o2)
         {
             return (o1.getValue()).compareTo(o2.getValue());
         }
     });
      
     // put data from sorted list to hashmap  
     HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
     for (Map.Entry<String, Double> aa : list) {
         temp.put(aa.getKey(), aa.getValue());
     }
     return temp;
 }
 public static void D(String y, String paragraphFile, String file2) throws IOException {
	 PARAGRAPH_FILE =paragraphFile ;
		File=file2;
		String s="1";
		int x=Integer.parseInt(y);
		
		
		paras=new HashMap<String, String>();
		  final FileInputStream fileInputStream2 = new FileInputStream(new File(PARAGRAPH_FILE));
	        for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2)) {
	            String paraId = p.getParaId();
	            String textOnly = p.getTextOnly();
	            paras.put(paraId, textOnly);

	          
	        }
	        Similarity(x);
	 
 }
 

 
}
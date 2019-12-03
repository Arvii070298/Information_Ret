
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import co.nstant.in.cbor.model.Array;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class ChampionList {
    private static HashMap<String, String> queries = null;
	/*
	 * private static final String File =
	 * "D:/test200/test200-train/train.pages.cbor-outlines.cbor"; private static
	 * String paragraphsFile =
	 * "D:/test200/test200-train/train.pages.cbor-paragraphs.cbor";
	 */
	
	
	
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
    
	
	
	
	
	
	
	
	public static void D(String paragraphsFile,String File) throws IOException {
		new step2();
		step2.File=File;
		step2.io=1;
		IndexSearcher searcher=step2.Similarity(1);
		
int maxResults = 100;
        

        List<String> out= new ArrayList<String>();
        HashMap<String,ArrayList<String>>  map=new HashMap<String, ArrayList<String>>();
        
        HashMap<String,HashMap<String,Double>> rank_map=new HashMap<String, HashMap<String,Double>>();
        for (Map.Entry<String, String> que: step2.queries.entrySet()) {
            String queryId = que.getKey();
            ArrayList<String> list=new ArrayList<String>();
            String query = que.getValue();
            TopDocs tops = searcher.search(step2.toQuery(query), maxResults);
            ScoreDoc[] scoreDoc = tops.scoreDocs;
            HashMap<String,Double> l=new HashMap<String, Double>();
            double rank = 1;
            int i=1;
            for (ScoreDoc score : scoreDoc) {
                final Document doc = searcher.doc(score.doc);
                String q = queryId + " " + "Q"+i + " " + doc.getField("id").stringValue() + " " + rank + " " + score.score + " " + " team7" + "-" + "value";
                out.add(q);   l.put(doc.getField("id").stringValue(), rank);
                list.add(doc.getField("id").stringValue());
                rank += 1;i +=1;
             
            			} 
            rank_map.put(queryId,l);
map.put(queryId, list);
}
        

        
        FileWriter fw = new FileWriter("ChampionList.txt", true);
		for(String runString:out)
			fw.write(runString+"\n");
		fw.close();
		
		
		queries = new HashMap<String, String>();

        File file = new File(File);
       
        final FileInputStream fileInputStream = new FileInputStream(file);
        for(Data.Page p: DeserializeData.iterableAnnotations(fileInputStream)) {
            String queryId = p.getPageId();
            String query = p.getPageName();
            queries.put(queryId, query);
    	
        }
        
        HashMap<String, String> para=new HashMap<String, String>();
        FileInputStream stream = new FileInputStream(new File(paragraphsFile));
		for(Data.Paragraph p: DeserializeData.iterableParagraphs(stream)) {
            String pId = p.getParaId();
            String textOnly = p.getTextOnly();
            para.put(pId, textOnly);


        }
		
		HashMap<String,Double> inner=new HashMap<String, Double>();
		HashMap<String, List<String>> m=new HashMap<String, List<String>>();
		List<String> token_list=new ArrayList<String>();
		HashMap<String,Double> updated_map=new HashMap<String, Double>();
		
		for (Entry<String, HashMap<String, Double>> que: rank_map.entrySet()) {
			ArrayList<String > new_list=new ArrayList<String>();

		String id=que.getKey();	
		inner=que.getValue();
		String s="";
		
		for (Entry<String, Double> inside: inner.entrySet()) {
		s=s+para.get(inside.getKey());
		}
		
		StandardAnalyzer analyzer = new StandardAnalyzer();
		token_list=tokenizeString(analyzer, s);
		updated_map.put(id,(double) token_list.size());

		}
		
		
		
		
		 HashMap<String,Double> l=new HashMap<String, Double>();
		
		 HashMap<String,HashMap<String, Double>> ma=new HashMap<String, HashMap<String,Double>>();
		 for (Map.Entry<String, String> que: queries.entrySet()) {
			 l=rank_map.get(que.getKey());
			 HashMap<String,Double> qwe=new HashMap<String, Double>();
			 double score= updated_map.get(que.getKey());
			 for (Entry<String, Double> ir: l.entrySet()) {
				double s= rank_map.get(que.getKey()).get(ir.getKey());
				double f=s+(s/score);
				qwe.put(ir.getKey(),f);
				 
			 }ma.put(que.getKey(), qwe);
			 
		 }
		 
		// System.out.println(ma);
	

}}
package team07.assign01;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import co.nstant.in.cbor.model.Map;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
public class L2R {
	private static final String File="" ;
	public static final String PARAGRAPH_FILE = null;
    private static HashMap<String, String> queries = null;
    static List<String> page_id=new ArrayList<String>();
	static HashMap<String, ArrayList<String>> relevanceMap = null;

	
	
	public static HashMap<String, ArrayList<String>> getRelevanceMapFromQrels(String qrelsPath){
		HashMap<String, ArrayList<String>> relMap = new HashMap<String, ArrayList<String>>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(qrelsPath));
			String line, paraid, pageid;
			while((line = br.readLine()) != null){
				pageid = line.split(" ")[0];
				paraid = line.split(" ")[2];
				if(relMap.keySet().contains(pageid)){
					relMap.get(pageid).add(paraid);
				}
				else{
					ArrayList<String> paralist = new ArrayList<String>();
					paralist.add(paraid);
					page_id.add(pageid);
					relMap.put(pageid, paralist);
				}
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return relMap;
	}
	
	
	public static void D(String File,String File2, String File3) throws IOException {
		
		ArrayList<HashMap<String, ArrayList<String>>> a=new ArrayList<HashMap<String, ArrayList<String>>>();
		ArrayList<IndexSearcher> s=new ArrayList<IndexSearcher>();
		HashMap<String,Integer> order=new HashMap<String, Integer>();
		new step2();
		step2.PARAGRAPH_FILE=File2;
	    step2.File=File;
	    step2.io=1;
	    s.add(step2.Similarity(1));
	    s.add(step2.Similarity(2));
	    s.add(step2.Similarity(4));
	    s.add(step2.Similarity(5));
	    s.add(step2.Similarity(6));

	    
	    
 int maxResults = 10;
	    for(int i=0;i<5;i++) {
	    HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();	
	    

	    for (Entry<String, String> que: step2.queries.entrySet()) {
	        String query = que.getValue();
	        TopDocs tops = s.get(i).search(step2.toQuery(query), maxResults);
	        ScoreDoc[] scoreDoc = tops.scoreDocs;
	      

	        ArrayList<String> out= new ArrayList<String>();

	        for (ScoreDoc score : scoreDoc) { 
	            final Document doc = s.get(i).doc(score.doc);
	            out.add(doc.getField("id").stringValue());
	            
	        			} map.put(que.getKey(), out);
	    
	     
	    } a.add(map);
	    
	    }
	    ArrayList<ArrayList<String>> uniq=new ArrayList<ArrayList<String>>();
	   int counter=0;
	    
	    relevanceMap = getRelevanceMapFromQrels(File3);
	    for(Entry<String, ArrayList<String>> inner: relevanceMap.entrySet()) {
	    	 ArrayList<String> pag=new ArrayList<String>();
	    	String q=inner.getKey();
	    	order.put(inner.getKey(),counter);
	    	counter++;
	    	if(a.get(0).get(q) != null) {
	    			pag.addAll(a.get(0).get(q));
	    	}
	    	if(a.get(1).get(q) != null) {
	    		for(int j=0;j<a.get(1).get(q).size();j++) {
	    			if(!(pag.contains(a.get(1).get(q).get(j)))){
	    				pag.add(a.get(1).get(q).get(j));
	    			}
	    		}
    			
    	}
	    	if(a.get(2).get(q) != null) {
	    		for(int j=0;j<a.get(2).get(q).size();j++) {
	    			if(!(pag.contains(a.get(2).get(q).get(j)))){
	    				pag.add(a.get(2).get(q).get(j));
	    			}
	    		}
    			
    	}
	    	if(a.get(3).get(q) != null) {
	    		for(int j=0;j<a.get(3).get(q).size();j++) {
	    			if(!(pag.contains(a.get(3).get(q).get(j)))){
	    				pag.add(a.get(3).get(q).get(j));
	    			}
	    		}
    			
    	}
	    	if(a.get(4).get(q) != null) {
	    		for(int j=0;j<a.get(4).get(q).size();j++) {
	    			if(!(pag.contains(a.get(4).get(q).get(j)))){
	    				pag.add(a.get(4).get(q).get(j));
	    			}
	    		}
    	}
	    	uniq.add(pag);
	    	
	    }
	    
	    
	    
	    ArrayList<String> in=new ArrayList<String>();
		ArrayList<String> runStringsForPage = new ArrayList<String>();

	    for(Entry<String, ArrayList<String>> inner: relevanceMap.entrySet()) {
	    	
	    	String id=inner.getKey();
	    	int pos=order.get(id);
			
			  for(int i=0;i<uniq.get(pos).size();i++) {
				  String out="";String rank="";
				 String z=uniq.get(pos).get(i);
				 if(a.get(0).containsKey(id)) {
					 in=a.get(0).get(id);
					 
					 double r=((in.indexOf(z)+1));
					 if(r==0) {
						 rank=rank+"1:"+(r)+" "; 
					 }else {
					 r=1/r;
					 rank=rank+"1:"+(r)+" ";}
					 }
				 
				 
				 if(a.get(1).containsKey(id)) {
					 in=a.get(1).get(id);
					 
					 double r=((in.indexOf(z)+1));
					 if(r==0) {
						 rank=rank+"1:"+(r)+" "; 
					 }else {
					 r=1/r;
					 rank=rank+"1:"+(r)+" ";}
					 }
				 
				 
				 if(a.get(2).containsKey(id)) {
					 in=a.get(2).get(id);
					 
					 double r=((in.indexOf(z)+1));
					 if(r==0) {
						 rank=rank+"1:"+(r)+" "; 
					 }else {
					 r=1/r;
					 rank=rank+"1:"+(r)+" ";}
					 }
				 
				 
				 
				 
				 if(a.get(3).containsKey(id)) {
					 in=a.get(3).get(id);
					 
					 double r=((in.indexOf(z)+1));
					 if(r==0) {
						 rank=rank+"1:"+(r)+" "; 
					 }else {
					 r=1/r;
					 rank=rank+"1:"+(r)+" ";}
					 }
				 
				 
				 if(a.get(4).containsKey(id)) {
					 in=a.get(4).get(id);
					 
					 double r=((in.indexOf(z)+1));
					 if(r==0) {
						 rank=rank+"1:"+(r)+" "; 
					 }else {
					 r=1/r;
					 rank=rank+"1:"+(r)+" ";}
					 }
				 
				 
				 
				 
				int rel=0;
				 if( relevanceMap.get(id).contains(z)) {
					 rel=1;
				}
				 
				  out=rel+" id:"+id+" "+rank+" #"+z;
				  runStringsForPage.add(out);
				 
			  }
			 
	    	pos=pos++;
	    	
	    FileWriter fw = new FileWriter("L2R.txt", true);
		for(String runString:runStringsForPage)
			fw.write(runString+"\n");
		fw.close();
	}
	    
	    BufferedReader reader = new BufferedReader(new FileReader("L2R.txt"));
	    Set<String> lines = new HashSet<String>(10000); 
	    String line;
	    while ((line = reader.readLine()) != null) {
	        lines.add(line);
	    }
	    reader.close();
	    BufferedWriter writer = new BufferedWriter(new FileWriter("new_L2R.txt"));
	    for (String unique : lines) {
	    	
	        writer.write(unique);
	        writer.newLine();
	    }
	    writer.close();
	    
	
	
	}
	

	
	
	
	
	
}
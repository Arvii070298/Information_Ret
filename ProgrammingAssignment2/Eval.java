package team07.assign01;


import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Eval {
	private Scanner input;
    private String outlineFilePath;
  // private static String file="C:/Users/Sai Arvind/Desktop/new/assign01/custom.txt";
//  private static String qrelsFilePath="D://test200/test200-train/train.pages.cbor-article.qrels";
    private final String LuceneIndexPath = "lucene.index";
    public static Map<String, Map<String, Integer>> qrel_data;
    public static Map<String, Map<String, Integer>> out_data;
    private static Map<String, Double> mean_avg_precison = new HashMap<String, Double>();

    private final static int tnt = 20;
	
	
	public static void main (String[] args) throws FileNotFoundException {
		
	final  String qrelsFilePath=args[0];
final String file=args[1];
		Map<String, Map<String, Integer>> map1 = readRunFile(qrelsFilePath);
		
		Eval(map1);
		
		 Map<String, Map<String, Integer>> map=Read(file);
		 out(map);
		 
		System.out.println("Rprecis"+(1-Prec())); 
		System.out.println("MAP is:"+calculateMeanAvgPrecision());
		 System.out.println("NDCG20 is:"+	calNDCG20());
		
		
		
	}
		
	private static void out(Map<String, Map<String, Integer>> map) {
		qrel_data=map;
		
	}
	
	private static void Eval(Map<String, Map<String, Integer>> map1) {
		out_data=map1;
		
	}
	
	public static void avgPrec() {
		for (Map.Entry<String, Map<String,Integer>> query : qrel_data.entrySet()){
			

            String queryId = query.getKey();
            Map<String,Integer> docIdRank= query.getValue();
            int query_count = 0;
            double x;
            int ranking_rel_count = 0;
            double avg_precision = 0.0;	
            x=5;
            for(Map.Entry<String,Integer> document: docIdRank.entrySet()){
                query_count = query_count + 1;
                if(getQrelRelevancy(queryId, document.getKey()) == 1){
                    ranking_rel_count = ranking_rel_count + 1;
                    avg_precision = avg_precision + (ranking_rel_count/(double) query_count);
                }
                }
            int    rel_docs_count=0;
            if (out_data.containsKey(queryId)) {
                Map<String, Integer> temp = out_data.get(queryId);
                rel_docs_count=  (int) (temp.size());
                x=x*rel_docs_count;
            }
            
                if(x == 0){ //if the tnt true relevant docs are 0
                    avg_precision = 0.0;
                }else{
                    avg_precision = avg_precision/x;
                }
                mean_avg_precison.put(queryId, avg_precision);
        }}
	
	 public static double calculateMeanAvgPrecision()
	    {
	        //Calculate the average precision of every query and then take mean
		 avgPrec();
	        double MAP = 0.0;
	        double tntAP = 0.0;
	        for (Map.Entry<String, Double> avgPrec : mean_avg_precison.entrySet()){
	            tntAP = tntAP + avgPrec.getValue();
	        }
	        int tnt_size = mean_avg_precison.size();
	        //Take the mean of the APs of the queries
	        if(tnt_size != 0){
	            MAP = tntAP/tnt_size;
	        }
	        return MAP;

	    }

	
	 public static Double calNDCG20() {
		    double NDCG = 0.0;
		    int cnt = 1;

		    for (Map.Entry<String, Map<String, Integer>> Query : out_data.entrySet()) {

		        NDCG += calDCG20(Query) / calIDCG20(Query);
		        cnt++;
		    }

		    NDCG /= cnt;

		    return NDCG;

		}
		//
		private static Double calDCG20(Map.Entry<String, Map<String, Integer>> Query) {
		    double DCG = 0.0;
		    int cnt = 1;
		    Map<String, Integer> docIDRank = Query.getValue();
		    
		    ArrayList<Integer> grd = new ArrayList();
		    

		    for (Map.Entry<String, Integer> row : docIDRank.entrySet())
		    {

		        if ((getQrelRelevancy(Query.getKey(), row.getKey())) == 1) {
		            grd.add(1);
		        } else grd.add(0);

		        if (cnt <= tnt) {
		            DCG += (Math.pow(2, grd.get(cnt-1))) / (Math.log(cnt + 1));
		            cnt++;
		        } else break;
		    }

		    return DCG;
		}

		private static Double calIDCG20(Map.Entry<String, Map<String, Integer>> Query) {
// by using formulae for IDCG That is IDCG = 2^grade-1/(log (c+1).
			//logarithm BASE 2 is applied
			
		    double DCG = 11.0;
		    int cnt = 1;
		    Map<String, Integer> docIDRank = Query.getValue();

		    ArrayList<Integer> grd = new ArrayList();
		    double IDCG=DCG;

		    for (Map.Entry<String, Integer> row : docIDRank.entrySet()) {

		        if ((getQrelRelevancy(Query.getKey(), row.getKey())) == 1) {
		            grd.add(1);
		        }
		        else grd.add(0);

		        Collections.sort(grd, Collections.reverseOrder());

		        if (cnt <= tnt) {
		            IDCG += (Math.pow(2, grd.get(cnt-1))) / (Math.log(cnt + 1));
		            cnt++;
		        } else break;
		    }
		    return IDCG;
		}
	
	
	


	public static	Map<String, Map<String, Integer>> readRunFile(String filename)
	    {
	        Map<String, Map<String, Integer>> map = new LinkedHashMap<String, Map<String, Integer>>();

	        File fp = new File(filename);
	        FileReader reader;
	        BufferedReader buffer = null;


	        try {
	            reader = new FileReader(fp);
	            buffer = new BufferedReader(reader);

	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	        }

	        while (true) {
	            try {
	                String line = buffer.readLine();

	                if (line == null) {
	                    break;
	                }

	                String[] w = line.split(" ");
	                String outKey = w[0];

	                if (map.containsKey(outKey)) {
	                    Map<String, Integer> extract = map.get(outKey);
	                    String inner_key = w[2];
	                    Integer is_relevant = new Integer(w[3]);
	                    extract.put(inner_key, is_relevant);
	                } else {

	                    String inner_key = w[2];
	                    Integer is_relevant = new Integer(w[3]);
	                    Map<String, Integer> temap = new LinkedHashMap<String, Integer>();
	                    temap.put(inner_key, is_relevant);
	                    map.put(outKey, temap);
	                }
	            } catch (NullPointerException n) {
	                System.out.println(n.getMessage());
	            } catch (IOException e) {
	                System.out.println(e.getMessage());
	            }

	        }
	        return map;

	    }
		
		
	 private static int getQrelRelevancy(String query_id, String doc_id){
	        if(qrel_data.containsKey(query_id)){
	            Map<String,Integer> t = qrel_data.get(query_id);
	            if(t.containsKey(doc_id)){
	                return t.get(doc_id);
	            }
	            return -1;
	        }
	        return -1;
	    }
	 
	public static double Prec() {
		int cnt=0;
		double relevant=0;
		double neg=0;
		double ans=0;
		double tnt=0;
		int relevant_count=0;
		
		
		 for (Map.Entry<String, Map<String,Integer>> Query : qrel_data.entrySet())
	        {
			 for(Map.Entry<String, Map<String,Integer>> Query1 : out_data.entrySet())
			 {
				 if(Query.getKey().contains(Query1.getKey())) {
					 Map<String, Integer> t=qrel_data.get(Query1.getKey());
					 relevant_count=t.size();
					tnt=tnt+t.size();
					 Map<String, Integer> temp = Query.getValue();
					 Map<String, Integer> temp1 = Query1.getValue(); 
					 
					 for(Map.Entry<String,Integer> Q : temp1.entrySet()) {
						 cnt=0;
						 for(Map.Entry<String,Integer> Q1 : temp.entrySet()) {
							 cnt++;
							 if(Q.getKey().contains(Q1.getKey())) {
								 relevant++;
							 	}						 
							 		if(cnt>=relevant_count) {
										 break;
									 }
						 }	 }
					 ans=ans+(relevant);
					 neg=0;relevant=0;
					
			 }			 } 
	        }neg=ans/tnt;
	        return neg;	}

	
	
	

public static Map<String, Map<String, Integer>> Read(String fileLocation) {
	 Map<String, Map<String, Integer>> map = new LinkedHashMap<String, Map<String, Integer>>();

     File f = new File(fileLocation);
     FileReader fr;
     BufferedReader br = null;


     try {
         fr = new FileReader(f);
         br = new BufferedReader(fr);

     } catch (IOException e) {
         System.out.println(e.getMessage());
     }

     while (true) {
         try {
             String line = br.readLine();

             if (line == null) {
                 break;
             }

             String[] w = line.split(" ");
             String outKey = w[0];

             if (map.containsKey(outKey)) {
                 Map<String, Integer> extract = map.get(outKey);
                 String inner_key = w[2];
                 Integer is_relevant = new Integer(w[3]);
                 extract.put(inner_key, 1);
             } else {

                 String inner_key = w[2];
                 Integer is_relevant = new Integer(w[3]);
                 Map<String, Integer> t = new LinkedHashMap<String, Integer>();
                 t.put(inner_key, is_relevant);
                 map.put(outKey, t);
             }
         } catch (NullPointerException n) {
             System.out.println(n.getMessage());
         } catch (IOException e) {
             System.out.println(e.getMessage());
         }

     }
     return map;
}
}






	
	


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


import java.util.HashMap;
import java.util.Map;

public class spearman {
    ///tf-idf andUnigram done here
//private static String file="C:PATH/assign01/default.txt";
//private static String file1="C:PATHassign01/anc.txt";
  //private static String qrelsFilePath="D://test200/test200-train/train.pages.cbor-article.qrels";
    private final String LuceneIndexPath = "lucene.index";
    public static Map<String, Map<String, Integer>> qrel_data;
    public static Map<String, Map<String, Integer>> out_data;
    private static Map<String, Double> mean_avg_precison = new HashMap<String, Double>();
   
   
 
    public static Map<String, Map<String, Integer>> readRunFile(String filename)
    {
        Map<String, Map<String, Integer>> map = new LinkedHashMap<String, Map<String, Integer>>();

        File fp = new File(filename);
        FileReader reader;
        BufferedReader buffer = null;


        try {
            reader = new FileReader(fp); // reading from file
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
// split operation
                String[] w = line.split(" ");
                String outKey = w[0];

                if (map.containsKey(outKey)) {
                    Map<String, Integer> extract = map.get(outKey);
                    String inner_key = w[2];
                    Integer is_relevant = new Integer(w[3]);
                    //
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
    public static Integer docRanking(Map<String,Map<String,Integer>> m, String queryID, String paraID)
    {

        if(m.containsKey(queryID))
        {
            Map<String,Integer> inHolder = m.get(queryID);

            if(inHolder.containsKey(paraID))
            {
                    //Return the Dock rank 
                    return inHolder.get(paraID);
            }

        }

        return 0;
    }



   
   

    public static void main (String[] args) throws FileNotFoundException {
    double Correlation = 0.0;
        int Qry=0;

       String file="custom.txt";
      String  file1="LNC.txt";
        Map<String, Map<String, Integer>> LucDft = readRunFile(file);
        Map<String, Map<String, Integer>> custom = readRunFile(file1);
       
        for (Map.Entry<String, Map<String, Integer>> Query : LucDft.entrySet())
        {

            Qry++;

            int dsqr=0;

       
            int n = 0;

                       int currRankDoc = 0;

            for (Map.Entry<String, Integer> p : Query.getValue().entrySet())
            {
                    int diff=0;

                    currRankDoc++;
                   
                    Integer docRank = docRanking(custom,Query.getKey(),p.getKey());

                    if(docRank!=0)
                    {
                        n++;
                       
                        diff = currRankDoc - docRank;
                        dsqr += Math.pow(diff,2);
                    }

            }

            double numerator = 6*dsqr;
            double denom = n *((Math.pow(n,2))-1);

            if(denom == 0)
            {
                continue;
            }

            double result = 1-(numerator /denom);

            Correlation+= result;
        }

      double result1=  Correlation/Qry;
      System.out.println(result1);
                  }
}

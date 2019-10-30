import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

/*Reading the files from the TREC Complex Answer Retrieval data set that has been unpacked */


public class App
{
    private static final String INDEX_DIR = "index";
    //private static final String FILE_DIR = "D:/test200/test200-train/train.pages.cbor-paragraphs.cbor";
  //  private static final String File = "F:/qwe/train/base.train.cbor-outlines.cbor";
    private static final String DEFAULT_SCORE_FILE = "final.txt";


    /* Documents are added in the form of Id and Text */

    private static HashMap<String, String> queries = null;


    private static Document createDocument(String id, String text)
    {
        Document document = new Document();
        
        /* 
        we do not want the paragraph id to be tokenized and hence you should not store it as a TextField in Lucene but rather as a
StringField. 
add this document to the index using the Lucene method IndexWriter.addDocument().

        
        */
        
        document.add(new StringField("id", id , Field.Store.YES));
        document.add(new TextField("text", text , Field.Store.YES));
        return document;
    }

    /* Index Writer is created in Lucene */




    public static void main( String[] args ) throws IOException, ParseException
    { 
    	System.setProperty("file.encoding", "UTF-8");	
    	String indexPath = INDEX_DIR;

        

        Path path1 = Paths.get(indexPath);
        Directory Dir1 = FSDirectory.open(path1);
        IndexReader reader = DirectoryReader.open(Dir1);

        /* Using IndexSearcher Lucene index is searched*/

        IndexSearcher searcher =new IndexSearcher(reader);

        /* BM25 Retrieval model is used */

        searcher.setSimilarity(new BM25Similarity());

       

            System.out.println("started");
            queries = new HashMap<String, String>();
            String File=args[0];
            File file = new File(File);
           
            final FileInputStream fileInputStream = new FileInputStream(file);
            for(Data.Page p: DeserializeData.iterableAnnotations(fileInputStream)) {
                String queryId = p.getPageId();
                String query = p.getPageName();
                queries.put(queryId, query);
        	
            }
            System.out.println("done wit outlines file");
            int maxResults = 10;
            

            List<String> out= new ArrayList<String>();

            for (Map.Entry<String, String> que: queries.entrySet()) {
                String queryId = que.getKey();
                String query = que.getValue();

                TopDocs tops = searcher.search(toQuery(query), maxResults);
                ScoreDoc[] scoreDoc = tops.scoreDocs;

                int rank = 1;
                for (ScoreDoc score : scoreDoc) {
                    final Document doc = searcher.doc(score.doc);
                    String q = queryId + " " + "Q0" + " " + doc.getField("id").stringValue() + " " + rank + " " + score.score + " " + "team7" + "-" + "BM25";
                    out.add(q);
                    rank += 1;
                			}  }
            
            FileWriter writer1 = new FileWriter(DEFAULT_SCORE_FILE);
            for(String output: out) {
                writer1.write(output + System.lineSeparator());
            }
            writer1.close();
            
         


        }
    
    
    
    public static BooleanQuery toQuery(String queryStr) throws IOException {
    	EnglishAnalyzer analyzer = new EnglishAnalyzer();
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

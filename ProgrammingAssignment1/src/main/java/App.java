package team07.assign01;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;


/*Reading the files from the TREC Complex Answer Retrieval data set that has been unpacked */


public class score

{
   // private static final String INDEX_DIR = "index";
   // private static final String FILE_DIR = "D:/test200/test200-train/train.pages.cbor-paragraphs.cbor";

    /* Documents are added in the form of Id and Text */



    private static Document createDocument(String id, String text)
    {
        Document document = new Document();
        document.add(new StringField("id", id , Field.Store.YES));
        document.add(new TextField("text", text , Field.Store.YES));
        return document;
    }

    /* Index Writer is created in Lucene */




    public static void main( String[] args ) throws IOException, ParseException
    {	
    	String P = args[1];
		final String P1 = args[0];

    	Path path = Paths.get(P);
        Directory Dir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer=  new IndexWriter(Dir, config);
        writer.deleteAll();


        /* Using the DeserializeData.iterableParagraphs,
        each paragraph from the corpus is converted to Lucene document */

        /* Using getParaId() and getTextOnly(), paragraph Id and Text is obtained for each para */

        FileInputStream stream = new FileInputStream(new File(P1));
        for(Data.Paragraph p: DeserializeData.iterableParagraphs(stream)) {
            String pId = p.getParaId();
            String textOnly = p.getTextOnly();
            Document document = createDocument(pId, textOnly);

            /* This document is added to the Index */

            writer.addDocument(document);

        }


        //writer.commit();
        writer.close();




        Path path1 = Paths.get(P);
        Directory Dir1 = FSDirectory.open(path1);
        IndexReader reader = DirectoryReader.open(Dir1);

        /* Using IndexSearcher Lucene index is searched*/

        IndexSearcher searcher =new IndexSearcher(reader);



    /* using the custom Scoriing function, instance of SimilarityBase is created */
        
        
        searcher.setSimilarity(new SimilarityBase() {
        	@Override
        	public String toString() {
        	// TODO Auto-generated method stub
        	return "similarity base";
        	}

        	@Override
        	protected float score(BasicStats stats, float freq, float docLen) {
        	// TODO Auto-generated method stub
        	return freq;
        	}
        	});
        

        int number = 10;
  
         String[] entry = new String[3];
         entry[0]="power nap bene?ts";
         entry[1]="whale vocalization production of sound ";
         entry[2]="pokemon puzzle league";
         System.out.println("Using custom scoring function");
         
         for(int i=0;i<entry.length;i++) {
             System.out.println("Requested query is: "+entry[i]);

             /* QueryParser will convert queries into Lucene queries  */


             Query  query = new QueryParser("text", new StandardAnalyzer()).parse(entry[i]);


             TopDocs top=searcher.search(query,number);

             /* Scoring the top documents using top.scoreDocs  */

             ScoreDoc[] scoreDoc = top.scoreDocs;

             for (ScoreDoc score : scoreDoc) {
             
                 final Document doc = searcher.doc(score.doc);
                 System.out.println(doc.getField("id").stringValue() + "-" + doc.getField("text").stringValue());
             }System.out.println("------------------------------------------------------------");

         }


     }
 }

package Team7.Assign1;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

/*Reading the files from the TREC Complex Answer Retrieval data set that has been unpacked */


public class App
{
    private static final String INDEX_DIR = "index";
    private static final String FILE_DIR = "D:/test200/test200-train/train.pages.cbor-paragraphs.cbor";

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

        Path path = FileSystems.getDefault().getPath(INDEX_DIR, "lucene");
        Directory Dir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer=  new IndexWriter(Dir, config);
        writer.deleteAll();


        /* Using the DeserializeData.iterableParagraphs,
        each paragraph from the corpus is converted to Lucene document */

        /* Using getParaId() and getTextOnly(), paragraph Id and Text is obtained for each para */

        final FileInputStream fileInputStream2 = new FileInputStream(new File(FILE_DIR));
        for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2)) {
            String pId = p.getParaId();
            String textOnly = p.getTextOnly();
            Document document = createDocument(pId, textOnly);

            /* This document is added to the Index */

            writer.addDocument(document);

        }


        //writer.commit();
        writer.close();




        Path path1 = FileSystems.getDefault().getPath(INDEX_DIR, "lucene");
        Directory Dir1 = FSDirectory.open(path1);
        IndexReader reader = DirectoryReader.open(Dir1);

        /* Using IndexSearcher Lucene index is searched*/

        IndexSearcher searcher =new IndexSearcher(reader);

        /* BM25 Retrieval model is used */

        searcher.setSimilarity(new BM25Similarity());

        int number = 10;

        String[] entry = new String[3];
        entry[0]="power nap benefits";
        entry[1]="whale vocalization production of sound ";
        entry[2]="pokemon puzzle league";
        System.out.println("Using BM25 scoring function");

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
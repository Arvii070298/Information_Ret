Team 07

Medhini Shankar Narayan  
Akhila Bezawada  
Sai Arvind Reddy Desireddy  


JDK Version used - 12.0.2  
Apache-maven version - 3.6.2  

 team7/ProgrammingAssignment2/  
This program has 2 java files (Eval.java and assign2.java) and 1 pom.xml file in total  

created a *default.txt* file to store the output

Download the TREC Complex Answer Retrieval “test200“ dataset and unpack

***wget http://trec-car.cs.unh.edu/datareleases/v2.0/test200.v2.0.tar.xz*
*tar xf test200.v2.0.tar.xz***


Directory://test200/test200-train/train.pages.cbor-article.qrels

For this program to work on Linux, Maven has to be installed  
Instructions to install Maven in Terminal:  

***sudo apt install maven***

Before compiling keep the Eval.java, assign2.java, paragraph.cbor file in the main directory where pom.xml is present.  
Program gets complied by

***mvn clean compile assembly:single***

To run the Program (Question 1) assign2.java make sure it is in current pom.xml directory,

***java -Xmx50g -cp ADD PATH/target/assign01-0.0.1-SNAPSHOT-jar-with-dependencies.jar assign2.java ADD PATH/fold-0-train.pages.cbor-paragraphs.cbor ADD PATH/files/***  
To run the Program (Question 3, 4, 5) Eval.java  make sure it is in current pom.xml directory,  

***java -Xmx50g -cp ADD PATH/target/assign01-0.0.1-SNAPSHOT-jar-with-dependencies.jar eval.java ADD PATH/fold-0-train.pages.cbor-paragraphs.cbor ADD PATH/files/***


 For Question 2
Download evaluation program trec eval 

***http://trec.nist.gov/trec_eval/***
Directory://test200/test200-train/train.pages.cbor-article.qrels
Rprec

***./trec_eval -m Rprec /ADD PATH/test200/test200-train/train.pages.cbor-article.qrels /ADD PATH/default.txt -q -c***

MAP

***./trec_eval -m map /ADD PATH/test200/test200-train/train.pages.cbor-article.qrels /ADD PATH/default.txt -q -c***



NDCG20

*** ./trec_eval -m ndcg_cut.20 /ADD PATH/test200/test200-train/train.pages.cbor-article.qrels /ADD PATH/default.txt -q -c***
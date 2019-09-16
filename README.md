Team 07

Medhini Shankar Narayan

Akhila Bezawada

Sai Arvind Reddy Desireddy


JDK Version used - 12.0.2
Apache-maven version - 3.6.2

cd team7/ProgrammingAssignment1/
This program has 2 java files (Eval.java and assign2.java) and 1 pom.xml file in total

created a default.txt file to store the output

Download the TREC Complex Answer Retri
eval “test200“ dataset and unpack

wget http://trec-car.cs.unh.edu/datareleases/v2.0/test200.v2.0.tar.xz
tar xf test200.v2.0.tar.xz
Diectory://test200/test200-train/train.pages.cbor-article.qrels

For this program to work on Linux, Maven has to be installed
Instructions to install Maven in Terminal:
sudo apt install maven
Before compiling keep the Eval.java,assign2.java,paragraph.cbor file in the main directory where pom.xml is present .
Program gets complied by
mvn clean compile assembly:single
To run the Program(assign2.java) make sure it`s in current pom.xml directory,

java -Xmx50g -cp ADD PATH/target/assign01-0.0.1-SNAPSHOT-jar-with-dependencies.jar assign2.java ADD PATH/fold-0-train.pages.cbor-paragraphs.cbor ADD PATH/files/
To run the Program(Eval.java) make sure it`s in current pom.xml directory,
java -Xmx50g -cp ADD PATH/target/assign01-0.0.1-SNAPSHOT-jar-with-dependencies.jar eval.java ADD PATH/fold-0-train.pages.cbor-paragraphs.cbor ADD PATH/files/
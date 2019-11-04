mkdir Folder
cd Folder
echo "---------------installing maven-----------"

sudo apt install maven

echo "----------------clone---------------"
git clone https://gitlab.cs.unh.edu/cs753-853-2019/team7.git
cd team7/ProgrammingAssignment4/

wget http://trec-car.cs.unh.edu/datareleases/v2.0/test200.v2.0.tar.xz
tar xf test200.v2.0.tar.xz
rm xf test200.v2.0.tar.xz
wget https://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz
tar -xf trec_eval_latest.tar.gz
rm trec_eval_latest.tar.gz

echo "-------------------------------mvn clean install------------------------"

mvn clean install
java -jar target/project-1.0-SNAPSHOT-jar-with-dependencies.jar test200/test200-train/train.pages.cbor-paragraphs.cbor test200/test200-train/train.pages.cbor-outlines.cbor 1
java -jar target/project-1.0-SNAPSHOT-jar-with-dependencies.jar test200/test200-train/train.pages.cbor-paragraphs.cbor test200/test200-train/train.pages.cbor-outlines.cbor 2
java -jar target/project-1.0-SNAPSHOT-jar-with-dependencies.jar test200/test200-train/train.pages.cbor-paragraphs.cbor test200/test200-train/train.pages.cbor-outlines.cbor 3
java -jar target/project-1.0-SNAPSHOT-jar-with-dependencies.jar test200/test200-train/train.pages.cbor-paragraphs.cbor test200/test200-train/train.pages.cbor-outlines.cbor 4

echo "--------------------------O/P Files have been created-----------------------"
cd trec_eval*
make

echo "------------------------------------Unigram laplace Results--------------------------------"

./trec_eval -m map ../test200/test200-train/train.pages.cbor-article.qrels ../UL.txt
./trec_eval -m Rprec ../test200/test200-train/train.pages.cbor-article.qrels ../UL.txt
./trec_eval -m ndcg_cut.20 ../test200/test200-train/train.pages.cbor-article.qrels ../UL.txt

echo "------------------------------------Unigram-JM Results--------------------------------"


./trec_eval -m map ../test200/test200-train/train.pages.cbor-article.qrels ../U-JM.txt
./trec_eval -m Rprec ../test200/test200-train/train.pages.cbor-article.qrels ../U-JM.txt
./trec_eval -m ndcg_cut.20 ../test200/test200-train/train.pages.cbor-article.qrels ../U-JM.txt

echo "------------------------------------Unigram-DS Results--------------------------------"


./trec_eval -m map ../test200/test200-train/train.pages.cbor-article.qrels ../U-DS.txt
./trec_eval -m Rprec ../test200/test200-train/train.pages.cbor-article.qrels ../U-DS.txt
./trec_eval -m ndcg_cut.20 ../test200/test200-train/train.pages.cbor-article.qrels ../U-DS.txt


echo "------------------------------------Bi gram-S Results--------------------------------"

./trec_eval -m map ../test200/test200-train/train.pages.cbor-article.qrels ../B-L.txt
./trec_eval -m Rprec ../test200/test200-train/train.pages.cbor-article.qrels ../B-L.txt
./trec_eval -m ndcg_cut.20 ../test200/test200-train/train.pages.cbor-article.qrels ../B-L.txt







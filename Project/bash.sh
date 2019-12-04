mkdir Folder
cd Folder
echo "---------------installing maven-----------"

sudo apt install maven

echo "----------------clone---------------"

git clone https://gitlab.cs.unh.edu/cs753-853-2019/team7.git
cd team7/Project/

wget http://trec-car.cs.unh.edu/datareleases/v2.0/benchmarkY1-train.v2.0.tar.xz

tar xf benchmarkY1-train.v2.0.tar.xz
rm xf benchmarkY1-train.v2.0.tar.xz



wget https://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz
tar -xf trec_eval_latest.tar.gz
rm trec_eval_latest.tar.gz

mvn clean install

java -jar target/project-1.0-SNAPSHOT-jar-with-dependencies.jar benchmarkY1/benchmarkY1-train/train.pages.cbor-paragraphs.cbor benchmarkY1/benchmarkY1-train/train.pages.cbor-outlines.cbor benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels

cd trec_eval*
make

echo("------------------------------------------BM25 Results:------------------------------------")
./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../BM25.txt -m map -m Rprec -m ndcg_cut.20


echo("------------------------------------------LNC.LTN Results--------------------------------------")
./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../LNC.txt -m map -m Rprec -m ndcg_cut.20


echo("------------------------------------------BNN.BNN Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../BNN.txt -m map -m Rprec -m ndcg_cut.20
 
 
 echo("------------------------------------------ANC.APN Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../ANC.txt -m map -m Rprec -m ndcg_cut.20
 
 
 
 echo("------------------------------------------UL Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../Lap.txt -m map -m Rprec -m ndcg_cut.20
 
 
 echo("------------------------------------------UJM Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../Jm.txt -m map -m Rprec -m ndcg_cut.20
 
 
 echo("------------------------------------------UD Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../Dir.txt -m map -m Rprec -m ndcg_cut.20
 
 
 echo("------------------------------------------bi_word Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../biword.txt -m map -m Rprec -m ndcg_cut.20
 
 
 
 echo("------------------------------------------Champion List Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../ChampionList.txt -m map -m Rprec -m ndcg_cut.20
 
 
 echo("------------------------------------------IndexElimination Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../IndexElimination.txt -m map -m Rprec -m ndcg_cut.20
 
 echo("------------------------------------------L2R Results--------------------------------------")
 ./trec_eval ../benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels ../new_L2R.txt -m map -m Rprec -m ndcg_cut.20
 
 
 
 










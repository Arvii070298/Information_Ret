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

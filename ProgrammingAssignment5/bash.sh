mkdir Folder
cd Folder
echo "---------------installing maven-----------"

sudo apt install maven

echo "----------------clone---------------"
git clone https://gitlab.cs.unh.edu/cs753-853-2019/team7.git
cd team7/ProgrammingAssignment5/

wget http://trec-car.cs.unh.edu/datareleases/v2.0/test200.v2.0.tar.xz
tar xf test200.v2.0.tar.xz
rm xf test200.v2.0.tar.xz
wget https://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz
tar -xf trec_eval_latest.tar.gz
rm trec_eval_latest.tar.gz

echo "-------------------------------mvn clean install------------------------"

mvn clean install
java -jar target/project-1.0-SNAPSHOT-jar-with-dependencies.jar 

echo "--------------------------O/P Files have been created-----------------------"

 cd ..
 echo "------------------------------------------L2R Results--------------------------------------"
 java -jar RankLib.jar -train new_L2R.txt -ranker 4 -kcv 5  -save ./benchmarkY1/benchmarkY1-train/train.pages.cbor-article.qrels -metric2t MAP

 
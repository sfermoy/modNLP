#DATE=`date +"%Y%m%d%H%M%S"`
INDEXLOG=logs/indexing.log

index:
	echo '\n'Indexing files on `date` '\n========================================' >> $(INDEXLOG) 2>&1
##       testing should have been performed elsewhere
##	./testincoming.sh  2>&1 | tee -a  $(INDEXLOG)
	./indexincoming.pl  2>&1 | tee -a  $(INDEXLOG)

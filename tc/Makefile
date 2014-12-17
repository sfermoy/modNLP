SRCCLASSPATH=.
TOPCLASSPATH=src
SOURCES=modnlp/*.java modnlp/util/*.java modnlp/dstruct/*.java modnlp/tc/util/*.java modnlp/tc/dstruct/*.java modnlp/tc/parser/*.java modnlp/tc/tsr/*.java modnlp/tc/evaluation/*.java modnlp/tc/induction/*.java modnlp/tc/classify/*.java 
DOCS=doc
SOURCEDIR=src
DISTDIR:=modnlp-tc-`cat VERSION`
BINDISTDIR=$(DISTDIR)-bin
DISTFILES=BUGS COPYING ChangeLog INSTALL Makefile README TODO VERSION AUTHORS\
	compile.sh exclude.txt run.sh doc lib/MinML2.jar $(subst modnlp/, src/modnlp/, $(SOURCES)) data
#JAVA=jamvm
#JAVAC=jikes-classpath
JAVA=java
JAVAC=javac
JAVAFLAGS=-classpath $(TOPCLASSPATH)
JAVACFLAGS= -classpath $(SRCCLASSPATH)  -encoding UTF-8 -target 1.5
JARTARGET=$(TOPDIR)lib/tc.jar
JAR=jar
JARFLAGS= cvf 
JARREDS=$(subst .java,.class,$(SOURCES))

build:
	cd $(SOURCEDIR) && $(JAVAC) $(JAVACFLAGS) $(SOURCES)

jar: build
	cd $(SOURCEDIR) && $(JAR) $(JARFLAGS) ../$(JARTARGET) $(JARREDS)

clean:
	find -L . -iregex '.*\(\.aux\|\.log\|\.dvi\|\.class\|~\|#.*#\b\)' -type f -print0 |xargs -0 -e rm -f

test:
	@echo "Testing TSR..."
	$(JAVA) $(JAVAFLAGS) modnlp.tc.tsr.MakeReducedTermSet data/tinycorpus.lst data/stopwds.txt 10 InfoGain acq  > /tmp/tinycorpus.tsr
	@echo "** TSR scores saved to /tmp/tinycorpus.tsr"
	@echo "Testing ARFF file generation..."
	$(JAVA) $(JAVAFLAGS) -classpath $(TOPCLASSPATH) modnlp.tc.util.MakeARFF data/tinycorpus.lst data/stopwds.txt 10 InfoGain acq tfidf > /tmp/tinycorpus.arff
	@echo "** ARFF file (of TFIDF vectors) saved to /tmp/tinycorpus.arff"
	@echo "Testing Probability Model Induction..."
	$(JAVA) $(JAVAFLAGS) -classpath $(TOPCLASSPATH) modnlp.tc.induction.MakeProbabilityModel data/tinycorpus.lst data/stopwds.txt 10 InfoGain acq /tmp/tinycorpus.pm
	@echo "Probability model saved to /tmp/tinycorpus.pm"
	@echo "Testing Classification..."
	$(JAVA) $(JAVAFLAGS) -classpath $(TOPCLASSPATH) modnlp.tc.classify.BVBayes data/tinycorpus.lst acq /tmp/tinycorpus.pm proportional >/tmp/tinycorpus.log
	@echo "** Classification results saved to /tmp/tinycorpus.log"
	@echo "Testing completed. See lines starting with '**' for location of log files."

bigtest:
	@echo "Testing TSR..."
	$(JAVA) $(JAVAFLAGS) modnlp.tc.tsr.MakeReducedTermSet data/reuters-21578-000-002.lst data/stopwds.txt 10 InfoGain acq  > /tmp/reuters-21578-000-002.tsr
	@echo "** TSR scores saved to /tmp/reuters-21578-000-002.tsr"
	@echo "Testing Probability Model Induction..."
	$(JAVA) $(JAVAFLAGS) -classpath $(TOPCLASSPATH) modnlp.tc.induction.MakeProbabilityModel data/reuters-21578-000-002.lst data/stopwds.txt 10 InfoGain acq /tmp/reuters-21578-000-002.pm
	@echo "Probability model saved to /tmp/reuters-21578-000-002.pm"
	@echo "Testing Classification..."
	$(JAVA) $(JAVAFLAGS) -classpath $(TOPCLASSPATH) modnlp.tc.classify.BVBayes data/reuters-21578-000-002.lst acq /tmp/reuters-21578-000-002.pm proportional >/tmp/reuters-21578-000-002.log
	@echo "** Classification results saved to /tmp/reuters-21578-000-002.log"
	@echo "Testing completed. See lines starting with '**' for location of log files."

docs:
	javadoc -d $(DOCS) -sourcepath $(SOURCEDIR) -subpackages modnlp.tc

dist: docs
	mkdir $(DISTDIR)
	cp -aL --parents $(DISTFILES) $(DISTDIR)
	tar cfvz /tmp/$(DISTDIR).tar.gz  --exclude-from=exclude.txt $(DISTDIR)
	rm -rf $(DISTDIR)


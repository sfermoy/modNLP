TOPDIR=
SOURCEDIR=$(TOPDIR)src/
LIBSPATH=$(TOPDIR)lib/gnu-regexp.jar:$(TOPDIR)lib/je.jar:$(TOPDIR)lib/antlr-2.7.6.jar:$(TOPDIR)lib/commons-pool-1.2.jar:$(TOPDIR)lib/exist-modules.jar:$(TOPDIR)lib/exist.jar:$(TOPDIR)lib/jgroups-all.jar:$(TOPDIR)lib/log4j-1.2.14.jar:$(TOPDIR)lib/resolver.jar:$(TOPDIR)lib/sunxacml.jar:$(TOPDIR)lib/xmldb.jar:$(TOPDIR)lib/xmlrpc-1.2-patched.jar:$(TOPDIR)lib/lucene-core-3.6.0.jar:$(TOPDIR)lib/lucene-analyzers-3.6.0.jar:$(TOPDIR)lib/lucene-kuromoji-3.6.0.jar
LIBS=$(subst :, ,$(LIBSPATH))
LIBLICENSES=$(TOPDIR)lib/COPYING-libs
DOCS=$(TOPDIR)doc
DATA=$(TOPDIR)data
SOURCES=$(SOURCEDIR)modnlp/Constants.java \
	$(SOURCEDIR)modnlp/util/PrintUtil.java \
	$(SOURCEDIR)modnlp/util/LogStream.java \
	$(SOURCEDIR)modnlp/util/Tokeniser.java \
	$(SOURCEDIR)modnlp/util/IOUtil.java \
	$(SOURCEDIR)modnlp/util/XMLSplitter.java \
	$(SOURCEDIR)modnlp/dstruct/CorpusList.java \
	$(SOURCEDIR)modnlp/dstruct/CorpusFile.java \
	$(SOURCEDIR)modnlp/dstruct/FrequencyHash.java \
	$(SOURCEDIR)modnlp/dstruct/IntegerSet.java \
	$(SOURCEDIR)modnlp/dstruct/IntOffsetArray.java \
	$(SOURCEDIR)modnlp/dstruct/PositionSet.java \
	$(SOURCEDIR)modnlp/dstruct/SubcorpusDelimPair.java	\
	$(SOURCEDIR)modnlp/dstruct/SubcorpusMap.java	\
	$(SOURCEDIR)modnlp/dstruct/StopWordList.java \
	$(SOURCEDIR)modnlp/dstruct/StringSet.java \
	$(SOURCEDIR)modnlp/dstruct/Token.java \
	$(SOURCEDIR)modnlp/dstruct/TokenMap.java \
	$(SOURCEDIR)modnlp/dstruct/TokenIndex.java \
	$(SOURCEDIR)modnlp/dstruct/WordForms.java \
	$(SOURCEDIR)modnlp/dstruct/WordFrequencyPair.java \
	$(SOURCEDIR)modnlp/idx/database/AlreadyIndexedException.java \
	$(SOURCEDIR)modnlp/idx/database/CaseTable.java \
	$(SOURCEDIR)modnlp/idx/database/DescIntComparator.java \
	$(SOURCEDIR)modnlp/idx/database/DictProperties.java \
	$(SOURCEDIR)modnlp/idx/database/Dictionary.java \
	$(SOURCEDIR)modnlp/idx/database/EmptyFileException.java \
	$(SOURCEDIR)modnlp/idx/database/FileTable.java \
	$(SOURCEDIR)modnlp/idx/database/FreqKeyCreator.java \
	$(SOURCEDIR)modnlp/idx/database/FreqTable.java \
	$(SOURCEDIR)modnlp/idx/database/IntegerSetBinding.java \
	$(SOURCEDIR)modnlp/idx/database/IntOffsetArrayBinding.java \
	$(SOURCEDIR)modnlp/idx/database/NotIndexedException.java \
	$(SOURCEDIR)modnlp/idx/database/StringIntKey.java \
	$(SOURCEDIR)modnlp/idx/database/StringIntKeyBinding.java \
	$(SOURCEDIR)modnlp/idx/database/StringSetBinding.java \
	$(SOURCEDIR)modnlp/idx/database/SubcorpusDelimPairBinding.java \
	$(SOURCEDIR)modnlp/idx/database/SubcorpusKeyCreator.java \
	$(SOURCEDIR)modnlp/idx/database/Table.java \
	$(SOURCEDIR)modnlp/idx/database/TPosTable.java \
	$(SOURCEDIR)modnlp/idx/database/WordFileTable.java \
	$(SOURCEDIR)modnlp/idx/database/WordPositionTable.java \
	$(SOURCEDIR)modnlp/idx/database/SubcorpusDirectory.java \
	$(SOURCEDIR)modnlp/idx/database/SubcorpusTable.java \
	$(SOURCEDIR)modnlp/idx/headers/HeaderDBManager.java \
	$(SOURCEDIR)modnlp/idx/headers/ConstraintCache.java \
	$(SOURCEDIR)modnlp/idx/inverted/StringSplitter.java \
	$(SOURCEDIR)modnlp/idx/inverted/SubcorpusIndexer.java \
	$(SOURCEDIR)modnlp/idx/inverted/TokeniserGNU.java \
	$(SOURCEDIR)modnlp/idx/inverted/TokeniserRegex.java \
	$(SOURCEDIR)modnlp/idx/inverted/TokeniserJP.java \
	$(SOURCEDIR)modnlp/idx/inverted/TokeniserJPLucene.java \
	$(SOURCEDIR)modnlp/idx/inverted/jpsegmenter/BasicCodePointReader.java \
	$(SOURCEDIR)modnlp/idx/inverted/jpsegmenter/CodePointReader.java \
	$(SOURCEDIR)modnlp/idx/inverted/jpsegmenter/PushbackCodePointReader.java \
	$(SOURCEDIR)modnlp/idx/inverted/jpsegmenter/TinySegmenterConstants.java \
	$(SOURCEDIR)modnlp/idx/inverted/jpsegmenter/TinySegmenter.java \
	$(SOURCEDIR)modnlp/idx/ConsumerQuery.java \
	$(SOURCEDIR)modnlp/idx/Dump.java \
	$(SOURCEDIR)modnlp/idx/MakeTECIndex.java \
	$(SOURCEDIR)modnlp/idx/MakeSectionIndex.java \
	$(SOURCEDIR)modnlp/idx/Query.java \
	$(SOURCEDIR)modnlp/idx/IndexManager.java \
	$(SOURCEDIR)modnlp/idx/IndexManagerProperties.java \
	$(SOURCEDIR)modnlp/idx/RemoveFileFromIndex.java \
	$(SOURCEDIR)modnlp/idx/gui/CorpusChooser.java \
	$(SOURCEDIR)modnlp/idx/gui/CorpusFilesChooser.java \
	$(SOURCEDIR)modnlp/idx/gui/HeaderURLChooser.java \
	$(SOURCEDIR)modnlp/idx/gui/IndexConfigChooser.java \
	$(SOURCEDIR)modnlp/idx/gui/IndexingReporter.java \
	$(SOURCEDIR)modnlp/idx/gui/IndexManagerUI.java \
	$(SOURCEDIR)modnlp/idx/gui/IndexManagerCL.java \
	$(SOURCEDIR)modnlp/idx/query/Horizon.java \
	$(SOURCEDIR)modnlp/idx/query/PrepContextQuery.java \
	$(SOURCEDIR)modnlp/idx/query/SubcorpusConstraints.java	\
	$(SOURCEDIR)modnlp/idx/query/WordQuery.java \
	$(SOURCEDIR)modnlp/idx/query/WordQueryException.java
TARGETS=$(subst .java,.class,$(SOURCES))
MAINJARS=$(subst $(SOURCEDIR),,$(TARGETS)) # doesn't work by itself: need to jar inner classess as well
JARREDS=$(MAINJARS) \
	eXistConf.xml \
	'modnlp/dstruct/FrequencyHash$$ValueSorter.class' \
	'modnlp/dstruct/TokenIndex$$TokenCoordinates.class' \
	'modnlp/idx/inverted/jpsegmenter/TinySegmenter$$1.class' \
	'modnlp/idx/inverted/jpsegmenter/TinySegmenter$$CharInfo.class' \
	'modnlp/idx/ConsumerQuery$$ConcordanceProducer.class' \
	'modnlp/idx/IndexManager$$DeindexingThread.class' \
	'modnlp/idx/IndexManager$$IndexingThread.class'
DISTDIR:=modnlp-idx-`cat VERSION`
BINDISTDIR=$(DISTDIR)-bin
BINDISTXTRAS=$(LIBLICENSES) TUTORIAL.txt
DISTFILES=AUTHORS COPYING Makefile README TUTORIAL.txt VERSION BUGS INSTALL TODO exclude.txt bin/runidx.sh \
	$(LIBS) $(DOCS) $(DATA) $(SOURCES) $(SOURCEDIR)modnlp/idx/prj.el $(SOURCEDIR)idx-manifest.txt
#JAVA=jamvm
JAVAC=javac -encoding UTF-8 #-target 1.5 
#JAVAC=ecj -1.5
#JAVAC=/usr/lib/jvm/java-1.5.0-sun-1.5.0.16/bin/javac
#JAVAC=jikes -bootclasspath /usr/lib/jvm/java-1.5.0-sun/jre/lib/rt.jar -source 1.5
JAVA=java
JAR=jar
JAVAFLAGS=-classpath $(SOURCEDIR):$(LIBSPATH)
JAVACFLAGS=-classpath $(SOURCEDIR):$(LIBSPATH)
MANIFEST=idx-manifest.txt
JARFLAGS= cvmf $(MANIFEST)
JARTARGET=$(TOPDIR)lib/idx.jar
#VPATH=$(SOURCEDIR)

.PHONY: build jar test docs dist

all: build jar test

%.class: %.java
		$(JAVAC) $(JAVAFLAGS) $?

build: $(TARGETS)

buildall:
	$(JAVAC) $(JAVAFLAGS) $(SOURCES)

clean:
	find -L . -iregex '.*\(\.aux\|\.log\|\.dvi\|\.class\|~\|#.*#\b\)' -type f -print0 |xargs -0 -e rm -f

jar: $(TARGETS)
	cd $(SOURCEDIR) && $(JAR)  $(JARFLAGS) ../$(JARTARGET) $(JARREDS)

test: $(TARGETS)
	@echo "Testing Indexer..."
	-rm -rf /tmp/idxtest/
	-mkdir /tmp/idxtest/
	-cp -a data/* /tmp/idxtest/
	mkdir -p /tmp/idxtest/index/
	mkdir -p /tmp/idxtest/data/ep/
	mkdir -p /tmp/idxtest/data/eph/
	cp src/dictionary.properties.ts /tmp/idxtest/index/dictionary.properties
	cp $(TOPDIR)data/idxtest.lst /tmp/idxtest/data/
	cp $(TOPDIR)data/ep/* /tmp/idxtest/data/ep/
	cp $(TOPDIR)data/eph/* /tmp/idxtest/data/eph/
	@echo "** Indexing /tmp/idxtest/data/idxtest.lst"
	$(JAVA) $(JAVAFLAGS) modnlp.idx.IndexManager /tmp/idxtest/index /tmp/idxtest/data/idxtest.lst /tmp/idxtest/data/eph http://localhost/ -v
	@echo "** Dumping index..."
	$(JAVA) $(JAVAFLAGS) modnlp.idx.Dump /tmp/idxtest/index
	@echo "** Querying concordance for word 'that'..."
	$(JAVA) $(JAVAFLAGS) modnlp.idx.Query /tmp/idxtest/index -l that
	@echo "** Testing completed"

docs: $(TARGETS)
	javadoc -encoding UTF-8 -d $(DOCS) -sourcepath $(SOURCEDIR) -classpath $(SOURCEDIR):$(LIBSPATH) -subpackages modnlp.idx

dist: clean
	rm -rf $(DISTDIR)
	mkdir $(DISTDIR)
	cp -aL --parents $(DISTFILES) $(DISTDIR)
	cp $(TOPDIR)src/idxmgr.properties.ecpc $(DISTDIR)/idxmgr.properties
	cp $(TOPDIR)src/dictionary.properties.ISO8859_1 $(DISTDIR)/dictionary.properties
	tar cfvz /tmp/$(DISTDIR).tar.gz  --exclude-from=exclude.txt $(DISTDIR)
	rm -rf $(DISTDIR)

bindist: jar
	mkdir -p $(BINDISTDIR)/data/ep/
	mkdir -p $(BINDISTDIR)/data/eph/
	cp  $(TOPDIR)data/idxtest.lst $(BINDISTDIR)/data
	cp  $(TOPDIR)data/ep/* $(BINDISTDIR)/data/ep
	cp  $(TOPDIR)data/eph/* $(BINDISTDIR)/data/eph
	cp README.bin $(BINDISTDIR)/README
	cp bin/runidx.sh $(BINDISTDIR)
	cp $(TOPDIR)src/idxmgr.properties.PLEASEEDIT $(BINDISTDIR)/idxmgr.properties.PLEASEEDIT
	cp $(LIBS) $(JARTARGET) $(BINDISTXTRAS) $(BINDISTDIR)
	tar cfvz /tmp/$(BINDISTDIR).tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)
	rm -rf $(BINDISTDIR)

bindistec: jar
	mkdir $(BINDISTDIR)-tec
	cp README.bin $(BINDISTDIR)-tec/README
	cp bin/runidx.sh $(BINDISTDIR)-tec
	cp $(TOPDIR)src/idxmgr.properties.tec $(BINDISTDIR)-tec/idxmgr.properties
	cp $(LIBS) $(JARTARGET) $(BINDISTXTRAS) $(BINDISTDIR)-tec
	tar cfvz /tmp/$(BINDISTDIR)-tec.tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)-tec
	rm -rf $(BINDISTDIR)-tec

bindisthc: jar
	mkdir $(BINDISTDIR)-hc
	cp README.bin $(BINDISTDIR)-hc/README
	cp bin/runidx.sh $(BINDISTDIR)-hc
	cp $(TOPDIR)src/idxmgr.properties.ecpc-hc $(BINDISTDIR)-hc/idxmgr.properties
	cp $(LIBS) $(JARTARGET) $(BINDISTXTRAS) $(BINDISTDIR)-hc
	tar cfvz /tmp/$(BINDISTDIR)-hc.tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)-hc
	rm -rf $(BINDISTDIR)-hc

bindistep: jar
	mkdir $(BINDISTDIR)-ep
	cp README.bin $(BINDISTDIR)-ep/README
	cp bin/runidx.sh $(BINDISTDIR)-ep
	cp $(TOPDIR)src/idxmgr.properties.ecpc-ep $(BINDISTDIR)-ep/idxmgr.properties
	cp $(LIBS) $(JARTARGET) $(BINDISTXTRAS) $(BINDISTDIR)-ep
	tar cfvz /tmp/$(BINDISTDIR)-ep.tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)-ep
	rm -rf $(BINDISTDIR)-ep

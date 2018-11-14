TOPDIR=
SOURCEDIR=$(TOPDIR)src/
LIBSPATH=$(TOPDIR)lib/je.jar:$(TOPDIR)lib/idx.jar:$(TOPDIR)lib/jung.jar:$(TOPDIR)lib/antlr-2.7.6.jar:$(TOPDIR)lib/commons-pool-1.2.jar:$(TOPDIR)lib/exist-modules.jar:$(TOPDIR)lib/exist.jar:$(TOPDIR)lib/jgroups-all.jar:$(TOPDIR)lib/log4j-1.2.14.jar:$(TOPDIR)lib/resolver.jar:$(TOPDIR)lib/sunxacml.jar:$(TOPDIR)lib/xmldb.jar:$(TOPDIR)lib/xmlrpc-1.2-patched.jar:$(TOPDIR)lib/prefuse.jar:$(TOPDIR)lib/wordleconc.jar:$(TOPDIR)lib/mosaic.jar:$(TOPDIR)lib/conctree.jar:$(TOPDIR)lib/comfre.jar:$(TOPDIR)lib/lucene-core-3.6.0.jar:$(TOPDIR)lib/lucene-analyzers-3.6.0.jar:$(TOPDIR)lib/lucene-kuromoji-3.6.0.jar
LIBS=$(subst :, ,$(LIBSPATH))
LIBLICENSES=$(TOPDIR)lib/COPYING-libs
DOCS=$(TOPDIR)doc
DATA=$(TOPDIR)data
MANIFEST=teccli-manifest.txt
SOURCES:=  $(SOURCEDIR)modnlp/Constants.java $(shell find $(SOURCEDIR)modnlp/tec/client -name *.java) $(SOURCEDIR)modnlp/gui/WrapLayout.java
TARGETS=$(subst .java,.class,$(SOURCES))
JARREDS=modnlp/*.class \
	modnlp/gui/*.class \
	modnlp/tec/client/*.class \
	modnlp/tec/client/gui/*.class \
	modnlp/tec/client/gui/event/*.class \
	modnlp/tec/client/plugin/*.class \
	modnlp/tec/client/icons/*.gif \
	modnlp/tec/client/icons/*.jpg \
	modnlp/tec/client/icons/*.png \
	modnlp/tec/client/help/*.html \
	modnlp/tec/client/help/*.jpg tecli.properties
PRJFILES:=`find $(SOURCEDIR) -name prj.el`
DISTDIR:=modnlp-teccli-`cat VERSION`
DISTFILES=AUTHORS COPYING Makefile README README.bin VERSION BUGS INSTALL TODO exclude.txt \
	$(LIBS) $(DOCS) $(DATA) $(SOURCEDIR)tecli.properties* $(SOURCEDIR)teclipluginlist.txt \
	$(TOPDIR)lib/COPYING-libs $(SOURCEDIR)modnlp/Constants.java $(SOURCEDIR)modnlp/gui \
	$(SOURCEDIR)modnlp/tec/client $(SOURCEDIR)$(MANIFEST) (SOURCEDIR)modnlp/tec/prj.el
#JAVA=jamvm
#JAVA_BIN=/usr/lib/jvm/java-6-oracle/bin/
JAVA=$(JAVA_BIN)java
JAVAC=$(JAVA_BIN)javac # -target 1.6
#JAVAC=jikes-jsdk
#JAVAC=jikes-classpath
#JAVAC=jikes -bootclasspath /usr/lib/jvm/java-1.5.0-sun/jre/lib/rt.jar -source 1.5
JAR=jar
JARFLAGS= cvmf $(MANIFEST)
JAVAFLAGS=-classpath $(SOURCEDIR):$(LIBSPATH)
JAVACFLAGS=-classpath $(SOURCEDIR):$(LIBSPATH) -encoding UTF-8 -target 7 -source 7 #ISO-8859-1
JARTARGET=$(TOPDIR)lib/teccli.jar
BINDISTDIR=$(DISTDIR)-bin
BINDISTXTRAS=$(LIBLICENSES) $(TOPDIR)*COPYING*
PROPS=tecli.properties.tec

#VPATH=$(SOURCEDIR)

.PHONY: build jar test docs dist

all: build jar test 

%.class: %.java
		$(JAVAC) $(JAVACFLAGS) $?

build: $(TARGETS)

buildall:
	$(JAVAC) $(JAVAFLAGS) $(SOURCES)

clean:
	find -L . -iregex '.*\(\.aux\|\.log\|\.dvi\|\.class\|~\|#.*#\b\)'  -type f  -print0 |xargs -0 -e rm -f

jar: $(TARGETS)
	echo props is $(PROPS)
	cd $(SOURCEDIR) && cp $(PROPS) tecli.properties && $(JAR) $(JARFLAGS) ../$(JARTARGET) $(JARREDS)

docs: $(TARGETS)
	javadoc -d $(DOCS) -sourcepath $(SOURCEDIR) -classpath $(SOURCEDIR):$(LIBSPATH) -subpackages modnlp.tec.client 

## distribution building, the joy of life...
dist:
	rm -rf $(DISTDIR)
	mkdir $(DISTDIR)
	cp -aL --parents $(DISTFILES) $(DISTDIR)
	tar cfvz /tmp/$(DISTDIR).tar.gz  --exclude-from=exclude.txt $(DISTDIR)
	rm -rf $(DISTDIR)

bindist: jar
	mkdir $(BINDISTDIR)
	cp README.bin $(BINDISTDIR)/README
	cp src/teclipluginlist.txt $(BINDISTDIR)/
	cp $(LIBS) $(JARTARGET) $(LIBLICENSES) $(BINDISTDIR)
	tar cfvz /tmp/$(BINDISTDIR).tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)
	rm -rf $(BINDISTDIR)

bindistec: MANIFEST=teccli-manifest.txt
bindistec: PROPS=tecli.properties.tec
bindistec: jar
	mkdir $(BINDISTDIR)-tec
	cp README.bin $(BINDISTDIR)-tec/README
	cp src/teclipluginlist.txt $(BINDISTDIR)-tec/
	cp $(LIBS) $(JARTARGET) $(LIBLICENSES) $(BINDISTDIR)-tec
	cp src/tecli.properties.tec $(BINDISTDIR)-tec/tecli.properties
	tar cfvz /tmp/$(BINDISTDIR)-tec.tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)-tec
	rm -rf $(BINDISTDIR)-tec

bindistgok: MANIFEST=teccli-manifest-gok.txt
bindistgok: PROPS=tecli.properties.gok
bindistgok: jar
	mkdir $(BINDISTDIR)-gok
	cp README.bin $(BINDISTDIR)-gok/README
	cp src/teclipluginlist.txt $(BINDISTDIR)-gok/
	cp $(LIBS) $(JARTARGET) $(LIBLICENSES) $(BINDISTDIR)-gok
	cp src/tecli.properties.gok $(BINDISTDIR)-gok/tecli.properties
	tar cfvz /tmp/$(BINDISTDIR)-gok.tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)-gok
	rm -rf $(BINDISTDIR)-gok

bindistecpc: MANIFEST=teccli-manifest.txt
bindistecpc: PROPS=tecli.properties.ecpc
bindistecpc: jar
	mkdir $(BINDISTDIR)-ecpc
	cp README.bin $(BINDISTDIR)-ecpc/README
	cp src/teclipluginlist.txt $(BINDISTDIR)-ecpc/
	cp $(LIBS) $(JARTARGET) $(LIBLICENSES) $(BINDISTDIR)-ecpc
	cp src/tecli.properties.ecpc $(BINDISTDIR)-ecpc/tecli.properties
	jar ufv $(BINDISTDIR)-ecpc/teccli.jar $(BINDISTDIR)-ecpc/tecli.properties
	tar cfvz /tmp/$(BINDISTDIR)-ecpc.tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)-ecpc
	rm -rf $(BINDISTDIR)-ecpc

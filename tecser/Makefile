TOPDIR=
SOURCEDIR=$(TOPDIR)src/
LIBSPATH=$(TOPDIR)lib/je.jar:lib/idx.jar:$(TOPDIR)lib/antlr-2.7.6.jar:$(TOPDIR)lib/commons-pool-1.2.jar:$(TOPDIR)lib/exist-modules.jar:$(TOPDIR)lib/exist.jar:$(TOPDIR)lib/jgroups-all.jar:$(TOPDIR)lib/log4j-1.2.14.jar:$(TOPDIR)lib/resolver.jar:$(TOPDIR)lib/sunxacml.jar:$(TOPDIR)lib/xmldb.jar:$(TOPDIR)lib/xmlrpc-1.2-patched.jar
LIBS=$(subst :, ,$(LIBSPATH))
DOCS=$(TOPDIR)doc
DATA=$(TOPDIR)data
MANIFEST=tecser-manifest.txt
SOURCES:= $(shell find $(SOURCEDIR)modnlp/tec/server -name *.java)
TARGETS=$(subst .java,.class,$(SOURCES))
JARREDS=modnlp/tec/server/*.class
PRJFILES:=`find $(SOURCEDIR) -name prj.el`
DISTDIR:=modnlp-tecser-`cat VERSION`
DISTFILES=AUTHORS COPYING Makefile README VERSION BUGS INSTALL TODO exclude.txt \
	 $(LIBS) $(DOCS) $(DATA) $(SOURCEDIR)server.properties \
	$(SOURCEDIR)modnlp/tec/server $(SOURCEDIR)$(MANIFEST) $(SOURCEDIR)modnlp/tec/prj.el
#JAVA=jamvm
#JAVAC=jikes-classpath
JAVA=java
JAVAC=javac
JAR=jar
JARFLAGS= cvmf $(MANIFEST)
JAVAFLAGS=-classpath $(SOURCEDIR):$(LIBSPATH)
JAVACFLAGS=-classpath $(SOURCEDIR):$(LIBSPATH)  -encoding UTF-8
JARTARGET=$(TOPDIR)lib/tecser.jar
BINDISTDIR=$(DISTDIR)-bin
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
	cd $(SOURCEDIR) && $(JAR) $(JARFLAGS) ../$(JARTARGET) $(JARREDS)

docs: $(TARGETS)
	javadoc -d $(DOCS) -sourcepath $(SOURCEDIR) -classpath $(SOURCEDIR):$(LIBSPATH) -subpackages modnlp.tec.server

## distribution building, the joy of life...
dist: docs
	rm -rf $(DISTDIR)
	mkdir $(DISTDIR)
	cp -aL --parents $(DISTFILES) $(DISTDIR)
	tar cfvz /tmp/$(DISTDIR).tar.gz  --exclude-from=exclude.txt $(DISTDIR)
	rm -rf $(DISTDIR)

bindist: jar
	mkdir $(BINDISTDIR)
	cp README.bin $(BINDISTDIR)/README
	cp $(LIBS) $(JARTARGET) bin/server.sh $(BINDISTDIR) 
	cp $(SOURCEDIR)server.properties $(BINDISTDIR)
	tar cfvz /tmp/$(BINDISTDIR).tar.gz  --exclude-from=exclude.txt $(BINDISTDIR)
	rm -rf $(BINDISTDIR)

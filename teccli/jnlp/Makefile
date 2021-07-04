all: jnlp
.PHONY: lib clean sign
TIMESAUTH=http://tsa.starfieldtech.com
#http://timestamp.verisign.com/scripts/timestamp.dll
#TIMESAUTH=http://timestamp.comodoca.com
JARSIGNER=jarsigner
##  COMODO cert
#JARSIGNER_OPT=-tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass`
##JARSIGNER_OPT=-storepass `cat luzs.cer.pass` 
## Our own cert (CA needs to be imported into javaws)
JARSIGNER_OPT=-tsa $(TIMESAUTH) -keystore ../security/gok-keystore -storepass `cat ../gok.cert.pass`
CERT_ALIAS='genealogies'
#CERT_ALIAS='gok'
#CERT_ALIAS='luzs'

jnlp:
#	cp ../plugins/WordleConc/lib/wordleconc.jar .
	cp ../lib/teccli.jar .
	jar ufv teccli.jar teclipluginlist.txt 
clean: 
	rm -f *~ *.jar

sign:   jnlp
	$(JARSIGNER) $(JARSIGNER_OPT)  ../lib/idx.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT)  teccli.jar $(CERT_ALIAS)
##	jarsigner -keystore keystore.jks -storepass `cat luzs.cer.pass` ../lib/idx.jar server
##	jarsigner -keystore keystore.jks -storepass `cat luzs.cer.pass` teccli.jar server
#	jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/idx.jar gok 
#	jarsigner -keystore ../.keystore -storepass `cat ../tecplugin.cer.pass` ../lib/idx.jar tecplugin 
#	jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` teccli.jar gok 
#	jarsigner -keystore ../.keystore -storepass `cat ../tecplugin.cer.pass` teccli.jar tecplugin 
#	jarsigner -storepass `cat luzs.cer.pass` ../lib/idx.jar luzs
#	jarsigner -storepass `cat luzs.cer.pass` teccli.jar luzs
#       now signed by Makefiles in the ../plugin/WordleConc etc directories
#	jarsigner -storepass `cat luzs.cer.pass` wordleconc.jar luzs

sign3rdparty:
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/je.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/exist.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/exist-modules.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/xmldb.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/antlr-2.7.6.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/commons-pool-1.2.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/jgroups-all.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/log4j-1.2.14.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/resolver.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/sunxacml.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/prefuse.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/xmlrpc-1.2-patched.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/lucene-core-3.6.0.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/lucene-analyzers-3.6.0.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/lucene-kuromoji-3.6.0.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/stanford-segmenter-3.8.0.jar $(CERT_ALIAS)
	$(JARSIGNER) $(JARSIGNER_OPT) ../lib/xercesImpl-2.11.0.jar $(CERT_ALIAS)


	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/je.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/exist.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/exist-modules.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/xmldb.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/antlr-2.7.6.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/commons-pool-1.2.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/jgroups-all.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/log4j-1.2.14.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/resolver.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/sunxacml.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/prefuse.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/xmlrpc-1.2-patched.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/lucene-core-3.6.0.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/lucene-analyzers-3.6.0.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/lucene-kuromoji-3.6.0.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/stanford-segmenter-3.8.0.jar gok
	# jarsigner  -tsa $(TIMESAUTH) -storetype pkcs12 -keystore ../gok-cert.pkcs.p12 -storepass `cat ../gok.cert.pass` ../lib/xercesImpl-2.11.0.jar gok

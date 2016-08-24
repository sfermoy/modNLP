all: jnlp
.PHONY: lib clean sign

jnlp:
#	cp ../plugins/WordleConc/lib/wordleconc.jar .
	cp ../lib/teccli.jar .
	jar ufv teccli.jar teclipluginlist.txt 
clean: 
	rm -f *~ *.jar

sign:   jnlp
#	jarsigner -keystore keystore.jks -storepass `cat luzs.cer.pass` ../lib/idx.jar server
#	jarsigner -keystore keystore.jks -storepass `cat luzs.cer.pass` teccli.jar server
	jarsigner -keystore ../.keystore -storepass `cat ../tecplugin.cer.pass` ../lib/idx.jar tecplugin 
	jarsigner -keystore ../.keystore -storepass `cat ../tecplugin.cer.pass` teccli.jar tecplugin 
#	jarsigner -storepass `cat luzs.cer.pass` ../lib/idx.jar luzs
#	jarsigner -storepass `cat luzs.cer.pass` teccli.jar luzs
#       now signed by Makefiles in the ../plugin/WordleConc etc directories
#	jarsigner -storepass `cat luzs.cer.pass` wordleconc.jar luzs


SRCPATH=../tc/src#  :../idx/src
SUBPACKS=modnlp.tc
WWW=amaral@shell.berlios.de:/home/groups/modnlp/htdocs/
all:
	@echo "***************************************************"
	@echo "**                                              ***"  
	@echo "** TYPE 'make sync' TO UPDATE modnlp.berlios.de ***"
	@echo "**                                              ***"  
	@echo "***************************************************"

sync:
	rsync -e ssh -CLavuzb --delete ./*  $(WWW) --exclude usage/*
	find ./ -name '*~' -type f  -print0 |xargs -0 -e rm -f

apidocs:
	javadoc -d doc -sourcepath $(SRCPATH) -subpackages $(SUBPACKS)

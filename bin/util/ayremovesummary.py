# -*- coding: utf-8 -*-
#from json2xml import json2xml
import sys
import os
import re
from shutil import copyfile

def main(fname, tag):
    print("Deleting {} from  {}\n".format(tag, fname))
    copyfile(fname, fname+'-bkp')
    cleantag_re = re.compile('(<{}.*?>).*?(</{}>)'.format(tag,tag), re.DOTALL)
    with open(fname, encoding='utf-8') as f:
        content = f.read()
    content = re.sub(cleantag_re, '', content)
    with open(fname,'w', encoding='utf-8') as f:
        f.write(content)


if __name__ == "__main__":
   main(sys.argv[1],sys.argv[2])





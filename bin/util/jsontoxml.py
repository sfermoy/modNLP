# -*- coding: utf-8 -*-
#from json2xml import json2xml
from xml.dom.minidom import parseString
#import xml.etree.ElementTree as ET
from json2xml.utils import readfromstring, readfromjson
import sys
import os
import re
from dicttoxml import dicttoxml

pgdir = os.path.dirname(os.path.realpath(sys.argv[0]))
with open(pgdir+'/retained.txt') as f:
    domains = f.read().splitlines()

def main(jsonlfname, prefix, start, end):
    print("Processing files from file {}-{:07}.xml to {}-{:07}.xml\n".format(prefix,start,prefix,end))
    jsonl = open(jsonlfname, encoding='utf-8')
    i = 0
    domain_re = r'.*?"source": {.*?"domain": "(.+?)"'
    body_re = r'"body":'
    cleanup_re = r'"categories": \[.+?\],|"links": \{.+?\}, |"links": null, |"score": null, |"media": \[.*?\], |"rankings": \{.*?\]\}, |"entities": \{.+?\]\}, (?="hashtags":)|"social_shares_count": \{.+?\]\}, |"summary": \{.+?\]\}, '
    cleanhead_re = re.compile('(<text_body.*?>).*?(</text_body>)', re.DOTALL)
    cleanhead_new = r'\1\2'
    for line in jsonl:
        i += 1
        if (i < start):
            continue
        if (i > end and end != 0):
            print('Stopped')
            return
        match = re.match(domain_re, line)
        if (match.group(1) not in domains):
            continue
        line = re.sub(body_re, '"text_body":', line, 1)
        line = re.sub(cleanup_re, '', line)
        #print(line)
        data = readfromstring(line)
        print("writing file {}-{:07}.xml".format(prefix,i))
        xml = open("{}-{:07}.xml".format(prefix,i),'w', encoding='utf-8')
        #xml.write(json2xml.Json2xml(data, wrapper="all", pretty=True).to_xml())
        xmlstr = '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n<!DOCTYPE ayliencov19 SYSTEM "ayliencov19.dtd"><ayliencov19>' + dicttoxml(data, attr_type=False, root=False, ids=True).decode("utf-8") + '</ayliencov19>\n'
        #xmltree = ET.fromstring(xmlstr)
        #item = xmltree.find('body')
        #item.tag = "text_body"
        #print(xmlstr.encode("utf-8"))
        xmlobj = parseString(xmlstr)
        xmlpretty = xmlobj.toprettyxml()
        xml.write(xmlpretty)
        xml.close()
        xmlpretty = re.sub(cleanhead_re, cleanhead_new, xmlpretty)
        print("writing file {}-{:07}.hed\n".format(prefix,i))
        xml = open("{}-{:07}.hed".format(prefix,i),'w', encoding='utf-8')
        xml.write(xmlpretty)
        xml.close()
        xmlobj.unlink()
    jsonl.close()


if __name__ == "__main__":
   main(sys.argv[1],sys.argv[2],int(sys.argv[3]), int(sys.argv[4]))





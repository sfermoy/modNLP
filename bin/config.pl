
## set these variables to point to your corpus files
$INDEX_DIR = '/disk2/TECv2/index';
$TEXT_DIR = '/disk2/TECv2/text';
$HEADERS_DIR = '/disk2/TECv2/headers';
$HEADERS_URL = 'http://ronaldo.cs.tcd.ie/tec2/headers/';
$IDX_BIN='/disk2/TECv2/software/modnlp-idx/';

$TEC_BIN="/disk2/TECv2/software/modnlp-tecser";
$TEC_SERVER="/usr/bin/java";
$ARGS="-Xms400m -Xmx450m -jar tecser.jar";
$SERVER_PID="/var/run/tecser/tec.pid";
$SYSLOGFILE="/var/log/tec/tecsys.log";
$ERRLOG="/var/log/tec/tecsys-error.log";

$DOS2UNIX='dos2unix';
# uncomment the following if you don't have d2u installed
#$DOS2UNIX='';
$XMLLINT='xmllint -noout -valid';
# uncomment the following if you don't have xmllint installed
#$XMLLINT='';
# comment out the following if you don't have file installed
$FILETYPE='file -bi';
$VISUALTOHEAD='visualstoheaders.pl';

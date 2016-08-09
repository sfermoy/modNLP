
## set these variables to point to your corpus files
$INDEX_DIR = '/disk2/gok/index';
$TEXT_DIR = '/disk2/gok/text';
$HEADERS_DIR = '/disk2/gok/headers';
$LOG_DIR='logs';
$HEADERS_URL = 'http://www.genealogiesofknowledge.net/gok/headers/';
$IDX_BIN='/disk2/gok/software/modnlp-idx/';

$DOS2UNIX='dos2unix';
# uncomment the following if you don't have d2u installed
#$DOS2UNIX='';
$XMLLINT='xmllint -noout -valid';
# uncomment the following if you don't have xmllint installed
#$XMLLINT='';
# comment out the following if you don't have file installed
$FILETYPE='file -bi';

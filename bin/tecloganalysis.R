## generate log with:

## perl parseaccesslog.pl -f /disk2/TEC/tec-server/data/log/tecserv.log >/tmp/teclog-old.csv

## and

## perl parseaccesslog.pl -f /disk2/TECv2/log/tecserv.log  >/tmp/teclog.csv


## then:


loadLogs <- function(){
  olog <- read.csv('/tmp/teclog-old.csv')
  nlog <- read.csv('/tmp/teclog.csv')
  
  rbind(olog,nlog)
}

## Example: plot access since Oct 2000:
##
## plotAccessByCountry(l[278:length(l$ip),],pdf='/tmp/accessbycountry.pdf')
plotAccessByCountry <- function(log, pdf='', ...){
  op <- par(no.readonly = TRUE); on.exit(par(op))
  par(mar=c(op$mar[1],op$mar[2]+3,op$mar[3],op$mar[4]))

  table <- sort(table(log$country))
  
  barplot(table, horiz=T, las=1, cex.names=.6)
  text(max(table)*.55,length(table)/3,
       sprintf('Accesses to TEC since %s %s\n\nTotal: %s\nfrom %s different locations (machines)\nin %s different countries',
               log[1,]$month, log[1,]$year,
               format(length(log$year), big.mark=",", scientific=FALSE),
               format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
               format(length(unique(log$country)),big.mark=",", scientific=FALSE)), pos=4)
               
  if (pdf !='')
    dev.copy2pdf(file=pdf, paper='special', family='Helvetica')
}

plotAccessByYear <- function(log, pdf='', ...){
  op <- par(no.readonly = TRUE); on.exit(par(op))
  #par(mar=c(op$mar[1],op$mar[2]+3,op$mar[3],op$mar[4]))

  table <- table(log$country,log$year)
  
  bp <- barplot(table, horiz=F, las=1, cex.names=1)
  text(0.5,max(sapply(1:length(table[1,]),function(X)sum(table[,X])))*.8,
       sprintf('Accesses to TEC since %s %s\n\nTotal: %s from %s different locations\nin %s different countries (indicated by different\nshades of gray).',
               log[1,]$month, log[1,]$year,
               format(length(log$year), big.mark=",", scientific=FALSE),
               format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
               format(length(unique(log$country)),big.mark=",", scientific=FALSE)), pos=4)
               
  if (pdf !='')
    dev.copy2pdf(file=pdf, paper='special', family='Helvetica')
}

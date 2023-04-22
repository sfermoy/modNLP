## generate log with:

## perl parseaccesslog.pl -f /disk2/TEC/tec-server/data/log/tecserv.log >/tmp/teclog-old.csv

## and

## perl parseaccesslog.pl -f /disk2/TECv2/log/tecserv.log  >/tmp/teclog.csv


## then:


loadLogs <- function(){
#  olog <- read.csv('/tmp/teclog-old.csv')
  nlog <- read.csv('/tmp/teclog.csv')
  
#  rbind(olog,nlog)
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
       sprintf('Accesses to GoK since %s %s\n\nTotal: %s\nfrom %s different locations (machines)\nin %s different countries',
               log[1,]$month, log[1,]$year,
               format(length(log$year), big.mark=",", scientific=FALSE),
               format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
               format(length(unique(log$country)),big.mark=",", scientific=FALSE)), pos=4)
               
  if (pdf !='')
      dev.copy2pdf(file=pdf, paper='special', family='Helvetica')
  table
}

## Cleveland style dot chart
dotchartAccessByCountry  <- function(log, pdf='', ...){
  op <- par(no.readonly = TRUE); on.exit(par(op))
  par(mar=c(op$mar[1],op$mar[2]+5,op$mar[3]+1,op$mar[4]))

  table <- sort(table(log$country))
  
  dotchart(log(table), xaxt='n', cex=0.6)
  axis(1, labels=table, at=log(table), cex.axis=.6)
  axis(3, cex.axis=0.6)
  mtext("Total number of data requests", 1, line=3)
  mtext("Log10 number of data requests", 3, line=3)
  text(max(log(table))*.55,length(log(table))/3,
       sprintf('Usage of TEC/GoK/OMC data\n\nTotal number of requests: %s\nfrom %s different locations (machines)\nin %s different countries',
               #log[1,]$month, log[1,]$year,
               format(length(log$year), big.mark=",", scientific=FALSE),
               format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
               format(length(unique(log$country)),big.mark=",", scientific=FALSE)), pos=4)
               
  if (pdf !='')
      dev.copy2pdf(file=pdf, paper='special', family='Helvetica')
  table
}


plotAccessByYear <- function(log, pdf='', manual.legend=F, ...){
  op <- par(no.readonly = TRUE); on.exit(par(op))
  #par(mar=c(op$mar[1],op$mar[2]+3,op$mar[3],op$mar[4]))

  table <- table(log$country,log$year)
  
  bp <- barplot(table, horiz=F, las=1, cex.names=1)
  if (manual.legend){
      l <- locator()
      x  <- l$x[1]
      y  <- l$y[1]
  }
  else {
      x  <- 0.5
      y <- max(sapply(1:length(table[1,]),function(X)sum(table[,X])))*.8
  }
  text(x, y,
       sprintf('Accesses to GoK since %s %s\n\nTotal: %s from %s different locations\nin %s different countries (indicated by different\nshades of gray).',
               log[1,]$month, log[1,]$year,
               format(length(log$year), big.mark=",", scientific=FALSE),
               format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
               format(length(unique(log$country)),big.mark=",", scientific=FALSE)), pos=4)
               
  if (pdf !='')
    dev.copy2pdf(file=pdf, paper='special', family='Helvetica')
  table
}

ggplotAccessByCountry <- function(tec=read.csv('/tmp/teclog-tec.csv'),
                      gok=read.csv('/tmp/teclog-gok.csv'),
                      omc=read.csv('/tmp/teclog-omc.csv'))
{
    library('tidyverse')
    log <- rbind(cbind(tec, corpus='TEC'),
                 cbind(gok, corpus='GoK'),
                 cbind(omc, corpus='OMC'))
    log$corpus <- as.factor(log$corpus)
    log$country <- as.factor(log$country)
    td <- as.data.frame(table(log$country,log$corpus))
    tt <- td %>%
        pivot_wider(names_from=Var2, values_from=Freq) %>%
        mutate(Total=GoK+OMC+TEC) %>%
        arrange(Total) %>%
        rename(Country=Var1) %>%
        mutate(Country=factor(Country, levels=Country))

    text <- sprintf('Queries to corpora since %s %s\nTotal: %s from %s different locations\nin %s different countries.',
                    log[1,]$month, log[1,]$year,
                    format(length(log$year), big.mark=",", scientific=FALSE),
                    format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
                    format(length(unique(log$country)),big.mark=",",
                           scientific=FALSE))
    tt %>%
        pivot_longer(cols=c(GoK,OMC,TEC,Total)) %>%
        rename(Accesses=value, Corpus=name) %>%
        ggplot(aes(x=log10(Accesses), y=Country, group=Corpus, colour=Corpus))+
        geom_point() +
        xlab('Number of accesses') +
        
        scale_x_log10(labels=function(X)prettyNum(round(10^X),
                                                  big.mark = ',', scientific=F)) +
        theme_light() +
        theme(legend.justification=c(.95,.15), legend.position=c(.95,.15),
              axis.text=element_text(size=7)) +
        annotate("text", x = 2.8, y = 5, size=3.3, label = text)

    ## Wide format alternative (how do we set a legend?)
    ## ggplot(tt) +
    ##     geom_point( aes(x=Country, y=log10(total)), color='black', size=1 ) +
    ##     geom_point( aes(x=Country, y=log10(tec)), color='green', size=1 ) +
    ##     geom_point( aes(x=Country, y=log10(gok)), color='red', size=1 ) +
    ##     geom_point( aes(x=Country, y=log10(omc)), color='blue', size=1 ) +
    ##     ylab('Number of accesses') +
    ##     coord_flip() +
    ##     scale_y_log10(labels=function(X)prettyNum(round(10^X), big.mark = ',', scientific=F)) +
    ##     ##        geom_jitter() +
    ##     theme_light()
}

ggplotAccessByCountrySingleCorpus <- function(log=read.csv('/tmp/teclog-omc.csv'),
                                              corpus='OMC')
{
    library('tidyverse')
    log$country <- as.factor(log$country)
    td <- as.data.frame(table(log$country))
    text <- sprintf('Queries to %s from %s %s to %s %s %s\nTotal: %s from %s different locations\nin %s different countries.',
                    corpus, log[1,]$month, log[1,]$year, log[nrow(log),]$day, log[nrow(log),]$month, log[nrow(log),]$year,
                    format(length(log$year), big.mark=",", scientific=FALSE),
                    format(length(unique(log$ip)),big.mark=",", scientific=FALSE),
                    format(length(unique(log$country)),big.mark=",",
                           scientific=FALSE))
    td %>%
        arrange(Freq) %>%
        mutate(Country=factor(Var1, levels=Var1)) %>%
        ggplot(aes(x=log10(Freq), y=Country)) +
        geom_point(size=2) +
        xlab('Number of accesses') +        
        scale_x_log10(labels=function(X)prettyNum(round(10^X),
                                                  big.mark = ',', scientific=F)) +
        theme_light() +
        annotate("text", x = 2.8, y = 5, size=3.3, label = text)
}

# A Mini corpus, and a short tutorial on how to index your own local corpora

Here you will find a 'mini corpus' consisting of 3 Shakespeare plays
downloaded from the Gutenberg project. This mini corpus is meant to
illustrate how the indexer (modnlp/idx) works. 

A complete copy of all files needed for this tutorial can be
downloaded from the modnlp SF repository in compressed format: 
[modnlp-idx-sampledata.tar.gz](https://sourceforge.net/projects/modnlp/files/modnlp-idx-sampledata.tar.gz/download)
The structure of the uncompressed directory is the following:

```console
.
├── README.md
├── config
│   ├── idxmgr.properties
│   └── idxmgr.properties-withheaders
├── hed
│   ├── min001.hed
│   ├── min002.hed
│   ├── min003.hed
│   └── minhead.dtd
├── templates
│   ├── min-template.hed
│   ├── min-template.xml
│   ├── min_begin.txt
│   └── min_end.txt
├── txt
│   ├── min001.txt
│   ├── min002.txt
│   └── min003.txt
└── xml
    ├── min001.xml
    ├── min002.xml
    ├── min003.xml
    └── mintext.dtd

```

The `config` directory contains modnlp/idx properties (configuration)
files to be used with this corpus, as explained in the brief tutorial
below. 

`txt` contains the original Gutenberg text files. 

`xml` contains the text files converted into XML format which conform
to the minimal DTD `mintext.dtd`. The files were created by pasting
`templates/min_begin.txt` to the top of each text file, and `templates/min_end.txt`
to the end (and adding some `<omit ...>` tags here and there, using a
text editor such as [jedit](http://www.jedit.org/). 

`hed` contains some minimalistic sample header files. These files are
optional. and will be used to demonstrate how to index a corpus
containing headers. 

## Brief tutorial: creating a mini-corpus with modnlp/idx

**NB**: For a more comprehensive tutorial on how to manage corpora using
the modnlp suite, see [Sally Marshall's tutorial](http://modnlp.sourceforge.net/doc/tutorials/SallyMarshall-CorpusBuildingwithTECToolsDec2011.pdf).

The modnlp corpus management suite, which consists of `modnlp-idx`
(the indexer), `modnlp-teccli` (the corpus/concordance browser) and
`modnlp-tecser` (the corpus server), was originally developed to
allow free access to linguistic material over the Internet.

The indexer, `modnlp-idx`, allows you to create an index, which you can
later access through the browser (`modnlp-teccli`). The corpus
will usually contain data (e.g. text-oriented XML files, which we
refer to as "text files") and metadata stored as separate
XML files, which we refer to as "header files". 

It is not essential that data and meta-data be stored in separate
files or even encoded in XML. However, if you wish to be able to
select sub-corpora, for instance, in order to make the concordancer
display only concordances coming from texts that share certain
features (e.g. all texts written by a given author), you need to
create meta-data files describing the features of interest and link
them back to the appropriate sections of the text files.

### Step by step guide to corpus indexing:

1. downloaded the latest versions of modnlp-teccli and modnlp-idx from
   the [modnlp sourceforge
   repository](https://sourceforge.net/projects/modnlp/files/). Those
   files are named something like `modnlp-teccli-0.9.0-bin-gok.tar.gz`
   and `modnlp-idx-0.8.7-bin-gok.tar.gz`. Choose the ones with the
   highest numbers (e.g. 0.8.7 is more recent than 0.8.6). In what
   follows we will assume we are using the numbers above. If a newer
   version is available, please replace the version numbers above by
   the version numbers of the latest files you downloaded.

2. Uncompress/extract the content of the downloaded files. This should
   generate directories (folders) named somethink like

`modnlp-idx-0.8.7-bin-gok`

and 

`modnlp-teccli-0.9.0-bin-gok`

  In `modnlp-idx-0.8.7-bin-gok`, there is a file named
  `idxmgr.properties`. This file control the basic settings of the
  indexer. In it, the lines starting with hash symbols (`#`) are
  comments, which document the options set in the file. 
  
  For instance, the following lines, from `idxmgr.properties`:

```properties
#modnlp.idx.IndexManager's properties (tailored to minimal corpus )
#Mon Mar 16 17:29:14 IST 2022

## should punctuation count as tokens?
index.punctuation=false
## should we index numerals?
index.numerals=true
## index metadata for subcorpus selection (header files)?
index.headers=false
```

  specify that the indexer must not index puctuation symbols
  (```index.punctuation=false```), must index numerals, and must not
  index header files.

3. Copy the file [idxmgr.properties](config/idxmgr.properties) found
   in the folder `config` into your newly decompressed modnlp-idx
   folder (`modnlp-teccli-0.9.0-bin-gok`).

#### Indexing a plain text corpus 

4. We will start by indexing the plain text files downloaded from
   Gutenberg:

```console
-- txt
   |-- min001.txt
   |-- min002.txt
   `-- min003.txt
```

This is the simplest way of using modnlp.

4.1. Open the modnlp-idx folder and click on `idx.jar` (it might
     simply appear as `idx` on Windows, next to an icon) or run

```console
java -jar idx.jar
```
        from a console (command prompt) started in the modnlp-idx folder. 

4.2. A window will appear asking you to specify a location for your
     index (i.e. where you would like the indexer to store the index
     used in concordance searches etc). Create a new folder by cliking
     on the create folder icon on your file selector window.  Call it,
     for instance `myindex`. Choose this folder for the index.

4.3. The main window of modnlp-idx should now appear. Click on 'Index
     new files' and choose the text files in the `txt` folder
     (min001.txt, etc).

4.4. Once indexing completes, quit the indexer. 

#### Indexing an XML corpus without headers.

5. If you do not wish to use headers with an XML-encoded corpus, such
   as the one in the `xml` folder (min001.xml, etc), you can index it
   using the same `idxmgr.properties` configuration file we used
   above (step 4.)

#### Indexing an XML corpus *with* headers.

6. We will now index a typical (minimalistic) corpus with
   headers. These files are in 
   
```console
 -- hed
|   |-- min001.hed
|   |-- min002.hed
|   |-- min003.hed
|   `-- minhead.dtd
`-- xml
    |-- min001.xml
    |-- min002.xml
    |-- min003.xml
    `-- mintext.dtd
```

6.1. In the modnlp-idx folder, open `idxmgr.properties` using a text editor (such as jedit). and
   copy the content of
   [config/idxmgr.properties-withheaders](idxmgr.properties-withheaders)
   into it (replacing the existing content). Save the `idxmgr.properties` file.
   
   The index configuration file, `idxmgr.properties`, allows you to
   specify sub-corpus selection options based on the metadata you
   specified in your header file. Take for example `min001.hed`:
   
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE text SYSTEM "minhead.dtd">
<text filename="min001">
  <title>Romeo and Juliet</title>
  <author> William Shakespeare</author>
  <section id='s1'/>
</text>
```

    which is the header file for `min001.xml`:
   
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE text SYSTEM "mintext.dtd">
<text filename="min001">
  <section id='s1'>
  <omit reason='frontmatter'>
    The Project Gutenberg EBook of Romeo and Juliet, by William Shakespeare
  </omit>
  [...]
  THE TRAGEDY OF ROMEO AND JULIET
       by William Shakespeare

    Dramatis Personae
    Chorus.
    Escalus, Prince of Verona.
    Paris, a young Count, kinsman to the Prince.
    Montague, heads of two houses at variance with each other.
    Capulet, heads of two houses at variance with each other.
    An old Man, of the Capulet family.
    [...]
```
   
   The header's DTD (take a look at the dtds in `hed/minhead.dtd` and
   `xml/mintext.dtd`) allows you to specify a few *elements*
   (e.g. `<title>`) and *attributes* (e.g. `filename="min001"`) all of
   which can be used to select subcorpora.
   
   To specify which elements and/or attributes the user interface
   (`modnlp/teccli`) should use, all you need to do is add the
   following lines to `idxmgr.properties`:
   
```sh
subcorpusindexer.element=(section)
subcorpusindexer.attribute=id
xquery.root.element.path=/text
xquery.return.attribute.path=/@id
xquery.attribute.chooser.specs=File name;/@filename;Author;/author;Title;/title
```

The first line defines which element should be indexed. That is, text within elements
specified here will be indexed and can form subcorpora. 
   
The second line which attribute from the element above will we use to
uniquely identify the text segments. In this case, the attribute `id`
from element `<section>` has been chosen. Note that the text between
`<section>... </section>` tags will be indexed.
   
The third line specifies the path on the XML tree to the *root
element* (i.e. the element in relation to which the selectors
specified by `xquery.attribute.chooser.specs` are defined). This is
specified in [XPath
syntax](https://www.w3schools.com/xml/xpath_syntax.asp). (NB: make
sure this element path does not end with a `/`).
   
The forth line specifies the element to be returned by xquery to
select which indexed texts should be included in the inverted-index
query. This will typically be the same as
`subcorpusindexer.attribute`, but in XPath syntax relative to the
*root element" (in this case `/@id`, which translates into `/text/@id`,
as `/text` is the root element. 

Finally, the fifth line specifies the selectable attributes, in
this case `File name;/@filename;Author;/author;Title;/title`. This
specifies the layout of the subcorpus selection tool. The general
format for each selection box is 
`description;path-in-header-xml;description2;path-in-header-xml2;...`
(note the semicolon separating the (human readable) description of
the selection (e.g. `File name`) and the path in the header
document (e.g. `/@filename` which translates to `/text/@filename`
and will select all filename values in `<text filename="min...">`)
Paths are all relative to xquery.root.element.path (see above).

6.2. Open the modnlp-idx folder and click on `idx.jar` (it might
  simply appear as `idx` on Windows, next to an icon) or run

```console
java -jar idx.jar
```

from a console (command prompt) started in the modnlp-idx folder. 

6.3. A window will appear asking you to specify a location for your
  index (i.e. where you would like the indexer to store the index
  used in concordance searches etc). Create a new folder by cliking
  on the create folder icon on your file selector window.  Call it,
  for instance `myindex-withheaders`. Choose this folder for the index.

6.4. The program will now ask you to choose the folder where the
  headers are stored. Choose the `hed` folder. Finally, the program
  will ask for a URL for public access to the header files. This is
  in case you want to make corpus searches available to other
  users. For this tutorial, simply press OK to accept the default
  URL. The main window should now appear.
  
6.5. The main window of modnlp-idx should now appear. Click on 'Index
  new files' and choose the text files in the `xml` folder
  (min001.xml, etc).

6.6. Once indexing finishes, quit the indexer. 


### Using the modnlp-teccli browser on your newly indexed corpora.

7. Open your modnlp-teccli folder and click on `teccli.jar`
or run

```console
java -jar teccli.jar
```
from a console (command prompt) started in the modnlp-teccli folder. 

8. Select 'Choose new local corpus'. A window will appear. Use it to
   select one of your previously created indices
   (`myindex-withheaders` or `myindex`, for instance).

Now you may use the modnlp-teccli tool to browse your newly indexed
concordances.

### Sharing your corpus

If you wish to make corpus data and concordances (though not
necessarily your full texts) available to the community over the
Internet your should download the modnlp-tecser module. 

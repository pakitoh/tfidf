# Tf/idf


## Introduction
Tf/idf (term frequency / inverse document frequency) is an statistic that reflects the importance of a term T in a 
document D (or the relevance of a document for a searched term) relative to a document set S (see 
[wikipedia](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)).
Tf/idf can be extended to a set of terms TT adding the tf/idf for each term.


## The problem
Assume that we have a directory `D` 
containing a document set `S`, with one file per document.
We are given a set of terms `TT`, 
and asked to compute the tf/idf of TT for each document in D, 
and report the `N` top documents sorted by relevance.


### Constraints
Documents will be added to that directory by external agents, but they will never be removed or overwritten.
The program must run as a daemon/service that is watching for new documents,
and dynamically updates the computed tf/idf for each document and the inferred ranking.


### Expected usage
For example:
./tdIdf -d dir -n 5 -p 300 -t "password try again" ...

Result examples:
- doc1.txt 0.78
- doc73.txt 0.76
- [...]


### Input params
The program will run with the parameters:
- The directory `D` where the documents will be written.
- The terms `TT` to be analyzed.
- The count `N` of top results to show.
- The period `P` to report the top N.


## Assumptions
- input documents are plain text files.
- input documents should not be TOO BIG.
- outcome would be written to standard output as plain text.
- the data type used for numeric calculations is a trade-off between accuracy and performance. We will use java Double.  
- required performance?


## The solution
We need to solve these problems in order to be able to provide a complete solution for the problem: 


### Find N top documents sorted by relevance
This is usually solved using a heap data structure.
Using a heap we will spend O(1) time to get the top of the heap and O(log n) to add a new element.
In java there is no Heap in standard library but PriorityQueue would work in the same way.
Inserting the list of scores with size O(numDocs) in the heap would take numDocs * O(log n) time.
And O(numDocs) in memory.
And getting the k-tops is constant time, k * O(1).

#### Extract words from a text document
This implementation is naive and is the computation that takes more time of the whole process according to my measurements.
I suspect the regular expression is what makes it slow. I guess there are better ways to achieve it.
Not sure about the time or space consumed by the Java regex engine.

### Compute tf-idf 
It's composed of several subproblems as well:

#### Calculate the frequency of each word from a collection of words
The algorithm would simply iterate over the collection and increase a counter for each word.
It would take linear time regarding the number of words.
We are going to use a HashMap(word -> freq) to store the data 
in order to be able to do fast lookups in the future. 
It will grow in space lineally as well 
but regarding the number of different words this time.  
Worst case would be each document containing different words then we will take also O(n) of memory.

#### Compute tf-idf for one document
Using the HashMap storing word frequencies we will be able to compute idf and tf.

Idf calculation would need to iterate once the whole HashMap.
So time complexity would be O(n) being n all words in all documents.
It would need linear space as well as we need to store a string (the word) 
and a number (the frequency) for each word in each document.

Tf calculation would need to calculate tf for each term 
and to do so it will have to traverse the complete HashMap again.  
If we are looking for m terms, we must repeat this process m times and that would make it O(nxm) in time complexity.
We are going to use another Map to store temp results, the size will grow linearly with the number of documents.


#### Adding all pieces
Being:
- n the number of words in all documents.
- m is the number of terms.
- numDocs < n

Steps are:
1) Split the words
- time: ?
- space: ?

3) calculate the frequency of each word for all documents.
- time: O(n)
- space: O(n)

4) then compute tfIdf for each term and doc
- time: O(nxm)
- space: O(numDocs)

5) and finally get the k better solutions
- time: numDocs * O(log n)
- space: O(numDocs)

Adding all up we have: 
- time: O(n) + O(nxm) + (numDocs * O(log n)) = O(nxm)
- space: O(n) + O(numDocs) + O(numDocs) = O(n)

but extracting words from a doc is unknown for me right now, and it seems the dominant term in my manual tests. 



### Schedule a computation in a fixed rate
There are several utils in `java.util.concurrent` to help us run our code in a concurrent way.
In our case we need to call our service at a fixed rate and `ScheduledExecutorService` will help us to do it.


### Parse cli input
We'll use:
- picocli to create java cli
- maven-shade-plugin to package the app as an executable uberjar.


### Optimizations
As documents ar going to be only appended to the dir, 
we store calculated word frequencies in a cache to trade performance by memory.


## Usage

### How to build

#### Jar file
The project uses [Apache Maven](https://maven.apache.org/) as a build tool, so a simple
```bash
mvn clean verify
```
in a system with Java and maven installed would compile, run the tests and create the uber jar in `target` folder.


### How to run
We use PicoCli to help us to create a rich command line application. 
It would provide common options like `help`:
```bash
paco@paco-desktop:~/Labs/devo/tf-idf$ java -jar target/tf-idf-0.0.1-SNAPSHOT.jar -h
Usage: tfidf [-hV] [-d=<dir>] [-n=<numResults>] [-p=<period>] [-t=<tt>]
Daemon that watches a dir and computes at a fixed rate TfIdf for all docs in
such dir and a set of terms passed as param and returns the K best
  -d, --dir=<dir>         Directory to watch
  -h, --help              Show this help message and exit.
  -n, --numResults=<numResults>
                          Number of results to return in each execution
  -p, --period=<period>   Rate in seconds to compute
  -t, --terms=<tt>        Set of terms separated by spaces to compute tfidf
  -V, --version           Print version information and exit.
```

and `version number`:
```bash
paco@paco-desktop:~/Labs/devo/tf-idf$ java -jar target/tf-idf-0.0.1-SNAPSHOT.jar -V
0.0.1

```

A more complete example would be to ask the daemon to watch dir `src/test/resources`
and look for `rabbit monster harpoon sisters` 
each 5 secs 
and return the 3 best results 
we need to run this command:
```bash
paco@paco-desktop:~/Labs/devo/tf-idf$ java -jar target/tf-idf-0.0.1-SNAPSHOT.jar -d "src/test/resources" -t "rabbit monster harpoon sisters" -p 5 -n 3
18:41:32.556 INFO  c.labs.devo.tfidf.app.TfIdfService - Computing the 3 better scores of Tf-Idf in src/test/resources/ for the terms: rabbit monster harpoon sisters
TfIdf { docName='Alice's Adventures in Wonderland by Lewis Carroll.txt', terms='rabbit monster harpoon sisters', score='0.0008' }
TfIdf { docName='Moby Dick by Herman Melville.txt', terms='rabbit monster harpoon sisters', score='0.0004' }
TfIdf { docName='Pride and Prejudice by Jane Austen.txt', terms='rabbit monster harpoon sisters', score='0.0003' }
18:41:37.550 INFO  c.labs.devo.tfidf.app.TfIdfService - Computing the 3 better scores of Tf-Idf in src/test/resources/ for the terms: rabbit monster harpoon sisters
TfIdf { docName='Alice's Adventures in Wonderland by Lewis Carroll.txt', terms='rabbit monster harpoon sisters', score='0.0008' }
TfIdf { docName='Moby Dick by Herman Melville.txt', terms='rabbit monster harpoon sisters', score='0.0004' }
TfIdf { docName='Pride and Prejudice by Jane Austen.txt', terms='rabbit monster harpoon sisters', score='0.0003' }
[...]
```

#### Logs
A detailed log file would be created in `logs` folder by default.
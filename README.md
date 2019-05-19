# Election 2016 Sentiment Analysis - Reddit
###### Term Project for CS435 @ CSU, Spring 2018
###### Team Members: Andy Dolan, Drew Boston, Khanh Nguyen Le, Jacob Servaty

### Goals
* Get some measure of discussion on Reddit leading up to the 2016 U.S. Presidential Election
* Find metrics on which of the two final candidates were more discussed online
* This codebase is only for the preprocessing step of this project

### This Software...
...performs the preprocessing of the full set of Reddit comments (approx. 300 GB of JSON data) from the time frame of
2016-07-20 00:00:00 GMT to 2016-11-09 00:00:00 GMT, filtering out all comments that are
either out of that time frame, or are not about either Hillary Clinton or Donald Trump. Then it performs textual sentiment analysis on reddit comments determined by preprocessing to be discussing either Donald Trump, Hillary Clinton, or both, and summarizes the data.
### Tools and APIs Used
* Hadoop + MapReduce
    * MRUnit (unit testing for MapReduce)
* Maven
* json-simple

### Dataset
* Available from [here](http://files.pushshift.io/reddit/comments/)
  * Note that we only use data from July 2016 through November 2016, ~177 GB of comment data

### Usage
#### Building and Running
* Use Maven to build this project; `mvn package` will create the proper jar with necessary dependencies (`json-simple`)
* The input for the file is the raw Reddit comment data, such as [this example](http://files.pushshift.io/reddit/comments/sample_data.json)
* The output will be of the form, where '\t' is a tab character:
```
<comment_id>\t<trump|hillary|both>\t<timestamp>\t<comment_body>
```
* The program can be invoked via Hadoop with the command `$HADOOP_HOME/bin/hadoop jar election16-coverage-1.0.jar columbia.FilterCommentsDriver <input_path> <output_path>`
  * Here, you are invoking `FilterCommentsDriver`
<h3>For Drew's sentiment Analysis:</h3>
$ mkdir build
$ $HADOOP_HOME/bin/hadoop com.sun.tools.javac.Main *.java -d build -Xlint
$ jar -cvf SentimentAnalysis.jar -C build/ .
$ rm -r build
This assumes you have all text files (ExampleInput.txt, negate-words.txt, pos-words.txt, and neg-words.txt) in /sentimentAnalysis directory in hdfs. Modify the paths to reflect any differences.
$HADOOP_HOME/bin/hadoop jar SentimentAnalysis.jar org.SentimentAnalysis.Driver /sentimentAnalysis/ExampleInput.txt /sentimentAnalysis/out -negation /sentimentAnalysis/negate-words.txt -pos /sentimentAnalysis/pos-words.txt -neg /sentimentAnalysis/neg-words.txt
As-is, it will take /sentimentAnalysis/ExampleInput.txt, run the program, and store the results in /sentimentAnalysis/out. This can be modified to a directory of input files by replacing sentimentAnalysis/ExampleInput.txt with /your-HDFS-Directory/
<h3>To summarize data:</h3>
$ mkdir build
$ $HADOOP_HOME/bin/hadoop com.sun.tools.javac.Main *.java -d build -Xlint
$ jar -cvf SentimentAnalysis.jar -C build/ .
$ rm -r build
$ $HADOOP_HOME/bin/hadoop jar Summary.jar org.Summary.Driver /SentimentAnalysis/out /SentimentAnalysis/summary

#### Dictionary Modification
* There are two resource files, `trump-dictionary.txt` and `hillary-dictionary.txt`
* These files contain the words that `RelevantFilterMapper` will look for in
deciding whether or not to keep comments
* More words can easily be added to these files, they are loaded at runtime

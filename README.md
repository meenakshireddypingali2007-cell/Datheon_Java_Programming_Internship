# Text Analysis Tool

A beginner-friendly Java console application that reads a UTF-8 text file and reports word and sentence statistics. It uses only the JDK.

## Features

- Reads a file path entered in the console.
- Normalizes text to lowercase and removes punctuation while keeping Unicode letters, numbers, and word boundaries.
- Reports total and unique word counts, sentence count, and average words per detected sentence.
- Counts words in a `HashMap` and lists up to 10 most frequent words. Ties are sorted alphabetically.
- Handles empty input, invalid paths, missing files, and file-reading errors with clear messages.

## Prerequisites

Install a JDK and make sure `java` and `javac` are available in the VS Code terminal. No external libraries are needed.

## Compile

Open this project folder in VS Code, open **Terminal > New Terminal**, and run:

```powershell
javac -encoding UTF-8 WordAnalyzer.java
```

## Run

```powershell
java WordAnalyzer
```

When prompted, enter a path such as `sample.txt` (relative to the terminal's current directory) or an absolute path.

## Sample output

For the included `sample.txt`, counts are 47 total words, 36 unique words, 4 sentences, and 11.75 average words per sentence. The first rows of the ranking are:

```text
Rank  Word                   Frequency
1     words                  6
2     and                    2
3     are                    2
4     how                    2
5     in                     2
6     repeated               2
7     the                    2
```

The full list may include more words; entries are sorted by frequency descending, then alphabetically.

## Limitations

Sentence counting treats one or more `.`, `!`, or `?` characters as one ending when followed by whitespace or the end of the file. It is intentionally simple: abbreviations such as `Dr.` can be counted as sentence endings, and a sentence without ending punctuation is not counted. Average sentence length uses all extracted words divided by the number of detected sentences, so unpunctuated trailing text still contributes words when at least one sentence ending exists. The tokenizer considers any run of whitespace a word boundary; apostrophes and hyphens split a word into parts.

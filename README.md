# Simple Transaction Analyzer
Hoolah test.

## Installation

Requires Java 8, Gradle.

## Usage

Build it with Gradle command:
```
gradle executableJar
```
Run it with command:
```
java -jar ./build/libs/challenge-all-1.0.jar <options> <filename>
```
Option -h (or --help) displays help
```
java -jar ./build/libs/challenge-all-1.0.jar -h
usage: java -jar challenge-all-1.0.jar
 -af,--analysis-function <function-key>    Analysis function, one of [avg | minmax ]. Default is 'avg'.
 -f,--file <file-name>                     Full or relative path to transaction CSV file to parse.
 -from,--date-from <dd/MM/yyyy hh:mm:ss>   Query start timestamp.
 -h,--help                                 Display the help text.
 -to,--date-to <dd/MM/yyyy hh:mm:ss>       Query end timestamp.
```

the analysis result is printed to stdout.

Note: -f (--file) option is optional, you can supply the file also as direct parameter, thus these 3 invocation methods are identical:

```
java -jar ./build/libs/challenge-all-1.0.jar testFile.csv
java -jar ./build/libs/challenge-all-1.0.jar -f testFile.csv
java -jar ./build/libs/challenge-all-1.0.jar --file testFile.csv
```

Note: -f option has precedence over direct parameter, thus

```
java -jar ./build/libs/challenge-all-1.0.jar -f testFile2.csv  testFile.csv
```

will process the testFile2.csv, not testFile.csv

default for -af is "avg"

to apply transaction average function, use:
```
$ java -jar build/libs/challenge-all-1.0.jar -af avg -from "20/08/2018 12:50:02" -to "20/08/2018 12:50:02" Test2.csv 
Number of Transactions = 5
Average Transaction Value = 25.00
```

to apply the Min-Max Transaction Amount analyzer function, use:
```
$ java -jar build/libs/challenge-all-1.0.jar -af minmax -from "20/08/2018 12:50:02" -to "20/08/2018 12:50:02" Test2.csv 
Minimal Amount = 5.00
Maximal Amount = 45.00
Number of Transactions = 5
```

# Design notes


package app;

import analyzer.AnalyzerFunction;
import analyzer.AveragingFunction;
import analyzer.MinMaxFunction;
import analyzer.TransactionAnalyzer;
import model.TransactionRecord;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

/*
 * Main class of the application. Parses command line, initializes analyzer, starts analysis and prints out the result.
 */
public class SimpleTransactionAnalyzer {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private CommandLine m_commandLine   = null;

    public SimpleTransactionAnalyzer(CommandLine commandLine) {
        this.m_commandLine = commandLine;
    }

    /**
     * Start execution of the analyzer.
     */
    public void run() {
        // get the data set
        String fileName = m_commandLine.getOptionValue("f", null);
        if (fileName == null) {
            if (m_commandLine.getArgList() != null && m_commandLine.getArgList().size() > 0) {
                fileName = m_commandLine.getArgList().get(0);
            }
        }
        File file = fileName != null ? new File(fileName) : null ;
        // continue only if data set exists
        if (file != null && file.exists()) {
            try {
                // initialize analyzer with analysis function and read in the data set from the file
                TransactionAnalyzer analyzer = getAnalyzer(m_commandLine.getOptionValue("af"));
                parseInput(file, analyzer);

                // get & convert the query range (from / to  timestamps)
                String dateFromStr = m_commandLine.getOptionValue("from", null);
                long dateFrom = dateFromStr != null ? DATE_FORMAT.parse(dateFromStr).getTime() : 0L;
                String dateToStr = m_commandLine.getOptionValue("to", null);
                long dateTo = dateFromStr != null ? DATE_FORMAT.parse(dateToStr).getTime() : 0L;

                // Invoke analyzer with the range limits
                Map<String, Object> result = analyzer.analyze(dateFrom, dateTo);

                // convert to human readable form and print result to STDOUT, exit
                System.out.println(analyzer.interpretResult(result));

            } catch (java.text.ParseException pex) {
                System.out.println("Query date format issue, please use date in format: dd/MM/yyyy hh:mm:ss");
            } catch (IOException e) {
                System.out.println("Input data problem, " + e.getMessage());
            }
        } else {
            System.out.println("File " + fileName + "does not exist.");
        }
    }

    /**
     * read in (parse) the CSV file with the data set. Assume the entries are time-ordered (no sort required).
     * @param file
     * @param analyzer
     * @throws IOException
     */
    public static void parseInput(File file, TransactionAnalyzer analyzer) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String csvLine = null;
        while ((csvLine = reader.readLine()) != null) {
            TransactionRecord record = TransactionRecord.of(csvLine);
            if (record != null) {
                analyzer.add(record); // builds indexes and handles transaction reversal
            }
        }
    }

    /**
     * A very simple factory method - create an TransactionAnalyzer initialized with correct AnalysisFunction.
     * @param analysisType
     * @return
     */
    private static final TransactionAnalyzer getAnalyzer(String analysisType) {
        if (analysisType == null) {
            analysisType = "avg";
        }
        AnalyzerFunction function = null;
        switch (analysisType.toLowerCase()) {
            case "minmax": {
                function = new MinMaxFunction();
                break;
            }
            default: {
                function = new AveragingFunction();
            }
        }
        return new TransactionAnalyzer(function);
    }

    /**
     * Application entry point from command line. Parses the parameters and prints the help/usage message
     * if requested by -h option or when input params do not conform expected format.
     * If all OK, it starts the analyzer (run())
     * @param args
     */
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption( Option.builder("f").longOpt( "file" )
                .desc( "Full or relative path to transaction CSV file to parse." )
                .hasArg()
                .argName("file-name")
                .build() );
        options.addOption( "h", "help", false, "Display the help text." );
        options.addOption(Option.builder("from").longOpt( "date-from" )
                .desc( "Query start timestamp." )
                .hasArg()
                .argName("dd/MM/yyyy hh:mm:ss")
                .build() );
        options.addOption(Option.builder("to").longOpt( "date-to" )
                .desc( "Query end timestamp." )
                .hasArg()
                .argName("dd/MM/yyyy hh:mm:ss")
                .build() );
        options.addOption(Option.builder("af").longOpt( "analysis-function" )
                .desc( "Analysis function, one of [avg | minmax ]. Default is 'avg'." )
                .hasArg()
                .argName("function-key")
                .build() );

        // create the command line parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if (line.hasOption('h')) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth(130);
                formatter.printHelp( "java -jar challenge-all-1.0.jar", options );
            } else {
                // all OK with command line, execute the tool
                new SimpleTransactionAnalyzer(line).run();

                System.exit(0); // ensure that all possible thread(s) originated from the tool terminate now too
            }
        }
        catch( ParseException parseException ) {
            // oops, something went wrong with the input
            System.err.println( "Parsing command line failed.  Reason: " + parseException.getMessage() );
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(130);
            formatter.printHelp( "java -jar challenge-all-1.0.jar", options );
        }
    }
}

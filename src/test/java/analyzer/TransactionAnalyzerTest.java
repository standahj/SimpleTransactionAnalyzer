package analyzer;

import app.SimpleTransactionAnalyzer;
import model.TransactionRecord;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static app.SimpleTransactionAnalyzer.DATE_FORMAT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TransactionAnalyzerTest {
    @Test
    public void addWithReverse() {
        TransactionAnalyzer transactionAnalyzer = new TransactionAnalyzer(new AveragingFunction());
        transactionAnalyzer.add(TransactionRecord.of("ABCDEF, 20/08/2018 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,"));
        transactionAnalyzer.add(TransactionRecord.of("AKNBVHMN, 20/08/2018 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, ABCDEF"));
        assertTrue(transactionAnalyzer.getDataSet().get(0).isReversed());
    }

    @Test
    public void addWithWrongReverse() {
        TransactionAnalyzer transactionAnalyzer = new TransactionAnalyzer(new AveragingFunction());
        transactionAnalyzer.add(TransactionRecord.of("ABCDEF, 20/08/2018 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,"));
        transactionAnalyzer.add(TransactionRecord.of("AKNBVHMN, 20/08/2018 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, XYZ1XYZ"));
        assertFalse(transactionAnalyzer.getDataSet().get(0).isReversed());
    }
    @Test
    public void analyze() throws Exception {
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new AveragingFunction());
        SimpleTransactionAnalyzer.parseInput(new File("./Test1.csv"), analyzer);
        long dateFrom = DATE_FORMAT.parse("20/08/2018 12:00:00").getTime();
        long dateTo = DATE_FORMAT.parse("20/08/2018 13:00:00").getTime();
        Map<String,Object> result = analyzer.analyze(dateFrom, dateTo);
        assertThat(result.get(AveragingFunction.KEY_SET[0]), is(2));
        assertThat(result.get(AveragingFunction.KEY_SET[1]), is("32.50"));
    }

    @Test
    public void analyzeWrongInput() throws Exception {
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new AveragingFunction());
        SimpleTransactionAnalyzer.parseInput(new File("./Test1.csv"), analyzer);
        long dateFrom = DATE_FORMAT.parse("20/08/2019 12:00:00").getTime();
        long dateTo = DATE_FORMAT.parse("20/08/2018 13:00:00").getTime();
        Map<String,Object> result = analyzer.analyze(dateFrom, dateTo);
        assertThat(result.get("ERROR"), is("date-from parameter must be before date-to. Actual requested range is from: " +
                "20/08/2019 12:00:00 to: 20/08/2018 13:00:00"));
    }

    @Test
    public void analyzeIdenticalDates() throws Exception {
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new AveragingFunction());
        SimpleTransactionAnalyzer.parseInput(new File("./Test2.csv"), analyzer);
        long dateFrom = DATE_FORMAT.parse("20/08/2018 12:50:02").getTime();
        long dateTo = DATE_FORMAT.parse("20/08/2018 12:50:02").getTime();
        Map<String,Object> result = analyzer.analyze(dateFrom, dateTo);
        assertThat(result.get(AveragingFunction.KEY_SET[0]), is(5));
        assertThat(result.get(AveragingFunction.KEY_SET[1]), is("25.00"));
    }

    @Test
    public void analyzeDefaultRange() throws Exception {
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new AveragingFunction());
        SimpleTransactionAnalyzer.parseInput(new File("./Test2.csv"), analyzer);
        long dateFrom = DATE_FORMAT.parse("20/08/2018 12:48:00").getTime();
        long dateTo = DATE_FORMAT.parse("20/08/2018 12:50:00").getTime();
        Map<String,Object> result = analyzer.analyze(dateFrom, dateTo);
        assertThat(result.get(AveragingFunction.KEY_SET[0]), is(0));
        assertThat(result.get(AveragingFunction.KEY_SET[1]), is("0.00"));
    }

    @Test
    public void minmaxRange() throws Exception {
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new MinMaxFunction());
        SimpleTransactionAnalyzer.parseInput(new File("./Test2.csv"), analyzer);
        long dateFrom = DATE_FORMAT.parse("20/08/2018 12:50:02").getTime();
        long dateTo = DATE_FORMAT.parse("20/08/2018 12:50:02").getTime();
        Map<String,Object> result = analyzer.analyze(dateFrom, dateTo);
        assertThat(result.get(MinMaxFunction.KEY_SET[0]), is("5.00"));
        assertThat(result.get(MinMaxFunction.KEY_SET[1]), is("45.00"));
        assertThat(result.get(MinMaxFunction.KEY_SET[2]), is(5));
    }

    @Test
    public void analyzeNotMatchingRange() throws Exception {
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new AveragingFunction());
        SimpleTransactionAnalyzer.parseInput(new File("./Test2.csv"), analyzer);
        Map<String,Object> result = analyzer.analyze(0, Long.MAX_VALUE);
        assertThat(result.get(AveragingFunction.KEY_SET[0]), is(8));
        assertThat(result.get(AveragingFunction.KEY_SET[1]), is("36.19"));
    }
}

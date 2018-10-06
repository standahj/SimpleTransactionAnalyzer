package app;

import analyzer.AveragingFunction;
import analyzer.TransactionAnalyzer;
import model.TransactionRecord;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SimpleTransactionAnalyzerTest {
    @Test
    public void inputParsing() {
        File input = new File("./Test1.csv");
        assertTrue(input.exists());
        TransactionAnalyzer analyzer = new TransactionAnalyzer(new AveragingFunction());
        try {
            SimpleTransactionAnalyzer.parseInput(input, analyzer);
            assertFalse(analyzer.getDataSet().isEmpty());
            assertFalse(analyzer.getTransacionIndex().isEmpty());
            assertEquals(analyzer.getDataSet().size(), analyzer.getTransacionIndex().size());
            assertEquals(6, analyzer.getDataSet().size());
            TransactionRecord reversed = analyzer.getTransacionIndex().get("YGXKOEIA");
            assertNotNull(reversed);
            assertEquals("YGXKOEIA", reversed.getId());
            assertEquals(TransactionRecord.TransactionType.PAYMENT, reversed.getType());
            assertTrue(reversed.isReversed());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}

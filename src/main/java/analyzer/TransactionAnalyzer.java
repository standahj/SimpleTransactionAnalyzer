package analyzer;

import model.TransactionRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.SimpleTransactionAnalyzer.DATE_FORMAT;

public class TransactionAnalyzer {

    private AnalyzerFunction<TransactionRecord> m_analyzerFunction;
    private Map<String, TransactionRecord>      m_TransactionIdIndex = new HashMap<>();
    private List<TransactionRecord>             m_dataSet = new ArrayList<>();

    public TransactionAnalyzer(AnalyzerFunction<TransactionRecord> analyzerFunction) {
        this.m_analyzerFunction = analyzerFunction;
    }

    public void add(TransactionRecord record) {
        if (record != null) {
            m_dataSet.add(record);
            m_TransactionIdIndex.put(record.getId(), record);
            if (TransactionRecord.TransactionType.REVERSAL.equals(record.getType())) {
                TransactionRecord reversedTransaction = m_TransactionIdIndex.get(record.getRelatedTransaction());
                if (reversedTransaction != null) {
                    reversedTransaction.setReversed(true);
                } else {
                    System.out.println("[WARN] - attempting to reverse non-existent transaction ID: " +
                            record.getRelatedTransaction());
                }
            }
        }
    }

    /**
     * Performs required transaction analysis
     *
     * @param from
     * @param to
     * @return
     */
    public Map<String, Object> analyze(long from, long to) {
        Map<String, Object> result = new HashMap<>();
        if (m_dataSet.isEmpty()) {
            result.put("ERROR", "TransactionAnalyzer has no transaction data to analyze.");
            return result;
        }
        if (from > to) {
            result.put("ERROR",
                    String.format("date-from parameter must be before date-to. Actual requested range is from: %s to: %s",
                    DATE_FORMAT.format(new Date(from)), DATE_FORMAT.format(new Date(to))));
            return result;
        }
        int toIndex = m_dataSet.size();
        int searchIndex = 0;
        // skip transactions that are before the requested start time
        while (searchIndex < toIndex && m_dataSet.get(searchIndex).getDate() < from) {
            searchIndex++;
        }
        // if found data that fall within the required range
        if (searchIndex < toIndex && m_dataSet.get(searchIndex).getDate() <= to) {
            m_analyzerFunction.accept(m_dataSet.get(searchIndex++));
            while (searchIndex < toIndex && m_dataSet.get(searchIndex).getDate() <= to) {
                m_analyzerFunction.accept(m_dataSet.get(searchIndex++));
            }
        }
        result = m_analyzerFunction.getResult();
        return result;
    }

    /**
     * Convert the result to the human readable format.
     * @param result
     * @return
     */
    public String interpretResult(Map<String, Object> result) {
        StringBuilder text = new StringBuilder();
        if (result != null && !result.isEmpty()) {
            Arrays.asList(m_analyzerFunction.getKeySet()).forEach(k -> text.append(k).append(" = ").append(result.get(k)).append("\n"));
        }
        return text.toString();
    }

    /**
     * Retrieve the current data set.
     *
     * @return
     */
    public List<TransactionRecord> getDataSet() {
        return m_dataSet;
    }

    /**
     * Retrieve the internal transaction index.
     *
     * @return
     */
    public Map<String, TransactionRecord> getTransacionIndex() {
        return m_TransactionIdIndex;
    }
}

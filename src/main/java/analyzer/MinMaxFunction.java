package analyzer;

import model.TransactionRecord;

import java.util.HashMap;
import java.util.Map;

import static model.TransactionRecord.TransactionType.PAYMENT;

public class MinMaxFunction implements AnalyzerFunction<TransactionRecord> {

    public static final String[] KEY_SET = new String[] {"Minimal Amount", "Maximal Amount", "Number of Transactions"};
    private double  m_minAmount = Double.MAX_VALUE;
    private double  m_maxAmount = 0.0d;
    private int     m_transactionCount = 0;

    /**
     * Calculate and return the final value.
     *
     * @return
     */
    @Override
    public Map<String, Object> getResult() {
        Map<String, Object> result = new HashMap<>();
        result.put(KEY_SET[0], String.format("%1$,.2f", m_transactionCount > 0 ? m_minAmount : 0.0d));
        result.put(KEY_SET[1], String.format("%1$,.2f", m_maxAmount));
        result.put(KEY_SET[2], m_transactionCount);
        return result;
    }

    /**
     * Key set used by the analyzer to represent result. This allows control display value order.
     *
     * @return
     */
    @Override
    public String[] getKeySet() {
        return KEY_SET;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param transactionRecord the function argument
     * @return the function result
     */
    @Override
    public void accept(TransactionRecord transactionRecord) {
        if (!transactionRecord.isReversed() && PAYMENT.equals(transactionRecord.getType())) {
            m_transactionCount++;
            if (transactionRecord.getAmount() > m_maxAmount) {
                m_maxAmount = transactionRecord.getAmount();
            }
            if (transactionRecord.getAmount() < m_minAmount) {
                m_minAmount = transactionRecord.getAmount();
            }
        }
    }
}

package analyzer;

import model.TransactionRecord;

import java.util.HashMap;
import java.util.Map;

import static model.TransactionRecord.TransactionType.PAYMENT;

/**
 * Implements "Average Transaction Value" aggregator function.
 */
public class AveragingFunction implements AnalyzerFunction<TransactionRecord>  {

    public  static final String[] KEY_SET = new String[]{"Number of Transactions", "Average Transaction Value"};
    private int     m_transactionCount  = 0;
    private double  m_averageValue      = 0.0d;

    /**
     * Applies this function to the given argument.
     *
     * @param transactionRecord the function argument
     */
    @Override
    public void accept(TransactionRecord transactionRecord) {
        if (!transactionRecord.isReversed() && PAYMENT.equals(transactionRecord.getType())) {
            m_transactionCount++;
            m_averageValue    += transactionRecord.getAmount();
        }
    }

    /**
     * Calculate and return the final value.
     *
     * @return
     */
    @Override
    public Map<String, Object> getResult() {
        Map<String, Object> result = new HashMap<>();
        if (m_transactionCount > 0) {
            m_averageValue = m_averageValue / m_transactionCount;
        }
        result.put(KEY_SET[0], m_transactionCount);
        result.put(KEY_SET[1], String.format("%1$,.2f", m_averageValue));
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

}

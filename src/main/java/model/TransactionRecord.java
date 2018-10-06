package model;

import java.util.Date;
import static app.SimpleTransactionAnalyzer.DATE_FORMAT;

/**
 * representation of the transaction as part of the data set.
 * The Date (Timestamp) is converted to Long value (milliseconds) in order to use less storage
 * and make comparison operations faster (it is a numeric comparison now)
 */
public class TransactionRecord {

    public static enum TransactionType {
        PAYMENT, REVERSAL, UNKNOWN;
    }
    private String          id;
    private long            date;
    private double          amount;
    private String          merchant;
    private TransactionType type = TransactionType.UNKNOWN;
    private String          relatedTransaction;
    private boolean         reversed;

    /**
     * Use a static builder method rather then constructor that allows returning null value in case of input not accepted.
     * E.g. possible legal (expected) invalid input is the CSV header line or empty line.
     *
     * @param csvLine CSV input line from the input file
     * @return
     */
    public static TransactionRecord of(String csvLine)  throws IllegalArgumentException  {
        TransactionRecord record = null;
        if (csvLine != null) {
            String[] tokens = csvLine.split(" *, *"); // trim the leading/trailing whitespaces around the commas
            if (tokens.length >= 5) {
                // it seems we have valid CSV input
                if (!"id".equals(tokens[0].toLowerCase()) &&
                        !"date".equals(tokens[1].toLowerCase())) {
                    // and it is not a header line
                    record = new TransactionRecord();
                    record.setId(tokens[0]);
                    try {
                        record.setDate(DATE_FORMAT.parse(tokens[1]).getTime());
                    } catch (Exception pex) {
                        record.setDate(Long.MIN_VALUE);
                    }
                    try {
                        record.setAmount(Double.parseDouble(tokens[2]));
                    } catch (Exception pex) {
                        // amout will remain at default 0.0d value
                    }
                    record.setMerchant(tokens[3]);
                    record.setType(TransactionType.valueOf(tokens[4]));
                    if (tokens.length > 5 && tokens[5].length() > 0) {
                        record.setRelatedTransaction(tokens[5]);
                    }
                }
            }
        }
        return record;
    }

    public TransactionRecord() {

    }

    /* getters/setters */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getRelatedTransaction() {
        return relatedTransaction;
    }

    public void setRelatedTransaction(String relatedTransaction) {
        this.relatedTransaction = relatedTransaction;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    /**
     * Custom display of the content, useful for debugging or for report generation
     * @return
     */
    @Override
    public String toString() {
        String formattedDate = DATE_FORMAT.format(new Date(date));
        String formattedAmount = String.format("%1$,.2f", amount);
        String corePart = String.format("ID: %s, Date: %s, Amount: %s, Merchant: %s, Type: %s",
                id, formattedDate, formattedAmount, merchant, type.name());
        String relatedTransaction = this.relatedTransaction != null && this.relatedTransaction.length() > 0 ?
                ", Related Transaction: "+this.relatedTransaction : "";
        String reversedTransaction = this.reversed ? ", Reversed" : "";
        return corePart + relatedTransaction + reversedTransaction + "\n";
    }

}

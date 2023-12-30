package edu.najah.cap.data.mongodb;

import edu.najah.cap.payment.Transaction;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionMapper implements IDocMapper<Transaction> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionMapper.class);

    @Override
    public Document mapToDocument(Transaction transaction) {
        Document document = new Document();
        try {
            document.append("userName", transaction.getUserName())
                    .append("amount", transaction.getAmount())
                    .append("description", transaction.getDescription());
            logger.info("Transaction mapped to Document");
        } catch (Exception e) {
            logger.error("error in mapping Transaction to document");
        }
        return document;
    }
}

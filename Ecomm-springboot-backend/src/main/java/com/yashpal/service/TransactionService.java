package com.yashpal.service;

import com.yashpal.model.Order;
import com.yashpal.model.Seller;
import com.yashpal.model.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);
    List<Transaction> getTransactionBySeller(Seller seller);
    List<Transaction>getAllTransactions();
}

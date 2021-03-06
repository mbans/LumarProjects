package com.lumar.moneymanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.lumar.moneymanager.domain.Account;
import com.lumar.moneymanager.domain.Transaction;
import com.lumar.moneymanager.util.TransactionFieldConfig.TransactionField;

public class TransactionGenerator {
			
	public Set<Transaction> createTransactions(Account account, List<String> transactions) {
		List<String> headings = account.getTransactionHeadingOrdering();
		Set<Transaction> toReturn = new HashSet<Transaction>(); 
		
		for(String transaction : transactions) {
			String[] transactionValues = transaction.split(account.getDelimiter());
			Transaction tran = createTransaction(transactionValues , headings); 
			toReturn.add(tran);
		}
		return toReturn;
	}
	
	/**
	 * Returns 
	 * @param account
	 * @param transactions
	 * @return
	 */
	public Map<Transaction,String> createTransactionMap(Account account, List<String> transactions) {
		Map<Transaction,String> toReturn = Maps.newHashMap();
		for(String transactionStr : transactions) {
			Transaction tran = createTransaction(account, transactionStr);
			toReturn.put(tran,transactionStr);
		}
		return toReturn;
	}
	
	private Transaction createTransaction(Account account, String transactionStr) {
		String[] transactionValues = transactionStr.split(account.getDelimiter());
		return createTransaction(transactionValues , account.getTransactionHeadingOrdering()); 
	}
	
	/**
	 * Inspects the credit and debit and assign amount accordingly
	 * @param tran
	 */
	private void setAmount(Transaction tran) {
		//Only do stuff if amount has a zero value
		if(Transaction.ZERO_BALANCE.compareTo(tran.getAmount()) != 0) {
			return;
		}
		
		//Amount is a Credit
		BigDecimal amount = null;
		if(Transaction.ZERO_BALANCE.compareTo(tran.getCredit()) == 0) { //Credit is ZERO, therefore must be a debit
			amount = tran.getDebit().negate().setScale(2, RoundingMode.CEILING);
		}
		else {	//Debit is ZERO, therefore must be a credit
			amount = tran.getCredit().setScale(2, RoundingMode.CEILING);
		}
		tran.setAmount(amount);
	}

	/**
	 * Creates a single Transaction
	 * @param tran
	 * @param headings
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Transaction createTransaction(String[] transactionValues, List<String> headingNamesForAccount) {
		Transaction tran = new Transaction();

		for(int i=0; i < transactionValues.length; i++) {
			String headingName=headingNamesForAccount.get(i);
			String tranFieldValue=transactionValues[i];
			
			TransactionField headingDefinition = TransactionField.getFromFieldName(headingName);
			
			//Assigns the value to the given transaction, including all required casting
			headingDefinition.assignToTran(tran, tranFieldValue);
		}
		setAmount(tran);
		return tran;
	}
	
	/**
	 * @param account
	 * @param rawTransactionUpload
	 * @return
	 */
	public Set<Transaction> createTransactions(Account account, String rawTransactionUpload) {
		List<String> transactions = Arrays.asList(rawTransactionUpload.split("\n"));
		return createTransactions(account,transactions);
	}
}

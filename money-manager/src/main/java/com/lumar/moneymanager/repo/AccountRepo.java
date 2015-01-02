package com.lumar.moneymanager.repo;

import java.util.Set;

import com.google.code.morphia.Key;
import com.lumar.moneymanager.domain.Account;
 
public interface AccountRepo {
	
	public Key<Account> saveAccount(Account account);

	public Set<Account> getAccountsByUsername(String username);
}
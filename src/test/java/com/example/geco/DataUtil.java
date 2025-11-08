package com.example.geco;

import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;

public class DataUtil {
	public static Account createAccountA(UserDetail detail) {
		Account account = new Account();
		account.setPassword("1234567890");
		account.setDetail(detail);
		
		return account;
	}
	
	public static UserDetail createUserDetailA() {
		UserDetail detail = new UserDetail();
		
		detail.setSurname("Creus");
		detail.setFirstName("Coleen");
		detail.setEmail("krysscoleen.creus@cvsu.edu.ph");
		
		return detail;
	}
}

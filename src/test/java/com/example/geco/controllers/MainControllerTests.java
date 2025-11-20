package com.example.geco.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.example.geco.AbstractControllerTest;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class MainControllerTests extends AbstractControllerTest{
	
	// to implement
	@Test
	public void canGetAverageMonthlyVisitors() throws Exception {
		
	}
	
	// to implement
	@Test
	public void canGetAverageYearlyVisitors() throws Exception {
		
	}
	
	// to implement
	@Test
	public void canGetAverageRating() throws Exception {
		
	}
	
	// to implement
	@Test
	public void canDisplayHomeData() throws Exception {
		
	}
}

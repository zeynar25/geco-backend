package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Booking.PaymentStatus;
import com.example.geco.dto.BookingRequest;
import com.example.geco.dto.BookingUpdateRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
		@Test
		public void canAddBooking() throws Exception {
			Account savedAccount = DataUtil.createUserAccountA(accountRepository);

		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
			
			// Save an account to the database.
			BookingRequest request = DataUtil.createBookingRequestA(
					savedAccount.getAccountId(),
					accountRepository, 
					packageInclusionRepository, 
					tourPackageRepository);
			
			String requestJson = objectMapper.writeValueAsString(request);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.account.accountId").value(request.getAccountId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.tourPackage.packageId").value(request.getTourPackageId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.bookingInclusions").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitDate").value(request.getVisitDate().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitTime").value(request.getVisitTime().toString() + ":00")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.groupSize").value(request.getGroupSize())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.bookingStatus").value(BookingStatus.PENDING.toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.paymentStatus").value(PaymentStatus.UNPAID.toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.totalPrice").exists()
			);
		}
		
		@Test
		public void canGetBooking() throws Exception {
			Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
			
			BookingRequest request = DataUtil.createBookingRequestA(
					savedAccount.getAccountId(),
					accountRepository,  
					packageInclusionRepository,
					tourPackageRepository);
			
		    Booking savedBookingA = bookingService.addBooking(request);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.bookingId").value(savedBookingA.getBookingId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedBookingA.getAccount().getAccountId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.tourPackage.packageId").value(savedBookingA.getTourPackage().getPackageId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.bookingInclusions").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitDate").value(savedBookingA.getVisitDate().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitTime").value(savedBookingA.getVisitTime().toString() + ":00")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.groupSize").value(savedBookingA.getGroupSize())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.bookingStatus").value(savedBookingA.getBookingStatus().name())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.paymentStatus").value(savedBookingA.getPaymentStatus().name())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.totalPrice").value(savedBookingA.getTotalPrice())
			);
		}
		
		@Test
		public void canGetAllMyBookingsWithPagination() throws Exception {
		    Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

		    BookingRequest requestA = DataUtil.createBookingRequestA(
		            savedAccount.getAccountId(),
		            accountRepository,
		            packageInclusionRepository,
		            tourPackageRepository);

		    BookingRequest requestB = DataUtil.createBookingRequestB(
		            savedAccount.getAccountId(),
		            accountRepository,
		            packageInclusionRepository,
		            tourPackageRepository);

		    Booking savedBookingA = bookingService.addBooking(requestA);
		    Booking savedBookingB = bookingService.addBooking(requestB);

		    // --- Page 0: should return Booking A only (size = 1) ---
		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/booking")
		                    .param("page", "0")
		                    .param("size", "1")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		            MockMvcResultMatchers.status().isOk()
		    )

		    // Page metadata
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.number").value(0))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.size").value(1))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.totalPages").value(2))

		    // Booking A data
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].bookingId")
		            	.value(savedBookingA.getBookingId()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].account.accountId")
		            	.value(savedBookingA.getAccount().getAccountId()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].tourPackage.packageId")
		            	.value(savedBookingA.getTourPackage().getPackageId()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].bookingInclusions").exists())
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].visitDate")
		            	.value(savedBookingA.getVisitDate().toString()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].visitTime")
		            	.value(savedBookingA.getVisitTime().toString() + ":00"))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].groupSize")
		            	.value(savedBookingA.getGroupSize()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].bookingStatus")
		            	.value(savedBookingA.getBookingStatus().name()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].paymentStatus")
		            	.value(savedBookingA.getPaymentStatus().name()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].totalPrice")
		            	.value(savedBookingA.getTotalPrice()));

		    // --- Page 1: should return Booking B only ---
		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/booking")
		                    .param("page", "1")
		                    .param("size", "1")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		            MockMvcResultMatchers.status().isOk()
		    )

		    // Page metadata
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.number").value(1))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.size").value(1))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.totalPages").value(2))

		    // Booking B data
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].bookingId")
		            	.value(savedBookingB.getBookingId()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].account.accountId")
		            	.value(savedBookingB.getAccount().getAccountId()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].tourPackage.packageId")
		            	.value(savedBookingB.getTourPackage().getPackageId()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].bookingInclusions").exists())
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].visitDate")
		            	.value(savedBookingB.getVisitDate().toString()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].visitTime")
		            	.value(savedBookingB.getVisitTime().toString() + ":00"))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].groupSize")
		            	.value(savedBookingB.getGroupSize()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].bookingStatus")
		            	.value(savedBookingB.getBookingStatus().name()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].paymentStatus")
		            	.value(savedBookingB.getPaymentStatus().name()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].totalPrice")
		            	.value(savedBookingB.getTotalPrice()));
		}

		
//		@Test
//		public void canGetAllBookingsWithinFewDays() throws Exception {
//		    // Create admin account and mock authentication
//		    Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
//		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
//
//		    BookingRequest requestA = DataUtil.createBookingRequestA(
//					savedAccount.getAccountId(),
//					accountRepository, 
//					packageInclusionRepository, 
//					tourPackageRepository);
//		    
//		    BookingRequest requestB = DataUtil.createBookingRequestA(
//					savedAccount.getAccountId(),
//					accountRepository, 
//					packageInclusionRepository, 
//					tourPackageRepository);
//
//		    Booking savedBookingA = bookingService.addBooking(requestA);
//		    Booking savedBookingB = bookingService.addBooking(requestB);
//
//		    String startDate = LocalDate.now().toString();
//		    String endDate = LocalDate.now().plusDays(4).toString();
//
//		    // Expect one booking within 4 days
//		    mockMvc.perform(
//		            MockMvcRequestBuilders.get("/booking")
//		                    .param("startDate", startDate)
//		                    .param("endDate", endDate)
//		                    .contentType(MediaType.APPLICATION_JSON)
//		    ).andExpect(MockMvcResultMatchers.status().isOk())
//		     .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
//		     .andExpect(MockMvcResultMatchers.jsonPath("$[0].bookingId").value(savedBookingA.getBookingId()));
//
//		    // Expect two bookings within 5 days
//		    endDate = LocalDate.now().plusDays(5).toString();
//		    mockMvc.perform(
//		            MockMvcRequestBuilders.get("/booking")
//		                    .param("startDate", startDate)
//		                    .param("endDate", endDate)
//		                    .contentType(MediaType.APPLICATION_JSON)
//		    ).andExpect(MockMvcResultMatchers.status().isOk())
//		     .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
//		     .andExpect(MockMvcResultMatchers.jsonPath("$[0].bookingId").value(savedBookingA.getBookingId()))
//		     .andExpect(MockMvcResultMatchers.jsonPath("$[1].bookingId").value(savedBookingB.getBookingId()));
//		}

		@Test
		public void canUpdateBooking() throws Exception {
		    Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

		    BookingRequest request = DataUtil.createBookingRequestA(
					savedAccount.getAccountId(),
					accountRepository, 
					packageInclusionRepository, 
					tourPackageRepository);
		    
		    Booking savedBookingA = bookingService.addBooking(request);

		    Booking newBooking = Booking.builder().groupSize(5).build();

		    String bookingJson = objectMapper.writeValueAsString(newBooking);

		    mockMvc.perform(
		            MockMvcRequestBuilders.patch("/booking/" + savedBookingA.getBookingId())
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(bookingJson)
		    ).andExpect(MockMvcResultMatchers.status().isOk())
		     .andExpect(MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedBookingA.getAccount().getAccountId()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.tourPackage.packageId").value(savedBookingA.getTourPackage().getPackageId()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.bookingInclusions").exists())
		     .andExpect(MockMvcResultMatchers.jsonPath("$.visitDate").value(savedBookingA.getVisitDate().toString()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.visitTime").value(savedBookingA.getVisitTime().toString() + ":00"))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.groupSize").value(newBooking.getGroupSize()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.bookingStatus").value(savedBookingA.getBookingStatus().name()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.totalPrice").value(2740));
		}

		@Test
		public void canUpdateBookingStatus() throws Exception {
		    Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

		    BookingRequest request = DataUtil.createBookingRequestA(
					savedAccount.getAccountId(),
					accountRepository, 
					packageInclusionRepository, 
					tourPackageRepository);
		    
		    Booking savedBooking = bookingService.addBooking(request);
		    
		    BookingUpdateRequest bookingStatusRequest = BookingUpdateRequest.builder()
		    		.bookingStatus(BookingStatus.APPROVED)
		    		.build();
		    
		    String bookingStatusRequestJson = objectMapper.writeValueAsString(bookingStatusRequest);

		    mockMvc.perform(
		            MockMvcRequestBuilders.patch("/booking/staff/" + savedBooking.getBookingId())
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(bookingStatusRequestJson)
		    ).andExpect(MockMvcResultMatchers.status().isOk())
		     .andExpect(MockMvcResultMatchers.jsonPath("$.bookingStatus").value(bookingStatusRequest
		    		 .getBookingStatus().toString()));
		}
		
		@Test
		public void canSoftDeleteBooking() throws Exception {
		    Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

		    BookingRequest request = DataUtil.createBookingRequestA(
					savedAccount.getAccountId(),
					accountRepository, 
					packageInclusionRepository, 
					tourPackageRepository);
		    
		    Booking savedBooking = bookingService.addBooking(request);

		    mockMvc.perform(
		            MockMvcRequestBuilders.delete("/booking/" + savedBooking.getBookingId())
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(MockMvcResultMatchers.status().isNoContent());
		    
		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/booking/" + savedBooking.getBookingId())
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(MockMvcResultMatchers.status().isOk())
		     .andExpect(MockMvcResultMatchers.jsonPath("$.bookingId").value(savedBooking.getBookingId()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.active").value("false"));
		}

		@Test
		public void canRestoreBooking() throws Exception {
		    Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

		    BookingRequest request = DataUtil.createBookingRequestA(
					savedAccount.getAccountId(),
					accountRepository, 
					packageInclusionRepository, 
					tourPackageRepository);
		    
		    Booking savedBooking = bookingService.addBooking(request);
		   
		    bookingService.softDeleteBooking(savedBooking.getBookingId());

		    mockMvc.perform(
		            MockMvcRequestBuilders.patch("/booking/restore/" + savedBooking.getBookingId())
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(MockMvcResultMatchers.status().isNoContent()); 
		    
		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/booking/" + savedBooking.getBookingId())
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(MockMvcResultMatchers.status().isOk())
		     .andExpect(MockMvcResultMatchers.jsonPath("$.bookingId").value(savedBooking.getBookingId()))
		     .andExpect(MockMvcResultMatchers.jsonPath("$.active").value("true"));
		}

	}
	
	@Nested
    class FailureTests {
	    @Test
	    public void addBooking_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
	        BookingRequest request = DataUtil.createBookingRequestA(
	                1, accountRepository, packageInclusionRepository, tourPackageRepository);

	        String requestJson = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.post("/booking")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(requestJson)
	        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
	    }

	    @Test
	    public void getBooking_NonExistentId_ShouldReturnNotFound() throws Exception {
	        Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	        mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/booking/9999")
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isNotFound());
	    }

	    @Test
	    public void updateBooking_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
	        BookingUpdateRequest request = BookingUpdateRequest.builder()
	                .bookingStatus(Booking.BookingStatus.APPROVED)
	                .build();
	        String json = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/booking/staff/1")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
	    }

	    @Test
	    public void updateBooking_NonExistentId_ShouldReturnNotFound() throws Exception {
	        Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	        mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

	        BookingUpdateRequest request = BookingUpdateRequest.builder()
	                .bookingStatus(Booking.BookingStatus.APPROVED)
	                .build();
	        String json = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/booking/staff/9999")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(MockMvcResultMatchers.status().isNotFound());
	    }

	    @Test
	    public void deleteBooking_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
	        Account savedAccount = DataUtil.createUserAccountA(accountRepository);
	        mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/booking/1")
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isForbidden());
	    }

	    @Test
	    public void restoreBooking_NonExistentId_ShouldReturnNotFound() throws Exception {
	        Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	        mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/booking/restore/9999")
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isNotFound());
	    }

	    @Test
	    public void updateBookingStatus_InvalidStatus_ShouldReturnBadRequest() throws Exception {
	        Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	        mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

	        BookingRequest request = DataUtil.createBookingRequestA(
	                savedAccount.getAccountId(),
	                accountRepository,
	                packageInclusionRepository,
	                tourPackageRepository
	        );
	        Booking savedBooking = bookingService.addBooking(request);

	        // Invalid enum value
	        String invalidJson = "{\"bookingStatus\": \"INVALID_STATUS\"}";

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/booking/staff/" + savedBooking.getBookingId())
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(invalidJson)
	        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
	    }

        @Test
        public void cannotAddBookingMissingAccount() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            request.setAccountId(null); // remove account

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                    MockMvcRequestBuilders.post("/booking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Booking's account ID is missing."));
        }

        @Test
        public void cannotAddBookingMissingPackage() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            request.setTourPackageId(null); // remove package

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                    MockMvcRequestBuilders.post("/booking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Booking's Tour package ID is missing."));
        }

        @Test
        public void cannotAddBookingMissingVisitDate() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            request.setVisitDate(null);

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                    MockMvcRequestBuilders.post("/booking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Booking's visit date is missing."));
        }

        @Test
        public void cannotAddBookingMissingVisitTime() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            request.setVisitTime(null);

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                    MockMvcRequestBuilders.post("/booking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Booking's visit time is missing."));
        }

        @Test
        public void cannotAddBookingInvalidGroupSize() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            request.setGroupSize(-1);

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                    MockMvcRequestBuilders.post("/booking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid Booking's group size."));
        }

        @Test
        public void cannotGetBookingNonExistent() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            int invalidId = 9999;
            mockMvc.perform(
                    MockMvcRequestBuilders.get("/booking/" + invalidId)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(MockMvcResultMatchers.status().isNotFound())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                     .value("Booking with ID '" + invalidId + "' not found."));
        }

        @Test
        public void cannotUpdateBookingNoFields() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            Booking savedBookingA = bookingService.addBooking(request);

            BookingRequest emptyRequest = new BookingRequest(); // nothing set
            String requestJson = objectMapper.writeValueAsString(emptyRequest);

            mockMvc.perform(
                    MockMvcRequestBuilders.patch("/booking/" + savedBookingA.getBookingId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                     .value("No fields provided to update booking."));
        }

        @Test
        public void cannotDeleteBookingNonExistent() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            int invalidId = 9999;
            mockMvc.perform(
                    MockMvcRequestBuilders.delete("/booking/" + invalidId)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(MockMvcResultMatchers.status().isNotFound())
             .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                     .value("Booking with ID '" + invalidId + "' not found."));
        }

        @Test
        public void cannotDeleteBookingUnauthorized() throws Exception {
            Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
            mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());

            BookingRequest request = DataUtil.createBookingRequestA(
                    savedAccount.getAccountId(),
                    accountRepository,
                    packageInclusionRepository,
                    tourPackageRepository
            );

            Booking savedBookingA = bookingService.addBooking(request);

            mockMvc.perform(
                    MockMvcRequestBuilders.delete("/booking/" + savedBookingA.getBookingId())
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(MockMvcResultMatchers.status().isForbidden());
        }
	}
}

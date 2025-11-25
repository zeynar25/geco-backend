package com.example.geco;

import java.util.ArrayList;
import java.util.List;

import com.example.geco.domains.Account;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.Faq;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.UserDetail;
import com.example.geco.domains.Account.Role;
import com.example.geco.dto.FeedbackCategoryRequest;
import com.example.geco.dto.SignupRequest;
import com.example.geco.dto.TourPackageRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.PackageInclusionRepository;

public class DataUtil {
	public static SignupRequest createSignupRequestA() {
		return SignupRequest.builder()
				.email("krysscoleen.creus@cvsu.edu.ph")
				.password("Hello12345678")
				.confirmPassword("Hello12345678")
				.build();
	}

	public static SignupRequest createSignupRequestB() {
		return SignupRequest.builder()
				.email("daniellalyn.soliman@cvsu.edu.ph")
				.password("Hi12345678")
				.confirmPassword("Hi12345678")
				.build();
	}
	
//	public static Account createAccount(AccountRepository accountRepository) {
//		String hashedPassword = passwordEncoder.encode(password);
//		
//		Account account = Account.builder()
//				.role(Role.USER)
//				.password(hashedPassword)
//				.detail(
//						UserDetail.builder()
//						.email("krysscoleen.creus@cvsu.edu.ph")
//						.build())
//				.build();
//		
//		return accountRepository.save(account);
//	}
	
	public static Attraction createAttractionA() {
		Attraction attraction = new Attraction();
		attraction.setAttractionId(0);
		attraction.setName("Hanging bridge");
		attraction.setDescription("A bridge that is hanging.");
		
		return attraction;
	}
	
	public static Attraction createAttractionB() {
		Attraction attraction = new Attraction();
		attraction.setAttractionId(1);
		attraction.setName("Inang Kalikasan");
		attraction.setDescription("A special statue.");
		
		return attraction;
	}
	
	public static Faq createFaqA() {
		return Faq.builder()
				.question("Where is this park located?")
				.answer("It is located inside the Cavite State University Main Campus.")
				.build();
	}
	
	public static Faq createFaqB() {
		return Faq.builder()
				.question("When does this park open and close?")
				.answer("It is usually open --:--am to 05:00pm every Monday to Thursday.")
				.build();
	}
	
	public static PackageInclusion createPackageInclusionA() {
		return PackageInclusion.builder()
				.inclusionName("Buffet Lunch")
				.inclusionPricePerPerson(500)
				.build();
	}
	
	public static PackageInclusion createPackageInclusionB() {
		return PackageInclusion.builder()
				.inclusionName("Horse Back Riding")
				.inclusionPricePerPerson(150)
				.build();
	}
	
	public static PackageInclusion createPackageInclusionC() {
		return PackageInclusion.builder()
				.inclusionName("Day Tour")
				.inclusionPricePerPerson(100)
				.build();
	}
	
	public static FeedbackCategoryRequest createFeedbackCategoryRequestA() {
		return FeedbackCategoryRequest.builder()
				.label("Attractions")
				.build();
	}
	
	public static FeedbackCategoryRequest createFeedbackCategoryRequestB() {
		return FeedbackCategoryRequest.builder()
				.label("Facilities")
				.build();
	}
	
	public static TourPackageRequest createTourPackageRequestA(PackageInclusionRepository packageInclusionRepository) {
		PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
		packageInclusionRepository.save(inclusionA);
		
		List<Integer> inclusionIds = new ArrayList<>();
		inclusionIds.add(inclusionA.getInclusionId());
		
		return TourPackageRequest.builder()
				.name("The best")
				.description("Detailed description about this package")
				.duration(60)
				.basePrice(500)
				.inclusionIds(inclusionIds)
				.build();
	}
	
	public static TourPackageRequest createTourPackageRequestB(PackageInclusionRepository packageInclusionRepository) {
		PackageInclusion inclusionA = DataUtil.createPackageInclusionB();
		packageInclusionRepository.save(inclusionA);
		
		PackageInclusion inclusionB = DataUtil.createPackageInclusionC();
		packageInclusionRepository.save(inclusionB);
		
		List<Integer> inclusionIds = new ArrayList<>();
		inclusionIds.add(inclusionA.getInclusionId());
		inclusionIds.add(inclusionB.getInclusionId());
		
		return TourPackageRequest.builder()
				.name("The double best")
				.description("Detailed description about this package")
				.duration(120)
				.basePrice(100)
				.inclusionIds(inclusionIds)
				.build();
	}
	
//	public static BookingInclusion createBookingInclusionA(PackageInclusionService packageInclusionService, Booking booking) {
//		PackageInclusion inclusion = DataUtil.createPackageInclusionA();
//		PackageInclusion savedInclusion = packageInclusionService.addInclusion(inclusion);
//		
//		BookingInclusion bookingInclusion = new BookingInclusion();
//		bookingInclusion.setBooking(booking);
//		bookingInclusion.setInclusion(savedInclusion);
//		bookingInclusion.setQuantity(2);
//		bookingInclusion.setPriceAtBooking(savedInclusion.getInclusionPricePerPerson());
//		
//		return bookingInclusion;
//	}
//	
//	public static BookingInclusion createBookingInclusionB(PackageInclusionService packageInclusionService, Booking booking) {
//		PackageInclusion inclusion = DataUtil.createPackageInclusionB();
//		PackageInclusion savedInclusion = packageInclusionService.addInclusion(inclusion);
//		
//		BookingInclusion bookingInclusion = new BookingInclusion();
//		bookingInclusion.setBooking(booking);
//		bookingInclusion.setInclusion(savedInclusion);
//		bookingInclusion.setQuantity(1);
//		bookingInclusion.setPriceAtBooking(savedInclusion.getInclusionPricePerPerson());
//		
//		return bookingInclusion;
//	}
//	
//
//	public static Booking createBookingA(AccountService accountService, 
//			TourPackageService tourPackageService,
//			PackageInclusionService packageInclusionService) {
//		Account account = DataUtil.createAccountA();
//		AccountResponse savedResponse = accountService.addTouristAccount(account);
//		
//		Account savedAccount = accountService.getAccount(savedResponse.getAccountId());
//		
//		TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
//		TourPackage savedPackage = tourPackageService.addPackage(packageA);
//		
//		Booking booking = new Booking();
//	    booking.setAccount(savedAccount);
//	    booking.setTourPackage(savedPackage);
//	    booking.setVisitDate(LocalDate.now().plusDays(3));
//	    booking.setVisitTime(LocalTime.of(9, 30));
//	    booking.setGroupSize(4);
//	    booking.setStatus(BookingStatus.PENDING);
//		
//		BookingInclusion bookingInclusion = DataUtil.createBookingInclusionA(packageInclusionService, booking);
//		List<BookingInclusion> inclusions = new ArrayList<>();
//		inclusions.add(bookingInclusion);
//		
//		booking.setInclusions(inclusions);
//	    
//	    int inclusionsPrice = 0;
//	    for (BookingInclusion inclusion : inclusions) {
//	    	inclusionsPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
//	    }
//	    
//	    booking.setTotalPrice(savedPackage.getBasePrice() * booking.getGroupSize() + inclusionsPrice);
//
//	    return booking;
//	}
//	
//	public static Booking createBookingB(AccountService accountService, 
//			TourPackageService tourPackageService,
//			PackageInclusionService packageInclusionService) {
//		Account account = DataUtil.createAccountA();
//		AccountResponse savedResponse = accountService.addTouristAccount(account);
//		
//		Account savedAccount = accountService.getAccount(savedResponse.getAccountId());
//		
//		TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
//		TourPackage savedPackage = tourPackageService.addPackage(packageA);
//		
//		Booking booking = new Booking();
//	    booking.setAccount(savedAccount);
//	    booking.setTourPackage(savedPackage);
//	    booking.setVisitDate(LocalDate.now().plusDays(5));
//	    booking.setVisitTime(LocalTime.of(9, 30));
//	    booking.setGroupSize(4);
//	    booking.setStatus(BookingStatus.PENDING);
//		
//		BookingInclusion bookingInclusion = DataUtil.createBookingInclusionB(packageInclusionService, booking);
//		List<BookingInclusion> inclusions = new ArrayList<>();
//		inclusions.add(bookingInclusion);
//		
//		booking.setInclusions(inclusions);
//	    
//	    int inclusionsPrice = 0;
//	    for (BookingInclusion inclusion : inclusions) {
//	    	inclusionsPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
//	    }
//	    
//	    booking.setTotalPrice(savedPackage.getBasePrice() * booking.getGroupSize() + inclusionsPrice);
//
//	    return booking;
//	}
//	
//	public static Feedback createFeedbackA(AccountService accountService, 
//			TourPackageService tourPackageService,
//			PackageInclusionService packageInclusionService,
//			BookingService bookingService,
//			FeedbackCategoryService feedbackCategoryService) {
//		
//		Booking booking = createBookingA(accountService, 
//				tourPackageService, 
//				packageInclusionService);
//		Booking savedBooking = bookingService.addBooking(booking);
//		
//		FeedbackCategory category = createFeedbackCategoryA();
//		FeedbackCategory savedCategory = feedbackCategoryService.addCategory(category);
//		
//		Feedback feedback = new Feedback();
//		feedback.setAccount(booking.getAccount());      
//		feedback.setBooking(savedBooking); 
//		feedback.setCategory(savedCategory); 
//		feedback.setStars(4.5);           
//		feedback.setComment("Great experience!"); 
//		feedback.setSuggestion("Keep the place clean.");
//		 
//		return feedback;
//	}
//	
//	public static Feedback createFeedbackB(AccountService accountService, 
//			TourPackageService tourPackageService,
//			PackageInclusionService packageInclusionService,
//			BookingService bookingService,
//			FeedbackCategoryService feedbackCategoryService) {
//		
//		Booking booking = createBookingB(accountService, 
//				tourPackageService, 
//				packageInclusionService);
//		Booking savedBooking = bookingService.addBooking(booking);
//		
//		FeedbackCategory category = createFeedbackCategoryB();
//		FeedbackCategory savedCategory = feedbackCategoryService.addCategory(category);
//		
//		Feedback feedback = new Feedback();
//		feedback.setAccount(booking.getAccount());      
//		feedback.setBooking(savedBooking); 
//		feedback.setCategory(savedCategory); 
//		feedback.setStars(4.5);           
//		feedback.setComment("Great experience!"); 
//		feedback.setSuggestion("Keep the place clean.");
//		 
//		return feedback;
//	}
}

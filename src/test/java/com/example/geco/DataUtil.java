package com.example.geco;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.geco.domains.Account;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.dto.AccountResponse;
import com.example.geco.services.AccountService;
import com.example.geco.services.BookingService;
import com.example.geco.services.FeedbackCategoryService;
import com.example.geco.services.PackageInclusionService;
import com.example.geco.services.TourPackageService;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.domains.Faq;
import com.example.geco.domains.Feedback;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.domains.UserDetail;

public class DataUtil {
	public static UserDetail createUserDetailA() {
		UserDetail detail = new UserDetail();
		
		detail.setSurname("Creus");
		detail.setFirstName("Coleen");
		detail.setEmail("krysscoleen.creus@cvsu.edu.ph");
		
		return detail;
	}
	
	public static UserDetail createUserDetailB() {
		UserDetail detail = new UserDetail();
		
		detail.setSurname("Soliman");
		detail.setFirstName("Daniella");
		detail.setEmail("daniellalyn.soliman@cvsu.edu.ph");
		
		return detail;
	}

	public static Account createAccountA() {
		UserDetail detail = createUserDetailA();
		
		Account account = new Account();
		account.setPassword("1234567890");
		account.setDetail(detail);
		
		return account;
	}
	
	public static Account createAccountB() {
		UserDetail detail = createUserDetailB();
		
		Account account = new Account();
		account.setPassword("1234567890");
		account.setDetail(detail);
		
		return account;
	}
	
	public static Attraction createAttractionA() {
		Attraction attraction = new Attraction();
		attraction.setAttractionId(0);
		attraction.setName("Inang Kalikasan");
		attraction.setDescription("A special statue.");
		
		return attraction;
	}
	
	public static Attraction createAttractionB() {
		Attraction attraction = new Attraction();
		attraction.setAttractionId(1);
		attraction.setName("Hanging bridge");
		attraction.setDescription("A bridge that is hanging.");
		
		return attraction;
	}
	
	public static FeedbackCategory createFeedbackCategoryA() {
		FeedbackCategory category = new FeedbackCategory();
		category.setLabel("Facilities");
		return category;
	}
	
	public static FeedbackCategory createFeedbackCategoryB() {
		FeedbackCategory category = new FeedbackCategory();
		category.setLabel("Attractions");
		return category;
	}
	
	public static Faq createFaqA() {
		Faq faq = new Faq();
		faq.setQuestion("Where is this park located?");
		faq.setAnswer("It is located inside the Cavite State University Main Campus.");
		return faq;
	}
	
	public static Faq createFaqB() {
		Faq faq = new Faq();
		faq.setQuestion("When does this park open and close?");
		faq.setAnswer("It is usually open --:--am to 05:00pm every Monday to Thursday.");
		return faq;
	}
	
	public static PackageInclusion createPackageInclusionA() {
		PackageInclusion inclusion = new PackageInclusion();
		inclusion.setInclusionName("Buffet Lunch");
		inclusion.setInclusionPricePerPerson(500);
		
		return inclusion;
	}
	
	public static PackageInclusion createPackageInclusionB() {
		PackageInclusion inclusion = new PackageInclusion();
		inclusion.setInclusionName("Horse Back Riding");
		inclusion.setInclusionPricePerPerson(150);
		
		return inclusion;
	}
	
	public static TourPackage createPackageA(PackageInclusionService packageInclusionService) {
		PackageInclusion inclusion = DataUtil.createPackageInclusionA();
		packageInclusionService.addInclusion(inclusion);
		
		List<PackageInclusion> inclusions = new ArrayList<>();
		inclusions.add(inclusion);
		
		TourPackage tourPackage = new TourPackage();
		tourPackage.setName("The best");
		tourPackage.setDuration(60);
		tourPackage.setDescription("Detailed description about this package.");
		tourPackage.setBasePrice(500);
		tourPackage.setInclusions(inclusions);
		
		return tourPackage;
	}
	
	public static TourPackage createPackageB(PackageInclusionService packageInclusionService) {
		PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
		packageInclusionService.addInclusion(inclusionA);
		
		PackageInclusion inclusionB = DataUtil.createPackageInclusionB();
		packageInclusionService.addInclusion(inclusionB);
		
		List<PackageInclusion> inclusions = new ArrayList<>();
		inclusions.add(inclusionA);
		inclusions.add(inclusionB);
		
		TourPackage tourPackage = new TourPackage();
		tourPackage.setName("The double best");
		tourPackage.setDuration(120);
		tourPackage.setDescription("Detailed description about this package but more pricey.");
		tourPackage.setBasePrice(1000);
		tourPackage.setInclusions(inclusions);
		
		return tourPackage;
	}
	
	public static BookingInclusion createBookingInclusionA(PackageInclusionService packageInclusionService, Booking booking) {
		PackageInclusion inclusion = DataUtil.createPackageInclusionA();
		PackageInclusion savedInclusion = packageInclusionService.addInclusion(inclusion);
		
		BookingInclusion bookingInclusion = new BookingInclusion();
		bookingInclusion.setBooking(booking);
		bookingInclusion.setInclusion(savedInclusion);
		bookingInclusion.setQuantity(2);
		bookingInclusion.setPriceAtBooking(savedInclusion.getInclusionPricePerPerson());
		
		return bookingInclusion;
	}
	
	public static BookingInclusion createBookingInclusionB(PackageInclusionService packageInclusionService, Booking booking) {
		PackageInclusion inclusion = DataUtil.createPackageInclusionB();
		PackageInclusion savedInclusion = packageInclusionService.addInclusion(inclusion);
		
		BookingInclusion bookingInclusion = new BookingInclusion();
		bookingInclusion.setBooking(booking);
		bookingInclusion.setInclusion(savedInclusion);
		bookingInclusion.setQuantity(1);
		bookingInclusion.setPriceAtBooking(savedInclusion.getInclusionPricePerPerson());
		
		return bookingInclusion;
	}
	

	public static Booking createBookingA(AccountService accountService, 
			TourPackageService tourPackageService,
			PackageInclusionService packageInclusionService) {
		Account account = DataUtil.createAccountA();
		AccountResponse savedResponse = accountService.addTouristAccount(account);
		
		Account savedAccount = accountService.getAccount(savedResponse.getAccountId());
		
		TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
		TourPackage savedPackage = tourPackageService.addPackage(packageA);
		
		Booking booking = new Booking();
	    booking.setAccount(savedAccount);
	    booking.setTourPackage(savedPackage);
	    booking.setVisitDate(LocalDate.now().plusDays(3));
	    booking.setVisitTime(LocalTime.of(9, 30));
	    booking.setGroupSize(4);
	    booking.setStatus(BookingStatus.PENDING);
		
		BookingInclusion bookingInclusion = DataUtil.createBookingInclusionA(packageInclusionService, booking);
		List<BookingInclusion> inclusions = new ArrayList<>();
		inclusions.add(bookingInclusion);
		
		booking.setInclusions(inclusions);
	    
	    int inclusionsPrice = 0;
	    for (BookingInclusion inclusion : inclusions) {
	    	inclusionsPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
	    }
	    
	    booking.setTotalPrice(savedPackage.getBasePrice() * booking.getGroupSize() + inclusionsPrice);

	    return booking;
	}
	
	public static Booking createBookingB(AccountService accountService, 
			TourPackageService tourPackageService,
			PackageInclusionService packageInclusionService) {
		Account account = DataUtil.createAccountA();
		AccountResponse savedResponse = accountService.addTouristAccount(account);
		
		Account savedAccount = accountService.getAccount(savedResponse.getAccountId());
		
		TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
		TourPackage savedPackage = tourPackageService.addPackage(packageA);
		
		Booking booking = new Booking();
	    booking.setAccount(savedAccount);
	    booking.setTourPackage(savedPackage);
	    booking.setVisitDate(LocalDate.now().plusDays(5));
	    booking.setVisitTime(LocalTime.of(9, 30));
	    booking.setGroupSize(4);
	    booking.setStatus(BookingStatus.PENDING);
		
		BookingInclusion bookingInclusion = DataUtil.createBookingInclusionB(packageInclusionService, booking);
		List<BookingInclusion> inclusions = new ArrayList<>();
		inclusions.add(bookingInclusion);
		
		booking.setInclusions(inclusions);
	    
	    int inclusionsPrice = 0;
	    for (BookingInclusion inclusion : inclusions) {
	    	inclusionsPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
	    }
	    
	    booking.setTotalPrice(savedPackage.getBasePrice() * booking.getGroupSize() + inclusionsPrice);

	    return booking;
	}
	
	public static Feedback createFeedbackA(AccountService accountService, 
			TourPackageService tourPackageService,
			PackageInclusionService packageInclusionService,
			BookingService bookingService,
			FeedbackCategoryService feedbackCategoryService) {
		
		Booking booking = createBookingA(accountService, 
				tourPackageService, 
				packageInclusionService);
		Booking savedBooking = bookingService.addBooking(booking);
		
		FeedbackCategory category = createFeedbackCategoryA();
		FeedbackCategory savedCategory = feedbackCategoryService.addCategory(category);
		
		Feedback feedback = new Feedback();
		feedback.setAccount(booking.getAccount());      
		feedback.setBooking(savedBooking); 
		feedback.setCategory(savedCategory); 
		feedback.setStars(4.5);           
		feedback.setComment("Great experience!"); 
		feedback.setSuggestion("Keep the place clean.");
		 
		return feedback;
	}
	
	public static Feedback createFeedbackB(AccountService accountService, 
			TourPackageService tourPackageService,
			PackageInclusionService packageInclusionService,
			BookingService bookingService,
			FeedbackCategoryService feedbackCategoryService) {
		
		Booking booking = createBookingB(accountService, 
				tourPackageService, 
				packageInclusionService);
		Booking savedBooking = bookingService.addBooking(booking);
		
		FeedbackCategory category = createFeedbackCategoryB();
		FeedbackCategory savedCategory = feedbackCategoryService.addCategory(category);
		
		Feedback feedback = new Feedback();
		feedback.setAccount(booking.getAccount());      
		feedback.setBooking(savedBooking); 
		feedback.setCategory(savedCategory); 
		feedback.setStars(4.5);           
		feedback.setComment("Great experience!"); 
		feedback.setSuggestion("Keep the place clean.");
		 
		return feedback;
	}
}

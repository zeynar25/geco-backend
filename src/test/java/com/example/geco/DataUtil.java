package com.example.geco;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.geco.domains.Account;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.domains.Faq;
import com.example.geco.domains.Feedback;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
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
	
	public static UserDetail createUserDetailB() {
		UserDetail detail = new UserDetail();
		
		detail.setSurname("Soliman");
		detail.setFirstName("Daniella");
		detail.setEmail("daniellalyn.soliman@cvsu.edu.ph");
		
		return detail;
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
	
	public static TourPackage createPackageA(List<PackageInclusion> inclusions) {
		TourPackage tourPackage = new TourPackage();
		tourPackage.setDuration(60);
		tourPackage.setDescription("Detailed description about this package.");
		tourPackage.setBasePrice(500);
		tourPackage.setInclusions(inclusions);
		
		return tourPackage;
	}
	
	public static TourPackage createPackageB(List<PackageInclusion> inclusions) {
		TourPackage tourPackage = new TourPackage();
		tourPackage.setDuration(120);
		tourPackage.setDescription("Detailed description about this package but more pricey.");
		tourPackage.setBasePrice(1000);
		tourPackage.setInclusions(inclusions);
		
		return tourPackage;
	}
	
	public static BookingInclusion createBookingInclusionA(Booking booking, PackageInclusion packageInclusion) {
		BookingInclusion bookingInclusion = new BookingInclusion();
		bookingInclusion.setBooking(booking);
		bookingInclusion.setInclusion(packageInclusion);
		bookingInclusion.setQuantity(2);
		bookingInclusion.setPriceAtBooking(packageInclusion.getInclusionPricePerPerson());
		
		return bookingInclusion;
	}
	
	public static BookingInclusion createBookingInclusionB(Booking booking, PackageInclusion packageInclusion) {
		BookingInclusion bookingInclusion = new BookingInclusion();
		bookingInclusion.setBooking(booking);
		bookingInclusion.setInclusion(packageInclusion);
		bookingInclusion.setQuantity(1);
		bookingInclusion.setPriceAtBooking(packageInclusion.getInclusionPricePerPerson());
		
		return bookingInclusion;
	}
	

	public static Booking createBookingA(Account account, TourPackage tourPackage, List<BookingInclusion> inclusions) {
	    Booking booking = new Booking();
	    booking.setAccount(account);
	    booking.setTourPackage(tourPackage);
	    booking.setInclusions(inclusions);
	    booking.setVisitDate(LocalDate.now().plusDays(3));
	    booking.setVisitTime(LocalTime.of(9, 30));
	    booking.setGroupSize(4);
	    booking.setStatus(BookingStatus.PENDING);
	    
	    int inclusionsPrice = 0;
	    for (BookingInclusion inclusion : inclusions) {
	    	inclusionsPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
	    }
	    
	    booking.setTotalPrice(tourPackage.getBasePrice() * booking.getGroupSize() + inclusionsPrice);

	    return booking;
	}
	
	public static Booking createBookingB(Account account, TourPackage tourPackage, List<BookingInclusion> inclusions) {
	    Booking booking = new Booking();
	    booking.setAccount(account);
	    booking.setTourPackage(tourPackage);
	    booking.setInclusions(inclusions);
	    booking.setVisitDate(LocalDate.now().plusDays(5));
	    booking.setVisitTime(LocalTime.of(10, 30));
	    booking.setGroupSize(4);
	    booking.setStatus(BookingStatus.PENDING);
	    
	    int inclusionsPrice = 0;
	    for (BookingInclusion inclusion : inclusions) {
	    	inclusionsPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
	    }
	    
	    booking.setTotalPrice(tourPackage.getBasePrice() * booking.getGroupSize() + inclusionsPrice);

	    return booking;
	}
	
	public static Feedback createFeedbackA(Account account, Booking booking, FeedbackCategory category) {
		 Feedback feedback = new Feedback();
		 feedback.setAccount(account);      
		 feedback.setBooking(booking); 
		 feedback.setCategory(category); 
		 feedback.setStars(4.5);           
		 feedback.setComment("Great experience!"); 
		 feedback.setSuggestion("Keep the place clean.");
		 
		 return feedback;
	}
}

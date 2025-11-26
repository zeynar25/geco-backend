package com.example.geco;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.domains.Faq;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.BookingInclusionRequest;
import com.example.geco.dto.BookingRequest;
import com.example.geco.dto.FeedbackCategoryRequest;
import com.example.geco.dto.SignupRequest;
import com.example.geco.dto.TourPackageRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;
import com.example.geco.repositories.PackageInclusionRepository;
import com.example.geco.repositories.TourPackageRepository;

public class DataUtil {
	
	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public static SignupRequest createSignupRequestA() {
		return SignupRequest.builder()
				.email("krysscoleen.creus@cvsu.edu.ph")
				.password("Hello12345678")
				.confirmPassword("Hello12345678")
				.build();
	}
	
	public static Account createUserAccountA(AccountRepository accountRepository) {
		String hashedPassword = passwordEncoder.encode("Hello12345678");
		
		Account account = Account.builder()
				.role(Role.USER)
				.password(hashedPassword)
				.detail(
						UserDetail.builder()
						.email("krysscoleen.creus@cvsu.edu.ph")
						.build())
				.build();
		
		return accountRepository.save(account);
	}
	
	public static Account createAdminAccountA(AccountRepository accountRepository) {
		String hashedPassword = passwordEncoder.encode("Hello12345678");
		
		Account account = Account.builder()
				.role(Role.ADMIN)
				.password(hashedPassword)
				.detail(
						UserDetail.builder()
						.email("krysscoleen.creus@cvsu.edu.ph")
						.build())
				.build();
		
		return accountRepository.save(account);
	}

	public static SignupRequest createSignupRequestB() {
		return SignupRequest.builder()
				.email("daniellalyn.soliman@cvsu.edu.ph")
				.password("Hi12345678")
				.confirmPassword("Hi12345678")
				.build();
	}
	
	public static Account createUserAccountB(AccountRepository accountRepository) {
		String hashedPassword = passwordEncoder.encode("Hi12345678");
		
		Account account = Account.builder()
				.role(Role.USER)
				.password(hashedPassword)
				.detail(
						UserDetail.builder()
						.email("daniellalyn.soliman@cvsu.edu.ph")
						.build())
				.build();
		
		return accountRepository.save(account);
	}
	
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

	public static PackageInclusion createPackageInclusionD() {
		return PackageInclusion.builder()
				.inclusionName("Noon Tour")
				.inclusionPricePerPerson(120)
				.build();
	}

	public static PackageInclusion createPackageInclusionE() {
		return PackageInclusion.builder()
				.inclusionName("Afternoon Tour")
				.inclusionPricePerPerson(140)
				.build();
	}

	public static PackageInclusion createPackageInclusionF() {
		return PackageInclusion.builder()
				.inclusionName("Skydiving")
				.inclusionPricePerPerson(300)
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
	
	public static TourPackage createTourPackageA(
			PackageInclusionRepository packageInclusionRepository,
			TourPackageRepository tourPackageRepository) {
		PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
		packageInclusionRepository.save(inclusionA);
		
		List<PackageInclusion> inclusions = new ArrayList<>();
		inclusions.add(inclusionA);
		
		return tourPackageRepository.save(
				TourPackage.builder()
					.name("The best")
					.description("Detailed description about this package")
					.duration(60)
					.basePrice(500)
					.inclusions(inclusions)
					.build()
		);
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

	
	public static TourPackage createTourPackageB(
			PackageInclusionRepository packageInclusionRepository,
			TourPackageRepository tourPackageRepository) {
		PackageInclusion inclusionA = DataUtil.createPackageInclusionB();
		packageInclusionRepository.save(inclusionA);
		
		PackageInclusion inclusionB = DataUtil.createPackageInclusionC();
		packageInclusionRepository.save(inclusionB);
		
		List<PackageInclusion> inclusions = new ArrayList<>();
		inclusions.add(inclusionA);
		inclusions.add(inclusionB);
		
		return tourPackageRepository.save(
				TourPackage.builder()
					.name("The double best")
					.description("Detailed description about this package")
					.duration(120)
					.basePrice(100)
					.inclusions(inclusions)
					.build()
		);
	}
	

	public static BookingInclusionRequest createBookingInclusionRequestA( 
			PackageInclusionRepository packageInclusionRepository) {
		PackageInclusion inclusion = DataUtil.createPackageInclusionD();
		PackageInclusion savedInclusion = packageInclusionRepository.save(inclusion);
		
		return BookingInclusionRequest.builder()
				.inclusionId(savedInclusion.getInclusionId())
				.quantity(2)
				.build();
	}

	public static BookingInclusion createBookingInclusionA( 
			Booking booking,
			PackageInclusionRepository packageInclusionRepository) {
		PackageInclusion inclusionA = DataUtil.createPackageInclusionD();
		PackageInclusion savedInclusionA = packageInclusionRepository.save(inclusionA);
		
		return BookingInclusion.builder()
				.booking(booking)
				.inclusion(savedInclusionA)
				.quantity(2)
				.priceAtBooking(savedInclusionA.getInclusionPricePerPerson())
				.build();
	}
	
	public static BookingInclusionRequest createBookingInclusionRequestB( 
			PackageInclusionRepository packageInclusionRepository) {
		PackageInclusion inclusion = DataUtil.createPackageInclusionE();
		PackageInclusion savedInclusion = packageInclusionRepository.save(inclusion);
		
		return BookingInclusionRequest.builder()
				.inclusionId(savedInclusion.getInclusionId())
				.quantity(1)
				.build();
	}
	
	public static BookingInclusionRequest createBookingInclusionRequestC( 
			PackageInclusionRepository packageInclusionRepository) {
		PackageInclusion inclusion = DataUtil.createPackageInclusionF();
		PackageInclusion savedInclusion = packageInclusionRepository.save(inclusion);
		
		return BookingInclusionRequest.builder()
				.inclusionId(savedInclusion.getInclusionId())
				.quantity(3)
				.build();
	}
	
	public static BookingRequest createBookingRequestA(
			Integer id,
			AccountRepository accountRepository,
			PackageInclusionRepository packageInclusionRepository,
			TourPackageRepository tourPackageRepository
	) {
		TourPackage tourPackageA = createTourPackageA(packageInclusionRepository, tourPackageRepository);

	    BookingRequest request = BookingRequest.builder()
	    		.accountId(id)
	            .tourPackageId(tourPackageA.getPackageId())
	            .visitDate(LocalDate.now().plusDays(3))
	            .visitTime(LocalTime.of(9, 30))
	            .groupSize(4)
	            .build(); 
	    
	    List<BookingInclusionRequest> bookingInclusionRequests = new ArrayList<>();
	    bookingInclusionRequests.add(createBookingInclusionRequestA(packageInclusionRepository));
	    
	    request.setBookingInclusionRequests(bookingInclusionRequests);
	    
	    return request;
	}
	
	public static Booking createBookingA(
			Integer id,
			AccountRepository accountRepository,
			PackageInclusionRepository packageInclusionRepository,
			TourPackageRepository tourPackageRepository
	) {
		Account accountA = createUserAccountA(accountRepository);
		TourPackage tourPackageA = createTourPackageA(packageInclusionRepository, tourPackageRepository);

	    Booking booking = Booking.builder()
	    		.account(accountA)
	            .tourPackage(tourPackageA)
	            .visitDate(LocalDate.now().plusDays(3))
	            .visitTime(LocalTime.of(9, 30))
	            .groupSize(2)
	            .bookingStatus(Booking.BookingStatus.PENDING)
	            .paymentStatus(Booking.PaymentStatus.UNPAID)
	            .build(); 
		
	    List<BookingInclusion> inclusions = new ArrayList<>();
	    inclusions.add(createBookingInclusionA(booking, packageInclusionRepository));
	    
	    booking.setBookingInclusions(inclusions);
	    
	    return booking;
	}
	
	public static BookingRequest createBookingRequestB(
			Integer id,
			AccountRepository accountRepository,
			PackageInclusionRepository packageInclusionRepository,
			TourPackageRepository tourPackageRepository
	) {
		TourPackage tourPackageA = createTourPackageB(packageInclusionRepository, tourPackageRepository);

	    BookingRequest request = BookingRequest.builder()
	    		.accountId(id)
	            .tourPackageId(tourPackageA.getPackageId())
	            .visitDate(LocalDate.now().plusDays(5))
	            .visitTime(LocalTime.of(9, 30))
	            .groupSize(4)
	            .build(); 
	    
	    List<BookingInclusionRequest> bookingInclusionRequests = new ArrayList<>();
	    bookingInclusionRequests.add(createBookingInclusionRequestB(packageInclusionRepository));
	    bookingInclusionRequests.add(createBookingInclusionRequestC(packageInclusionRepository));
	    
	    request.setBookingInclusionRequests(bookingInclusionRequests);
	    
	    return request;
	}
	

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

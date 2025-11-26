package com.example.geco.domains;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="booking")
public class Booking {
	public enum BookingStatus {
	    PENDING, APPROVED, CANCELLED, REJECTED, COMPLETED;
		
		public boolean canTransitionTo(BookingStatus newStatus) {
	        return switch (this) {
	            case PENDING -> newStatus == APPROVED || newStatus == CANCELLED || newStatus == REJECTED;
	            case APPROVED -> newStatus == COMPLETED || newStatus == CANCELLED;
	            case COMPLETED -> false; // cannot move backwards
	            case CANCELLED, REJECTED -> false; // cannot move backwards
	        };
	    }
	}
	
	public enum PaymentStatus {
	    UNPAID, PENDING_VERIFICATION, VERIFIED, REJECTED, REFUNDED;

	    public boolean canTransitionTo(PaymentStatus newStatus) {
	        return switch (this) {
	            case UNPAID -> newStatus == PENDING_VERIFICATION || newStatus == REJECTED;
	            case PENDING_VERIFICATION -> newStatus == VERIFIED || newStatus == REJECTED;
	            case VERIFIED -> newStatus == REFUNDED; // can refund but not go back
	            case REJECTED, REFUNDED -> false;
	        };
	    }              
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer bookingId;
	
	@ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;
	
	@ManyToOne
	@JoinColumn(name = "package_id", referencedColumnName = "packageId")
	private TourPackage tourPackage;
	
	// Extra services availed.
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@Builder.Default
    private List<BookingInclusion> bookingInclusions = new ArrayList<>();
	
//	@ManyToOne
//	@JoinColumn(name = "discountId", referencedColumnName = "discountId")
//	private Discount discount;
	
	private LocalDate visitDate;
	private LocalTime visitTime;
	
	private Integer groupSize;
	
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	
	private Integer totalPrice;
	
	@Builder.Default
	boolean isActive = true;
	
	@Lob
	private byte[] proofOfPayment;
}

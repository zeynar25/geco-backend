package com.example.geco.domains;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
	    PENDING, CANCELLED, APPROVED, REJECTED, COMPLETED;
	}
	
	public enum PaymentStatus {
	    UNPAID, PAYMENT_VERIFICATION, VERIFIED, REJECTED, REFUNDED;  
	}
	
	public enum PaymentMethod {
	    ONLINE, PARK;        
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
	private PaymentMethod paymentMethod;
	
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	
	private Double totalPrice;
	
	@Builder.Default
	boolean isActive = true;
	
	private String proofOfPaymentPhoto;
	
	private String staffReply;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
	    LocalDateTime now = LocalDateTime.now();
	    this.createdAt = now;
	    this.updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
	    this.updatedAt = LocalDateTime.now();
	}
}

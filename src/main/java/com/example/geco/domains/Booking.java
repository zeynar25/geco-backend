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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="booking")
public class Booking {
	public enum BookingStatus {
	    PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED
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
    private List<BookingInclusion> inclusions = new ArrayList<>();
	
//	@ManyToOne
//	@JoinColumn(name = "discountId", referencedColumnName = "discountId")
//	private Discount discount;
	
	private LocalDate visitDate;
	private LocalTime visitTime;
	
	private Integer groupSize;
	
	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	
	private Integer totalPrice;
}

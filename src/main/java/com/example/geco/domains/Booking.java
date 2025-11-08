package com.example.geco.domains;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int bookingId;
	
	@ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Account account;
	
	@ManyToOne
	@JoinColumn(name = "packageId", referencedColumnName = "packageId")
	private TourPackage tourPackage;
	
//	@ManyToOne
//	@JoinColumn(name = "discountId", referencedColumnName = "discountId")
//	private Discount discount;
	
	private LocalDate visitDate;
	private LocalTime visitTime;
	
	private int groupSize;
	private String status;
	private int totalPrice;
}

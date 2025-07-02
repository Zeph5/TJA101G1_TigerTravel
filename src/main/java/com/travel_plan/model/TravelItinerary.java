package com.travel_plan.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class TravelItinerary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "travel_itinerary_id", nullable = false, unique = true)
	private Integer travelItineraryId;
	
	@ManyToOne
	@JoinColumn(name = "travel_plan_id", nullable = false)
	private TravelPlan travelPlan;
	
	@Column(name = "max_tourist", nullable = false)
	private Integer maxTourist;
	
	@Column(name = "total_price", precision = 10, scale = 2)
	private BigDecimal totalPrice;
	
	@Column(name = "start_date", nullable = false)	
	private LocalDate startDate;
	
	@Column(name = "end_date", nullable = false)	
	private LocalDate endDate;
	
	@Column(name = "published_date", nullable = false, updatable = false)
	private LocalDateTime publishedDate;
	
	@Column(name = "last_modified_date", nullable = false)
	private LocalDateTime lastModifiedDate;

	public Integer getTravelItineraryId() {
		return travelItineraryId;
	}

	public void setTravelItineraryId(Integer travelItineraryId) {
		this.travelItineraryId = travelItineraryId;
	}

	public TravelPlan getTravelPlan() {
		return travelPlan;
	}

	public void setTravelPlan(TravelPlan travelPlan) {
		this.travelPlan = travelPlan;
	}

	public Integer getMaxTourist() {
		return maxTourist;
	}

	public void setMaxTourist(Integer maxTourist) {
		this.maxTourist = maxTourist;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public LocalDateTime getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(LocalDateTime publishedDate) {
		this.publishedDate = publishedDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public String toString() {
		return "TravelItinerary [travelItineraryId=" + travelItineraryId + ", travelPlan=" + travelPlan
				+ ", maxTourist=" + maxTourist + ", totalPrice=" + totalPrice + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", publishedDate=" + publishedDate + ", lastModifiedDate=" + lastModifiedDate
				+ "]";
	}
	
	
}

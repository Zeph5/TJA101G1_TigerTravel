	package com.travel_plan.model;
	
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
	public class TravelPlanDay {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "travel_plan_day_id", nullable = false, unique = true)
		private Integer travelPlanDayId;
		@ManyToOne
		@JoinColumn(name = "travel_itinerary_id", nullable = false) 
		private TravelItinerary travelItinerary; 
		
		@Column(name = "scenery_id", nullable = false)
		private Integer sceneryId;  //景點??????????
		
		@Column(name = "travel_day_num", nullable = false)
		private Integer travelDayNumber;
		
		@Column(name = "travel_seq_num", nullable = false)
		private Integer travelSequenceNumber;
		
		@Column(name = "create_time", nullable = false, updatable = false)
		private LocalDateTime createtime;
		
		@Column(name = "travel_time", nullable = false)
		private LocalDate traveltime;  //旅行日期?
		
		public Integer getTravelPlanDayId() {
			return travelPlanDayId;
		}
		public void setTravelPlanDayId(Integer travelPlanDayId) {
			this.travelPlanDayId = travelPlanDayId;
		}
		public TravelItinerary getTravelItinerary() {
			return travelItinerary;
		}
		public void setTravelItinerary(TravelItinerary travelItinerary) {
			this.travelItinerary = travelItinerary;
		}
		public Integer getSceneryId() {
			return sceneryId;
		}
		public void setSceneryId(Integer sceneryId) {
			this.sceneryId = sceneryId;
		}
		public Integer getTravelDayNumber() {
			return travelDayNumber;
		}
		public void setTravelDayNumber(Integer travelDayNumber) {
			this.travelDayNumber = travelDayNumber;
		}
		public Integer getTravelSequenceNumber() {
			return travelSequenceNumber;
		}
		public void setTravelSequenceNumber(Integer travelSequenceNumber) {
			this.travelSequenceNumber = travelSequenceNumber;
		}
		public LocalDateTime getCreatetime() {
			return createtime;
		}
		public void setCreatetime(LocalDateTime createtime) {
			this.createtime = createtime;
		}
		public LocalDate getTraveltime() {
			return traveltime;
		}
		public void setTraveltime(LocalDate traveltime) {
			this.traveltime = traveltime;
		}
		@Override
		public String toString() {
			return "TravelPlanDay [travelPlanDayId=" + travelPlanDayId + ", travelItinerary=" + travelItinerary
					+ ", sceneryId=" + sceneryId + ", travelDayNumber=" + travelDayNumber + ", travelSequenceNumber="
					+ travelSequenceNumber + ", createtime=" + createtime + ", traveltime=" + traveltime + "]";
		}
		
		
	}

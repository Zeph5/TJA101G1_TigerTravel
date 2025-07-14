	package com.travel_plan.model;
	
	import java.time.LocalDate;
	import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.scenery.model.SceneryVO;

import jakarta.persistence.Column;
	import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.JoinColumn;
	import jakarta.persistence.ManyToOne;
	
	@Entity
	@EntityListeners(AuditingEntityListener.class)
	public class TravelPlanDay {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "travel_plan_day_id", nullable = false, unique = true)
		private Integer travelPlanDayId;
		@ManyToOne(fetch = FetchType.EAGER) 
		@JoinColumn(name = "travel_itinerary_id", referencedColumnName = "travel_itinerary_id", nullable = false)
		private TravelItinerary travelItinerary;
		
		@ManyToOne(fetch = FetchType.LAZY) // 或 EAGER
		@JoinColumn(name = "travel_plan_id", nullable = false)
		private TravelPlan travelPlan; // 確保有這個屬性
		
		
		@ManyToOne(fetch = FetchType.EAGER) 
		@JoinColumn(name = "scenery_id", referencedColumnName = "scenery_id", nullable = false) // 確保 nullable=false
		private SceneryVO scenery;
		
		
		@Column(name = "travel_day_num", nullable = false)
		private Integer travelDayNumber;
		
		@Column(name = "travel_seq_num", nullable = false)
		private Integer travelSequenceNumber;
		
		@Column(name = "create_time", nullable = false, updatable = false)
		@CreatedDate
		private LocalDateTime createtime;
		
		@Column(name = "travel_time", nullable = false)
		private LocalDate traveltime;  //旅行日期?
		
		
		
		public TravelPlan getTravelPlan() {
			return travelPlan;
		}
		public void setTravelPlan(TravelPlan travelPlan) {
			this.travelPlan = travelPlan;
		}
		public SceneryVO getScenery() {
			return scenery;
		}
		public void setScenery(SceneryVO scenery) {
			this.scenery = scenery;
		}
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
					+ ", scenery=" + scenery + ", travelDayNumber=" + travelDayNumber + ", travelSequenceNumber="
					+ travelSequenceNumber + ", createtime=" + createtime + ", traveltime=" + traveltime + "]";
		}
		
		
		
	}

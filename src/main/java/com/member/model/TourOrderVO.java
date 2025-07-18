package com.member.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import com.travel_plan.model.TravelItinerary;

import jakarta.persistence.*;

@Entity
@Table(name = "tour_order")
public class TourOrderVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_order_id")
    private Integer tourOrderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private memVO member;

    @Column(name = "travel_itinerary_id")
    private Integer travelItineraryId;

    @Column(name = "tour_price")
    private Integer tourPrice;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "total_after_coupon")
    private Integer totalAfterCoupon;

    @Column(name = "tour_order_status")
    private String tourOrderStatus;
    
    @Column(name = "people_count") // or 根據你的欄位名稱
    private Integer peopleCount;

    @Column(name = "create_time", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createTime;
    
    
    @Column(name = "card_last_four")
    private String cardLastFour;

    @Column(name = "card_expiry_date")
    private String cardExpiryDate;

    // ✅ 關聯到 TravelItinerary
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_itinerary_id", insertable = false, updatable = false)
    private TravelItinerary travelItinerary;
    
    @OneToMany(mappedBy = "tourOrder", cascade = CascadeType.ALL)
    private Set<TouristVO> tourists = new HashSet<>();
    
    public TourOrderVO() {};
    
    public TourOrderVO(Integer tourOrderId, memVO member, Integer travelItineraryId, Integer tourPrice,
			Integer totalAmount, Integer totalAfterCoupon, String tourOrderStatus, LocalDateTime createTime,
			TravelItinerary travelItinerary , Set<TouristVO> tourists,String cardExpiryDate,String cardLastFour) {
		super();
		this.tourOrderId = tourOrderId;
		this.member = member;
		this.travelItineraryId = travelItineraryId;
		this.tourPrice = tourPrice;
		this.totalAmount = totalAmount;
		this.totalAfterCoupon = totalAfterCoupon;
		this.tourOrderStatus = tourOrderStatus;
		this.createTime = createTime;
		this.travelItinerary = travelItinerary;
		this.tourists = tourists;
		this.cardExpiryDate = cardExpiryDate;
		this.cardLastFour = cardLastFour;
	}
    
    public String getCardLastFour() {
		return cardLastFour;
	}

	public void setCardLastFour(String cardLastFour) {
		this.cardLastFour = cardLastFour;
	}

	public String getCardExpiryDate() {
		return cardExpiryDate;
	}

	public void setCardExpiryDate(String cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}

	public Integer getPeopleCount() {
        return peopleCount;
    }
    

    public Set<TouristVO> getTourists() {
		return tourists;
	}

	public void setTourists(Set<TouristVO> tourists) {
		this.tourists = tourists;
	}

	public void setPeopleCount(Integer peopleCount) {
		this.peopleCount = peopleCount;
	}

	// ===== Getter / Setter =====
	public Integer getTourOrderId() {
        return tourOrderId;
    }

    public void setTourOrderId(Integer tourOrderId) {
        this.tourOrderId = tourOrderId;
    }


    public memVO getMember() {
		return member;
	}

	public void setMember(memVO member) {
		this.member = member;
	}

	public Integer getTravelItineraryId() {
        return travelItineraryId;
    }

    public void setTravelItineraryId(Integer travelItineraryId) {
        this.travelItineraryId = travelItineraryId;
    }

    public Integer getTourPrice() {
        return tourPrice;
    }

    public void setTourPrice(Integer tourPrice) {
        this.tourPrice = tourPrice;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalAfterCoupon() {
        return totalAfterCoupon;
    }

    public void setTotalAfterCoupon(Integer totalAfterCoupon) {
        this.totalAfterCoupon = totalAfterCoupon;
    }

    public String getTourOrderStatus() {
        return tourOrderStatus;
    }

    public void setTourOrderStatus(String tourOrderStatus) {
        this.tourOrderStatus = tourOrderStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public TravelItinerary getTravelItinerary() {
        return travelItinerary;
    }

    public void setTravelItinerary(TravelItinerary travelItinerary) {
        this.travelItinerary = travelItinerary;
    }
}
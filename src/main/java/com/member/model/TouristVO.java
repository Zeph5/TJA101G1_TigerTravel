package com.member.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tourist_id")
public class TouristVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourist_id", nullable = false, unique = true)
    private Integer touristId;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY 避免載太多訂單資料
    @JoinColumn(name = "tour_order_id", nullable = false)
    private TourOrderVO tourOrder; // 連訂單 VO

    @Column(name = "tourist_name", nullable = false, length = 50)
    private String touristName;

    @Column(name = "tourist_personal_id", nullable = false, length = 20)
    private String touristPersonalId;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "create_time", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createTime;
    
    @Column(name = "people_count")
    private Integer peopleCount;
    
    public TouristVO() {}
    
    public TouristVO(Integer touristId, TourOrderVO tourOrder, String touristName, String touristPersonalId,
			String contactNumber, Timestamp createTime) {
    	super();
		this.touristId = touristId;
		this.tourOrder = tourOrder;
		this.touristName = touristName;
		this.touristPersonalId = touristPersonalId;
		this.contactNumber = contactNumber;
		this.createTime = createTime;
	}



	public Integer getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(Integer peopleCount) {
		this.peopleCount = peopleCount;
	}

	public Integer getTouristId() {
        return touristId;
    }

    public void setTouristId(Integer touristId) {
        this.touristId = touristId;
    }

    public TourOrderVO getTourOrder() {
        return tourOrder;
    }

    public void setTourOrder(TourOrderVO tourOrder) {
        this.tourOrder = tourOrder;
    }

    public String getTouristName() {
        return touristName;
    }

    public void setTouristName(String touristName) {
        this.touristName = touristName;
    }

    public String getTouristPersonalId() {
        return touristPersonalId;
    }

    public void setTouristPersonalId(String touristPersonalId) {
        this.touristPersonalId = touristPersonalId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TouristVO [touristId=" + touristId + ", tourOrder=" + tourOrder + ", touristName=" + touristName
                + ", touristPersonalId=" + touristPersonalId + ", contactNumber=" + contactNumber + ", createTime="
                + createTime + "]";
    }
}
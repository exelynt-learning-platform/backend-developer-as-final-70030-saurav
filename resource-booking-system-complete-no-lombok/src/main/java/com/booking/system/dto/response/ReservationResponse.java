package com.booking.system.dto.response;

import com.booking.system.enums.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private Long resourceId;
    private String resourceName;
    private String userEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private BigDecimal price;

    public ReservationResponse(Long id, Long resourceId, String resourceName, String userEmail,
                               LocalDateTime startTime, LocalDateTime endTime,
                               ReservationStatus status, BigDecimal price) {
        this.id=id; this.resourceId=resourceId; this.resourceName=resourceName; this.userEmail=userEmail;
        this.startTime=startTime; this.endTime=endTime; this.status=status; this.price=price;
    }
    public Long getId(){return id;}
    public Long getResourceId(){return resourceId;}
    public String getResourceName(){return resourceName;}
    public String getUserEmail(){return userEmail;}
    public LocalDateTime getStartTime(){return startTime;}
    public LocalDateTime getEndTime(){return endTime;}
    public ReservationStatus getStatus(){return status;}
    public BigDecimal getPrice(){return price;}
}

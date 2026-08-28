package com.booking.system.controller;

import com.booking.system.dto.request.*;
import com.booking.system.dto.response.ReservationResponse;
import com.booking.system.enums.ReservationStatus;
import com.booking.system.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.create(request, authentication));
    }

    @GetMapping
    public Page<ReservationResponse> getAll(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Authentication authentication) {

        return reservationService.getReservations(status, minPrice, maxPrice,
                page, size, sortBy, direction, authentication);
    }

    @PutMapping("/{id}")
    public ReservationResponse update(@PathVariable Long id,
                                      @Valid @RequestBody ReservationUpdateRequest request,
                                      Authentication authentication) {
        return reservationService.update(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       Authentication authentication) {
        reservationService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }
}

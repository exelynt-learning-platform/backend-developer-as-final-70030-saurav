package com.booking.system.service;

import com.booking.system.dto.request.ReservationRequest;
import com.booking.system.dto.request.ReservationUpdateRequest;
import com.booking.system.dto.response.ReservationResponse;
import com.booking.system.entity.*;
import com.booking.system.enums.ReservationStatus;
import com.booking.system.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ResourceRepository resourceRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public ReservationResponse create(ReservationRequest request, Authentication authentication) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        User user = currentUser(authentication);
        Resource resource = findResource(request.getResourceId());

        if (!Boolean.TRUE.equals(resource.getAvailable())) {
            throw new RuntimeException("Resource is not available");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user); // USER comes from JWT
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPrice(resource.getPrice());

        return map(reservationRepository.save(reservation));
    }

    public Page<ReservationResponse> getReservations(
            ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice,
            int page, int size, String sortBy, String direction,
            Authentication authentication) {

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new RuntimeException("minPrice cannot be greater than maxPrice");
        }

        String property = isAllowedSort(sortBy) ? sortBy : "id";
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(property).descending()
                : Sort.by(property).ascending();

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);

        Specification<Reservation> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!admin) {
                predicates.add(cb.equal(root.get("user").get("email"), authentication.getName()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return reservationRepository.findAll(specification, pageable).map(this::map);
    }

    public ReservationResponse update(Long id, ReservationUpdateRequest request,
                                      Authentication authentication) {
        Reservation reservation = findReservation(id);
        checkOwnerOrAdmin(reservation, authentication);

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        Resource resource = findResource(request.getResourceId());
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(request.getStatus());
        reservation.setPrice(resource.getPrice());

        return map(reservationRepository.save(reservation));
    }

    public void delete(Long id, Authentication authentication) {
        Reservation reservation = findReservation(id);
        checkOwnerOrAdmin(reservation, authentication);
        reservationRepository.delete(reservation);
    }

    private boolean isAllowedSort(String value) {
        return value != null && List.of("id", "price", "status", "startTime", "endTime")
                .contains(value);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private Resource findResource(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
    }

    private void checkOwnerOrAdmin(Reservation reservation, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!admin && !reservation.getUser().getEmail().equals(authentication.getName())) {
            throw new RuntimeException("You are not allowed to access this reservation");
        }
    }

    private ReservationResponse map(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getResource().getId(),
                r.getResource().getName(),
                r.getUser().getEmail(),
                r.getStartTime(),
                r.getEndTime(),
                r.getStatus(),
                r.getPrice()
        );
    }
}

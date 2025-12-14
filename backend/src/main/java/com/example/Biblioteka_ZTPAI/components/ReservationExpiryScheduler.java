package com.example.Biblioteka_ZTPAI.components;

import com.example.Biblioteka_ZTPAI.models.Reservation;
import com.example.Biblioteka_ZTPAI.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReservationExpiryScheduler {

    private final ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 60000*10)
    public void expireReservations() {
        List<Reservation> expiredReservations = reservationRepository.findByEndDateIsNullAndExpirationDateBefore(LocalDateTime.now());
        for (Reservation reservation : expiredReservations) {
            reservation.setEndDate(LocalDateTime.now());
        }
        reservationRepository.saveAll(expiredReservations);
    }
}

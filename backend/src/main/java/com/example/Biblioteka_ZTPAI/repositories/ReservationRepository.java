package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.dto.checkout.ReservationUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT r FROM Reservation r WHERE r.bookCopy.id = :bookCopyId AND (r.endDate IS NULL OR r.endDate > CURRENT_TIMESTAMP)")
    Optional<Reservation> findActiveReservationByBookCopyId(@Param("bookCopyId") Integer bookCopyId);
    @Query("SELECT r FROM Reservation r WHERE r.bookCopy.id = :bookCopyId AND r.user.id = :userId AND r.endDate IS NULL")
    Optional<Reservation> findActiveReservationByBookCopyAndUser(@Param("bookCopyId") Integer bookCopyId,
                                                                 @Param("userId") Integer userId);

    @Query("""
    SELECT new com.example.Biblioteka_ZTPAI.dto.checkout.ReservationUserBookDTO(
        r.id, r.startDate, r.endDate,
        u.username,
        b.title
    )
    FROM Reservation r
    JOIN r.user u
    JOIN r.bookCopy bc
    JOIN bc.book b
    WHERE r.endDate IS NULL
    """)
    List<ReservationUserBookDTO> findActiveReservationsWithUserAndBook();

    @Query("SELECT r FROM Reservation r WHERE r.bookCopy.id IN :copyIds AND (r.endDate IS NULL OR r.endDate > CURRENT_TIMESTAMP)")
    List<Reservation> findActiveReservationsByBookCopyIds(@Param("copyIds") List<Integer> copyIds);

}

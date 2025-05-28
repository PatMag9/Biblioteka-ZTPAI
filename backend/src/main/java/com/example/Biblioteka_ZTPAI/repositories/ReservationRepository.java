package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.dto.ReservationUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.Reservation;
import com.example.Biblioteka_ZTPAI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT r FROM Reservation r WHERE r.bookCopy.id_book_copy = :bookCopyId AND (r.endDate IS NULL OR r.endDate > CURRENT_TIMESTAMP)")
    Optional<Reservation> findActiveReservationByBookCopyId(@Param("bookCopyId") Integer bookCopyId);
    @Query("SELECT r FROM Reservation r WHERE r.bookCopy.id_book_copy = :bookCopyId AND r.user.id_user = :userId AND r.endDate IS NULL")
    Optional<Reservation> findActiveReservationByBookCopyAndUser(@Param("bookCopyId") Integer bookCopyId,
                                                                 @Param("userId") Integer userId);

    @Query("""
    SELECT new com.example.Biblioteka_ZTPAI.dto.ReservationUserBookDTO(
        r.id_reservation, r.start_date, r.endDate,
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
}

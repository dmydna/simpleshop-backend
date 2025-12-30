package com.techlab.store.repository;

import com.techlab.store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // TODO ver DT0s.

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.details od " +
            "JOIN FETCH od.product p")
    List<Order> findAllWithDetailsAndClient();

    List<Order> findByClientId(Long clientId);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.details od " +
            "JOIN FETCH od.product p " +
            "WHERE c.id = :clientId")
    List<Order> findAllWithDetailsAndClientById(@Param("clientId") Long clientId);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.details od " +
            "JOIN FETCH od.product p " +
            "WHERE o.id = :orderId")
    Optional<Order> findOneWithDetailsAndClientById(@Param("orderId") Long orderId);


}

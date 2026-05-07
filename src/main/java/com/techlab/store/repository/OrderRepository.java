package com.techlab.store.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.techlab.store.enums.OrderStatus;

import com.techlab.store.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> ,JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.items od " +
            "JOIN FETCH od.listing p")
    List<Order> findAllWithDetailsAndClient();

    List<Order> findByClientId(Long clientId);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.items od " +
            "JOIN FETCH od.listing p " +
            "WHERE c.id = :clientId")
    List<Order> findAllWithDetailsAndClientById(@Param("clientId") Long clientId);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.items od " +
            "JOIN FETCH od.listing p " +
            "WHERE o.id = :orderId")
    Optional<Order> findOneWithDetailsAndClientById(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.client c " +
            "JOIN FETCH o.items od " +
            "JOIN FETCH od.listing p " +
            "WHERE c.firstName = :name")
    List<Order> findAllByFirstName(@Param("name") String name);

    List<Order> findAllByStatus(OrderStatus status);

    List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAt);
}

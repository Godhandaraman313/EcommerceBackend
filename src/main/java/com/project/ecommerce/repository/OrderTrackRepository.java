package com.project.ecommerce.repository;

import com.project.ecommerce.model.OrderTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderTrackRepository extends JpaRepository<OrderTrack, Long> {

    List<OrderTrack> findByOrder_IdOrderByCreatedAtAsc(Long orderId);
}

package com.devbooks.repository;

import com.devbooks.cart.CartItem; // Import file bạn đã có
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
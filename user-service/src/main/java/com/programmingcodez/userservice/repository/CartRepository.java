package com.programmingcodez.userservice.repository;

import com.programmingcodez.userservice.entity.Cart;
import com.programmingcodez.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
}

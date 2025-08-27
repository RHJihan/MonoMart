package com.monomart.repository;

import com.monomart.entities.User;
import com.monomart.entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // Find all addresses of a specific user
    List<UserAddress> findByUser(User user);
}
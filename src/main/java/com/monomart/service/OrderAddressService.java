package com.monomart.service;

import com.monomart.entities.OrderAddress;
import com.monomart.entities.UserAddress;
import com.monomart.repository.OrderAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderAddressService {

    private final OrderAddressRepository orderAddressRepository;

    public OrderAddressService(OrderAddressRepository orderAddressRepository) {
        this.orderAddressRepository = orderAddressRepository;
    }

    /**
     * Creates a snapshot copy of a user address for order storage.
     * This ensures the order retains the address even if the user modifies or
     * deletes their saved address.
     * 
     * @param source The UserAddress to copy from
     * @return The saved OrderAddress snapshot
     */
    @Transactional
    public OrderAddress createFromUserAddress(UserAddress source) {
        OrderAddress snapshot = new OrderAddress();
        snapshot.setAddressType(source.getAddressType());
        snapshot.setAddressLine1(source.getAddressLine1());
        snapshot.setAddressLine2(source.getAddressLine2());
        snapshot.setUpazila(source.getUpazila());
        snapshot.setCity(source.getCity());
        snapshot.setCountry(source.getCountry());
        snapshot.setPhone(source.getPhone());
        snapshot.setEmail(source.getEmail());

        return orderAddressRepository.save(snapshot);
    }
}

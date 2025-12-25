package com.monomart.service;

import com.monomart.dto.user.UserAddressDtos;
import com.monomart.entities.User;
import com.monomart.entities.UserAddress;
import com.monomart.repository.UserAddressRepository;
import com.monomart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressService {

    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;

    public UserAddressService(UserRepository userRepository, UserAddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    // ----------------- VERIFICATION -----------------
    /**
     * Verifies that the given address belongs to the specified user.
     * 
     * @param addressId The ID of the address to verify
     * @param userId    The ID of the user who should own the address
     * @return The UserAddress if ownership is verified
     * @throws RuntimeException if address not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public UserAddress verifyAddressOwnership(Long addressId, Long userId) {
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address does not belong to the current user");
        }

        return address;
    }

    // ----------------- CREATE -----------------
    @Transactional
    public UserAddress addOrUpdateAddress(Long userId, UserAddressDtos.CreateRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setAddressType(dto.getAddressType());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setUpazila(dto.getUpazila());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setEmail(dto.getEmail());

        return addressRepository.save(address);
    }

    // ----------------- UPDATE -----------------
    @Transactional
    public UserAddress addOrUpdateAddress(Long userId, UserAddressDtos.UpdateRequest dto) {
        if (dto.getId() == null || dto.getId() <= 0)
            throw new IllegalArgumentException("Invalid address id for update");

        UserAddress address = addressRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId))
            throw new RuntimeException("Cannot modify address of another user");

        address.setAddressType(dto.getAddressType());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setUpazila(dto.getUpazila());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setEmail(dto.getEmail());

        return addressRepository.save(address);
    }

    // ----------------- READ -----------------
    @Transactional(readOnly = true)
    public List<UserAddress> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    // ----------------- DELETE -----------------
    @Transactional
    public void deleteAddress(Long addressId, User user) {
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot delete address of another user");
        }

        addressRepository.delete(address);
    }

    // Admin-specific deletion (bypasses ownership check)
    @Transactional
    public void deleteAddressAsAdmin(Long addressId) {
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
    }
}

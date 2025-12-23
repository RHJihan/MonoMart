package com.monomart.controller;

import com.monomart.dto.user.UserAddressDtos;
import com.monomart.entities.User;
import com.monomart.entities.UserAddress;
import com.monomart.mapper.Mappers;
import com.monomart.service.UserAddressService;
import com.monomart.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.monomart.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/v1/addresses")
public class UserAddressController {

    private final UserAddressService addressService;
    private final UserService userService;
    private final Mappers mappers;

    public UserAddressController(UserAddressService addressService, UserService userService, Mappers mappers) {
        this.addressService = addressService;
        this.userService = userService;
        this.mappers = mappers;
    }

    // ------------------ HELPER ------------------
    private Long currentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser au) {
            return au.getUserId();
        }
        throw new IllegalStateException("Invalid authentication principal");
    }

    private User currentUser(Authentication auth) {
        Long userId = currentUserId(auth);
        return userService.findById(userId);
    }

    // ------------------ USER OPERATIONS ------------------

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserAddressDtos.Response>> getMyAddresses(Authentication auth) {
        User user = currentUser(auth);
        List<UserAddressDtos.Response> addresses = addressService.getAddressesByUser(user)
                .stream()
                .map(mappers::toUserAddressResponse)
                .toList();
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/me")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAddressDtos.Response> addMyAddress(Authentication auth,
                                                                 @Valid @RequestBody UserAddressDtos.CreateRequest dto) {
        User user = currentUser(auth);
        UserAddress address = addressService.addOrUpdateAddress(user.getId(), dto);
        return ResponseEntity.ok(mappers.toUserAddressResponse(address));
    }

    @PutMapping("/me/{addressId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAddressDtos.Response> updateMyAddress(Authentication auth,
                                                                    @PathVariable Long addressId,
                                                                    @Valid @RequestBody UserAddressDtos.UpdateRequest dto) {
        User user = currentUser(auth);
        dto.setId(addressId);
        UserAddress address = addressService.addOrUpdateAddress(user.getId(), dto);
        return ResponseEntity.ok(mappers.toUserAddressResponse(address));
    }

    @DeleteMapping("/me/{addressId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMyAddress(Authentication auth,
                                                @PathVariable Long addressId) {
        User user = currentUser(auth);
        addressService.deleteAddress(addressId, user);
        return ResponseEntity.noContent().build();
    }

    // ------------------ ADMIN OPERATIONS ------------------

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserAddressDtos.Response>> getAddressesByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<UserAddressDtos.Response> addresses = addressService.getAddressesByUser(user)
                .stream()
                .map(mappers::toUserAddressResponse)
                .toList();
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAddressDtos.Response> addAddressForUser(@PathVariable Long userId,
                                                                      @Valid @RequestBody UserAddressDtos.CreateRequest dto) {
        UserAddress address = addressService.addOrUpdateAddress(userId, dto);
        return ResponseEntity.ok(mappers.toUserAddressResponse(address));
    }

    @PutMapping("/users/{userId}/{addressId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAddressDtos.Response> updateAddressForUser(@PathVariable Long userId,
                                                                         @PathVariable Long addressId,
                                                                         @Valid @RequestBody UserAddressDtos.UpdateRequest dto) {
        dto.setId(addressId);
        UserAddress address = addressService.addOrUpdateAddress(userId, dto);
        return ResponseEntity.ok(mappers.toUserAddressResponse(address));
    }

    @DeleteMapping("/users/{userId}/{addressId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAddressForUser(@PathVariable Long userId,
                                                     @PathVariable Long addressId) {
        User user = userService.findById(userId);
        addressService.deleteAddress(addressId, user);
        return ResponseEntity.noContent().build();
    }
}

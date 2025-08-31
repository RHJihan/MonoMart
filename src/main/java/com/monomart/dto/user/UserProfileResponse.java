package com.monomart.dto.user;

import com.monomart.dto.cart.CartDtos;
import com.monomart.dto.order.OrderDtos;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
@Builder
public class UserProfileResponse {
    Long id;
    String email;
    String phone;

    List<UserAddressDtos.Response> addresses;
    Page<CartDtos.CartItemResponse> cartItems;
    Page<OrderDtos.OrderResponse> orders;
}

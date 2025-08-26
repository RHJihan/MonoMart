package com.monomart.mapper;

import com.monomart.entities.Category;
import com.monomart.entities.Product;
import com.monomart.entities.CartItem;
import com.monomart.dto.category.CategoryDtos;
import com.monomart.dto.product.ProductDtos;
import com.monomart.dto.cart.CartDtos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mappers {

    Category toCategory(CategoryDtos.CreateCategoryRequest request);
    void updateCategoryFromDto(CategoryDtos.UpdateCategoryRequest request, @org.mapstruct.MappingTarget Category category);

    @Mapping(target = "id", source = "id")
    CategoryDtos.CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "categoryId", source = "category.id")
    ProductDtos.ProductResponse toProductResponse(Product product);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())))")
    CartDtos.CartItemResponse toCartItemResponse(CartItem cartItem);
}



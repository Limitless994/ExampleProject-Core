package com.examplecode.core.mappers;

import com.examplecode.core.dtos.ProductDto;
import com.examplecode.core.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    public static ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    ProductDto toDto(Product product);
    Product toEntity(ProductDto productDto);
}
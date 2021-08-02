package one.digitalinnovation.mynaturastock.mapper;

import one.digitalinnovation.mynaturastock.dto.ProductDTO;
import one.digitalinnovation.mynaturastock.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toModel(ProductDTO productDTO);

    ProductDTO toDTO(Product product);
}

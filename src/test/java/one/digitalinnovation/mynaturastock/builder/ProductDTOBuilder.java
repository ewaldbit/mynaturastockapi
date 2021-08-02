package one.digitalinnovation.mynaturastock.builder;

import lombok.Builder;
import one.digitalinnovation.mynaturastock.dto.ProductDTO;
import one.digitalinnovation.mynaturastock.enums.Category;

@Builder
public class ProductDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Homem";

    @Builder.Default
    private int max = 2;

    @Builder.Default
    private int quantity = 12;

    @Builder.Default
    private Category type = Category.PERFUMARIA;

    public ProductDTO toProductDTO() {
        return new ProductDTO(id,
                name,
                max,
                quantity,
                type);
    }
}

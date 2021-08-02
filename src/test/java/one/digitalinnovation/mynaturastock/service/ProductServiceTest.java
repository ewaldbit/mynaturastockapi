package one.digitalinnovation.mynaturastock.service;

import one.digitalinnovation.mynaturastock.builder.ProductDTOBuilder;
import one.digitalinnovation.mynaturastock.dto.ProductDTO;
import one.digitalinnovation.mynaturastock.entity.Product;
import one.digitalinnovation.mynaturastock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.mynaturastock.exception.ProductNotFoundException;
import one.digitalinnovation.mynaturastock.exception.ProductStockExceededException;
import one.digitalinnovation.mynaturastock.mapper.ProductMapper;
import one.digitalinnovation.mynaturastock.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private ProductRepository productRepository;

    private ProductMapper productMapper = ProductMapper.INSTANCE;

    @InjectMocks
    private ProductService productService;

    @Test
    void whenProductInformedThenItShouldBeCreated() throws ProductAlreadyRegisteredException {
        // given
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedSavedProduct = productMapper.toModel(expectedProductDTO);

        // when
        when(productRepository.findByName(expectedProductDTO.getName())).thenReturn(Optional.empty());
        when(productRepository.save(expectedSavedProduct)).thenReturn(expectedSavedProduct);

        //then
        ProductDTO createdProductDTO = productService.createProduct(expectedProductDTO);

        assertThat(createdProductDTO.getId(), is(equalTo(expectedProductDTO.getId())));
        assertThat(createdProductDTO.getName(), is(equalTo(expectedProductDTO.getName())));
        assertThat(createdProductDTO.getQuantity(), is(equalTo(expectedProductDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredProductInformedThenAnExceptionShouldBeThrown() {
        // given
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product duplicatedProduct = productMapper.toModel(expectedProductDTO);

        // when
        when(productRepository.findByName(expectedProductDTO.getName())).thenReturn(Optional.of(duplicatedProduct));

        // then
        assertThrows(ProductAlreadyRegisteredException.class, () -> productService.createProduct(expectedProductDTO));
    }

    @Test
    void whenValidProductNameIsGivenThenReturnAProduct() throws ProductNotFoundException {
        // given
        ProductDTO expectedFoundProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedFoundProduct = productMapper.toModel(expectedFoundProductDTO);

        // when
        when(productRepository.findByName(expectedFoundProduct.getName())).thenReturn(Optional.of(expectedFoundProduct));

        // then
        ProductDTO foundProductDTO = productService.findByName(expectedFoundProductDTO.getName());

        assertThat(foundProductDTO, is(equalTo(expectedFoundProductDTO)));
    }

    @Test
    void whenNotRegisteredProductNameIsGivenThenThrowAnException() {
        // given
        ProductDTO expectedFoundProductDTO = ProductDTOBuilder.builder().build().toProductDTO();

        // when
        when(productRepository.findByName(expectedFoundProductDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class, () -> productService.findByName(expectedFoundProductDTO.getName()));
    }

    @Test
    void whenListProductIsCalledThenReturnAListOfProducts() {
        // given
        ProductDTO expectedFoundProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedFoundProduct = productMapper.toModel(expectedFoundProductDTO);

        //when
        when(productRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundProduct));

        //then
        List<ProductDTO> foundListProductsDTO = productService.listAll();

        assertThat(foundListProductsDTO, is(not(empty())));
        assertThat(foundListProductsDTO.get(0), is(equalTo(expectedFoundProductDTO)));
    }

    @Test
    void whenListProductIsCalledThenReturnAnEmptyListOfProducts() {
        //when
        when(productRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<ProductDTO> foundListProductsDTO = productService.listAll();

        assertThat(foundListProductsDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenAProductShouldBeDeleted() throws ProductNotFoundException {
        // given
        ProductDTO expectedDeletedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedDeletedProduct = productMapper.toModel(expectedDeletedProductDTO);

        // when
        when(productRepository.findById(expectedDeletedProductDTO.getId())).thenReturn(Optional.of(expectedDeletedProduct));
        doNothing().when(productRepository).deleteById(expectedDeletedProductDTO.getId());

        // then
        productService.deleteById(expectedDeletedProductDTO.getId());

        verify(productRepository, times(1)).findById(expectedDeletedProductDTO.getId());
        verify(productRepository, times(1)).deleteById(expectedDeletedProductDTO.getId());
    }

    @Test
    void whenDecrementIsCalledThenDecrementProductStock() throws ProductNotFoundException, ProductStockExceededException {
        //given
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        //when
        when(productRepository.findById(expectedProductDTO.getId())).thenReturn(Optional.of(expectedProduct));
        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        int quantityToDecrement = 3;
        int expectedQuantityAfterDecrement = expectedProductDTO.getQuantity() + quantityToDecrement;

        // then
        ProductDTO decrementedProductDTO = productService.decrement(expectedProductDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(decrementedProductDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, lessThan(expectedProductDTO.getMin()));
    }

    @Test
    void whenDecrementIsGreatherThanMinThenThrowException() {
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        when(productRepository.findById(expectedProductDTO.getId())).thenReturn(Optional.of(expectedProduct));

        int quantityToDecrement = 20;
        assertThrows(ProductStockExceededException.class, () -> productService.decrement(expectedProductDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementAfterSubtractIsGreatherThanMinThenThrowException() {
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        when(productRepository.findById(expectedProductDTO.getId())).thenReturn(Optional.of(expectedProduct));

        int quantityToDecrement = 15;
        assertThrows(ProductStockExceededException.class, () -> productService.decrement(expectedProductDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(productRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.decrement(INVALID_BEER_ID, quantityToDecrement));
    }
}

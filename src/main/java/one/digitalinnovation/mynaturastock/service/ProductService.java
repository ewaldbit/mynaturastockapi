package one.digitalinnovation.mynaturastock.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.mynaturastock.dto.ProductDTO;
import one.digitalinnovation.mynaturastock.entity.Product;
import one.digitalinnovation.mynaturastock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.mynaturastock.exception.ProductNotFoundException;
import one.digitalinnovation.mynaturastock.exception.ProductStockExceededException;
import one.digitalinnovation.mynaturastock.mapper.ProductMapper;
import one.digitalinnovation.mynaturastock.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductDTO createProduct(ProductDTO productDTO) throws ProductAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(productDTO.getName());
        Product product = productMapper.toModel(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    public ProductDTO findByName(String name) throws ProductNotFoundException {
        Product foundProduct = productRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException(name));
        return productMapper.toDTO(foundProduct);
    }

    public List<ProductDTO> listAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws ProductNotFoundException {
        verifyIfExists(id);
        productRepository.deleteById(id);
    }

    public ProductDTO decrement(Long id, int quantityToDecrement) throws ProductNotFoundException, ProductStockExceededException {
        Product productToDecrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToDecrement - productToDecrementStock.getQuantity();
        if (quantityAfterIncrement <= productToDecrementStock.getMin()) {
            productToDecrementStock.setQuantity(productToDecrementStock.getQuantity() - quantityToDecrement);
            Product decrementedProductStock = productRepository.save(productToDecrementStock);
            return productMapper.toDTO(decrementedProductStock);
        }
        throw new ProductStockExceededException(id, quantityToDecrement);
    }

    public ProductDTO increment(Long id, int quantityToIncremente) throws ProductNotFoundException {
        Product productToIncrementStock = verifyIfExists(id);
        productToIncrementStock.setQuantity(productToIncrementStock.getQuantity() + quantityToIncremente);
        Product incrementedProductStock = productRepository.save(productToIncrementStock);
        return productMapper.toDTO(incrementedProductStock);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws ProductAlreadyRegisteredException {
        Optional<Product> optSavedBeer = productRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new ProductAlreadyRegisteredException(name);
        }
    }

    private Product verifyIfExists(Long id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }


}

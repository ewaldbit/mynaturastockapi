package one.digitalinnovation.mynaturastock.controller;

import lombok.AllArgsConstructor;
import one.digitalinnovation.mynaturastock.dto.ProductDTO;
import one.digitalinnovation.mynaturastock.dto.QuantityDTO;
import one.digitalinnovation.mynaturastock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.mynaturastock.exception.ProductNotFoundException;
import one.digitalinnovation.mynaturastock.exception.ProductStockExceededException;
import one.digitalinnovation.mynaturastock.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController implements ProductControllerDocs {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductDTO productDTO) throws ProductAlreadyRegisteredException {
        return productService.createProduct(productDTO);
    }

    @GetMapping("/{name}")
    public ProductDTO findByName(@PathVariable String name) throws ProductNotFoundException {
        return productService.findByName(name);
    }

    @GetMapping
    public List<ProductDTO> listProducts() {
        return productService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws ProductNotFoundException {
        productService.deleteById(id);
    }

    @PatchMapping("/{id}/decrement")
    public ProductDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws ProductNotFoundException, ProductStockExceededException {
        return productService.decrement(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/increment")
    public ProductDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws ProductNotFoundException {
        return productService.increment(id, quantityDTO.getQuantity());
    }
}

package one.digitalinnovation.mynaturastock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String productName) {
        super(String.format("Product with name %s not found in the system.", productName));
    }

    public ProductNotFoundException(Long id) {
        super(String.format("Product with id %s not found in the system.", id));
    }
}

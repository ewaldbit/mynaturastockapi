package one.digitalinnovation.mynaturastock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductStockExceededException extends Exception {

    public ProductStockExceededException(Long id, int quantityToDecrement) {
        super(String.format("Products with %s ID to decrement informed exceeds the min stock capacity: %s", id, quantityToDecrement));
    }
}

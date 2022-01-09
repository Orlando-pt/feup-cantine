package pt.feup.les.feupfood.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DataIntegrityException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public DataIntegrityException(String message) {
        super(message);
    }
}

package pt.feup.les.feupfood.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ResourceNotOwnedException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public ResourceNotOwnedException(String message) {
        super(message);
    }
    
}

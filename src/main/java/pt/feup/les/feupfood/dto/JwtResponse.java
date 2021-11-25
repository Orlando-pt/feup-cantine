package pt.feup.les.feupfood.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse implements Serializable{
    
    private static final long serialVersionUID = -8091879091924046844L;
	private String jwttoken;
}

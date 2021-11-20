package pt.feup.les.feupfood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document("restaurants")
public class Restaurant {
    
    @Id
    private String name;

    private String location;
}

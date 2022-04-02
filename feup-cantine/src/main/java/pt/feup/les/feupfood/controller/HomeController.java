package pt.feup.les.feupfood.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "Home")
public class HomeController {
    
    @GetMapping("/")
    public String index() {
        return "Hello hello!";
    }
}

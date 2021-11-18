package pt.feup.les.feupfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin().permitAll()
                .and()
            .logout().permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        var user = User.withDefaultPasswordEncoder()
                    .username("feup")
                    .password("feup")
                    .roles("ADMIN")
                    .build();
        
        return new InMemoryUserDetailsManager(user);
    }
    
}

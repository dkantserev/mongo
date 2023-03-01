package com.mongo.mongo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Value("${admin.password}")
    String adminP;
    @Value("${user.password}")
    String userP;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {


        httpSecurity
                .authorizeHttpRequests()
                .requestMatchers("/file/**").hasRole("ADMIN")
                .requestMatchers("/createBase/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .logout().logoutSuccessUrl("/home");
        return httpSecurity.build();
    }



    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(userP)
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("admin")
                .password(adminP)
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user,user2);
    }

    @Bean
    public OpenAPI openAPI(){
        return  new OpenAPI().info(new Info().title("XlsxTOMongoDb")
                .description("Программа для создания и управления mongoDb базы из xlsx файла"));
    }


}

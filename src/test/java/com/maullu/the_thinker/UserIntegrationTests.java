package com.maullu.the_thinker;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.maullu.the_thinker.Model.Role;
import com.maullu.the_thinker.Model.User;
import com.maullu.the_thinker.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIntegrationTests extends BaseIntegrationTest{
    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();

        User user1 = new User(null, "Carlos", "cgimenez@gmail.com", "123", Role.USER);
        User user2 = new User(null, "Mauricio", "maurillugdar@gmail.com", "brime", Role.ADMIN);
        this.user1 = userRepository.save(user1);
        this.user2 = userRepository.save(user2);

    }

    @Test
    void shouldReturnUserById(){
        ResponseEntity<String> response = restTemplate
                .getForEntity("/user/"+ user1.getId(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        assertThat(user1.getName()).isEqualTo(documentContext.read("$.name"));
    }

    @Test @DirtiesContext
    void shouldCreateUser(){
        User user = new User(null, "Mauri", "mauriciojllugdar@gmail.com", "123", Role.USER);
        ResponseEntity<Void> createResponse = restTemplate
                .postForEntity("/user", user, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationCreatedUser = createResponse.getHeaders().getLocation();
        ResponseEntity<String> response = restTemplate
                .getForEntity(locationCreatedUser, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo(user.getName());
    }

    @Test @DirtiesContext
    void shouldDeleteUser(){
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/user/"+user1.getId(), HttpMethod.DELETE, null,Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

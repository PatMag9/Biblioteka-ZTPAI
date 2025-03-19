package com.example.Biblioteka_ZTPAI.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private static final Map<Integer, Map<String, String>> users = new
            HashMap<>() {{
                put(1, Map.of("id", "1", "nickname", "Patmag", "info","asd", "favourite_book","Pan Tadeusz"));
                put(2, Map.of("id", "1", "nickname", "Jankow", "info","fgh", "favourite_book","Krzyżacy"));
                put(3, Map.of("id", "1", "nickname", "PioNow", "info","jkl", "favourite_book","Romeo i Julia"));
            }};

    public ResponseEntity<Object> getProfile() {
        return ResponseEntity.ok(users.get(1).values());
    }

    public ResponseEntity<Object> editProfile(){
        //todo
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

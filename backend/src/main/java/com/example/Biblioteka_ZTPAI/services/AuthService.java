package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.repositories.RoleRepository;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE  = "registerExchange";
    private static final String ROUTING_KEY  = "registerRoutingKey";


    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>())
                .build();

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Could not find the 'USER' role"));

        user.addRole(userRole);

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        LOGGER.info(String.format("Message sent -> %s", request.getEmail()));
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, request.getEmail());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @RabbitListener(queues = "registerQueue")
    public void reqisterEmail(String email){
        LOGGER.info(String.format("Received message -> %s", email));
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public ResponseEntity<String> logout(){
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}

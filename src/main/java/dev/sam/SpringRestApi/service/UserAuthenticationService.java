package dev.sam.SpringRestApi.service;

import dev.sam.SpringRestApi.dto.LoginDTO;
import dev.sam.SpringRestApi.dto.UserDTO;
import dev.sam.SpringRestApi.model.Role;
import dev.sam.SpringRestApi.model.User;
import dev.sam.SpringRestApi.repository.UserRepository;
import dev.sam.SpringRestApi.response.AuthenticationResponse;
import dev.sam.SpringRestApi.response.ResponseHandler;
import dev.sam.SpringRestApi.security_config.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);
    ResponseHandler responseHandler;

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserAuthenticationService(ResponseHandler responseHandler, UserRepository repository, PasswordEncoder passwordEncoder, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.responseHandler = responseHandler;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<AuthenticationResponse> register(UserDTO userDTO) {
        // Check if user already exists
        Optional<User> existingUser = repository.findUserByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            return responseHandler.responseBuilder("User already exists", null, HttpStatus.CONFLICT);
        }

        // Validate password length
        String password = userDTO.getPassword();
        if (password.length() < 8 || password.length() > 16) {
            return responseHandler.responseBuilder(null, "Password must be between 8 and 16 characters long", HttpStatus.BAD_REQUEST);
        }

        logger.info("Registering user with email: {}", userDTO.getEmail());

        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole(Role.USER);

        // Log the new user details before saving
        logger.info("New user details before saving: {}", newUser.getName());
//        logger.info("New user details before saving: {}", newUser);

        // Save the new user entity
        try {
            newUser = repository.save(newUser);
        } catch (DataAccessException e) {
            // Handle any data access exceptions
            logger.error("Error occurred during user registration", e);
            return responseHandler.responseBuilder(null, "Failed to register user. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Generate JWT token for the registered user
        String accessToken = jwtService.generateToken(newUser);

        // Return success response with JWT token
        return responseHandler.responseBuilder("User registration was successful", accessToken, HttpStatus.CREATED);
    }

    public ResponseEntity<AuthenticationResponse> authenticateUser(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        User user = repository.findUserByEmail(loginDTO.getEmail()).orElseThrow();
        String accessToken = jwtService.generateToken(user);

        return responseHandler.responseBuilder("User login was successful", accessToken, HttpStatus.OK);
    }
}

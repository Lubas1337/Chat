package org.lubas.service;


import jakarta.transaction.Transactional;
import org.lubas.domain.entity.AppUser;
import org.lubas.domain.dto.request.RegisterRequest;
import org.lubas.domain.dto.response.UserResponse;
import org.lubas.domain.repository.UserRepository;
import org.lubas.security.jwt.JwtUtils;
import org.lubas.security.jwt.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with given username: " + username));
    }

    public boolean existsByUsername(String username) throws UsernameNotFoundException{
        return userRepository.existsByUsername(username);
    }

    public AppUser registerUser(RegisterRequest registerRequest){
        AppUser appUser = new AppUser(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword())
        );
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = new Date();
        String strDate = dateFormatter.format(currentDate);
        updateLastSeenAt(appUser, strDate);
        return userRepository.save(appUser);
    }

    public void deleteUser(AppUser appUser){
        userRepository.deleteById(appUser.getId());
    }

    public void updateLastSeenAt(AppUser appUser, String lastSeenAt){
        appUser.setLastSeenAt(lastSeenAt);
        userRepository.save(appUser);
    }

    public String parseJwtToken(String jwtToken){
        if(StringUtils.hasText(jwtToken) && jwtToken.startsWith("Bearer ")){
            return jwtToken.substring(7);
        }
        return null;
    }

    public AppUser findUserFromJwtToken(String jwtToken){
        try{
            String parsedJwtToken = parseJwtToken(jwtToken);
            String jwtUsername = jwtUtils.getUsernameFromJwtToken(parsedJwtToken);
            return userRepository.findUserByUsername(jwtUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid Jwt Token"));
        } catch (Exception e){
            return null;
        }

    }
    public List<UserResponse> getAllUsers(){
        List<UserResponse> userList = new ArrayList<>();
        for(AppUser appUser : userRepository.findAll()){
            userList.add(new UserResponse(appUser.getUsername(), appUser.getLastSeenAt()));
        }

        return userList;
    }


}

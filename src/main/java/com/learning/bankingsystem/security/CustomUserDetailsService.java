package com.learning.bankingsystem.security;

import com.learning.bankingsystem.entity.PasswordStatus;
import com.learning.bankingsystem.entity.PasswordType;
import com.learning.bankingsystem.repository.PasswordRepository;
import com.learning.bankingsystem.repository.UserRepository;
import com.learning.bankingsystem.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordRepository passwordRepository;

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        var userPassword = passwordRepository.findByUser_UuidAndTypeAndStatus(user.getUuid(), PasswordType.LOGIN_PASSWORD, PasswordStatus.ACTIVE);
        if (userPassword == null) {
            throw new UsernameNotFoundException("Password not found for user: " + email);
        }

        var userRoles = userRoleRepository.findByUser_Uuid(user.getUuid());

        Set<GrantedAuthority> authorities = userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(ROLE_PREFIX + userRole.getRole()))
                .collect(Collectors.toSet());

        return new User(user.getEmail(), userPassword.getPassword(), authorities);
    }
}

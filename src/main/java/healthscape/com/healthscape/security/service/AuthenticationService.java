package healthscape.com.healthscape.security.service;

import healthscape.com.healthscape.security.model.JwtAuthenticationRequest;
import healthscape.com.healthscape.security.model.UserTokenState;
import healthscape.com.healthscape.security.util.TokenUtils;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AuthenticationService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;

    public UserTokenState login(JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
        log.debug("L - user: {}", authenticationRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUser user = (AppUser) authentication.getPrincipal();

        return getAuthentication(user);
    }

    public UserTokenState getAuthentication(AppUser user) {
        log.debug("GA U: {}", user.getEmail());
        String accessToken = tokenUtils.generateToken(user.getEmail(), user.getRole().getAuthority(), user.getRole().getPermissionNames());
        String refreshToken = tokenUtils.generateRefreshToken(user.getEmail(), user.getRole().getAuthority(), user.getRole().getPermissionNames());
        return new UserTokenState(accessToken, refreshToken, tokenUtils.getExpiredIn(), getRoles(user));
    }

    public List<String> getRoles(AppUser user) {
        log.debug("GR U: {}", user.getEmail());
        return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    public UserTokenState refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Refreshing token...");
        String refresh_token = tokenUtils.getToken(request);
        if (refresh_token != null) {
            try {
                log.info("Refresh token found");
                String email = tokenUtils.getEmailFromToken(refresh_token);
                AppUser user = userRepo.findByEmail(email);
                String accessToken = tokenUtils.generateToken(email, user.getRole().getAuthority(), user.getRole().getPermissionNames());
                String refreshToken = tokenUtils.generateToken(email, user.getRole().getAuthority(), user.getRole().getPermissionNames());
                return new UserTokenState(accessToken, refreshToken, tokenUtils.getExpiredIn(), getRoles(user));
            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage(), exception);
            }
        } else {
            log.info("Refresh token not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found.", null);
        }
    }
}

package com.unosquare.carmigo.service;

import com.unosquare.carmigo.dto.CreateAuthenticationDTO;
import com.unosquare.carmigo.dto.GrabAuthenticationDTO;
import com.unosquare.carmigo.security.UserSecurityService;
import com.unosquare.carmigo.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserSecurityService userSecurityService;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtils jwtTokenUtils;

  public GrabAuthenticationDTO createAuthenticationToken(final CreateAuthenticationDTO createAuthenticationDTO) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        createAuthenticationDTO.getEmail(), createAuthenticationDTO.getPassword()));
    final UserDetails userDetails = userSecurityService.loadUserByUsername(createAuthenticationDTO.getEmail());
    final String jwt = jwtTokenUtils.generateToken(userDetails);
    final GrabAuthenticationDTO grabAuthenticationDTO = new GrabAuthenticationDTO();
    grabAuthenticationDTO.setJwt(jwt);
    return grabAuthenticationDTO;
  }
}

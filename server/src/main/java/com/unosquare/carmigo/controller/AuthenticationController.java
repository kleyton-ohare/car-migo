package com.unosquare.carmigo.controller;

import com.unosquare.carmigo.dto.CreateAuthenticationDTO;
import com.unosquare.carmigo.dto.GrabAuthenticationDTO;
import com.unosquare.carmigo.model.request.CreateAuthenticationViewModel;
import com.unosquare.carmigo.model.response.AuthenticationViewModel;
import com.unosquare.carmigo.service.AuthenticationService;
import com.unosquare.carmigo.service.PlatformUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "Authentication Controller")
public class AuthenticationController {

  private final ModelMapper modelMapper;
  private final AuthenticationService authenticationService;

  @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<AuthenticationViewModel> createAuthenticationToken(
      @Valid @RequestBody final CreateAuthenticationViewModel createAuthenticationViewModel) {
    final CreateAuthenticationDTO createAuthenticationDTO = modelMapper.map(
        createAuthenticationViewModel, CreateAuthenticationDTO.class);
    final GrabAuthenticationDTO grabAuthenticationDTO =
        authenticationService.createAuthenticationToken(createAuthenticationDTO);
    final AuthenticationViewModel authenticationViewModel = modelMapper.map(
        grabAuthenticationDTO, AuthenticationViewModel.class);
    return new ResponseEntity<>(authenticationViewModel, HttpStatus.CREATED);
  }
}

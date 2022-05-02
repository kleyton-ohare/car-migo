package com.unosquare.carmigo.controller;

import com.github.fge.jsonpatch.JsonPatch;
import com.unosquare.carmigo.dto.CreateDriverDTO;
import com.unosquare.carmigo.dto.CreatePlatformUserDTO;
import com.unosquare.carmigo.dto.GrabDriverDTO;
import com.unosquare.carmigo.dto.GrabPassengerDTO;
import com.unosquare.carmigo.dto.GrabPlatformUserDTO;
import com.unosquare.carmigo.exception.ResourceNotFoundException;
import com.unosquare.carmigo.model.request.CreateAuthenticationViewModel;
import com.unosquare.carmigo.model.request.CreateDriverViewModel;
import com.unosquare.carmigo.model.request.CreatePlatformUserViewModel;
import com.unosquare.carmigo.model.response.AuthenticationViewModel;
import com.unosquare.carmigo.model.response.DriverViewModel;
import com.unosquare.carmigo.model.response.PassengerViewModel;
import com.unosquare.carmigo.model.response.PlatformUserViewModel;
import com.unosquare.carmigo.service.CarMigoUserDetailsService;
import com.unosquare.carmigo.service.PlatformUserService;
import com.unosquare.carmigo.util.JwtTokenUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Tag(name = "Platform User Controller")
public class PlatformUserController
{
    private final ModelMapper modelMapper;
    private final PlatformUserService platformUserService;
    private final AuthenticationManager authenticationManager;
    private final CarMigoUserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationViewModel> createAuthenticationToken(
            @Valid @RequestBody final CreateAuthenticationViewModel createAuthenticationViewModel)
    {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    createAuthenticationViewModel.getEmail(), createAuthenticationViewModel.getPassword()));
        } catch (final BadCredentialsException ex) {
            throw new ResourceNotFoundException("Incorrect email and/or password");
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(createAuthenticationViewModel.getEmail());
        final String jwt = jwtTokenUtils.generateToken(userDetails);
        final AuthenticationViewModel authenticationViewModel = new AuthenticationViewModel();
        authenticationViewModel.setJwt(jwt);
        return new ResponseEntity<>(authenticationViewModel, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PlatformUserViewModel> getPlatformUserById(@PathVariable final int id)
    {
        final GrabPlatformUserDTO grabPlatformUserDTO = platformUserService.getPlatformUserById(id);
        final PlatformUserViewModel platformUserViewModel = modelMapper.map(
                grabPlatformUserDTO, PlatformUserViewModel.class);
        return ResponseEntity.ok(platformUserViewModel);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PlatformUserViewModel> createPlatformUser(
            @Valid @RequestBody final CreatePlatformUserViewModel createPlatformUserViewModel)
    {
        final CreatePlatformUserDTO createPlatformUserDTO = modelMapper.map(
                createPlatformUserViewModel, CreatePlatformUserDTO.class);
        final GrabPlatformUserDTO grabPlatformUserDTO = platformUserService.createPlatformUser(createPlatformUserDTO);
        final PlatformUserViewModel platformUserViewModel = modelMapper.map(
                grabPlatformUserDTO, PlatformUserViewModel.class);
        return new ResponseEntity<>(platformUserViewModel, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PlatformUserViewModel> patchPlatformUser(@PathVariable final int id,
                                                                   @RequestBody final JsonPatch patch)
    {
        final GrabPlatformUserDTO grabPlatformUserDTO = platformUserService.patchPlatformUser(id, patch);
        final PlatformUserViewModel platformUserViewModel = modelMapper.map(
                grabPlatformUserDTO, PlatformUserViewModel.class);
        return ResponseEntity.ok(platformUserViewModel);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deletePlatformUser(@PathVariable final int id)
    {
        platformUserService.deletePlatformUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/drivers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DriverViewModel> getDriverById(@PathVariable final int id)
    {
        final GrabDriverDTO grabDriverDTO = platformUserService.getDriverById(id);
        final DriverViewModel driverViewModel = modelMapper.map(
                grabDriverDTO, DriverViewModel.class);
        return ResponseEntity.ok(driverViewModel);
    }

    @PostMapping(value = "/{id}/drivers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DriverViewModel> createDriver(
            @PathVariable final int id, @Valid @RequestBody final CreateDriverViewModel createDriverViewModal)
    {
        final CreateDriverDTO createDriverDTO = modelMapper.map(createDriverViewModal, CreateDriverDTO.class);
        final GrabDriverDTO driverDTO = platformUserService.createDriver(id, createDriverDTO);
        return new ResponseEntity<>(modelMapper.map(driverDTO, DriverViewModel.class), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/drivers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteDriver(@PathVariable final int id)
    {
        platformUserService.deleteDriverById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/passengers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PassengerViewModel> getPassengerById(@PathVariable final int id)
    {
        final GrabPassengerDTO grabPassengerDTO = platformUserService.getPassengerById(id);
        final PassengerViewModel passengerViewModel = modelMapper.map(
                grabPassengerDTO, PassengerViewModel.class);
        return ResponseEntity.ok(passengerViewModel);
    }

    @PostMapping(value = "/{id}/passengers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PassengerViewModel> createPassenger(@PathVariable final int id)
    {
        final GrabPassengerDTO passengerDTO = platformUserService.createPassenger(id);
        return new ResponseEntity<>(modelMapper.map(passengerDTO, PassengerViewModel.class), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/passengers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deletePassenger(@PathVariable final int id)
    {
        platformUserService.deletePassengerById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.unosquare.carmigo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.unosquare.carmigo.dto.CreatePlatformUserDTO;
import com.unosquare.carmigo.dto.GrabPlatformUserDTO;
import com.unosquare.carmigo.entity.PlatformUser;
import com.unosquare.carmigo.entity.UserAccessStatus;
import com.unosquare.carmigo.exception.ResourceNotFoundException;
import com.unosquare.carmigo.repository.PlatformUserRepository;
import com.unosquare.carmigo.security.Authorization;
import java.time.Instant;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformUserService {

  private static final int INITIAL_USER_STATUS = 1;

  private final PlatformUserRepository platformUserRepository;
  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final EntityManager entityManager;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final Authorization authorization;

  public GrabPlatformUserDTO getPlatformUserById(final int id) {
    return modelMapper.map(findPlatformUserById(id), GrabPlatformUserDTO.class);
  }

  public GrabPlatformUserDTO createPlatformUser(final CreatePlatformUserDTO createPlatformUserDTO) {
    final PlatformUser platformUser = modelMapper.map(createPlatformUserDTO, PlatformUser.class);
    platformUser.setCreatedDate(Instant.now());
    platformUser.setPassword(bCryptPasswordEncoder.encode(createPlatformUserDTO.getPassword()));
    platformUser.setUserAccessStatus(entityManager.getReference(UserAccessStatus.class, INITIAL_USER_STATUS));
    return modelMapper.map(platformUserRepository.save(platformUser), GrabPlatformUserDTO.class);
  }

  public GrabPlatformUserDTO patchPlatformUserById(final int id, final JsonPatch patch) {
    final GrabPlatformUserDTO grabPlatformUserDTO = modelMapper.map(findPlatformUserById(id), GrabPlatformUserDTO.class);
    try {
      final JsonNode platformUserNode = patch.apply(objectMapper.convertValue(grabPlatformUserDTO, JsonNode.class));
      final PlatformUser patchedPlatformUser = objectMapper.treeToValue(platformUserNode, PlatformUser.class);
      return modelMapper.map(platformUserRepository.save(patchedPlatformUser), GrabPlatformUserDTO.class);
    } catch (final JsonPatchException | JsonProcessingException ex) {
      throw new ResourceNotFoundException(String.format("Error updating user id %d", id));
    }
  }

  public void deletePlatformUserById(final int id) {
    findPlatformUserById(id);
    platformUserRepository.deleteById(id);
  }

  private PlatformUser findPlatformUserById(final int id) {
    authorization.verifyUserAuthorization(id);
    return platformUserRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException(String.format("PlatformUser id %d not found.", id)));
  }
}

package com.unosquare.carmigo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.github.fge.jsonpatch.JsonPatch;
import com.unosquare.carmigo.dto.CreatePlatformUserDTO;
import com.unosquare.carmigo.dto.GrabPlatformUserDTO;
import com.unosquare.carmigo.entity.PlatformUser;
import com.unosquare.carmigo.entity.UserAccessStatus;
import com.unosquare.carmigo.repository.PlatformUserRepository;
import com.unosquare.carmigo.security.Authorization;
import com.unosquare.carmigo.util.PatchUtility;
import com.unosquare.carmigo.util.ResourceUtility;
import java.time.Instant;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PlatformUserServiceTest {

  private static final String PATCH_PLATFORM_USER_VALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PatchPlatformUserValid.json");

  @Mock private PlatformUserRepository platformUserRepositoryMock;
  @Mock private ModelMapper modelMapperMock;
  @Mock private ObjectMapper objectMapperMock;
  @Mock private EntityManager entityManagerMock;
  @Mock private BCryptPasswordEncoder bCryptPasswordEncoderMock;
  @Mock private Authorization authorizationMock;
  @InjectMocks private PlatformUserService platformUserService;

  @Fixture private PlatformUser platformUserFixture;
  @Fixture private CreatePlatformUserDTO createPlatformUserDTOFixture;
  @Fixture private GrabPlatformUserDTO grabPlatformUserDTOFixture;

  @BeforeEach
  public void setUp() {
    final JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    FixtureAnnotations.initFixtures(this, jFixture);
  }

  @Test
  public void get_PlatformUser_By_Id_Returns_GrabPlatformUserDTO() {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(platformUserRepositoryMock.findById(anyInt())).thenReturn(Optional.of(platformUserFixture));
    when(modelMapperMock.map(platformUserFixture, GrabPlatformUserDTO.class)).thenReturn(grabPlatformUserDTOFixture);
    final GrabPlatformUserDTO grabPlatformUserDTO = platformUserService.getPlatformUserById(anyInt());

    assertThat(grabPlatformUserDTO.getId()).isEqualTo(grabPlatformUserDTOFixture.getId());
    assertThat(grabPlatformUserDTO.getCreatedDate()).isEqualTo(grabPlatformUserDTOFixture.getCreatedDate());
    assertThat(grabPlatformUserDTO.getDob()).isEqualTo(grabPlatformUserDTOFixture.getDob());
    assertThat(grabPlatformUserDTO.getEmail()).isEqualTo(grabPlatformUserDTOFixture.getEmail());
    assertThat(grabPlatformUserDTO.getFirstName()).isEqualTo(grabPlatformUserDTOFixture.getFirstName());
    assertThat(grabPlatformUserDTO.getLastName()).isEqualTo(grabPlatformUserDTOFixture.getLastName());
    assertThat(grabPlatformUserDTO.getPhoneNumber()).isEqualTo(grabPlatformUserDTOFixture.getPhoneNumber());
    verify(platformUserRepositoryMock).findById(anyInt());
  }

  @Test
  public void create_PlatformUser_Returns_GrabPlatformUserDTO() {
    final PlatformUser spyPlatformUser = spy(new PlatformUser());
    when(modelMapperMock.map(createPlatformUserDTOFixture, PlatformUser.class)).thenReturn(spyPlatformUser);
    when(platformUserRepositoryMock.save(spyPlatformUser)).thenReturn(platformUserFixture);
    when(modelMapperMock.map(platformUserFixture, GrabPlatformUserDTO.class)).thenReturn(grabPlatformUserDTOFixture);
    spyPlatformUser.setCreatedDate(any(Instant.class));
    spyPlatformUser.setFirstName(anyString());
    spyPlatformUser.setLastName(anyString());
    spyPlatformUser.setEmail(anyString());
    spyPlatformUser.setPassword(bCryptPasswordEncoderMock.encode(anyString()));
    spyPlatformUser.setUserAccessStatus(entityManagerMock.getReference(eq(UserAccessStatus.class), anyInt()));
    final GrabPlatformUserDTO grabPlatformUserDTO = platformUserService.createPlatformUser(createPlatformUserDTOFixture);

    assertThat(grabPlatformUserDTO.getCreatedDate()).isEqualTo(grabPlatformUserDTOFixture.getCreatedDate());
    assertThat(grabPlatformUserDTO.getFirstName()).isEqualTo(grabPlatformUserDTOFixture.getFirstName());
    assertThat(grabPlatformUserDTO.getLastName()).isEqualTo(grabPlatformUserDTOFixture.getLastName());
    assertThat(grabPlatformUserDTO.getEmail()).isEqualTo(grabPlatformUserDTOFixture.getEmail());
    verify(platformUserRepositoryMock).save(any(PlatformUser.class));
  }

  @Test
  public void patch_PlatformUser_Returns_GrabPlatformUserDTO() throws Exception {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(platformUserRepositoryMock.findById(anyInt())).thenReturn(Optional.of(platformUserFixture));
    when(modelMapperMock.map(platformUserFixture, GrabPlatformUserDTO.class)).thenReturn(grabPlatformUserDTOFixture);
    final JsonPatch patch = PatchUtility.jsonPatch(PATCH_PLATFORM_USER_VALID_JSON);
    final JsonNode platformUserNode = PatchUtility.jsonNode(platformUserFixture, patch);
    when(objectMapperMock.convertValue(grabPlatformUserDTOFixture, JsonNode.class)).thenReturn(platformUserNode);
    when(objectMapperMock.treeToValue(platformUserNode, PlatformUser.class)).thenReturn(platformUserFixture);
    when(platformUserRepositoryMock.save(platformUserFixture)).thenReturn(platformUserFixture);
    when(modelMapperMock.map(platformUserFixture, GrabPlatformUserDTO.class)).thenReturn(grabPlatformUserDTOFixture);
    final GrabPlatformUserDTO grabPlatformUserDTO = platformUserService.patchPlatformUserById(
        platformUserFixture.getId(), patch);

    assertThat(grabPlatformUserDTO.getFirstName()).isEqualTo(grabPlatformUserDTOFixture.getFirstName());
    assertThat(grabPlatformUserDTO.getLastName()).isEqualTo(grabPlatformUserDTOFixture.getLastName());
    assertThat(grabPlatformUserDTO.getDob()).isEqualTo(grabPlatformUserDTOFixture.getDob());
    assertThat(grabPlatformUserDTO.getCreatedDate()).isEqualTo(grabPlatformUserDTOFixture.getCreatedDate());
    verify(platformUserRepositoryMock).findById(anyInt());
    verify(platformUserRepositoryMock).save(any(PlatformUser.class));
  }

  @Test
  public void delete_PlatformUser_Returns_Void() {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(platformUserRepositoryMock.findById(anyInt())).thenReturn(Optional.of(platformUserFixture));
    platformUserService.deletePlatformUserById(anyInt());
    verify(platformUserRepositoryMock).findById(anyInt());
  }
}

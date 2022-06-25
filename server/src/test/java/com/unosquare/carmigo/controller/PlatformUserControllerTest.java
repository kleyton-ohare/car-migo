package com.unosquare.carmigo.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.github.fge.jsonpatch.JsonPatch;
import com.unosquare.carmigo.dto.CreateDriverDTO;
import com.unosquare.carmigo.dto.GrabDriverDTO;
import com.unosquare.carmigo.dto.GrabPassengerDTO;
import com.unosquare.carmigo.dto.GrabPlatformUserDTO;
import com.unosquare.carmigo.exception.UnauthorizedException;
import com.unosquare.carmigo.model.response.DriverViewModel;
import com.unosquare.carmigo.model.response.PassengerViewModel;
import com.unosquare.carmigo.model.response.PlatformUserViewModel;
import com.unosquare.carmigo.security.AppUser;
import com.unosquare.carmigo.service.PlatformUserService;
import com.unosquare.carmigo.util.ResourceUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
public class PlatformUserControllerTest {

  private static final String API_LEADING = "/v1/users/";
  private static final String POST_AUTHENTICATION_VALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PostAuthenticationValid.json");
  private static final String POST_AUTHENTICATION_INVALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PostAuthenticationInvalid.json");
  private static final String POST_PLATFORM_USER_VALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PostPlatformUserValid.json");
  private static final String POST_PLATFORM_USER_INVALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PostPlatformUserInvalid.json");
  private static final String PATCH_PLATFORM_USER_VALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PatchPlatformUserValid.json");
  private static final String PATCH_PLATFORM_USER_INVALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PatchPlatformUserInvalid.json");
  private static final String POST_DRIVER_VALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PostDriverValid.json");
  private static final String POST_DRIVER_INVALID_JSON =
      ResourceUtility.generateStringFromResource("requestJson/PostDriverInvalid.json");

  private MockMvc mockMvc;

  @Mock private ModelMapper modelMapperMock;
  @Mock private PlatformUserService platformUserServiceMock;
  @Mock private AppUser appUserMock;

  @Fixture private GrabPlatformUserDTO grabPlatformUserDTOFixture;
  @Fixture private GrabDriverDTO grabDriverDTOFixture;
  @Fixture private GrabPassengerDTO grabPassengerDTOFixture;
  @Fixture private PlatformUserViewModel platformUserViewModelFixture;
  @Fixture private DriverViewModel driverViewModelFixture;
  @Fixture private PassengerViewModel passengerViewModelFixture;

  @BeforeEach
  public void setUp() {
    final JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    FixtureAnnotations.initFixtures(this, jFixture);

    mockMvc = MockMvcBuilders.standaloneSetup(
        new PlatformUserController(modelMapperMock, platformUserServiceMock, appUserMock))
        .build();
  }

  @Test
  public void post_Create_Authentication_Token_Returns_HttpStatus_Created() throws Exception {
    mockMvc.perform(post(API_LEADING + "authenticate")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_AUTHENTICATION_VALID_JSON))
        .andExpect(status().isCreated());
    verify(platformUserServiceMock).createAuthenticationToken(any());
  }

  @Test
  public void post_Create_Authentication_Token_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(post(API_LEADING + "authenticate")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_AUTHENTICATION_INVALID_JSON))
        .andExpect(status().isBadRequest());
    verify(platformUserServiceMock, times(0)).createAuthenticationToken(any());
  }

  @Test
  public void get_PlatformUser_By_Id_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "1").
            contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
    verify(platformUserServiceMock).getPlatformUserById(anyInt(), any());
  }

  @Test
  public void get_PlatformUser_By_Id_Throws_UnauthorizedException() {
    try {
      when(platformUserServiceMock.getPlatformUserById(anyInt(), any(AppUser.Current.class)))
          .thenThrow(UnauthorizedException.class);

      mockMvc.perform(get(API_LEADING + "1").contentType(MediaType.APPLICATION_JSON_VALUE));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).getPlatformUserById(anyInt(), any());
  }

  @Test
  public void post_PlatformUser_Returns_HttpStatus_Created() throws Exception {
    mockMvc.perform(post(API_LEADING)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_PLATFORM_USER_VALID_JSON))
        .andExpect(status().isCreated());
    verify(platformUserServiceMock).createPlatformUser(any());
  }

  @Test
  public void post_PlatformUser_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(post(API_LEADING)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_PLATFORM_USER_INVALID_JSON))
        .andExpect(status().isBadRequest());
    verify(platformUserServiceMock, times(0)).createPlatformUser(any());
  }

  @Test
  public void patch_PlatformUser_Returns_HttpStatus_Accepted() throws Exception {
    mockMvc.perform(patch(API_LEADING + "1")
            .contentType("application/json-patch+json")
            .content(PATCH_PLATFORM_USER_VALID_JSON))
        .andExpect(status().isOk());
    verify(platformUserServiceMock).patchPlatformUser(anyInt(), any(JsonPatch.class), any());
  }

  @Test
  public void patch_PlatformUser_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(patch(API_LEADING + "1")
            .contentType("application/json-patch+json")
            .content(PATCH_PLATFORM_USER_INVALID_JSON))
        .andExpect(status().isBadRequest());
    verify(platformUserServiceMock, times(0)).patchPlatformUser(anyInt(), any(JsonPatch.class), any());
  }

  @Test
  public void patch_PlatformUser_Throws_UnauthorizedException() {
    try {
      when(platformUserServiceMock.patchPlatformUser(
          anyInt(), any(JsonPatch.class), any(AppUser.Current.class))).thenThrow(UnauthorizedException.class);
      mockMvc.perform(patch(API_LEADING + "1")
          .contentType("application/json-patch+json")
          .content(PATCH_PLATFORM_USER_VALID_JSON));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).patchPlatformUser(anyInt(), any(JsonPatch.class), any());
  }

  @Test
  public void delete_PlatformUser_Returns_HttpStatus_No_Content() throws Exception {
    doNothing().when(platformUserServiceMock).deletePlatformUserById(anyInt(), any(AppUser.Current.class));
    mockMvc.perform(delete(API_LEADING + "1"))
        .andExpect(status().isNoContent());
    verify(platformUserServiceMock).deletePlatformUserById(anyInt(), any());
  }

  @Test
  public void delete_PlatformUser_Throws_UnauthorizedException() {
    try {
      doThrow(UnauthorizedException.class).when(platformUserServiceMock)
          .deletePlatformUserById(anyInt(), any(AppUser.Current.class));
      mockMvc.perform(delete(API_LEADING + "1"))
          .andExpect(status().isNoContent());
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).deletePlatformUserById(anyInt(), any());
  }

  @Test
  public void get_Driver_By_Id_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "drivers/1")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
    verify(platformUserServiceMock).getDriverById(anyInt(), any());
  }

  @Test
  public void get_Driver_By_Id_Throws_UnauthorizedException() {
    try {
      when(platformUserServiceMock.getDriverById(anyInt(), any(AppUser.Current.class)))
          .thenThrow(UnauthorizedException.class);

      mockMvc.perform(get(API_LEADING + "drivers/1").contentType(MediaType.APPLICATION_JSON_VALUE));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).getDriverById(anyInt(), any());
  }

  @Test
  public void post_Driver_Returns_HttpStatus_Created() throws Exception {
    mockMvc.perform(post(API_LEADING + "1/drivers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_DRIVER_VALID_JSON))
        .andExpect(status().isCreated());
    verify(platformUserServiceMock).createDriver(anyInt(), any(), any());
  }

  @Test
  public void post_Driver_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(post(API_LEADING + "1/drivers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_DRIVER_INVALID_JSON))
        .andExpect(status().isBadRequest());

    verify(platformUserServiceMock, times(0)).createDriver(anyInt(), any(), any());
  }

  @Test
  public void post_Driver_Throws_UnauthorizedException() {
    try {
      when(platformUserServiceMock.createDriver(anyInt(), any(CreateDriverDTO.class), any())).thenThrow(
          UnauthorizedException.class);

      mockMvc.perform(post(API_LEADING + "1/drivers")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(POST_DRIVER_VALID_JSON));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).createDriver(anyInt(), any(), any());
  }

  @Test
  public void delete_Driver_Returns_HttpStatus_No_Content() throws Exception {
    doNothing().when(platformUserServiceMock).deleteDriverById(anyInt(), any());
    mockMvc.perform(delete(API_LEADING + "/drivers/1"))
        .andExpect(status().isNoContent());
    verify(platformUserServiceMock).deleteDriverById(anyInt(), any());
  }

  @Test
  public void delete_Driver_Throws_UnauthorizedException() {
    try {
      doThrow(UnauthorizedException.class).when(platformUserServiceMock).deleteDriverById(anyInt(), any());

      mockMvc.perform(delete(API_LEADING + "/drivers/1"));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).deleteDriverById(anyInt(), any());
  }

  @Test
  public void get_Passenger_By_Id_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "/passengers/1")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
    verify(platformUserServiceMock).getPassengerById(anyInt(), any());
  }

  @Test
  public void get_Passenger_By_Id_Throws_UnauthorizedException() {
    try {
      when(platformUserServiceMock.getPassengerById(anyInt(), any())).thenThrow(UnauthorizedException.class);

      mockMvc.perform(get(API_LEADING + "/passengers/1")
          .contentType(MediaType.APPLICATION_JSON_VALUE));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).getPassengerById(anyInt(), any());
  }

  @Test
  public void post_Passenger_Returns_HttpStatus_Created() throws Exception {
    mockMvc.perform(post(API_LEADING + "1/passengers")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
    verify(platformUserServiceMock).createPassenger(anyInt(), any());
  }

  @Test
  public void post_Passenger_Throws_UnauthorizedException() {
    try {
      when(platformUserServiceMock.createPassenger(anyInt(), any())).thenThrow(UnauthorizedException.class);

      mockMvc.perform(post(API_LEADING + "1/passengers")
          .contentType(MediaType.APPLICATION_JSON_VALUE));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).createPassenger(anyInt(), any());
  }

  @Test
  public void delete_Passenger_Returns_HttpStatus_No_Content() throws Exception {
    doNothing().when(platformUserServiceMock).deletePassengerById(anyInt(), any());
    mockMvc.perform(delete(API_LEADING + "/passengers/1"))
        .andExpect(status().isNoContent());
    verify(platformUserServiceMock).deletePassengerById(anyInt(), any());
  }

  @Test
  public void delete_Passenger_Throws_UnauthorizedException() {
    try {
      doThrow(UnauthorizedException.class).when(platformUserServiceMock).deletePassengerById(anyInt(), any());

      mockMvc.perform(delete(API_LEADING + "/passengers/1"));
    } catch (final Exception ex) {
      assertTrue(ex.getCause() instanceof UnauthorizedException);
    }
    verify(platformUserServiceMock).deletePassengerById(anyInt(), any());
  }
}

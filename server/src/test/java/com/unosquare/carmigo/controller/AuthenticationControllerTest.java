package com.unosquare.carmigo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.unosquare.carmigo.service.AuthenticationService;
import com.unosquare.carmigo.util.ControllerUtility;
import com.unosquare.carmigo.util.ResourceUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

  private static final String API_LEADING = "/v1/";
  private static final String POST_AUTHENTICATION_VALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PostAuthenticationValid.json");
  private static final String POST_AUTHENTICATION_INVALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PostAuthenticationInvalid.json");

  private MockMvc mockMvc;

  @Mock private ModelMapper modelMapperMock;
  @Mock private AuthenticationService authenticationServiceMock;

  private ControllerUtility controllerUtility;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(
            new AuthenticationController(modelMapperMock, authenticationServiceMock))
        .build();

    controllerUtility = new ControllerUtility(mockMvc, API_LEADING);
  }

  @Test
  public void post_Create_Authentication_Token_Returns_HttpStatus_Created() throws Exception {
    controllerUtility.makePostRequest("login", POST_AUTHENTICATION_VALID_JSON, status().isCreated());
    verify(authenticationServiceMock).createAuthenticationToken(any());
  }

  @Test
  public void post_Create_Authentication_Token_Returns_HttpStatus_BadRequest() throws Exception {
    controllerUtility.makePostRequest("login", POST_AUTHENTICATION_INVALID_JSON, status().isBadRequest());
    verify(authenticationServiceMock, times(0)).createAuthenticationToken(any());
  }
}

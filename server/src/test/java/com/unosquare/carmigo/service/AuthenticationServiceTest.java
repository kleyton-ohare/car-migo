package com.unosquare.carmigo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.unosquare.carmigo.dto.CreateAuthenticationDTO;
import com.unosquare.carmigo.dto.GrabAuthenticationDTO;
import com.unosquare.carmigo.security.UserSecurityService;
import com.unosquare.carmigo.util.JwtTokenUtils;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

  @Mock private UserSecurityService userSecurityServiceMock;
  @Mock private AuthenticationManager authenticationManagerMock;
  @Mock private JwtTokenUtils jwtTokenUtilsMock;
  @InjectMocks private AuthenticationService authenticationService;

  @Fixture private CreateAuthenticationDTO createAuthenticationDTOFixture;
  @Fixture private GrabAuthenticationDTO grabAuthenticationDTOFixture;

  @BeforeEach
  public void setUp() {
    final JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    FixtureAnnotations.initFixtures(this, jFixture);
  }

  @Test
  public void create_Authentication_Token_Returns_GrabAuthenticationDTO() {
    final UserDetails spyUserDetails = spy(new User("foo", "foo", new ArrayList<>()));
    when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(any());
    when(userSecurityServiceMock.loadUserByUsername(createAuthenticationDTOFixture.getEmail())).thenReturn(
        spyUserDetails);
    when(jwtTokenUtilsMock.generateToken(spyUserDetails)).thenReturn(anyString());
    final GrabAuthenticationDTO grabAuthenticationDTO = authenticationService.createAuthenticationToken(
        createAuthenticationDTOFixture);
    grabAuthenticationDTO.setJwt(grabAuthenticationDTOFixture.getJwt());

    assertThat(grabAuthenticationDTO.getJwt()).isEqualTo(grabAuthenticationDTOFixture.getJwt());
    verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userSecurityServiceMock).loadUserByUsername(anyString());
    verify(jwtTokenUtilsMock).generateToken(any(UserDetails.class));
  }
}

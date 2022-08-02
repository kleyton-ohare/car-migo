package com.unosquare.carmigo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.unosquare.carmigo.dto.GrabPassengerDTO;
import com.unosquare.carmigo.entity.Passenger;
import com.unosquare.carmigo.entity.PlatformUser;
import com.unosquare.carmigo.repository.PassengerRepository;
import com.unosquare.carmigo.security.Authorization;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {

  @Mock private PassengerRepository passengerRepositoryMock;
  @Mock private ModelMapper modelMapperMock;
  @Mock private EntityManager entityManagerMock;
  @Mock private Authorization authorizationMock;
  @InjectMocks private PassengerService passengerService;

  @Fixture private PlatformUser platformUserFixture;
  @Fixture private Passenger passengerFixture;
  @Fixture private GrabPassengerDTO grabPassengerDTOFixture;

  @BeforeEach
  public void setUp() {
    final JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    FixtureAnnotations.initFixtures(this, jFixture);
  }

  @Test
  public void get_Passenger_By_Id_Returns_GrabPassengerDTO() {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(passengerRepositoryMock.findById(anyInt())).thenReturn(Optional.of(passengerFixture));
    when(modelMapperMock.map(passengerFixture, GrabPassengerDTO.class)).thenReturn(grabPassengerDTOFixture);
    final GrabPassengerDTO grabPassengerDTO = passengerService.getPassengerById(1);

    assertThat(grabPassengerDTO.getId()).isEqualTo(grabPassengerDTOFixture.getId());
    assertThat(grabPassengerDTO.getPlatformUser()).isEqualTo(grabPassengerDTOFixture.getPlatformUser());
    verify(passengerRepositoryMock).findById(anyInt());
  }

  @Test
  public void create_Passenger_Returns_GrabPassengerDTO() {
    when(entityManagerMock.getReference(eq(PlatformUser.class), anyInt())).thenReturn(platformUserFixture);
    passengerService.createPassengerById(anyInt());
    verify(passengerRepositoryMock).save(any(Passenger.class));
  }

  @Test
  public void delete_Passenger_By_Id_Returns_Void() {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(passengerRepositoryMock.findById(anyInt())).thenReturn(Optional.of(passengerFixture));
    passengerService.deletePassengerById(anyInt());
    verify(passengerRepositoryMock).findById(anyInt());
  }
}

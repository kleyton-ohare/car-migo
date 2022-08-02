package com.unosquare.carmigo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.unosquare.carmigo.dto.CreateDriverDTO;
import com.unosquare.carmigo.dto.GrabDriverDTO;
import com.unosquare.carmigo.entity.Driver;
import com.unosquare.carmigo.entity.PlatformUser;
import com.unosquare.carmigo.repository.DriverRepository;
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
public class DriverServiceTest {

  @Mock private DriverRepository driverRepositoryMock;
  @Mock private ModelMapper modelMapperMock;
  @Mock private EntityManager entityManagerMock;
  @Mock private Authorization authorizationMock;
  @InjectMocks private DriverService driverService;

  @Fixture private PlatformUser platformUserFixture;
  @Fixture private Driver driverFixture;
  @Fixture private CreateDriverDTO createDriverDTOFixture;
  @Fixture private GrabDriverDTO grabDriverDTOFixture;

  @BeforeEach
  public void setUp() {
    final JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    FixtureAnnotations.initFixtures(this, jFixture);
  }

  @Test
  public void get_Driver_By_Id_Returns_GrabDriverDTO() {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(driverRepositoryMock.findById(anyInt())).thenReturn(Optional.of(driverFixture));
    when(modelMapperMock.map(driverFixture, GrabDriverDTO.class)).thenReturn(grabDriverDTOFixture);
    final GrabDriverDTO grabDriverDTO = driverService.getDriverById(1);

    assertThat(grabDriverDTO.getId()).isEqualTo(grabDriverDTOFixture.getId());
    assertThat(grabDriverDTO.getLicenseNumber()).isEqualTo(grabDriverDTOFixture.getLicenseNumber());
    assertThat(grabDriverDTO.getPlatformUser()).isEqualTo(grabDriverDTOFixture.getPlatformUser());
    verify(driverRepositoryMock).findById(anyInt());
  }

  @Test
  public void create_Driver_Returns_GrabDriverDTO() {
    final Driver spyDriver = spy(new Driver());
    when(modelMapperMock.map(createDriverDTOFixture, Driver.class)).thenReturn(spyDriver);
    when(entityManagerMock.getReference(eq(PlatformUser.class), anyInt())).thenReturn(platformUserFixture);
    when(driverRepositoryMock.save(spyDriver)).thenReturn(driverFixture);
    when(modelMapperMock.map(driverFixture, GrabDriverDTO.class)).thenReturn(grabDriverDTOFixture);
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    final GrabDriverDTO grabDriverDTO = driverService.createDriverById(1, createDriverDTOFixture);

    assertThat(grabDriverDTO.getLicenseNumber()).isEqualTo(grabDriverDTOFixture.getLicenseNumber());
    assertThat(grabDriverDTO.getPlatformUser()).isEqualTo(grabDriverDTOFixture.getPlatformUser());
    verify(driverRepositoryMock).save(any(Driver.class));
  }

  @Test
  public void delete_Driver_By_Id_Returns_Void() {
    doNothing().when(authorizationMock).verifyUserAuthorization(anyInt());
    when(driverRepositoryMock.findById(anyInt())).thenReturn(Optional.of(driverFixture));
    driverService.deleteDriverById(anyInt());
    verify(driverRepositoryMock).findById(anyInt());
  }
}

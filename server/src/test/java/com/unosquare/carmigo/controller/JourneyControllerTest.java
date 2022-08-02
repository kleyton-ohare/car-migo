package com.unosquare.carmigo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
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
import com.unosquare.carmigo.dto.GrabJourneyDTO;
import com.unosquare.carmigo.model.request.CreateCalculateDistanceCriteria;
import com.unosquare.carmigo.model.response.JourneyDriverViewModel;
import com.unosquare.carmigo.service.JourneyService;
import com.unosquare.carmigo.util.ResourceUtility;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class JourneyControllerTest {

  private static final String API_LEADING = "/v1/journeys/";
  private static final String POST_JOURNEY_VALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PostJourneyValid.json");
  private static final String POST_JOURNEY_INVALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PostJourneyInvalid.json");
  private static final String PATCH_JOURNEY_VALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PatchJourneyValid.json");
  private static final String PATCH_JOURNEY_INVALID_JSON =
      ResourceUtility.generateStringFromResource("jsonAssets/PatchJourneyInvalid.json");

  private MockMvc mockMvc;

  @Mock private ModelMapper modelMapperMock;
  @Mock private JourneyService journeyServiceMock;

  @Fixture private GrabJourneyDTO grabJourneyDTOFixture;
  @Fixture private JourneyDriverViewModel journeyViewModelFixture;
  @Fixture private List<GrabJourneyDTO> grabJourneyDTOList;

  @BeforeEach
  public void setUp() throws Exception {
    final JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    FixtureAnnotations.initFixtures(this, jFixture);

    mockMvc = MockMvcBuilders.standaloneSetup(new JourneyController(modelMapperMock, journeyServiceMock)).build();
  }

  @Test
  public void get_Journey_By_Id_Returns_HttpStatus_Ok() throws Exception {
    when(journeyServiceMock.getJourneyById(anyInt())).thenReturn(grabJourneyDTOFixture);
    when(modelMapperMock.map(grabJourneyDTOFixture, JourneyDriverViewModel.class)).thenReturn(journeyViewModelFixture);

    mockMvc.perform(get(API_LEADING + anyInt())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
    verify(journeyServiceMock).getJourneyById(anyInt());
  }

  @Test
  public void get_Journey_By_Id_Returns_HttpStatus_MethodNotAllowed() throws Exception {
    mockMvc.perform(get(API_LEADING)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isMethodNotAllowed());
    verify(journeyServiceMock, times(0)).getJourneyById(anyInt());
  }

  @Test
  public void search_Journeys_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "/search")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("locationIdFrom", "1")
            .param("locationIdTo", "2")
            .param("dateTimeFrom", "2022-12-01T09:00:00Z")
            .param("dateTimeTo", "2023-12-01T09:00:00Z"))
        .andExpect(status().isOk());
    verify(journeyServiceMock).searchJourneys(any());
  }

  @Test
  public void search_Journeys_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(get(API_LEADING + "/search")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("locationIdFrom", "1")
            .param("locationIdTo", "2"))
        .andExpect(status().isBadRequest());
    verify(journeyServiceMock, times(0)).searchJourneys(any());
  }

  @Test
  public void get_Journeys_By_Driver_Id_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "/drivers/" + anyInt())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
    verify(journeyServiceMock).getJourneysByDriverId(anyInt());
  }

  @Test
  public void get_Journeys_By_Driver_Id_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(get(API_LEADING + "/drivers/")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
    verify(journeyServiceMock, times(0)).getJourneysByDriverId(anyInt());
  }

  @Test
  public void get_Journeys_By_Passenger_Id_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "/passengers/" + anyInt())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
    verify(journeyServiceMock).getJourneysByPassengersId(anyInt());
  }

  @Test
  public void get_Journeys_By_Passenger_Id_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(get(API_LEADING + "/passengers/")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
    verify(journeyServiceMock, times(0)).getJourneysByPassengersId(anyInt());
  }

  @Test
  public void post_Journey_Returns_HttpStatus_Created() throws Exception {
    mockMvc.perform(post(API_LEADING)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_JOURNEY_VALID_JSON))
        .andExpect(status().isCreated());
    verify(journeyServiceMock).createJourney(any());
  }

  @Test
  public void post_Journey_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(post(API_LEADING)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(POST_JOURNEY_INVALID_JSON))
        .andExpect(status().isBadRequest());
    verify(journeyServiceMock, times(0)).createJourney(any());
  }

  @Test
  public void patch_Journey_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(patch(API_LEADING + "1")
            .contentType("application/json-patch+json")
            .content(PATCH_JOURNEY_VALID_JSON))
        .andExpect(status().isOk());
    verify(journeyServiceMock).patchJourney(anyInt(), any(JsonPatch.class));
  }

  @Test
  public void patch_Journey_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(patch(API_LEADING + "1")
            .contentType("application/json-patch+json")
            .content(PATCH_JOURNEY_INVALID_JSON))
        .andExpect(status().isBadRequest());
    verify(journeyServiceMock, times(0)).patchJourney(anyInt(), any(JsonPatch.class));
  }

  @Test
  public void delete_Journey_Returns_HttpStatus_No_Content() throws Exception {
    doNothing().when(journeyServiceMock).deleteJourneyById(anyInt());
    mockMvc.perform(delete(API_LEADING + anyInt()))
        .andExpect(status().isNoContent());
    verify(journeyServiceMock).deleteJourneyById(anyInt());
  }

  @Test
  public void delete_PassengerJourney_Returns_HttpStatus_No_Content() throws Exception {
    doNothing().when(journeyServiceMock).deleteByJourneyIdAndPassengerId(anyInt(), anyInt());
    mockMvc.perform(delete(API_LEADING + "1/passengers/1"))
        .andExpect(status().isNoContent());
    verify(journeyServiceMock).deleteByJourneyIdAndPassengerId(anyInt(), anyInt());
  }

  @Test
  public void calculate_Distance_Returns_HttpStatus_Ok() throws Exception {
    mockMvc.perform(get(API_LEADING + "/calculateDistance")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("locationFrom", "Belfast")
            .param("countryFrom", "GBR")
            .param("locationTo", "Newry")
            .param("countryTo", "GBR"))
        .andExpect(status().isOk());
    verify(journeyServiceMock).calculateDistance(any(CreateCalculateDistanceCriteria.class));
  }

  @Test
  public void calculate_Distance_Returns_HttpStatus_BadRequest() throws Exception {
    mockMvc.perform(get(API_LEADING + "/calculateDistance")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("locationFrom", "Belfast")
            .param("countryFrom", "GBR"))
        .andExpect(status().isBadRequest());
    verify(journeyServiceMock, times(0)).calculateDistance(any(CreateCalculateDistanceCriteria.class));
  }
}

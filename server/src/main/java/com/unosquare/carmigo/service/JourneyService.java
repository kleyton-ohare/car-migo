package com.unosquare.carmigo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.unosquare.carmigo.dto.CreateJourneyDTO;
import com.unosquare.carmigo.dto.GrabJourneyDTO;
import com.unosquare.carmigo.entity.Driver;
import com.unosquare.carmigo.entity.Journey;
import com.unosquare.carmigo.entity.Location;
import com.unosquare.carmigo.exception.PatchException;
import com.unosquare.carmigo.exception.ResourceNotFoundException;
import com.unosquare.carmigo.model.request.CreateCalculateRouteCriteria;
import com.unosquare.carmigo.model.request.CreateSearchJourneysCriteria;
import com.unosquare.carmigo.openfeign.Distance;
import com.unosquare.carmigo.openfeign.DistanceHolder;
import com.unosquare.carmigo.repository.JourneyRepository;
import com.unosquare.carmigo.repository.PassengerJourneyRepository;
import com.unosquare.carmigo.util.MapperUtils;
import java.time.Instant;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JourneyService {

  private final JourneyRepository journeyRepository;
  private final PassengerJourneyRepository passengerJourneyRepository;
  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final EntityManager entityManager;
  private final Distance distance;

  public GrabJourneyDTO getJourneyById(final int id) {
    return modelMapper.map(findJourneyById(id), GrabJourneyDTO.class);
  }

  public List<GrabJourneyDTO> searchJourneys(final CreateSearchJourneysCriteria createSearchJourneysCriteria) {
    final List<Journey> result = journeyRepository.findJourneysByLocationFromIdAndLocationToIdAndDateTimeBetween(
        createSearchJourneysCriteria.getLocationIdFrom(), createSearchJourneysCriteria.getLocationIdTo(),
        createSearchJourneysCriteria.getDateTimeFrom(), createSearchJourneysCriteria.getDateTimeTo());
    if (result.isEmpty()) {
      throw new ResourceNotFoundException("No journeys found for this route. " + createSearchJourneysCriteria);
    }
    return MapperUtils.mapList(result, GrabJourneyDTO.class, modelMapper);
  }

  public List<GrabJourneyDTO> getJourneysByDriverId(final int id) {
    final List<Journey> result = journeyRepository.findJourneysByDriverId(id);
    if (result.isEmpty()) {
      throw new ResourceNotFoundException(String.format("No journeys found for driver id %d.", id));
    }
    return MapperUtils.mapList(result, GrabJourneyDTO.class, modelMapper);
  }

  public List<GrabJourneyDTO> getJourneysByPassengersId(final int id) {
    final List<Journey> result = journeyRepository.findJourneysByPassengersId(id);
    if (result.isEmpty()) {
      throw new ResourceNotFoundException(String.format("No journeys found for passenger id %d.", id));
    }
    return MapperUtils.mapList(result, GrabJourneyDTO.class, modelMapper);
  }

  public GrabJourneyDTO createJourney(final CreateJourneyDTO createJourneyDTO) {
    final Journey journey = modelMapper.map(createJourneyDTO, Journey.class);
    journey.setCreatedDate(Instant.now());
    journey.setLocationFrom(entityManager.getReference(Location.class, createJourneyDTO.getLocationIdFrom()));
    journey.setLocationTo(entityManager.getReference(Location.class, createJourneyDTO.getLocationIdTo()));
    journey.setDriver(entityManager.getReference(Driver.class, createJourneyDTO.getDriverId()));
    return modelMapper.map(journeyRepository.save(journey), GrabJourneyDTO.class);
  }

  public GrabJourneyDTO patchJourney(final int id, final JsonPatch patch) {
    final GrabJourneyDTO grabJourneyDTO = modelMapper.map(findJourneyById(id), GrabJourneyDTO.class);
    try {
      final JsonNode journeyNode = patch.apply(objectMapper.convertValue(grabJourneyDTO, JsonNode.class));
      final Journey patchedJourney = objectMapper.treeToValue(journeyNode, Journey.class);
      return modelMapper.map(journeyRepository.save(patchedJourney), GrabJourneyDTO.class);
    } catch (final JsonPatchException | JsonProcessingException ex) {
      throw new PatchException(String.format("It was not possible to patch journey id %d - %s", id, ex.getMessage()));
    }
  }

  public DistanceHolder calculateRoute(final CreateCalculateRouteCriteria createCalculateRouteCriteria) {
    final String request = prepareRequestToDistanceOpenFeign(createCalculateRouteCriteria);
    return distance.getDistance(request);
  }

  public void deleteJourneyById(final int id) {
    journeyRepository.deleteById(id);
  }

  @Transactional
  public void deleteByJourneyIdAndPassengerId(final int journeyId, final int passengerId) {
    passengerJourneyRepository.deleteByJourneyIdAndPassengerId(journeyId, passengerId);
  }

  private Journey findJourneyById(final int id) {
    return journeyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("Journey id %d not found.", id)));
  }

  private String prepareRequestToDistanceOpenFeign(final CreateCalculateRouteCriteria criteria) {
    final StringBuilder request = new StringBuilder("[{\"t\":");
    request.append("\"").append(criteria.getCityFrom()).append(",").append(criteria.getCountryFrom()).append("\"");
    request.append("},{\"t\":");
    request.append("\"").append(criteria.getCityTo()).append(",").append(criteria.getCountryTo()).append("\"");
    request.append("}]");
    return request.toString();
  }
}

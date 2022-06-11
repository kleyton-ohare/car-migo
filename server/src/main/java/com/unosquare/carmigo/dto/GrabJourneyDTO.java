package com.unosquare.carmigo.dto;

import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class GrabJourneyDTO {

  private int id;

  private Instant createdDate;

  private GrabLocationDTO locationFrom;

  private GrabLocationDTO locationTo;

  private int maxPassengers;

  private Instant dateTime;

  private GrabDriverDTO driver;

  private List<GrabPassengerDTO> passengers;
}

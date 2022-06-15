package com.unosquare.carmigo.openfeign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MidpointProperty {

  private String type;

  private Geocode geocode;

  private List<Region> regions;

  private List<Airport> airports;

  private Stats stats;
}

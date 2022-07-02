package com.unosquare.carmigo.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "passenger_journey")
public class PassengerJourney {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id", updatable = false, nullable = false)
  private int id;

  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "passenger_id", nullable = false)
  private Passenger passenger;

  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "journey_id", nullable = false)
  private Journey journey;
}

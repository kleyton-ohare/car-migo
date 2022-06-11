package com.unosquare.carmigo.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDriverViewModel {

  @Size(max = 100)
  @NotEmpty
  @NotNull
  @JsonProperty("licenseNumber")
  private String licenseNumber;
}

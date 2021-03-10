package de.deftone.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FreeLocation {

    @NotBlank
    private String name;
    @NotBlank
    private String location;

}

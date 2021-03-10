package de.deftone.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GivenLocation {

    @NotBlank
    private String name;
    @NotBlank
    private String id;

    public Long getIdFromString(){
        // im model ist es ein String, wir brauchen aber das Long
        // da im html die id als Long uebergeben wird sollte es hier nie knallen!
        return Long.valueOf(this.id);
    }

}

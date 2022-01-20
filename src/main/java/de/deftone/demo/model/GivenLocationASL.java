package de.deftone.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GivenLocationASL {

    @NotBlank
    private String vorUndNachName;
    @NotBlank
    private String strasseHausNr;
    @NotBlank
    private String plzOrt;
    @NotBlank
    private String emailAdresse;
    @NotBlank
    private String weitereTeilnehmer;
    private String id;
    private String freeLocation;

    public Long getIdFromString() {
        // im model ist es ein String, wir brauchen aber das Long
        // da im html die id als Long uebergeben wird sollte es hier nie knallen!
        if (this.id.isEmpty() || this.id.isBlank()){
            return -1L;
        }

        return Long.valueOf(this.id);
    }

}

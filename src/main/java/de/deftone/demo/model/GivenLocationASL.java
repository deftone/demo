package de.deftone.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GivenLocationASL {

    @NotBlank(message = "Bitte Vor- und Nachname eingeben, z.B. Max Mustermann")
    private String vorUndNachName;
    @NotBlank(message = "Bitte Straße und Hausnr eingeben, z.B. Darmstädter Str. 1")
    private String strasseHausNr;
    @NotBlank(message = "Bitte PLZ und Ort eingeben, z.B. 64380 Rodorf")
    private String plzOrt;
    @NotBlank(message = "Bitte eine gültige Email-Adresse eingeben, z.B. Max@Mustermann.de")
    private String emailAdresse;
    @NotBlank(message = "Bitte eine Telefonnummer eingeben, z.B. 06154-123456")
    private String telefonNr;
    private String weitereTeilnehmer;
    private String id;
    private String freeLocation;
    private String personenDaten;
    private String fotosMachen;

    public Long getIdFromString() {
        // im model ist es ein String, wir brauchen aber das Long
        // da im html die id als Long uebergeben wird sollte es hier nie knallen!
        if (this.id == null || this.id.isEmpty() || this.id.isBlank()){
            return -1L;
        }

        return Long.valueOf(this.id);
    }

}

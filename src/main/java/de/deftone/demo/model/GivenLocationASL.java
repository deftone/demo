package de.deftone.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class GivenLocationASL {

    @NotBlank(message = "Bitte Vor- und Nachname eingeben, z.B. Max Mustermann")
    @Size(max = 100, message = "Bitte maximal 100 Zeichen eingeben.")
    private String vorUndNachName;

    @NotBlank(message = "Bitte Straße und Hausnr eingeben, z.B. Darmstädter Str. 1")
    @Size(max = 100, message = "Bitte maximal 100 Zeichen eingeben.")
    private String strasseHausNr;

    @NotBlank(message = "Bitte PLZ und Ort eingeben, z.B. 64380 Rodorf")
    @Size(max = 100, message = "Bitte maximal 100 Zeichen eingeben.")
    private String plzOrt;

    @NotBlank(message = "Bitte eine gültige Email-Adresse eingeben, z.B. Max@Mustermann.de")
    @Size(max = 100, message = "Bitte maximal 100 Zeichen eingeben.")
    private String emailAdresse;

    @NotBlank(message = "Bitte eine Telefonnummer eingeben, z.B. 06154-123456")
    @Size(max = 100, message = "Bitte maximal 100 Zeichen eingeben.")
    private String telefonNr;

    @Size(max = 250, message = "Bitte maximal 250 Zeichen eingeben")
    private String weitereTeilnehmer;

    private String id;

    @Size(max = 100, message = "Bitte maximal 100 Zeichen eingeben.")
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

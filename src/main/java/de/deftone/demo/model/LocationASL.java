package de.deftone.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LocationASL implements Comparable<LocationASL> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idString;

    private String name;

    private Boolean free;


    @Override
    public int compareTo(LocationASL o) {
        return this.getIdString().compareTo(o.getIdString());
    }

    // wird direkt im html template aufgerufen
    public String getFrei(){
        if (this.free){
            return "Ja";
        }
        else return "Nein";
    }

    //wird aus html template aufgerufen
    public String getCompactIdAndName(){
        return this.idString+"/"+this.name;
    }
}

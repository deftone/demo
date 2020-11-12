package de.deftone.demo.service;

import de.deftone.demo.model.Location;
import de.deftone.demo.repo.LocationRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LocationService {

    private final LocationRepo locationRepo;

    public List<Location> getAllLocations() {
        return locationRepo.findAll();
    }

    public List<Location> getFreeLocations() {
        return locationRepo.findAll()
                .stream()
                .filter(l -> !l.getBooked())
                .collect(Collectors.toList());
    }

    public Location addLocation(Location location) {
        long count = locationRepo.findAll()
                .stream()
                .filter(l -> l.getName().equalsIgnoreCase(location.getName().trim()))
                .count();
        if (count == 0L) {
            location.setName(location.getName().trim());
            return locationRepo.save(location);
        } else {
            //todo: bessere Exception!
            throw new RuntimeException("Location " + location.getName().trim() + " already exists!");
        }
    }

    public List<Location> resetAllLocation() {
        for (Location location : locationRepo.findAll()) {
            location.setBooked(false);
            locationRepo.save(location);
        }
        return locationRepo.findAll();
    }

    public String getLocationNameById(int id) {
        Optional<Location> byId = locationRepo.findById(id);
        if (byId.isPresent()) {
            return byId.get().getName();
        }
        return "no location with id " + id;
    }

    public void setLocationToBooked(int id) {
        Location location = locationRepo.findById(id)
                //todo: bessere exception
                .orElseThrow(() -> new RuntimeException("Keine Location mit id " + id));
        location.setBooked(true);
        locationRepo.save(location);
    }
}

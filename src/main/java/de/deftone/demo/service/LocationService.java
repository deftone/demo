package de.deftone.demo.service;

import de.deftone.demo.model.Location;
import de.deftone.demo.repo.LocationRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepo locationRepo;

    public LocationService(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

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
        //todo: pruefen, dass nicht doppelt
        return locationRepo.save(location);
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

    public void setLocationToBooked(int id){
        Location location = locationRepo.findById(id)
                //todo: bessere exception
                .orElseThrow(() -> new RuntimeException("Keine Location mit id " + id));
        location.setBooked(true);
        locationRepo.save(location);
    }
}

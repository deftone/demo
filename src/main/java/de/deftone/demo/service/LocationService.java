package de.deftone.demo.service;

import de.deftone.demo.model.Location;
import de.deftone.demo.repo.LocationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepo locationRepo;

    public LocationService(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    public List<Location> getAllLocations() {
        return locationRepo.findAll();
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
}

package de.deftone.demo.service;

import de.deftone.demo.model.Location;
import de.deftone.demo.model.LocationASL;
import de.deftone.demo.repo.LocationASLRepo;
import de.deftone.demo.repo.LocationRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LocationService {

    private final LocationRepo locationRepo;
    private final LocationASLRepo locationASLRepo;

    public List<Location> getAllLocations() {
        List<Location> all = locationRepo.findAll();
        Collections.sort(all);
        return all;
    }

    public List<LocationASL> getAllASLLocations() {
        List<LocationASL> all = locationASLRepo.findAll();
        Collections.sort(all);
        return all;
    }

    public List<Location> getFreeLocations() {
        return locationRepo.findAll()
                .stream()
                .filter(Location::getFree)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<LocationASL> getFreeASLLocations() {
        return locationASLRepo.findAll()
                .stream()
                .filter(LocationASL::getFree)
                .sorted()
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

    // hier ohne exception, alle die gehen hinzufuegen
    public List<Location> addLocationList(List<Location> locations) {
        List<Location> savedLocations = new ArrayList<>();
        for (Location location : locations) {
            long count = locationRepo.findAll()
                    .stream()
                    .filter(l -> l.getName().equalsIgnoreCase(location.getName().trim()))
                    .count();
            if (count == 0L) {
                location.setName(location.getName().trim());
                savedLocations.add(locationRepo.save(location));
            }
        }
        return savedLocations;
    }

    public List<LocationASL> addASLLocationList(List<LocationASL> locations) {
        List<LocationASL> savedLocations = new ArrayList<>();
        for (LocationASL location : locations) {
            long count = locationASLRepo.findAll()
                    .stream()
                    .filter(l -> l.getName().equalsIgnoreCase(location.getName().trim()))
                    .count();
            if (count == 0L) {
                location.setName(location.getName().trim());
                savedLocations.add(locationASLRepo.save(location));
            }
        }
        return savedLocations;
    }

    public List<Location> resetAllLocation() {
        for (Location location : locationRepo.findAll()) {
            location.setFree(true);
            locationRepo.save(location);
        }
        return locationRepo.findAll();
    }

    public List<LocationASL> resetAllASLLocations() {
        for (LocationASL location : locationASLRepo.findAll()) {
            location.setFree(true);
            locationASLRepo.save(location);
        }
        return locationASLRepo.findAll();
    }

    public String getLocationNameById(long id) {
        Optional<Location> byId = locationRepo.findById(id);
        if (byId.isPresent()) {
            return byId.get().getName();
        }
        return "no location with id " + id;
    }

    public String getASLLocationNameById(long id) {
        Optional<LocationASL> byId = locationASLRepo.findById(id);
        if (byId.isPresent()) {
            return byId.get().getName();
        }
        return "no location with id " + id;
    }

    public void setLocationToBooked(long id) {
        Location location = locationRepo.findById(id)
                //todo: bessere exception
                .orElseThrow(() -> new RuntimeException("Keine Location mit id " + id));
        location.setFree(false);
        locationRepo.save(location);
    }

    public void setASLLocationToBooked(long id) {
        LocationASL location = locationASLRepo.findById(id)
                //todo: bessere exception
                .orElseThrow(() -> new RuntimeException("Keine Location mit id " + id));
        location.setFree(false);
        locationASLRepo.save(location);
    }

    public void deleteLocation(long id) {
        Location location = locationRepo.findById(id)
                //todo: bessere exception
                .orElseThrow(() -> new RuntimeException("Keine Location mit id " + id));
        locationRepo.delete(location);
    }

    public void deleteAllLocation() {
        locationRepo.deleteAll();
    }
}

package de.deftone.demo.service;

import de.deftone.demo.model.Location;
import de.deftone.demo.repo.LocationASLRepo;
import de.deftone.demo.repo.LocationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepo locationRepoMock;

    @Mock
    private LocationASLRepo locationASLRepoMock;

    private LocationService service;

    @BeforeEach
    void setUp() {
        service = new LocationService(locationRepoMock, locationASLRepoMock);
    }

    @Test
    void locationGibtEsSchonMitAndererSchreibweise() {
        when(locationRepoMock.findAll()).thenReturn(createLocations());
        try {
            //testet auch indirekt den trimm!
            service.addLocation(new Location(4L, "R2", "  rehBERg ", false));
            fail();
        } catch (RuntimeException e) {
            assertEquals("Location rehBERg already exists!", e.getMessage());
        }
    }

    @Test
    void locationGibtEsNochNicht() {
        when(locationRepoMock.findAll()).thenReturn(createLocations());

        Location newLocation = new Location(4L, "R2", "Spielplatz", false);
        when(locationRepoMock.save(newLocation)).thenReturn(newLocation);

        Location savedLocation = service.addLocation(newLocation);

        assertEquals(newLocation.getId(), savedLocation.getId());
        assertEquals(newLocation.getName(), savedLocation.getName());
        assertEquals(newLocation.getFree(), savedLocation.getFree());
    }

    @Test
    void locationListHinzufuegen() {
        Location locationOK = new Location(4L, "R4", "B38", true);
        Location locationOK2 = new Location(6L, "R6", "xyz", true);
        when(locationRepoMock.findAll()).thenReturn(createLocations());
        when(locationRepoMock.save(locationOK)).thenReturn(locationOK);
        when(locationRepoMock.save(locationOK2)).thenReturn(locationOK2);
        List<Location> newLocations = new ArrayList<>();
        newLocations.add(new Location(2L, "R2", "Ortskern", true));
        newLocations.add(locationOK);
        newLocations.add(new Location(5L, "R5", "Rehberg", true));
        newLocations.add(locationOK2);
        List<Location> locations = service.addLocationList(newLocations);
        assertEquals(2, locations.size());
        assertEquals("R4", locations.get(0).getIdString());
        assertEquals("R6", locations.get(1).getIdString());
    }

    private List<Location> createLocations() {
        List<Location> list = new ArrayList<>();
        list.add(new Location(1L, "R1", "Grundschule", true));
        list.add(new Location(2L, "R2", "Ortskern", true));
        list.add(new Location(3L, "R3", "Rehberg", true));
        return list;
    }
}
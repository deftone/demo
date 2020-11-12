package de.deftone.demo.service;

import de.deftone.demo.model.Location;
import de.deftone.demo.repo.LocationRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
// Klasse muss public sein, sonst wird kein Test erkannt!
public class LocationServiceTest {

    @Mock
    private LocationRepo locationRepoMock;

    private LocationService service;

    @Before
    public void setUp() {
        service = new LocationService(locationRepoMock);
    }

    @Test
    public void locationGibtEsSchonMitAndererSchreibweise() {
        when(locationRepoMock.findAll()).thenReturn(createLocations());
        try {
            //testet auch indirekt den trimm!
            service.addLocation(new Location(4L, "  rehBERg ", false));
            fail();
        } catch (RuntimeException e) {
            assertEquals("Location rehBERg already exists!", e.getMessage());
        }
    }

    @Test
    public void locationGibtEsNochNicht() {
        when(locationRepoMock.findAll()).thenReturn(createLocations());

        Location newLocation = new Location(4L, "Spielplatz", false);
        when(locationRepoMock.save(newLocation)).thenReturn(newLocation);

        Location savedLocation = service.addLocation(newLocation);

        assertEquals(newLocation.getId(), savedLocation.getId());
        assertEquals(newLocation.getName(), savedLocation.getName());
        assertEquals(newLocation.getBooked(), savedLocation.getBooked());
    }

    private List<Location> createLocations() {
        List<Location> list = new ArrayList<>();
        list.add(new Location(1L, "Grundschule", true));
        list.add(new Location(2L, "Ortskern", true));
        list.add(new Location(3L, "Rehberg", true));
        return list;
    }
}
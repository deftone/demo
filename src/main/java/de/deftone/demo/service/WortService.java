package de.deftone.demo.service;

import de.deftone.demo.model.Wort;
import de.deftone.demo.repo.WortRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WortService {

    private final WortRepo wortRepo;

    public void addWort(String word) {
        Wort wort = new Wort();
        wort.setWord(word);
        wort.setBenutzt(false);
        wortRepo.save(wort);
    }

    public List<Wort> getAllWords(){
        return wortRepo.findAll();
    }

    public String getWort() {
        List<Wort> all = wortRepo.findAll();
        Optional<Wort> first = all.stream().filter(w -> !w.isBenutzt()).findFirst();
        if (first.isPresent()){
            Wort wort = first.get();
            wort.setBenutzt(true);
            wortRepo.save(wort);
            return  wort.getWord();
        }
        return "kein Wort mehr vorhanden!";
    }
}

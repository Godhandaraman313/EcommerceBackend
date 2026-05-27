
package com.project.ecommerce.controller;

import com.project.ecommerce.model.Country;
import com.project.ecommerce.repository.CountryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:5173")
public class CountryController {

    private final CountryRepository repo;

    public CountryController(CountryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/countries/list")
    public List<Country> listCountries() {
        return repo.findAll();
    }

    @GetMapping("/api/countries")
    public List<Country> getAll() {
        return repo.findAll();
    }
}
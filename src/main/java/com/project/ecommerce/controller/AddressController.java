
package com.project.ecommerce.controller;

import com.project.ecommerce.model.Address;
import com.project.ecommerce.repository.AddressRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/address_book")
@CrossOrigin("http://localhost:5173")
public class AddressController {

    private final AddressRepository repo;

    public AddressController(AddressRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Map<String, Object> getAddresses(Authentication auth) {

        String email = auth.getName();

        List<Address> addresses = repo.findByEmail(email);

        Address primary = addresses.stream()
                .findFirst()
                .filter(Address::isDefaultForShipping)
                .orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("addresses", addresses);
        response.put("primary", primary);

        return response;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
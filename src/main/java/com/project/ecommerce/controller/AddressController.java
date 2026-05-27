package com.project.ecommerce.controller;

import com.project.ecommerce.dto.AddressRequest;
import com.project.ecommerce.model.Address;
import com.project.ecommerce.service.AddressService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/address_book")
@CrossOrigin("http://localhost:5173")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public Map<String, Object> getAddresses(Authentication auth) {
        return addressService.listForUser(auth.getName());
    }

    @GetMapping("/{id}")
    public Address getAddress(@PathVariable Long id, Authentication auth) {
        return addressService.getById(id, auth.getName());
    }

    @PostMapping
    public Address create(@RequestBody AddressRequest request, Authentication auth) {
        return addressService.create(auth.getName(), request);
    }

    @PutMapping("/{id}")
    public Address update(
            @PathVariable Long id,
            @RequestBody AddressRequest request,
            Authentication auth
    ) {
        return addressService.update(id, auth.getName(), request);
    }

    @PatchMapping("/{id}/default")
    public Address setDefault(@PathVariable Long id, Authentication auth) {
        return addressService.setDefault(id, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        addressService.delete(id, auth.getName());
    }
}

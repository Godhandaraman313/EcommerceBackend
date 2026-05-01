
package com.project.ecommerce.repository;

import com.project.ecommerce.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
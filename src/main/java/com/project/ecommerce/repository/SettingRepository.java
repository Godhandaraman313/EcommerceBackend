package com.project.ecommerce.repository;

import com.project.ecommerce.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Setting findByKey(String key);
    java.util.List<Setting> findByCategory(String category);
}

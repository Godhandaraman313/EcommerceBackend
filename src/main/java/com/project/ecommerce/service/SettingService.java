package com.project.ecommerce.service;

import com.project.ecommerce.model.Setting;
import com.project.ecommerce.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> getAllSettings() {
        return settingRepository.findAll();
    }

    public Optional<Setting> getSettingByKey(String key) {
        return Optional.ofNullable(settingRepository.findByKey(key));
    }

    public List<Setting> getByCategory(String category) {
        return settingRepository.findByCategory(category);
    }

    public Iterable<Setting> saveAll(Iterable<Setting> settings) {
        return settingRepository.saveAll(settings);
    }

    public Setting createOrUpdateSetting(Setting setting) {
        // if key exists, update value/type
        Setting existing = settingRepository.findByKey(setting.getKey());
        if (existing != null) {
            existing.setValue(setting.getValue());
            existing.setType(setting.getType());
            return settingRepository.save(existing);
        }
        return settingRepository.save(setting);
    }

    public void deleteByKey(String key) {
        Setting existing = settingRepository.findByKey(key);
        if (existing != null) {
            settingRepository.delete(existing);
        }
    }
}

package com.project.ecommerce.controller;

import com.project.ecommerce.model.Setting;
import com.project.ecommerce.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@CrossOrigin("http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingController {

    @Autowired
    private SettingService settingService;

    @GetMapping
    public ResponseEntity<List<Setting>> getAllSettings() {
        return ResponseEntity.ok(settingService.getAllSettings());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Setting>> getSettingsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(settingService.getByCategory(category));
    }

    @PostMapping("/save_general")
    public ResponseEntity<Iterable<Setting>> saveGeneral(@RequestBody List<Setting> settings) {
        return ResponseEntity.ok(settingService.saveAll(settings));
    }

    @PostMapping("/save_mail_server")
    public ResponseEntity<Iterable<Setting>> saveMailServer(@RequestBody List<Setting> settings) {
        return ResponseEntity.ok(settingService.saveAll(settings));
    }

    @PostMapping("/save_mail_templates")
    public ResponseEntity<Iterable<Setting>> saveMailTemplates(@RequestBody List<Setting> settings) {
        return ResponseEntity.ok(settingService.saveAll(settings));
    }

    @PostMapping("/save_payment")
    public ResponseEntity<Iterable<Setting>> savePayment(@RequestBody List<Setting> settings) {
        return ResponseEntity.ok(settingService.saveAll(settings));
    }

    @GetMapping("/{key}")
    public ResponseEntity<Setting> getSetting(@PathVariable String key) {
        return settingService.getSettingByKey(key)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Setting> createOrUpdate(@RequestBody Setting setting) {
        Setting saved = settingService.createOrUpdateSetting(setting);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        settingService.deleteByKey(key);
        return ResponseEntity.noContent().build();
    }
}

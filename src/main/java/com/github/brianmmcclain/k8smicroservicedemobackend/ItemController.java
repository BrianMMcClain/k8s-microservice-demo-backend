package com.github.brianmmcclain.k8smicroservicedemobackend;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ItemController {

    private final ItemRepository repository;

    @Autowired
    Environment env;

    @Autowired
    JdbcTemplate jdbcTemplate;

    ItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/item")
    List<Item> all() {
        return repository.findAll();
    }

    @GetMapping("/item/{id}")
    Item getOne(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException());
    }

    @GetMapping("/info")
    public String getConfig() {
        Map<String, String> payload = new HashMap<>();
        String profiles = "[" + String.join(", ", this.env.getActiveProfiles()) + "]";
        String databaseType = "Unknown";
        try {
            databaseType = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        payload.put("profiles", profiles);
        payload.put("database", databaseType);
        
        try {
			return new ObjectMapper().writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}
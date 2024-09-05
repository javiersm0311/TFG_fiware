package com.javiersanchez.irregularitiesApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javiersanchez.irregularitiesApp.persistence.entity.Zonas;
import com.javiersanchez.irregularitiesApp.service.ZonaService;

import java.util.List;

@RestController
@RequestMapping("/zonas")
public class ZonasController {
    @Autowired
    private ZonaService zonaService;

    @GetMapping
    public ResponseEntity<List<Zonas>> getZonas(@RequestParam String unidadId) {
        System.out.println(unidadId);
        List<Zonas> zonas = zonaService.getZonasByUnidad(unidadId);
        System.out.println(zonas);
        return ResponseEntity.ok(zonas);
    }

    @DeleteMapping("/{zonaId}")
    public ResponseEntity<Void> deleteZona(@PathVariable String zonaId) {
        boolean isRemoved = zonaService.deleteZona(zonaId);
        if (isRemoved) {
            return ResponseEntity.noContent().build(); // Código 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Código 404 Not Found
        }
    }

}

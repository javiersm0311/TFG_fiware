package com.javiersanchez.irregularitiesApp.controller;

import org.springframework.web.bind.annotation.RestController;
import com.javiersanchez.irregularitiesApp.persistence.entity.Unidad;
import com.javiersanchez.irregularitiesApp.persistence.entity.Zonas;
import com.javiersanchez.irregularitiesApp.persistence.repository.UnidadRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class NotificationController {
    @Autowired
    private UnidadRepository UnidadRepository;

    @PostMapping("/notification")
    public void handleNotification(@RequestBody String notification) {
        try {
            JSONObject jsonNotification = new JSONObject(notification);
            JSONArray dataArray = jsonNotification.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                String unidadId = dataObject.getString("id");
                String unidadType = dataObject.getString("type");
                JSONObject zonasObject = dataObject.getJSONObject("zonas");

                Unidad unidad = new Unidad();
                unidad.setId(unidadId);
                unidad.setType(unidadType);

                JSONArray valueArray = zonasObject.getJSONArray("value");
                List<Zonas> zonaList = new ArrayList<>();

                for (int j = 0; j < valueArray.length(); j++) {
                    JSONObject zoneWrapperObject = valueArray.getJSONObject(j);

                    // Itera sobre las claves del JSONObject
                    Iterator<String> keys = zoneWrapperObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject zonaObject = zoneWrapperObject.getJSONObject(key);

                        Zonas zona = new Zonas();
                        zona.setZonaId(zonaObject.getString("id"));
                        zona.setType(zonaObject.getString("type"));
                        zona.setZoneValue(zonaObject.getString("value"));
                        zona.setUnidad(unidad);
                        zonaList.add(zona);
                    }
                }
                unidad.setZonas(zonaList);
                UnidadRepository.save(unidad);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

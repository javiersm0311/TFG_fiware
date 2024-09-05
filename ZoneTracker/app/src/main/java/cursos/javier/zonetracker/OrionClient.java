package cursos.javier.zonetracker;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class OrionClient {

    private static final String TAG = "OrionClient";
    private static final String ORION_URL = "http://10.0.2.2:1026/v2/entities";
    private RequestQueue requestQueue;

    public OrionClient(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    public void createEntity(String unidad) {
        JSONObject updateRequest = new JSONObject();
        try {
            updateRequest.put("id", "unidad" + unidad);
            updateRequest.put("type", "UnidadMantenimiento");
            updateRequest.put("zonas", new JSONObject().put("value", new JSONArray()));

            System.out.println((updateRequest));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Make a POST request to update the entity attribute
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ORION_URL,
                updateRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response == null || response.length() == 0) {
                    // Respuesta vacía
                    Log.d(TAG, "Respuesta vacía del servidor");
                } else {
                    // Procesar respuesta JSON
                    Log.d(TAG, "Respuesta del servidor: " + response.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e(TAG, "Error creating entity: " + error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void updateEntityAttribute(float latitude, float longitude, String unidad) {
        JSONObject zonaNueva = new JSONObject();
        int randomId = (int)(Math.random() * 90000) + 10000;
        String randomIdStr = String.valueOf(randomId);
        try {
            zonaNueva.put(randomIdStr, new JSONObject()
                    .put("id", randomIdStr)
                    .put("type", "geo:point")
                    .put("value", latitude + "," + longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ORION_URL + "/unidad" + unidad + "/attrs/zonas/value", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("RESPUESTA: " + response);
                        try {
                            List<JSONObject> zonesList = new ArrayList<>();
                            if (response != null) {
                                for (int i = 0; i < response.length(); i++) {
                                    zonesList.add(response.getJSONObject(i));
                                }
                            }
                            zonesList.add(zonaNueva);

                            // Crear la petición de actualización
                            JSONObject zonas = new JSONObject();
                            zonas.put("type", "listaZonas");

                            JSONArray zonasArray = new JSONArray(zonesList);
                            zonas.put("value", zonasArray);

                            System.out.println("LISTA: " + zonesList);
                            JSONObject updateRequest = new JSONObject();
                            updateRequest.put("zonas", zonas);

                            //enviar la peticion
                            updateEntityInBroker(updateRequest, unidad);
                            System.out.println("REQUEST: " + updateRequest);
                            Log.d(TAG, "Entidad recibida correctamente");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error getting entity: " + error.getMessage());
                    }
                });


        requestQueue.add(request);



    }

    private void updateEntityInBroker(JSONObject updateRequest, String unidad) {
        JsonObjectRequest  jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, ORION_URL + "/unidad"+unidad+"/attrs",
                updateRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    // Procesar respuesta JSON
                    Log.d(TAG, "Respuesta del servidor: " + response.toString());
            }
        },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    Log.e(TAG, "Error updating entity: " + error.toString());
                }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void deleteZoneFromOrion(String zoneId,String unidadId, final DeleteZoneCallback callback) {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ORION_URL + "/unidad" + unidadId + "/attrs/zonas/value", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("RESPUESTA: " + response);
                        try {
                            List<JSONObject> zonesList = new ArrayList<>();
                            if (response != null) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject zone = response.getJSONObject(i);
                                    // Verificar si la zona no es la que se quiere eliminar
                                    if (!zone.has(zoneId)) {
                                        zonesList.add(zone);
                                    }
                                }
                            }

                            // Crear la petición de actualización
                            JSONObject zonas = new JSONObject();
                            zonas.put("type", "listaZonas");

                            JSONArray zonasArray = new JSONArray(zonesList);
                            zonas.put("value", zonasArray);

                            System.out.println("LISTA: " + zonesList);
                            JSONObject updateRequest = new JSONObject();
                            updateRequest.put("zonas", zonas);

                            //enviar la peticion
                            updateEntityInBroker(updateRequest, unidadId);

                            System.out.println("REQUEST: " + updateRequest);
                            Log.d(TAG, "Entidad recibida correctamente");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error getting entity: " + error.getMessage());
                    }
                });
        requestQueue.add(request);
    }

    interface ZonesCallback {
        void onSuccess(List<String> zones);

        void onError(VolleyError error);
    }
}


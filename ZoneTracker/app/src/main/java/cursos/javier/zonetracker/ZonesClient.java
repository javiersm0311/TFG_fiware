package cursos.javier.zonetracker;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ZonesClient {

    private static final String TAG = "ZonesClient";
    private static final String SERVER_URL = "http://10.0.2.2:1029";
    private RequestQueue mRequestQueue;


    public ZonesClient(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    public void getZones(String valor, final ZonesCallback callback) {
        String url = SERVER_URL + "/zonas?unidadId=" + valor;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Zona> zonas = new ArrayList<>();
                            System.out.println(response);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject zonaObject = response.getJSONObject(i);
                                Zona zona = new Zona();
                                zona.setID(zonaObject.getString("zonaId"));
                                zona.setCOORDENADAS(zonaObject.getString("zoneValue"));
                                zonas.add(zona);
                            }
                            callback.onSuccess(zonas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.getMessage());
                    }
                });
        System.out.println(request);
        mRequestQueue.add(request);
    }

    public void deleteZone(String id, final DeleteZoneCallback callback) {
        String url = SERVER_URL + "/zonas/" + id;
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Respuesta inesperada del servidor");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            int statusCode = networkResponse.statusCode;
                            if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                                callback.onError("Zona no encontrada");
                            } else {
                                callback.onError("Error al eliminar la zona: " + statusCode);
                            }
                        } else {
                            callback.onError("Error de red: " + error.getMessage());
                        }
                    }
                });

        mRequestQueue.add(request);
    }
}


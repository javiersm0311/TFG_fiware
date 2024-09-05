package cursos.javier.zonetracker;

import java.util.List;

public interface ZonesCallback {
    void onSuccess(List<Zona> zonas);
    void onError(String error);
}

package cursos.javier.zonetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

    private Button startButton;
    private Button zonesButton;
    private final static int MAIN = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), UnidadActivity.class);
                startActivity(intent);
            }
        });
        zonesButton = (Button) findViewById(R.id.zonesButton);
        zonesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ZonasActivity.class);
                startActivity(intent);
            }
        });

    };

};

package cursos.javier.zonetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class UnidadActivity extends Activity {

    private Button cordobaButton;
    private Button sevillaButton;
    private Button cadizButton;
    private Button huelvaButton;
    private Button almeriaButton;
    private Button jaenButton;
    private Button granadaButton;
    private Button malagaButton;
    private Button atrasButton;



    private final static int MAIN = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unidad);

        cordobaButton = (Button) findViewById(R.id.cordobaButton);
        cadizButton = (Button) findViewById(R.id.cadizButton);
        huelvaButton= (Button) findViewById(R.id.huelvaButton);
        sevillaButton = (Button) findViewById(R.id.sevillaButton);
        granadaButton = (Button) findViewById(R.id.granadaButton);
        almeriaButton = (Button) findViewById(R.id.almeriaButton);
        jaenButton = (Button) findViewById(R.id.jaenButton);
        malagaButton = (Button) findViewById(R.id.malagaButton);


        cordobaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "cordoba");
                startActivity(intent);
            }
        });
        cadizButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "cadiz");
                startActivity(intent);
            }
        });
        huelvaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "huelva");
                startActivity(intent);
            }
        });
        sevillaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "sevilla");
                startActivity(intent);
            }
        });
        malagaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "malaga");
                startActivity(intent);
            }
        });
        jaenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "jaen");
                startActivity(intent);
            }
        });
        granadaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "granada");
                startActivity(intent);
            }
        });
        almeriaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("unidad", "almeria");
                startActivity(intent);
            }
        });
        atrasButton = (Button) findViewById(R.id.atrasButton);
        atrasButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onBackPressed();

            }
        });

    };

};

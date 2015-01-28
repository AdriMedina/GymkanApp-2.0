package npi.example.puntofotobrujula;

import npi.example.puntofotobrujula.ActividadBrujula;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity implements OnItemSelectedListener{

	// Lista desplegable 
	private Spinner spPuntosCardinales;
	
	// Variables que guardan que elemento del spinner se ha seleccionado
	private String cardSeleccionado;
	private int cardPosicion;
	
	/**
	 * Método que se inicia al crear la actividad principal
	 * Dentro de él, creamos el spinner que contiene la lista de los puntos cardinales
	 * y ejecutamos el método que genera los valores que contendrá ese spinner, es decir, 
	 * los puntos cardinales.
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.spPuntosCardinales = (Spinner) findViewById(R.id.sp_puntos_cardinales);
        loadSpinnerPuntosCardinales();
        
    }
    
    /**
     * Función necesaria cuando se implementa OnItemSelectedListener.
     * 
     * Guarda el elemento seleccionado en el spinner.
     */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		cardSeleccionado = parent.getItemAtPosition(position).toString();
		cardPosicion = position;
	}

	/**
     * Función necesaria cuando se implementa OnItemSelectedListener.
     * 
     * Se ejecuta cuando no se selecciona ningún elemento del spinner.
     */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
    
    /**
     * Método que carga los distintos puntos cardinales en el spinner.
     */
    private void loadSpinnerPuntosCardinales(){
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
    			this, R.array.puntosCardinales, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	this.spPuntosCardinales.setAdapter(adapter);
    	spPuntosCardinales.setOnItemSelectedListener(this);
    }

    /**
     * Función que se activa al pulsar el boton Buscar de la interfaz para pasar a la siguiente actividad.
     * 
     * @param view
     */
    public void botonBuscarActividadBrujula(View view){
		Intent act = new Intent(this, ActividadBrujula.class);
		act.putExtra("seleccion", cardSeleccionado);
		act.putExtra("posicion", cardPosicion);
		startActivity(act);
	}
}

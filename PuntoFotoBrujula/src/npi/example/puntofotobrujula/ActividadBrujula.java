package npi.example.puntofotobrujula;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ActividadBrujula extends Activity implements SensorEventListener {

	// TextViews

    private TextView infoSeleccionado;

    // Objeto de la clase ImageView
    private ImageView imagenBrujula;
    private ImageView imagenFlecha;
    
    // Variables para controlar los grados de la brujula
    private float actualGrado = 0.0f;
    private float ultimoGrado = 0.0f;
    private float gradoMin;
    private float gradoMax;

    // Variables que contienen el elemento seleccionado del spinner
    private int posicionSeleccionado;
    private String cardinalSeleccionado;
    
    // Objeto de la clase SensorManager para cargar la brujula
    private SensorManager mSensorManager;

	// Variables de la camara
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private ImageView imgPreview;
    private int nVeces = 0;

	
    /**
	 * Método que se inicia al crear la actividad.
	 * Dentro de él, inicializamos todos los objetos y variables que nos harán falta. 
	 * Obtiene los valores seleccionados del spinner a traves del Intent en la actividad principal y 
	 * llama a la función buscaCardinalSeleccinado.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_brujula);
		
        imagenBrujula = (ImageView) findViewById(R.id.imagenBrujula);
        imagenFlecha = (ImageView) findViewById(R.id.imagenFlecha);
        
        infoSeleccionado = (TextView) findViewById(R.id.tv_info_seleccionado);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        cardinalSeleccionado = getIntent().getExtras().getString("seleccion");
        posicionSeleccionado = getIntent().getExtras().getInt("posicion");
        
        buscaCardinalSeleccionado();
	}

	/**
     * Método que se activa cuando el usuario va a interactuar con la aplicación.
     * 
     * Instancia el sensor a utilizar, en este caso, la brujula.
     */
	@Override
    protected void onResume() {
        super.onResume();

        // Continue listening the orientation sensor
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Método llamado cuando se pausa la actividad.
     * 
     * Se detiene el registro de los valores de la brujula
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Stop listening the sensor
        mSensorManager.unregisterListener(this);
    }
	
    /**
	 * Método necesario cuando se implementa SensorEventListener y que se llama cada vez que cambian los valores. 
	 * 
	 * Obtenemos el valor del grado actual de la brujula.
	 * 
	 * Es necesario separar la comprobación del punto cardinal "Norte" del resto, ya que no podemos
	 * medirlo igual para crear un rango de posibilidad en la que se puede encontrar de la misma manera 
	 * que el resto de puntos cardinales.
	 * 
	 * Creamos la animación de la brujula y la giramos según el valor del grado y el anterior
	 * 
	 */
    @Override
    public void onSensorChanged(SensorEvent event) {
    	
        // Conseguimos el valor del grado de la brujula
        ultimoGrado = Math.round(event.values[0]);

        // Si el punto cardinal NO es el norte ...
        if(posicionSeleccionado != 0){
        	// Comprobamos que la brujula se encuentre dentro del rango permitido apuntando resto de puntos cardinales
	        if (ultimoGrado > gradoMin && ultimoGrado < gradoMax) {
	        	imagenFlecha.setImageResource(R.drawable.flecha_verde);
	        	
	        	// Controlamos que lance solo una fotografia cada vez que apunta
	        	nVeces++;
	        	if(nVeces == 1){
	        		capturarImagen();
	        		Toast.makeText(getApplicationContext(), 
	        				"Tomando fotografia.. No mueva la cámara", Toast.LENGTH_SHORT).show();
	        	}
	        } else {
	        	
	        	imagenFlecha.setImageResource(R.drawable.flecha_roja);
	        	nVeces = 0;
	        }
	    // Si el punto cardinal SI es el norte ...
        }else{
        	// Comprobamos que la brujula se encuentre dentro del rango permitido apuntando al norte
        	if ((ultimoGrado > gradoMin && ultimoGrado <= 359) || (ultimoGrado >= 0 && ultimoGrado < gradoMax)) {
        		imagenFlecha.setImageResource(R.drawable.flecha_verde);
        		
	        	// Controlamos que lance solo una fotografia cada vez que apunta
	        	nVeces++;
	        	if(nVeces == 1){
	        		capturarImagen();
	        		Toast.makeText(getApplicationContext(), 
	        				"Tomando fotografia.. No mueva la cámara", Toast.LENGTH_SHORT).show();
	        	}
	        } else {
	        	imagenFlecha.setImageResource(R.drawable.flecha_roja);
	        	nVeces = 0;
	        }
        }
        
        // Creamos y ejecutamos la animación de la brujula
        RotateAnimation imRotacion;
        imRotacion = new RotateAnimation(
                actualGrado,
                -ultimoGrado,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        imRotacion.setDuration(300);
        imRotacion.setFillAfter(true);
        imagenBrujula.startAnimation(imRotacion);

        // Actualizamos los grados
        actualGrado = -ultimoGrado;
    }

    /**
	 * Método necesario cuando se implementa SensorEventListener
	 */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Función que asigna el rango según el punto cardinal seleccionado en el spinner
     */
    public void buscaCardinalSeleccionado(){
    	infoSeleccionado.setText("Estoy buscando el " + cardinalSeleccionado);
    	switch (posicionSeleccionado) {
	    	// Norte
			case 0:
				gradoMin = 345; gradoMax = 15;
				break;		
			// Sur
			case 1:
				gradoMin = 165; gradoMax = 195;
				break;
			// Oeste
			case 2:
				gradoMin = 255; gradoMax = 285;
				break;
			// Este
			case 3:
				gradoMin = 75; gradoMax = 105;
				break;
			default:
				break;
		}
    }
    
    /**
     * Función que inicia la cámara para capturar la foto
     */
    private void capturarImagen() {
    	// Dar un nombre a la fotografía para almacenarla en la carpeta por defecto del movil
        String formatoFecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    	
    	ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMG_" + formatoFecha + ".jpg");
 
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);    
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
     
        // activa el intent de la captura
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    /**
     * Método que manda algun resultado a la actividad que la ha invocado
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprobar la solicitud del usuario de guardar la fotografía
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previsualizacionImagenCapturada();  
                onStop();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "Usuario ha cancelado la captura de imagen", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "ERROR! Fallo al capturar la imagen", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    /**
     * Método que muestra una previsualización de la imagen capturada
     */
    private void previsualizacionImagenCapturada() {
        try {
            imgPreview.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),options);
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    
    
}

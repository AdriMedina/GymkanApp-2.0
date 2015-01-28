/*
 * Detecta gesto en "L".
 */
package npi.example.puntogestosqr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity{
	
	//Etiquetas para la salida de datos.
	private static final String DEBUG_TAG = "Fase";
	
	//String donde se almacenar� la informaci�n recogida con el detector de gestos QR.
	String posicion;
		
	//Variables necesarias para comprobar gestos.
	float x_inicial, y_inicial, x_actual, y_actual, y_final;
	boolean correcto = true;
	
	//Objeto utilizado para dibujar y detectar el gesto.
	Gesto gestoUsuario;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gestoUsuario = new Gesto(this);
		setContentView(gestoUsuario);
		gestoUsuario.requestFocus();
	}
	
	/*
	 * Funci�n que se activa cada vez que hay un evento t�ctil.
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent evento)
	{
		/*
		 * El objeto "gestoUsuario" declarado en "onCreate()" contendr� un valor 
		 * llamado "fase" que puede ser "-1" o "3". Si el valor es -1 significar�
		 * que el gesto "L" no ha sido realizado correctamente.
		 * Por el contrario, si devuelve un 3 significar� que el gesto se ha
		 * realizado de forma correcta. (Ver "src/Gesto.java")
		 */
		Log.d(DEBUG_TAG, "Fase:" + gestoUsuario.fase);
		/*
		 * @see: http://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application
		 */
		/*
		 * Si el gesto del usuario es correcto (fase = 3)
		 */
		if(gestoUsuario.fase == 3)
		{
			/*
			 * La actividad intentar� abrir la aplicaci�n "Barcode Scanner" que es open source para el escaneo
			 * del c�digo QR. (https://play.google.com/store/apps/details?id=com.google.zxing.client.android).
			 */
			try
			{
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				/*
				 * Empezamos la actividad con el Intent definido anteriormente y con
				 * c�digo de solicitud = 0. El c�digo servir� para identificar 
				 */
				startActivityForResult(intent, 0);
			}
			/*
			 * Si no puede abirla, crear� una excepci�n en la que se abrir� Google Play para descargar la
			 * aplicaci�n.
			 */
			catch(Exception e)
			{
				Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
			    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
			    startActivity(marketIntent);
			}
		}		
		
		return true;
	}
	
	/*
	 * Esta funci�n se llamar� para recoger los resultados del intent en el que llamamos
	 * a Barcode Scanner.
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 0)
		{
			/*
			 * Si el resultado es correcto, almacenamos en el string 'posicion' el contenido
			 * del c�digo QR analizado.
			 */
			if(resultCode == RESULT_OK)
			{
				/*
				 * Capturamos el string y lo almacenamos en posici�n.
				 */
				posicion = data.getStringExtra("SCAN_RESULT");
				/*
				 * Llamamos a la funci�n que mostar� los resuldatos obtenidos.
				 */
				MostrarResultados();
	
			}
		}
	}
	
	/*
	 * Funci�n que muestra por patantalla los datos obtenidos por el escaner QR.
	 */
	public void MostrarResultados()
	{
		/*
		 * Muestro el layout que tiene los display para mostrar las coordenadas
		 * recogidas.
		 */
		setContentView(R.layout.activity_main);

		if(posicion.contains("LATITUD") && posicion.contains("LONGITUD"))
		{
			/*
			 * Descomponemos es String que contiene los datos de manera que el String division
			 * queda de la siguiente manera:
			 * 
			 * 		division ={	LATITUD,	30.543...(datos),	LONGITUD,	-3.753...(longitud)};
			 * 						[0]				[1]				[2]				[3] 
			 */
			String[] division = posicion.split("_");

			/*
			 * Ense�amos las componentes de "division" que nos iteresan (1 y 3) en el display
			 * correspondiente.
			 */
			TextView display = (TextView) findViewById(R.id.displayLatitud);
			
			display.setText(division[1]);
			
			display = (TextView) findViewById(R.id.displayLongitud);
			
			display.setText(division[3]);
		}
		
		/*
		 * Mientras se muestra la informaci�n en los correspondientes displays, fijamos el fondo como
		 * un "OnClickListener" a la espera de que se toque la pantalla.
		 */
		final View fondo = (View) findViewById(R.id.fondo);
		fondo.setOnClickListener(new View.OnClickListener() {
			/*
			 * Cuando se toca la panatalla, se le devuelve el focus al objeto "gesto usuario" por si se
			 * desea capturar un nuevo c�digo QR tras hacer el gesto de nuevo. 
			 */
			@Override
			public void onClick(View v) {
				setContentView(gestoUsuario);
				gestoUsuario.requestFocus();
			}
		});	
	}
}





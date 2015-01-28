/*
 * Ver:
 * https://github.com/zoraidacallejas/sandra/tree/master/Apps/ASRWithIntent
 */
package npi.example.puntogpsvoz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.RecognizerIntent;


public class MainActivity extends Activity {
	
	/*
	 * Google implementa dos modos de reconocimiento de lenguaje.
	 * Yo he escogido "LANGUAGE_MODEL_FREE_FORM" ya que el otro está basado
	 * en lenguaje para la web.
	 */
	private String languageModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
	private int numeroResultados = 1;
	
	/*
	 * Código empleado para el objeto Intent que invocará al reconocimiento de voz.
	 */
	private static int codigo_solicitud_hablar = 123;
	/*
	 * Etiqueta para la salida de datos.
	 */
	private static final String DEBUGTAG = "cadena";
	
	/*
	 * Controla en qué campo se han de intrucir mediante el habla, una de las coordenadas, ya sea
	 * latitud o longitud.
	 */
	private boolean estado = false;
	/*
	 * String que contendrán los datos obtenidos mediante el reconocimiento de voz.
	 */
	private String datos_latitud = new String();
	private String datos_longitud = new String();
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * Configuramos el botón "Hablar" para que haga lo necesario
         * cuando se pulsa.
         */
        setSpeakButton();
    }
    
    /*
     * Tras pulsar el botón "Hablar" se ejecuta esta función. Lo que hace es 
     * invocar el reconocimiento del habla de Google.
     */
    private void escuchar()
    {
    	/*
    	 * Cada vez que escuchamos, modificamos el estado a su valor contrario para
    	 * insertar el valor escuchado en un campo distinto del anterior. Por ejemplo
    	 * si la ultima vez que se escuchó se introdujo la latitud, cuando volvamos a 
    	 * escuchar de nuevo introduciremos la longitud.
    	 */
    	estado = !estado;
    	
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    	/*
    	 * Añadimos información extra para el reconocedor de voz, como el modo de 
    	 * reconocimiento del lenguaje, y el número de resultados que queremos obtener
    	 * como máximo.
    	 */
    	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);
    	
    	intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, numeroResultados);
    	/*
    	 * Iniciamos el reconocedor con el código apropiado para capturarlo mas adelante.
    	 */
    	startActivityForResult(intent, codigo_solicitud_hablar);
    }
    
    /*
     * Cuando tocas el boton "Hablar" llama a la función escuchar.
     */
    @SuppressLint("DefaultLocale")
	private void setSpeakButton() {
    	
    	Button boton_hablar = (Button) findViewById(R.id.talk_button);
    	
    	boton_hablar.setOnClickListener(new View.OnClickListener() {
    		@Override
			public void onClick(View v) {
    			escuchar();
    		}
    	});
    }
    
    /*
     * Recoge los valores devueltos por el analizador de voz.
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @SuppressLint("InlinedApi")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	/*
    	 * Comprobamos que la llamada se realizó para el código con el que enviamos el intent.
    	 */
    	if(requestCode == codigo_solicitud_hablar)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			if(data != null)
    			{    				
    				/*
    				 * Obtenemos los datos en un String y los mandamos a la función que los analizará.
    				 */
    				String datos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
    				analizarDatos(datos);
    			}
    		}
    	}
    }
    
    /*
     * Función que invoca la navegación GPS si se ha pronunciado "correcto".
     */
    private void GPS()
    {
    	/*
    	 * Formamos el Uri con las coordenadas que recibirá la navegación GPS con los
    	 * datos recogidos en la función "analizarDatos"
    	 */
    	//Uri coordenadas = Uri.parse("geo:0,0?q="+datos_latitud+","+datos_longitud);
        Uri coordenadas = Uri.parse("http://maps.google.com/maps?daddr="+datos_latitud+","+datos_longitud);

    	/*
    	 * Iniciamos la navegación GPS.
    	 */
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	/*
    	 * Incluimos información extra al Intent que son las coordenadas a las que queremos ir.
    	 */
    	intent.setData(coordenadas);
    	
    	if(intent.resolveActivity(getPackageManager())!=null)
			startActivity(intent);
    }
    
    /*
     * Función que analiza los datos recogidos por el analizador de voz.
     */
    private void analizarDatos(String datos)
    {    	
    	Log.d(DEBUGTAG, datos);
    	TextView correcto = (TextView) findViewById(R.id.display_correcto);
    	
    	String salida = "";
    	/*
    	 * En "elementos" guardamos una lista de las palabras que contiene datos.
    	 */
    	List<String> elementos = new ArrayList<String>(Arrays.asList(datos.split(" ")));
    	
    	/*
    	 * Si hemos recogido datos de longitud y latitud y se incluye la palabra "correcto"
    	 */
    	if(datos_longitud.length() > 0 && datos_latitud.length() > 0 && datos.contains("correcto"))
    	{
    		correcto.setVisibility(0);
    		/*
   	    	 * Lanzamos la navegación GPS.
   	    	 */
   			GPS();
    	}
    	/*
    	 * Si no:
    	 */
    	else
    	{
    		/*
    		 * 
    		 * Si el usuario quiere introducir un valor de latitud, elminamos la palabra de la lista
    		 * y modificamos el estado para introducir el valor en el campo correcto.
    		 */
    		if(elementos.contains("latitud")){
    			elementos.remove(elementos.indexOf("latitud"));
    			estado = true;
    		}
    		/*
    		 * Si el usuario quiere introducir un valor de longitud, elminamos la palabra de la lista
    		 * y modificamos el estado para introducir el valor en el campo correcto.
    		 */
    		else if(elementos.contains("longitud")){
    			elementos.remove(elementos.indexOf("longitud"));
    			estado = false;
    		}
    		
    		/*
    		 * Si hemos recogido un valor negativo con la palabra "menos", sustituimos
    		 * esa palabra en la lista por el "caracter menos" "-"
    		 */
    		if(elementos.contains("menos"))
    			elementos.set(elementos.indexOf("menos"), "-");
    		/*
    		 * Hacemos lo mismo que para el caso de la palabra "menos" pero para el
    		 * caracter que separa la parte entera de la decimal en un número en coma flotante.
    		 * Comprobamos las distintas formas de decirlo y alunos errores que se puedan
    		 * producir en el análisis de la frase y los sustituimos por el caracter "."
    		 */
    		if(elementos.contains("coma"))
    			elementos.set(elementos.indexOf("coma"), ".");
    		else if(elementos.contains("punto"))
    			elementos.set(elementos.indexOf("punto"), ".");
    		else if(elementos.contains("puntos"))
    			elementos.set(elementos.indexOf("puntos"), ".");
    		else if(elementos.contains("como"))
    			elementos.set(elementos.indexOf("como"), ".");
    		
    		/*
    		 * Hacemos lo mismo con los números que detecta como letra.
    		 * Resta eficiencia pero ganamos en utilidad.
    		 */
    		if(elementos.contains("uno"))
    			elementos.set(elementos.indexOf("uno"), "1");
    		if(elementos.contains("dos"))
    			elementos.set(elementos.indexOf("dos"), "2");
    		if(elementos.contains("tres"))
    			elementos.set(elementos.indexOf("tres"), "3");
    		if(elementos.contains("cuatro"))
    			elementos.set(elementos.indexOf("cuatro"), "4");
    		if(elementos.contains("cinco"))
    			elementos.set(elementos.indexOf("cinco"), "5");
    		if(elementos.contains("seis"))
    			elementos.set(elementos.indexOf("seis"), "6");
    		if(elementos.contains("siete"))
    			elementos.set(elementos.indexOf("siete"), "7");
    		if(elementos.contains("ocho"))
    			elementos.set(elementos.indexOf("ocho"), "8");
    		if(elementos.contains("nueve"))
    			elementos.set(elementos.indexOf("nueve"), "9");
    		
    		/*
    		 * Concatenamos los elementos de la lista para formar un único string que será el
    		 * que se muestre por pantalla y el que reciba el GPS.
    		 */
    		for(int i=0; i<elementos.size(); i++)
    			salida = salida + elementos.get(i);
    	
    		Log.d(DEBUGTAG, salida);
    		
    		/*
    		 * Si estado = true es equivalente a decir, si el usuario pretende introducir
    		 * la latitud o bien, el último elemento que se introdujo es la longitud.
    		 * En ese caso:
    		 */
    		if(estado)
    		{
    			/*
    			 * Modificamos el display de la latitud con el texto "salida".
    			 */
    			TextView latitud = (TextView) findViewById(R.id.displayLatitud);
    			latitud.setText(salida);
    			datos_latitud = salida;
    		}
    		/*
    		 * Si estado = false es equivalente a decir, si el usuario pretende introducir
    		 * la longitud o bien, el último elemento que se introdujo es la latitud.
    		 * En ese caso:
    		 */
    		else
    		{
    			/*
    			 * Modificamos el display de la longitud con el texto "salida".
    			 */
    			TextView longitud = (TextView) findViewById(R.id.displayLongitud);
    			longitud.setText(salida);
    			datos_longitud = salida;
    		}
    		if(datos_longitud.length() > 0 && datos_latitud.length() > 0)
    			correcto.setVisibility(1);
    	}
    }
    
    /*
     * Función que carga la actividad Ayuda.java tras pulsar el boton de ayuda, que muestra
     * unas indicaciones por pantalla de cómo usar la aplicación.
     */
    public void ayuda(View v)
    {
    	Intent intent = new Intent(this, Ayuda.class);
    	this.startActivity(intent);
    }
 }

/*
 * Fuente:
 * https://thenewcircle.com/s/post/1036/android_2d_graphics_example
 */

package npi.example.puntogestosqr;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressLint("ClickableViewAccessibility")
public class Gesto extends View implements OnTouchListener{
	
/*
 * 	Declaración de variables necesarias.
 */
	/*
	 * Conjunto de etiquetas para diferenciar las salidas por pantalla.
	 */
	private static final String TAG = "DrawView";
	private static final String TAG1 = "Comprobación";
	
	/*
	 *  Lista de puntos que almacenará los puntos que van a ser pintados
	 *  por pantalla.
	 */
    List<Point> points = new ArrayList<Point>();
    
    /*
     *  Objeto de tipo Paint que sirve para seleccionar el color en el que
     *  queremos pintar los puntos de la lista anterior.
     */
    Paint paint = new Paint();
    
    /*
     *  Conjunto de variables usadas para hacer un tracking correcto del
     *  gesto.
     */
    float x_inicial, y_inicial, x_actual, y_actual, check_pointX, check_pointY;
    
    /*
     *	Fase de detección del gesto. Al detectar el gesto " L " hay 3 fases claramente
     *	diferenciadas.
     *	Definimos la fase 0 como la fase en la que se realiza el scroll vertical, la
     *	fase 1 como la fase en la que el usuario deja de hacer scroll vertial y comienza
     *	a realizar el scroll horizontal y la fase 2 que no servirá para comprobar la
     *	validez de ese scroll horizontal.
     */
    int fase = 0;

    /*
     * Constructor de la clase en el que indicamos que puede ser foco
     * de eventos táctiles.
     */
    public Gesto(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        paint.setColor(getResources().getColor(R.color.pintura_azul));
        paint.setAntiAlias(true);
    }

    /*
     * Función de dibujado. Cada vez que se lanza dibuja el conjunto
     * de puntos de la lista de puntos recogidos
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    public void onDraw(Canvas canvas) {
    		for (Point point : points)
    			canvas.drawCircle(point.x, point.y, 5, paint);
    }
    
    /*
     * Función que se activa siempre que hay un evento táctil.
     * Recibe una vista y el tipo de evento que ha recogido. 
     * 
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    public boolean onTouch(View view, MotionEvent evento) {    		
   
    	/*
    	 * En función del tipo de evento recogido, se actuará de
    	 * una forma distinta.
    	 */
       	switch(evento.getActionMasked()){
    		/*
    		 * Caso en el que se acaba de situar el dedo sobre la pantalla.
    		 */
    		case(MotionEvent.ACTION_DOWN):
    			
    			/*
    			 * Limpio la lista de puntos que se hubiesen recogido en
    			 * detecciones de gestos anteriores.
    			 */
    			points.clear();
    			/*
    			 * La llamada a la función invalidate() nos asegura que
    			 * la función de dibujado será llamada en un futuro.
    			 */
    			invalidate();
    			/*
    			 * Estas dos acciónes combinadas nos permiten limpiar la
    			 * pantalla de puntos cada vez que comencemos a realizar
    			 * un nuevo gesto.
    			 */
    			
    			/*
    			 *	Selecciono el color de la pintura por defecto con la que se
    			 * 	pintarán los puntos y el color de fondo.
    			 */    			
	        	paint.setColor(getResources().getColor(R.color.pintura_azul));
    			view.setBackgroundColor(getResources().getColor(R.color.fondo_gris_coloreando));
	        	/*
	        	 *	Recojo el punto inicial en el que empieza el gesto y lo selecciono
	        	 *	como punto actual.
	        	 */
    			x_inicial = check_pointX = evento.getX(evento.getPointerId(0));
    			y_inicial = check_pointY = evento.getY(evento.getPointerId(0));
    			/*
    			 * 	Indicamos la fase de detección actual.
    			 */
    			fase=0;
    			/*
    			 * Salida por pantalla de las coordenadas.
    			 */
    			Log.d(TAG1,"Punto inicial: " + x_inicial + " - " + y_inicial);
        		
    			break;
        		
    		/*
    		 * Caso en el que un dedo se está moviendo por la pantalla
    		 */
    		case(MotionEvent.ACTION_MOVE):
    			/*
    			 * Capuramos el punto del dedo en todo momento.
    			 */
    			x_actual = evento.getX(evento.getPointerId(0));
    			y_actual = evento.getY(evento.getPointerId(0));
    			/*
    			 * Según los valores recogidos de la posición actual
    			 * se analizan con respecto a la posición inicial.
    			 */
    			switch (fase)
    			{
    				/*
    				 * Fase 0: Detección del scroll vertical hacia abajo.
    				 */
    				case 0:
    	    			/*
    	    			 * Este bloque 'if' controla que no se realicen las siguientes acciones:
    	    			 * 	-El scroll sea hacia arriba.
    	    			 * 	-El scroll vertical no se desvíe a la izquierda o derecha dentro
    	    			 *	de un rango de 50 pixels.
    	    			 *
    	    			 * Si alguna de estas cosas sucede, marcaremos el gesto como incorrecto
    	    			 * introduciéndolo en la fase de error -1.
    	    			 */
    					if(y_actual + 25 < y_inicial || x_actual + 50 < x_inicial || x_actual - 50 > x_inicial)
    	    				fase = -1;
    					/*
    					 * Este bloque 'if' comprueba que, si scroll vertical está siendo correcto
    					 * y ha alcanzado una longitud como mínimo de 250 pixels, pasamos a la fase 1.
    					 */
    					if(y_actual > y_inicial+250)
    	    				fase = 1;
    					
    					break;
    				/*
    				 * Fase 1: El scroll vertical ha sido correcto y ha alcanzado el mínimo de longitud
    				 * establecida en la fase 0 (250 pixels). Sin embargo el usuario puede seguir
    				 * con el scroll vertical hasta cuando le convenga y el gesto sería igualmente
    				 * correcto. Es por esto que esta fase se encarga de controlar cuándo el usuario ha
    				 * dejado de hacer scroll vertical para comenzar a hacer el scroll horizontal.
    				 */
    				case 1:
    					/*
    					 * Si el scroll es hacia arriba o hacia la izquierda entramos en fase de error.
    					 */
    					if(y_actual > y_inicial + 250 &&  x_actual + 50 < x_inicial)
    	    				fase = -1;
    					
    					/*
    					 * Si el scroll avanza 50 pixels hacia la derecha consideramos que el usuario
    					 * ha comenzado el scroll horizontal. Por tanto pasamos a la fase 2.
    					 * Actualizamos la posición inicial para comenzar a detectar el scroll horizontal.
    					 */
    					if(x_actual - 50 > x_inicial)
    	    			{
    	    				fase = 2;
    	    				y_inicial = y_actual;
    	    				x_inicial = x_actual;
    	    			}
    					
    					break;
    				/*
    				 * Fase 2: Detección del scroll vertical hacia la derecha.	
    				 */
    				case 2:
    					/*
    					 * Si mientras detectamos el scroll horizontal hacia la derecha el usuario
    					 * hace un scroll hacia la izquierda o sobrepasa 50 pixels hacia arriba o hacia
    					 * abajo, enviamos el gesto a la fase de error.
    					 */
    					if(x_actual + 25 < x_inicial ||	y_actual + 50 < y_inicial || y_actual - 50 > y_inicial )
    		    			fase = -1;
    					
    					break;
    					
    				case -1:
    					break;
    			}
    			
    			//Log.d(TAG1, "Fase " + fase );
    			
    			/*
    			 *	Por motivos de eficiencia se dibuja solo cuando el dedo se ha
    			 *	movido por lo menos 20 pixels en cualquier dirección para no
    			 *	saturar dibujando cada vez que se avanza un pixel.
    			 */
    			if( check_pointX < x_actual-20 || check_pointX > x_actual+20 ||
    				check_pointY < y_actual-20 || check_pointY > y_actual+20)
    			{
    				/*
    				 * Capturamos el punto que será dibujado y actualizamos el nuevo
    				 * punto de referencia (check_point).
    				 */
    				Point point = new Point();
    				point.x = check_pointX = x_actual;
    				point.y = check_pointY = y_actual;
    				/*
    				 * Y lo añadimos a la lista.
    				 */
    				points.add(point);
    				/*
    				 * Por último dibujamos los puntos que tengamos hasta el momento.
    				 */
	    			invalidate();
	    			/*
	    			 * Salida por pantalla del punto recogido.
	    			 */
	    			Log.d(TAG, "point: " + point );
    			}
    		
    			break;
    		
    		/*
    		 *	Caso en el que se detecta que se deja de tocar la pantalla
    		 *	con el dedo:
    		 */
    		case(MotionEvent.ACTION_UP):
    			/*
    			 * Si al levantar el dedo el gesto no se encuentra en fase de error, si no que 
    			 * ha alcanzado la última fase y el scroll horizontal ha tenido una longitud
    			 * entre 75 y 350 pixels de largo consideramos el gesto concluido satisfactoriamente
    			 * (fase 3) en caso contrario el gesto se considera incorrecto.
    			 */
    			if(fase == 2 && x_actual > x_inicial + 75 && x_actual < x_inicial + 350 )
    				fase = 3;
    			else
    				fase = -1;
			//Log.d(TAG1, "Faseee " + fase );
    			/*
    			 * Si lo recopilado por el caso "MotionEvent.ACTION_MOVE" determina que
    			 * el gesto no ha sido correcto:
    			 */
    			if(fase == -1)
    			{
    				/*
    				 * Pintamos los puntos con la pintura roja.
    				 */
    		        paint.setColor(getResources().getColor(R.color.pintura_roja));
    		        invalidate();
    			}
    			break;
    	}
       	/*
       	 * Cuando termina el gesto (ACTION_UP) devolvemos la información 
       	 * recogida a la actividad principal. En particular solo nos interesa
       	 * saber la 'fase' en la que terminó el gesto.
       	 */
       	if(evento.getActionMasked() == MotionEvent.ACTION_UP)
       		return super.onTouchEvent(evento);
        
       	return true;
    }
}

class Point {
    float x, y;

    @Override
    public String toString() {
        return x + ", " + y;
    }
}
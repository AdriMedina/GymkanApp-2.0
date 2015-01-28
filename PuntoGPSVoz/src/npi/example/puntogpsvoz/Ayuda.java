package npi.example.puntogpsvoz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Ayuda extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		@SuppressWarnings("unused")
		Intent intent = getIntent();
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.ayuda);
	}
}
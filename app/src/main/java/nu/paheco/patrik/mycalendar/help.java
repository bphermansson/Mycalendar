package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by user on 11/1/14.
 */
public class help extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        Log.d("Help", "In onCreate()");
        TextView helptext = (TextView)findViewById(R.id.helptext);
        helptext.setTypeface(null, Typeface.BOLD);
        helptext.setText("Hjälp för Förskoleplaneraren");
        helptext.setTypeface(null, Typeface.NORMAL);
        helptext.append("Detta program används för att skriva ut ett schema över barnens förskoletider.\n" +
                "För att använda programmet behövs ett Google-konto med en Google-kalender. I denna kalender skapas en särskild kalender " +
                "för barnet där tiderna när barnet ska vara på förskolan läggs in." +
                "Överst i Förskoleplaneraren syns namnet på din Google-kalender. Har du flera kalendrar kan du peka på namnet och välja barnets kalender." +
                "De händelser som finns inlagda i kalendern kommer då att visas i sammanställningen." +
                "Under kalendernamnet visas aktuell vecka. Som standard visas aktuell vecka, och det går att bläddra i årets veckor med hjälp av piltangenterna. " +
                "Till höger om pilarna finns en rund symbol, peka på denna för att komma tillbaka till aktuell vecka. " +
                "Nästa symbol visar en skrivare. Peka på denna för att se en förhandsgranskning över det som kommer att skrivas ut." +
                "");
        //TextView curinfo=(TextView)findViewById(R.id.curinfo);

    }
}

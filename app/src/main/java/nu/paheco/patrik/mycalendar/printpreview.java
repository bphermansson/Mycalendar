package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by user on 10/10/14.
 */
public class printpreview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Printpreview", "In onCreate()");

        //setContentView(R.layout.activity_main);
        //TextView curinfo = (TextView) findViewById(R.id.curinfo );
        //String sweeknow = curinfo.getText().toString();
        String sweeknow = Constants.week;
        Integer endweek = Integer.valueOf(sweeknow)+4;
        Log.d("Fetched week:", sweeknow);
        String[] arrday = Constants.arrdayStore;
        //String[] arrDayMonth = Constants.arrdayMonthStore;
        String[] arrdate = Constants.arrdateStore;

        //Test
        for (int mday=0; mday<28; mday++) {
            Log.d("Weekday: ", arrday[mday]);
        }


            // Strings for html-code
        String[] dayline;
        String[] eventline;

        Integer noofevents = Constants.noofevents;
        Log.d("No of events:", String.valueOf(noofevents));

        dayline=new String [28];
        eventline = new String[100];

        // Start html-line
        String htmlDocument =
                "<html>" +
                        "<head>" +
                        "<style>table { border-collapse: collapse; }" +
                        "tr { border: solid thin; }" +
                        "td { padding: 3px; }" +
                        "</style>" +
                        "</head>" +
                        "<body><h1>Vecka " + sweeknow  +"-"+ endweek + "</h1>" +
                        "<table>";


        // Loop through dates
        for (int day=0; day<28; day++) {
            dayline[day] = arrday[day] + " " + arrdate[day];

            // Modify so first char is uppercase
            dayline[day] = Character.toUpperCase(dayline[day].charAt(0))
                    + dayline[day].subSequence(1, dayline[day].length()).toString();
            htmlDocument = htmlDocument + "<tr><td>" + dayline[day] + "</td>";


//            Log.d("Fetched day-date:", arrdate[day] + "-" + arrday[day]);
//            Log.d("Fetched day-date:", dayline[day]);

            eventline[day]="";

            // Loop through events
            for (int x = 0; x < noofevents - 1; x++) {
                // Find date for event
                String start = Constants.arreventsStore[x][2];
                Long ds = Long.valueOf(start);
                String rstart = new SimpleDateFormat("yyyy/MM/dd")
                        .format(new Date(ds));

                if (rstart.equals(arrdate[day])) {
                    // Fetch event end time
                    String end = Constants.arreventsStore[x][3];
                    Long de = Long.valueOf(end);
                    //Convert to real time
                    String starttime = new SimpleDateFormat("HH:mm").format(new Date(ds));
                    String endtime = new SimpleDateFormat("HH:mm").format(new Date(de));

                    //eventline[day] = eventline[day] + starttime + "-" + endtime + "-" + Constants.arreventsStore[x][1];
                    eventline[day] = starttime + "-" + endtime + "-" + Constants.arreventsStore[x][1];

                    htmlDocument = htmlDocument + "<td>" + eventline[day] + "</td>";

                    Log.d("?", "Match!");
                    Log.d("Event: ", arrdate[day] + "-" + arrday[day] + ":" +
                            starttime + "-" + endtime + "-" + Constants.arreventsStore[x][1]);

                }
            }
            htmlDocument = htmlDocument + "</tr>";
        }

        //Log.d("test", dayline[1]);
        //Log.d("test", eventline[1]);

        // End html-line
        htmlDocument = htmlDocument + "</table></body></html>";
        Log.d("HTML", htmlDocument);

        // Select current view
        setContentView(R.layout.printpreview);
        WebView web = (WebView) findViewById(R.id.webInfo);

        web.loadDataWithBaseURL(null, htmlDocument,
                "text/HTML", "UTF-8", null);
    }


}

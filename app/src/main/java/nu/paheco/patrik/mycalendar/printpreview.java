package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.SyncStateContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 10/10/14.
 */


public class printpreview extends Activity {
    private WebView mWebView;

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
        Log.d("enddate: ", arrdate[27]);

        //Test
        /*
        for (int mday=0; mday<28; mday++) {
            Log.d("Weekday: ", arrday[mday]);
        }
        */

            // Strings for html-code
        String[] dayline;
        //String[] dayline14;
        String[] eventline;

        Integer noofevents = Constants.noofevents;
        Log.d("No of events:", String.valueOf(noofevents));


        // Start create html for webview
        String htmlDocument =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">" +
                        "<head>" +
                        "<title>Mandagher</title>" +
                        "<style type=\"text/css\">" +
                        "h1 { font-size: 12pt}" +
                        "table.cal {border:1px solid black;border-collapse: collapse; width: 50%; float:left}" +
                        "table.info {width: 100%}" +
                        "tr { border:1px solid black; }" +
                        "td { border: 1px solid black; padding: 3px;}" +
                        "td.date { width: 100%; background-color: #CCFFCC; font-weight: bold;}" +

                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<table class=\"info\">" +
                        "<tr><td colspan=\"2\">"+arrdate[0]+"</td><td colspan=\"2\">"+arrdate[27]+"</td><td>Avdelning</td></tr>" +
                        "<tr><td>Barnets namn</td><td>Persnr</td><td colspan=\"2\">Adress</td><td>Telefon</td></tr>" +
                        "<tr><td>Vårdnadshavare 1</td><td>Persnr</td><td>Arbetsplats</td><td>Telefon arbete</td><td>Mobil</td></tr>" +
                        "<tr><td>Vårdnadshavare 2</td><td>Persnr</td><td>Arbetsplats</td><td>Telefon arbete</td><td>Mobil</td></tr>" +
                        "<tr><td colspan=\"2\">Epost1</td><td colspan=\"3\">Epost2</td></tr>" +
                        "</table>" +
                        "<h1>Vecka " + sweeknow  +"-"+ endweek + "</h1>";



        dayline=new String [30];
        eventline = new String[100];

        int startday=0;
        int endday=0;
        // Loop through dates
        // Run two times, one for each table
        for (Integer run=0; run<2; run++) {
            if (run==0) {
                startday= 0;
                endday = 14;
            }
            else {
                startday= 14;
                endday = 28;
            }
            htmlDocument = htmlDocument + "<table class=\"cal\">";
            // First run day 0-13, second run 14-27
            for (int day = startday; day < endday; day++) {
                dayline[day] = arrday[day] + " " + arrdate[day];
                // Modify so first char is uppercase
                dayline[day] = Character.toUpperCase(dayline[day].charAt(0))
                        + dayline[day].subSequence(1, dayline[day].length()).toString();
                Log.d("Fetched day-date:", dayline[day]);
                htmlDocument = htmlDocument + "<tr>";
                htmlDocument = htmlDocument + "<td class=\"date\" >" + dayline[day] + "</td>";
                htmlDocument = htmlDocument + "</tr>";
                Log.d("Fetched day-date:", arrdate[day] + "-" + arrday[day]);

                eventline[day] = "";

                // Loop through events
                for (int x = 0; x < noofevents - 1; x++) {
                    // Find date for event
                    String start = Constants.arreventsStore[x][2];
                    Long ds = Long.valueOf(start);
                    String rstart = new SimpleDateFormat("yyyy/MM/dd")
                            .format(new Date(ds));

                    if (rstart.equals(arrdate[day])) {
                        Log.d("Match and arrday=", arrdate[day]);
                        // Fetch event end time
                        String end = Constants.arreventsStore[x][3];
                        Long de = Long.valueOf(end);
                        //Convert to real time
                        String starttime = new SimpleDateFormat("HH:mm").format(new Date(ds));
                        String endtime = new SimpleDateFormat("HH:mm").format(new Date(de));

                        // Starttime and endtime in millis
                        String sstart[] = starttime.split(":");
                        int hstart = Integer.parseInt (sstart[0]);
                        int mstart = Integer.parseInt (sstart[1]);

                        String send[] = endtime.split(":");
                        int hend = Integer.parseInt (send[0]);
                        int mend = Integer.parseInt (send[1]);

                        Long dur = (TimeUnit.HOURS.toMillis(hend) + TimeUnit.MINUTES.toMillis(mend))-(TimeUnit.HOURS.toMillis(hstart) + TimeUnit.MINUTES.toMillis(mstart));
                        String sdur = String.format("%d h, %d m",
                                TimeUnit.MILLISECONDS.toHours(dur),
                                TimeUnit.MILLISECONDS.toMinutes(dur)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dur))
                        );
                        Log.d("sdur: ", sdur);

                        eventline[day] = starttime + "-" + endtime + "(" + sdur + ")" + "-" + Constants.arreventsStore[x][1];
                        htmlDocument = htmlDocument + "<tr><td>" + eventline[day] + "</td></tr>";
                        //Log.d("?", "Match!");
                        //Log.d("Event: ", arrdate[day] + "-" + arrday[day] + ":" +
                        //        starttime + "-" + endtime + "-" + Constants.arreventsStore[x][1]);
                    }
                }
                //htmlDocument = htmlDocument + "</tr>";
            }
            htmlDocument = htmlDocument + "</table>";
        }
        //Log.d("test", dayline[1]);
        //Log.d("test", eventline[1]);

        // End html-line
        htmlDocument = htmlDocument +
                "</body></html>";
        //Log.d("HTML", htmlDocument);

        // Select current view
        setContentView(R.layout.printpreview);
        WebView web = (WebView) findViewById(R.id.webInfo);

        web.loadDataWithBaseURL(null, htmlDocument,
                "text/HTML", "UTF-8", null);

    }

    // Button print clicked
    public void print(View v) {
        // "Brm"
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        WebView web = (WebView) findViewById(R.id.webInfo);
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = web.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + " Print Test";
        printManager.print(jobName, printAdapter,new PrintAttributes.Builder().build());
    }
    // Button back clicked
    // Close window
    public void back(View v) {
        this.finish();

    }
}

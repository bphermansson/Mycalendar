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
import java.util.Calendar;
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

        // Get stored shared settings from "settings"
        String kidsname = getSharedPreferences("settings", MODE_PRIVATE).getString("kidsname", "");
        String kidspersnr = getSharedPreferences("settings", MODE_PRIVATE).getString("kidspersnr","");
        String department = getSharedPreferences("settings", MODE_PRIVATE).getString("department","");
        String address = getSharedPreferences("settings", MODE_PRIVATE).getString("address","");
        String phone = getSharedPreferences("settings", MODE_PRIVATE).getString("phone","");
        String parent1 = getSharedPreferences("settings", MODE_PRIVATE).getString("parent1","");
        String parent2 = getSharedPreferences("settings", MODE_PRIVATE).getString("parent2","");
        String p1persnr = getSharedPreferences("settings", MODE_PRIVATE).getString("p1persnr","");
        String p2persnr = getSharedPreferences("settings", MODE_PRIVATE).getString("p2persnr","");
        String p1work = getSharedPreferences("settings", MODE_PRIVATE).getString("p1work","");
        String p2work = getSharedPreferences("settings", MODE_PRIVATE).getString("p2work","");
        String p1workphone = getSharedPreferences("settings", MODE_PRIVATE).getString("p1workphone","");
        String p2workphone = getSharedPreferences("settings", MODE_PRIVATE).getString("p2workphone","");
        String p1mobile = getSharedPreferences("settings", MODE_PRIVATE).getString("p1mobile","");
        String p2mobile = getSharedPreferences("settings", MODE_PRIVATE).getString("p2mobile","");
        String p1mail = getSharedPreferences("settings", MODE_PRIVATE).getString("p1mail","");
        String p2mail = getSharedPreferences("settings", MODE_PRIVATE).getString("p2mail","");

        //Log.d ("Stored name: ", kidsname);

        //setContentView(R.layout.activity_main);
        //TextView curinfo = (TextView) findViewById(R.id.curinfo );
        //String sweeknow = curinfo.getText().toString();
        String sweeknow = Constants.week;
        Integer iweeknow = Integer.valueOf(sweeknow);
        Integer endweek = Integer.valueOf(sweeknow)+5;
        Log.d("Fetched week:", sweeknow);
        String[] arrday = Constants.arrdayStore;
        //String[] arrDayMonth = Constants.arrdayMonthStore;
        String[] arrdate = Constants.arrdateStore;
        Log.d("enddate: ", arrdate[41]);

        Integer noofevents = Constants.noofevents;
        Log.d("No of events:", String.valueOf(noofevents));

        // Start create html for webview
        // Upper table with info about parents and kid
        String htmlDocument =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">" +
                        "<head>" +
                        "<title>Mandagher</title>" +
                        "<style type=\"text/css\">" +
                        "h1 { font-size: 12pt}" +
                        "table.cal {border:1px solid black;border-collapse: collapse; width: 45%; float:left; margin:3px;}" +
                        "table.info {width: 100%}" +
                        "tr { border:1px solid black; }" +
                        "td { border: 1px solid black; padding: 3px; font-size: 16px}" +
                        "td.date { width: 100%; background-color: #CCFFCC; font-weight: bold;}" +
                        "td.week { width: 100%; font-weight: bold;}" +
                        "td.50 { width: 50%; }" +
                        "td.33 { width: 33%; }" +
                        "td.25 { width: 25%; }" +
                        "h1 {font-size: 22px}" +

                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<!--<table class=\"info\">" +
                        "<th style=\"width: 30%;\">Schema för " + kidsname + ", " + arrdate[0] + "-" + arrdate[41] + "</th>" +
                        "<tr><td colspan=\"2\">Från: <b>"+arrdate[0]+"</b></td><td colspan=\"2\">Till: <b>"+arrdate[41]+"</b></td><td>Avdelning: <b>"+department+"</b></td></tr>" +
                        "<tr><td>Barnets namn: <br><b>"+kidsname+"</b></td><td>Barnets personnummer: <br><b>"+kidspersnr+"</b></td><td colspan=\"2\">Adress: <b>"+address+"</b></td><td>Telefon: <b>"+phone+"</b></td></tr>" +
                        "<tr><td>Namn vårdnadshavare 1: <b>"+parent1+"</b></td><td>Personnummer vårdnadshavare 1: <b>"+p1persnr+"</b></td><td>Arbetsplats vårdnadshavare 1: "+p1work +"</td><td>"+p1workphone+"</td><td>"+p1mobile +"</td></tr>" +
                        "<tr><td>Namn vårdnadshavare 2: <b>"+parent2+"</b></td><td>Personnummer vårdnadshavare 2: <b>"+p2persnr+"</b></td><td>Arbetsplats vårdnadshavare 2: "+p2work+"</td><td>"+p2workphone +"</td><td>"+p2mobile+"</td></tr>" +
                        "<tr><td colspan=\"2\">E-post vårdnadshavare 1: "+p1mail+"</td><td colspan=\"3\">E-post vårdnadshavare 2: "+p2mail+"</td></tr>" +


                        "</table>-->" +
                        "<h1>Schema för " + kidsname + "<br>Vecka " + sweeknow  +"-"+ endweek + ", " + arrdate[0] + "-" + arrdate[41] + "</h1>";

                        htmlDocument = htmlDocument + "<table style=\"width: 100%; border: \"collapse\"; font-size: \"14px\";>" +
                        "<!--<tr><td>Schema för <b>" + arrdate[0] + "-" + arrdate[41] + "</b></td></tr>-->" +
                        "<tr>" +
                        "<td><b>" + kidspersnr +" - "+ kidsname + "</b>" + "<br>" + address + "<br>Telefon: " + phone + "<br>Avdelning: " + department + "</td>" +

                        "</tr>" +
                        "<tr><td><b>Vårdnadshavare 1</b><br> " + p1persnr + " - " + parent1 + "<br>Arbetsplats: " + p1work + "<br>Telefon arbete: " + p1workphone + "<br>Mobil: " + p1mobile + "<br>E-post: " + p1mail + "</td>" +
                        "<td><b>Vårdnadshavare 2</b><br> " + p2persnr + " - " + parent2 + "<br>Arbetsplats: " + p2work + "<br>Telefon arbete: " + p2workphone + "<br>Mobil: " + p2mobile + "<br>E-post: " + p2mail + "</td></tr>" +

                                "</table>";
        //dayline=new String [30];
        //eventline = new String[100];
        Integer offset=0;

        // Draw two tables beside each other.
        for (int z=0;z<2;z++) {
            // Draw table with three first weeks, then second three weeks.
            htmlDocument = htmlDocument + "<table class=\"cal\">";
            for (int y = 0; y < 3; y++) {

                htmlDocument = htmlDocument + "<tr>" +
                        "<td class=\"week\">Vecka " + sweeknow +
                        "<td>Barnet lämnas klockan</td>" +
                        "<td>Barnet hämtas klockan</td>" +
                        "<td>Antal timmar/dag</td>" +
                        "</tr>";

                for (int wl = 0+offset; wl < 5+offset; wl++) {
                    arrday[wl] = Character.toUpperCase(arrday[wl].charAt(0))
                            + arrday[wl].subSequence(1, arrday[wl].length()).toString();

                    htmlDocument = htmlDocument + "<tr>" +
                            "<td>" + arrday[wl] + "</td>";

                    int eventFlag = 0;

                    // Loop through events
                    for (int x = 0; x < noofevents - 1; x++) {

                        // Find start time for event
                        String start = Constants.arreventsStore[x][2];
                        Long ds = Long.valueOf(start);
                        String rstart = new SimpleDateFormat("yyyy/MM/dd")
                                .format(new Date(ds));

                        if (rstart.equals(arrdate[wl])) {
                            eventFlag = 1; // An event was found
                            // Find event description
                            String desc = Constants.arreventsStore[x][1];
                            Log.d("Desc: ", desc);
                            // Find end time
                            String end = Constants.arreventsStore[x][3];
                            Long de = Long.valueOf(end);
                            // Format time values
                            String starttime = new SimpleDateFormat("HH:mm").format(new Date(ds));
                            String endtime = new SimpleDateFormat("HH:mm").format(new Date(de));

                            // Get duration
                            Long idur = de - ds;
                            // Convert duration in milliseconds to duration in hours & minutes
                            String sdur = String.format("%dh %dm",
                                    TimeUnit.MILLISECONDS.toHours(idur),
                                    TimeUnit.MILLISECONDS.toMinutes(idur) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(idur))
                            );
                            Log.d("sdur: ", sdur);

                            // Add info to Gui
                            if (desc.equals("Ledig")) {
                                htmlDocument = htmlDocument + "<td>&nbsp</td>";
                                htmlDocument = htmlDocument + "<td>&nbsp</td>";
                                htmlDocument = htmlDocument + "<td>" + desc + "</td>";
                            }
                            else {
                                htmlDocument = htmlDocument + "<td>" + starttime + "</td>";
                                htmlDocument = htmlDocument + "<td>" + endtime + "</td>";
                                htmlDocument = htmlDocument + "<td>" + sdur + "</td>";
                            }
                        }
                    }
                    if (eventFlag == 0) {   // No events this day, fill row with empty cells
                        htmlDocument = htmlDocument + "<td>&nbsp</td>";
                        htmlDocument = htmlDocument + "<td>&nbsp</td>";
                        htmlDocument = htmlDocument + "<td>&nbsp</td>";
                    }
                    // End row
                    htmlDocument = htmlDocument + "</tr>";
                }
                // Go to next week
                iweeknow++;
                sweeknow = String.valueOf(iweeknow);
                offset=offset+7;

                // Make an empty row with no border and end the table, but not on the tables in the bottom
                if (y != 2) {
                    htmlDocument = htmlDocument + "<tr><td style=\"border: 0px;\">&nbsp</td></tr>";
                }
            }
            htmlDocument = htmlDocument + "</table>";
        }
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

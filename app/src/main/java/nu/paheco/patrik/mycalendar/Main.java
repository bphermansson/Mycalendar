package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class Main extends Activity {
    private Cursor mCursor = null;
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                  // 3
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND

    };
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_DESC_INDEX = 4;
    private static final int PROJECTION_DTSTART_INDEX = 5;
    private static final int PROJECTION_DTEND_INDEX = 6;

    // For storing events data
    Long[] eventsstart = new Long[20];
    Long[] eventsend = new Long[20];
    //String[] eventsdesc = new String[20];
    String eventsall[][] = new String[100][100];
    String calstartdate;
    String calenddate;
    String calid;
    Integer intcalid;
    Integer intcalpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG", "In onCreate()");

        // Which calendar was selected last time? Get value.
        String lastCalid = getPreferences(MODE_PRIVATE).getString("calpos", "");
        if (lastCalid=="") {
            lastCalid="1";
        }
        Integer intLastCalid = Integer.valueOf(lastCalid);

        Log.d("Last calid= ", lastCalid);
        intcalpos = Integer.valueOf(lastCalid);

        Calendar calendar = Calendar.getInstance();

        // Todays date
        Integer year = calendar.get(Calendar.YEAR);
        String smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        Integer day = calendar.get(Calendar.DATE);
        // Convert to strings
        String syear = String.valueOf(year);
        String sday = String.valueOf(day);
        Log.d("Today: ", syear + "-" + smonth  + "-" + sday);

        // Find calendars
        String calid[][];
        calid = listCals();
        //Integer len = calid[0].length;
        //Log.d("Length;", String.valueOf(len));

        // Set week number in Gui
        Integer weeknow = calendar.get(Calendar.WEEK_OF_YEAR);
        TextView curinfo=(TextView)findViewById(R.id.curinfo);
        String info = String.valueOf(weeknow);
        curinfo.setText(info);

        // Find date for last monday
        String date = findLastMon();
        Log.d("Last monday was: ", date);

        // Show weekdays and dates in Gui. Provide date for last monday, = start date for list
        listDays(date);

        // Convert string info to int
        Integer weeknowreal=Integer.parseInt(info);
        String nextdate=findnextweek(weeknowreal);

        // List  events
        // From http://developer.android.com/guide/topics/providers/calendar-provider.html
        // Used below in selection event
        calstartdate = date;
        calenddate = nextdate;

        // Populate dropdown with calendars name
        Spinner dropdown = (Spinner)findViewById(R.id.calSelect);
        // Create array for data
        final ArrayList<String> items = new ArrayList<String>();
        String temp;
       // String selcalid="";
        // Add items to array
        for (int i = 0; i < calid.length; i++) {

            Log.d("calendar1: ", calid[i][0] + "-" + calid[i][1]);
            //temp = calid[i][0] + "-" + calid[i][1];
            //items.add(temp);
            // Add calendar names to dropdown
            items.add(calid[i][1]);
            // Store
            //int arrPos = Integer.valueOf(calid[i][0]); // arrPos = calendar id
            //Log.d("arrPos: ", String.valueOf(arrPos));
            Constants.calidArr[i]=calid[i][0];

        }
        // Add to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        // Set default position
        dropdown.setSelection(intLastCalid);

        // Selection event
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                String selcal = items.get(position);
                //String[] selcalitem;
                //selcalitem = selcal.split("-");
                // Get just the id
                //selcal = (selcalitem[0]);
                //intcalid = Integer.parseInt(selcal);
                //Log.d("You've clicked: ", selcalid);
                Log.d("Position", String.valueOf(position));
                // Store selection permanently
                getPreferences(MODE_PRIVATE).edit().putString("calpos",String.valueOf(position)).commit();
                Log.d("Stored: ", String.valueOf(position));
                // Store position/id
                Constants.pos = position;
                //Constants.calname = items.get(position);
                Log.d("Selected: ", Constants.calname);
                //Constants.id = calid[i][0];

                // Find events and add them to Gui
                Log.d("Call: ", "caldatanow");
                caldatanow(calstartdate,calenddate);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
    // Button printpreview clicked
    public void printpreview(View v) {
        // "Brm"
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        TextView curinfo = (TextView) findViewById(R.id.curinfo );
        String sweeknow = curinfo.getText().toString();
        //Store value
        Constants.week = sweeknow;

        Intent myIntent = new Intent(v.getContext(), printpreview.class);
        Log.d("Call ","print preview");
        startActivityForResult(myIntent, 0);

    }
    // Button settings
    public void settings(View v) {
        // "Brm"
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Intent settings = new Intent(v.getContext(), settings.class);
        Log.d("Call ","settings");
        startActivityForResult(settings, 0);
    }
    // Button prev clicked
    public void prevweek(View v) {
        // "Brm"
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        String dir="back";
        changeweek(dir);
    }
    // Button next clicked
    public void nextweek(View v) {
        // "Brm"
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        String dir="forward";
        changeweek(dir);
    }
    // Today 'clicked'
    public void today(View v) {
        String todaysdate = todaysdate();
        Log.d("Today is: ", todaysdate);
        // Find date for last monday
        String date = findLastMon();
        Log.d("Last monday was: ", date);

        // Set week number in Gui
        Calendar calendar = Calendar.getInstance();
        Integer weeknow = calendar.get(Calendar.WEEK_OF_YEAR);
        TextView curinfo=(TextView)findViewById(R.id.curinfo);
        String info = String.valueOf(weeknow);
        curinfo.setText(info);

        // Show weekdays and dates in Gui. Provide date for last monday, = start date for list
        listDays(date);

        // List events
        caldatanow(date,calenddate);

    }
    public void changeweek(String dir) {
        Integer idir=0;
        if (dir=="forward") {
            idir=1;
        } else if (dir=="back") {
            idir=-1;
        }
        Log.d("Dir: ", dir);

        Calendar calendar = Calendar.getInstance();
        // Get current value from textview
        TextView curinfo=(TextView)findViewById(R.id.curinfo);
        String sweeknow = curinfo.getText().toString();
        Log.d("Week now: ", sweeknow);
        Integer weeknow = Integer.parseInt(sweeknow);
        // Set calendar to current week
        calendar.set(Calendar.WEEK_OF_YEAR, weeknow);

        // Move to last monday
        Integer c=0;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String weekDay = dayFormat.format(calendar.getTime());
        while (!weekDay.equals("måndag")) {
            calendar.add(calendar.DATE,-1);
            weekDay = dayFormat.format(calendar.getTime());
            String sc = String.valueOf(c);
            //Log.d("Count:" , sc);
            c++;
        }

        // Move forward or backward
        calendar.add(Calendar.WEEK_OF_YEAR, idir);
        weeknow = calendar.get(Calendar.WEEK_OF_YEAR);
        sweeknow=String.valueOf(weeknow);
        Log.d("Week next: ", sweeknow);
        String info = String.valueOf(weeknow);
        curinfo.setText(info);
        // Convert week to start date and end date
        Integer year = calendar.get(Calendar.YEAR);
        String smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        String sday = String.format("%02d",calendar.get(Calendar.DATE));

        // Convert to strings
        String syear = String.valueOf(year);

        String newdate = syear + "/" + smonth  + "/" + sday;
        Log.d("New date: ", newdate);
        calendar.add(Calendar.DATE,+7);

        year = calendar.get(Calendar.YEAR);
        smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        //day = calendar.get(Calendar.DATE);
        sday = String.format("%02d",calendar.get(Calendar.DATE));

        syear = String.valueOf(year);
        //sday = String.valueOf(day);

        String enddate = syear + "/" + smonth  + "/" + sday;
        Log.d("End date: ", enddate);

        listDays(newdate);
        caldatanow(newdate,enddate);

    }
    public String listDays(String date) {
        Log.d("Class: ", "listDays");
        //Log.d("Start date: ", date);

        // Split string in year, month, day
        String[] separated = date.split("/");
        String syeardate=separated[0];
        Integer iyeardate=Integer.parseInt(syeardate);
        String smonthdate=separated[1];
        Integer imonthdate=Integer.parseInt(smonthdate)-1;
        String sdaydate=separated[2];
        Integer idaydate=Integer.parseInt(sdaydate);

        Calendar calendar = Calendar.getInstance();
        //Set start date
        calendar.set(Calendar.YEAR, iyeardate);
        calendar.set(Calendar.MONTH, imonthdate);
        calendar.set(Calendar.DATE, idaydate);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

        // Find Gui elements
        TextView txtDay0 = (TextView)findViewById(R.id.textView0);
        TextView txtDay1 = (TextView)findViewById(R.id.textView1);
        TextView txtDay2 = (TextView)findViewById(R.id.textView2);
        TextView txtDay3 = (TextView)findViewById(R.id.textView3);
        TextView txtDay4 = (TextView)findViewById(R.id.textView4);
        TextView txtDay5 = (TextView)findViewById(R.id.textView5);
        TextView txtDay6 = (TextView)findViewById(R.id.textView6);
        TextView txtdate0 = (TextView)findViewById(R.id.date0);
        TextView txtdate1 = (TextView)findViewById(R.id.date1);
        TextView txtdate2 = (TextView)findViewById(R.id.date2);
        TextView txtdate3 = (TextView)findViewById(R.id.date3);
        TextView txtdate4 = (TextView)findViewById(R.id.date4);
        TextView txtdate5 = (TextView)findViewById(R.id.date5);
        TextView txtdate6 = (TextView)findViewById(R.id.date6);

        // Loop through weekdays and store six weeks
        String[] arrDay = new String[42];
        String[] arrDate = new String[42];
        Integer c=0;
        for (c=0; c<42; c++ ) {
            // Get weekday
            arrDay[c] = dayFormat.format(calendar.getTime());
            // Get date
            Integer year = calendar.get(Calendar.YEAR);
            String smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
            String sday = String.format("%02d",calendar.get(Calendar.DATE));
            // Convert to strings
            String syear = String.valueOf(year);
            arrDate[c] = syear + "/" + smonth  + "/" + sday;
            calendar.add(calendar.DATE,+1);
        }
        // Rewind
        calendar.add(calendar.DATE,-7);

        Integer day = calendar.get(Calendar.DATE);
        String sday = String.valueOf(day);
        //Log.d("Rewind to ",sday);

        //Store
        Constants.arrdayStore=arrDay;
        Constants.arrdateStore=arrDate;

        // Add weekdays to Gui
        // We have fetched four weeks, but the main Gui just shows the first week.
        txtDay0.setText(arrDay[0]);
        txtDay1.setText(arrDay[1]);
        txtDay2.setText(arrDay[2]);
        txtDay3.setText(arrDay[3]);
        txtDay4.setText(arrDay[4]);
        txtDay5.setText(arrDay[5]);
        txtDay6.setText(arrDay[6]);

        txtdate0.setText(arrDate[0]);
        txtdate1.setText(arrDate[1]);
        txtdate2.setText(arrDate[2]);
        txtdate3.setText(arrDate[3]);
        txtdate4.setText(arrDate[4]);
        txtdate5.setText(arrDate[5]);
        txtdate6.setText(arrDate[6]);

        String dummy="1";
        return(dummy);
    }
    public String findLastMon() {
        Log.d(": ", "findLastMon");
        Calendar calendar = Calendar.getInstance();

        // Loop until we find the last monday
        Integer c=0;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String weekDay = dayFormat.format(calendar.getTime());
        while (!weekDay.equals("måndag")) {
            calendar.add(calendar.DATE,-1);
            weekDay = dayFormat.format(calendar.getTime());
            String sc = String.valueOf(c);
            //Log.d("Count:" , sc);
            c++;
        }
        // Get date for last monday
        Integer year = calendar.get(Calendar.YEAR);
        String smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        Integer month = calendar.get(Calendar.MONTH);
        Integer day = calendar.get(Calendar.DATE);
        // Convert to strings
        String syear = String.valueOf(year);
        String sday = String.valueOf(day);
        String date = syear + "/" + smonth  + "/" + sday;
        return(date);
    }
    //public String caldata(Integer day, Integer month, Integer year,
    //                      Integer nextday, Integer nextmonth, Integer nextyear)
    public String caldatanow(String calstartdate, String calenddate)
    {
        Integer pos = Constants.pos;
        //String calname = Constants.calname;
        String sid = Constants.calidArr[pos];

        // Here we go through the calendar and find events.
        Log.d("In caldatanow: ", calstartdate +"-"+ calenddate + ". pos="+ pos + " sid = " + sid);

        //Oskars calendar has id 12
        //String calendarID="12";

        // Split strings in year, month, day
        // Start date
        String[] separated = calstartdate.split("/");
        String syeardate=separated[0];
        Integer iyeardate=Integer.parseInt(syeardate);
        String smonthdate=separated[1];
        Integer imonthdate=Integer.parseInt(smonthdate)-1;
        String sdaydate=separated[2];
        Integer idaydate=Integer.parseInt(sdaydate);
        Log.d("In caldatanow: ", "Start date= "+ syeardate+"-"+smonthdate+"-"+sdaydate);

        // End date
        separated = calenddate.split("/");
        syeardate=separated[0];
        Integer ieyeardate=Integer.parseInt(syeardate);
        smonthdate=separated[1];
        Integer iemonthdate=Integer.parseInt(smonthdate)-1;
        sdaydate=separated[2];
        Integer iedaydate=Integer.parseInt(sdaydate);
        Log.d("In caldatanow: ", "End date= "+ syeardate+"-"+smonthdate+"-"+sdaydate);

        // Convert dates to epoch
        //http://pastebin.com/mw4fRJ3D
        Time t = new Time();
        t.set(0, 0, 0, idaydate, imonthdate, iyeardate);
        String dtStart = Long.toString(t.toMillis(false));
        t.set(59, 59, 23, iedaydate, iemonthdate, ieyeardate);
        String dtEnd = Long.toString(t.toMillis(false));

        Log.d("caldata - Selection start: ", dtStart);
        Log.d("caldata - Selection end: ", dtEnd);
        //Log.d("caldata - Calname: ", calname);

        // Convert calenderid intcalid to string
//        String scalid = String.valueOf(Constants.id+1);

        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String myselection = "((" + CalendarContract.Events.DTSTART + " <= ?) AND (" + CalendarContract.Events.DTEND + " >= ?) AND (" + CalendarContract.Events.CALENDAR_ID + " = ?))";
        //String myselection = "((" + CalendarContract.Events.DTSTART + " <= ?) AND (" + CalendarContract.Events.DTEND + " >= ?) AND (" + CalendarContract.Calendars.NAME + " = ?))";

        String[] mysqlargs= new String[] {dtEnd, dtStart, sid};

        Log.d("In caldatanow, args", mysqlargs[0] +"-"+ mysqlargs[1]);

        cur = cr.query(
                uri,
                EVENT_PROJECTION,
                myselection,
                mysqlargs,
                "dtStart ASC");

        //Number of results
        Integer count = cur.getCount();
        String scount = String.valueOf(count);
        Log.d("In caldata, We count to ", scount + " events");

        // Iterate through results
        cur.moveToFirst();
        Log.d("In caldatanow", "cur.moveToFirst()");

        Integer c=0;

        // Dont proceed if there are no results, just clear the events
        if (count==0) {
            // Find textviews
            int[] textViewIDsact = new int[] {R.id.act0, R.id.act1, R.id.act2, R.id.act3, R.id.act4, R.id.act5, R.id.act6  };
            for(int i=0; i < textViewIDsact.length; i++) {
                TextView tv = (TextView ) findViewById(textViewIDsact[i]);
                tv.setText("");
            }
            String dummy="1";
            return(dummy);
        }
        do {
                //Log.d("In caldata", "loop da loop");
                String desc = null;
                long starttime = 0;
                long endtime = 0;

                // Get the field values
                desc = cur.getString(PROJECTION_DESC_INDEX);
                starttime = cur.getLong(PROJECTION_DTSTART_INDEX);
                endtime = cur.getLong(PROJECTION_DTEND_INDEX);

                String sc = String.valueOf(c);
                String sstart = String.valueOf(starttime);
                String send = String.valueOf(endtime);

                eventsall[c][0] = sc;
                eventsall[c][1] = desc;
                eventsall[c][2] = sstart;
                eventsall[c][3] = send;
                //Add to counter
                c++;
                //}
            } while (cur.moveToNext());

        cur.close();
        // Log. c starts at 0 so we add 1.
        Log.d("Cursor closed", c+1 + " events accounted for");

        //Log.d("Result 0", eventsdesc[0] + "-" + eventsstart[0] + "-" + eventsend[0]);
        Constants.arreventsStore = eventsall;
        Constants.noofevents = c+1;

        // Array of event textviews
        int[] textViewIDsact = new int[] {R.id.act0, R.id.act1, R.id.act2, R.id.act3, R.id.act4, R.id.act5, R.id.act6  };
        int[] textViewIDs = new int[] {R.id.date0, R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6  };

        // Clear event textviews

        for (int tc=0; tc<7; tc++) {
            TextView tvact = (TextView ) findViewById(textViewIDsact[tc]);
            tvact.setText("");
        }

        for (int x=0; x<c; x++ ) {
            //Startdate
            Long sdate = Long.parseLong(eventsall[x][2]);
            //String ssdate = String.valueOf(sdate);
            Long edate = Long.parseLong(eventsall[x][3]);
            //String sedate = String.valueOf(edate);

            //Log.d("Results", x + "-" + eventsall[x][1]);
            //Log.d("ssdate", x + "-" + ssdate);

            // Convert to real date
            String datestart = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date (sdate));
            String timestart = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (sdate));
            String timeend = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (edate));

            // Loop throught textviews
            for (int tc=0; tc<7; tc++) {
                //Integer test = textViewIDs[tc];

                //String sstc = String.valueOf(test);
                //Log.d("stc", sstc);

                TextView tv = (TextView ) findViewById(textViewIDs[tc]);
                String caldate = tv.getText().toString();
                //String sweeknow = curinfo.getText().toString();

                if (datestart.equals(caldate)) {
                    //Log.d("Searching...", "Match!!!");
                    //Log.d("cal date from gui", caldate);
                    //Log.d("datestart: ",datestart);
                    String sdesc = eventsall[x][1];
                    Log.d("Desc", sdesc);

                    // Starttime and endtime in millis
                    String sstart[] = timestart.split(":");
                    int hstart = Integer.parseInt (sstart[0]);
                    int mstart = Integer.parseInt (sstart[1]);

                    String send[] = timeend.split(":");
                    int hend = Integer.parseInt (send[0]);
                    int mend = Integer.parseInt (send[1]);

                    Long dur = (TimeUnit.HOURS.toMillis(hend) + TimeUnit.MINUTES.toMillis(mend))-(TimeUnit.HOURS.toMillis(hstart) + TimeUnit.MINUTES.toMillis(mstart));
                    //Log.d("dur: ", String.valueOf(dur));

                    String sdur = String.format("%dh%dm",
                            TimeUnit.MILLISECONDS.toHours(dur),
                            TimeUnit.MILLISECONDS.toMinutes(dur)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dur))
                    );
                    Log.d("sdur: ", sdur);


                    TextView tvact = (TextView ) findViewById(textViewIDsact[tc]);
                    //tvact.append(sdesc+"   "+getResources().getString(R.string.kl)+" "+timestart+"-"+timeend+"\n");
                    if (sdesc.equals("Ledig")) {
                        tvact.append("Ledig\n");
                    }
                    else {
                        tvact.append(timestart + "-" + timeend + " (" + sdur + "): " + sdesc + "\n");
                    }

                }

            }

        }





        /*
        // Convert date/time
        String timedatestart = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (starttime));
        String timedateend = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (endtime));

        // Get just the date
        String datestart = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date (starttime));
        Log.d("datestart: ",datestart);

        // Get just the times
        String timestart = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (starttime));
        Log.d("Timestart: ",timestart);
        String timeend = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (endtime));
        Log.d("Timeend: ",timeend);
        */

        String dummy="1";
        return(dummy);
    }
    public String caldata(String calstartdate, String calenddate, Integer calid)
    {
        Log.d("In caldata: ", calstartdate +"-"+ calenddate);

        // Split string in year, month, day
        // Start date
        String[] separated = calstartdate.split("/");
        String syeardate=separated[0];
        Integer iyeardate=Integer.parseInt(syeardate);
        String smonthdate=separated[1];
        Integer imonthdate=Integer.parseInt(smonthdate)-1;
        String sdaydate=separated[2];
        Integer idaydate=Integer.parseInt(sdaydate);
        Log.d("In caldata: ", "Day= "+ sdaydate);


        // End date
        separated = calenddate.split("/");
        syeardate=separated[0];
        Integer ieyeardate=Integer.parseInt(syeardate);
        smonthdate=separated[1];
        Integer iemonthdate=Integer.parseInt(smonthdate)-1;
        sdaydate=separated[2];
        Integer iedaydate=Integer.parseInt(sdaydate);

        TextView txtdate0 = (TextView)findViewById(R.id.date0);
        TextView txtdate1 = (TextView)findViewById(R.id.date1);
        TextView txtdate2 = (TextView)findViewById(R.id.date2);
        TextView txtdate3 = (TextView)findViewById(R.id.date3);
        TextView txtdate4 = (TextView)findViewById(R.id.date4);
        TextView txtdate5 = (TextView)findViewById(R.id.date5);
        TextView txtdate6 = (TextView)findViewById(R.id.date6);

        TextView txtact0 = (TextView)findViewById(R.id.act0);
        TextView txtact1 = (TextView)findViewById(R.id.act1);
        TextView txtact2 = (TextView)findViewById(R.id.act2);
        TextView txtact3 = (TextView)findViewById(R.id.act3);
        TextView txtact4 = (TextView)findViewById(R.id.act4);
        TextView txtact5 = (TextView)findViewById(R.id.act5);
        TextView txtact6 = (TextView)findViewById(R.id.act6);

        // Clear activities in Gui
        txtact0.setText("");
        txtact1.setText("");
        txtact2.setText("");
        txtact3.setText("");
        txtact4.setText("");
        txtact5.setText("");
        txtact6.setText("");

        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        Time t = new Time();

        //http://pastebin.com/mw4fRJ3D
        //t.setToNow();
        t.set(0, 0, 0, idaydate, imonthdate, iyeardate);
        String dtStart = Long.toString(t.toMillis(false));
        t.set(59, 59, 23, iedaydate, iemonthdate, ieyeardate);
        String dtEnd = Long.toString(t.toMillis(false));

        Log.d("caldata - Selection start: ", dtStart);
        Log.d("caldata - Selection end: ", dtEnd);


        //Oskars calendar has id 12
        String calendarID="12";

        String myselection = "((" + CalendarContract.Events.DTSTART + " <= ?) AND (" + CalendarContract.Events.DTEND + " >= ?) AND (" + CalendarContract.Events.CALENDAR_ID + " = ?))";
        String[] mysqlargs= new String[] {dtEnd, dtStart, calendarID};

        Log.d("In caldate", mysqlargs[0] +"-"+ mysqlargs[1]);

        cur = cr.query(
                uri,
                EVENT_PROJECTION,
                myselection,
                mysqlargs,
                null);
        // Iterate through results
        cur.moveToFirst();
        Log.d("In caldate", "cur.moveToFirst()");

        Integer pos = cur.getCount();
        String spos = String.valueOf(pos);
        Log.d("In caldata, count=", spos);

        while (cur.moveToNext()) {
            Log.d("In caldata", "loop da loop");

            long calID = 0;
            //String displayName = null;
            //String accountName = null;
            //String ownerName = null;
            String desc = null;
            long starttime = 0;
            long endtime = 0;

            // Get the field values
            //calID = cur.getLong(PROJECTION_ID_INDEX);
            //displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            //accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            //ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            desc = cur.getString(PROJECTION_DESC_INDEX);
            starttime = cur.getLong(PROJECTION_DTSTART_INDEX);
            endtime = cur.getLong(PROJECTION_DTEND_INDEX);

            // Do something with the values...
            //String scalID=String.valueOf(calID);
            //String sstarttime=String.valueOf(starttime);
            //String sendtime=String.valueOf(endtime);
            // Convert date/time
            String timedatestart = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (starttime));
            String timedateend = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (endtime));

            // Get just the date
            String datestart = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date (starttime));
            Log.d("datestart: ",datestart);

            // Get just the times
            String timestart = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (starttime));
            Log.d("Timestart: ",timestart);
            String timeend = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (endtime));
            Log.d("Timeend: ",timeend);

            //Log.d("calID", scalID);
            //Log.d("displayName=", displayName);
            Log.d("desc=", desc);
            //Log.d("starttime(millis)=", sstarttime);
            Log.d("Start: ", timedatestart);
            Log.d("End: ", timedateend);
            Log.d("Date: ", datestart);

            //mondayinfo.append(datestart + "\n");
            //mondayinfo.append(desc+"\n");

            // Get date from textviews
            String checkdate;
            checkdate = txtdate0.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate0: ", checkdate);
                txtact0.setText(desc+"\n"+timestart+"-"+timeend);

            }
            checkdate = txtdate1.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate1: ", checkdate);
                txtact1.setText(desc+"\n"+timestart+"-"+timeend);
            }
            checkdate = txtdate2.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate2: ", checkdate);
                txtact2.setText(desc+"\n"+timestart+"-"+timeend);
            }
            checkdate = txtdate3.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate3: ", checkdate);
                txtact3.setText(desc+"\n"+timestart+"-"+timeend);
            }
            checkdate = txtdate4.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate4: ", checkdate);
                txtact4.setText(desc+"\n"+timestart+"-"+timeend);
            }
            checkdate = txtdate5.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate5: ", checkdate);
                txtact5.setText(desc+"\n"+timestart+"-"+timeend);
            }
            checkdate = txtdate6.getText().toString();
            // Is the textviews date equal to the events date?
            if (checkdate.equals(datestart)) {
                Log.d("check? ", "Match!");
                Log.d("checkdate6: ", checkdate);
                txtact6.setText(desc+"\n"+timestart+"-"+timeend);
            }
            /*
            Log.d("-------", stodaysdate + "==" + datestart);
            // Do something today?
            if (stodaysdate.equals(datestart)) {
                Log.d(desc," today!");
            }
            */
        }
        cur.close();
        String dummy="1";
        return(dummy);
    }
    public String[][] listCals() {
        // List calendars
        Log.d("In listCals", "onCreate");
        String[] projection = {CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.IS_PRIMARY,
                CalendarContract.Calendars.VISIBLE};
        String selection = String.format("%s = 1", CalendarContract.Calendars.VISIBLE);

        Cursor c = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                null, null);

        // Check number of calendars
        Integer ccount = c.getCount();
        Log.d("c count", String.valueOf(ccount));

        String sid="";
        // Array size depends on number of calendars
        String calid[][]=new String[ccount][ccount];
        Integer count=0;

        c.moveToFirst();
        do {
            // the cursor, c, contains all the projection data items
            // access the cursor’s contents by array index as declared in
            // your projection
            long id = c.getLong(0);
            sid = String.valueOf(id);
            String name = c.getString(1);
            String owner = c.getString(2);
            String type = c.getString(3);
            String dname = c.getString(4);

            calid[count][0]=sid;
            calid[count][1]=name;
            count++;


            Log.d("cal id", String.valueOf(count) + "-" + sid + " - " + name);
            Log.d("cal name", name);
            Log.d("cal owner", owner);
            /*
            Log.d("cal type", type);
            Log.d("cal dname", dname);
            */
        } while (c.moveToNext());
        c.close();
        Log.d("count: ", String.valueOf(count));

        return (calid);

    }
    public String findnextweek (Integer weeknow) {
        Log.d("Class: ", "findnextweek");
        String sweeknow=String.valueOf(weeknow);
        Log.d("Week now: ", sweeknow);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, weeknow);
        // Next week
        //weeknow = weeknow +4;
        //calendar.set(Calendar.WEEK_OF_YEAR,weeknow);
        calendar.add(Calendar.WEEK_OF_YEAR,+5);

        //Find next sunday
        // Calendar is at next monday, move one day back
        //calendar.add(Calendar.DATE,-1);

        Integer nextyear = calendar.get(Calendar.YEAR);
        String nextsmonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        //Integer nextmonth = calendar.get(Calendar.MONTH);
        String nextsday = String.format("%02d",calendar.get(Calendar.DATE));

        weeknow = calendar.get(Calendar.WEEK_OF_YEAR);
        Log.d("Week now +5 : ", String.valueOf(weeknow));

        // Convert to strings
        String nextsyear = String.valueOf(nextyear);
        String nextdate = nextsyear + "/" + nextsmonth  + "/" + nextsday;
        Log.d("Last sunday is: ", nextdate);
        return (nextdate);
    }
    public String todaysdate() {
        // Get today's date
        Calendar calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH)+1;
        //Integer mnt = calendar.get(Calendar.)
        Integer day = calendar.get(Calendar.DATE);
        // Convert to strings
        String syear = String.valueOf(year);
        String smonth = String.valueOf(month);
        String sday = String.valueOf(day);
        //Log.d("Dag: ", syear + "/" + smonth + "/" + sday);
        Log.d("M: ", smonth);
        //Fix month
        if (month<=9) {
            smonth="0" + smonth;
        }
        else {
            //smonth="1" + smonth;
        }

        return syear + "/" + smonth + "/" + sday;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_help) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

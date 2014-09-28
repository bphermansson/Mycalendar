package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG", "In onCreate()");

        Calendar calendar = Calendar.getInstance();

        // Todays date
        Integer year = calendar.get(Calendar.YEAR);
        String smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        Integer day = calendar.get(Calendar.DATE);
        // Convert to strings
        String syear = String.valueOf(year);
        String sday = String.valueOf(day);
        Log.d("Today: ", syear + "-" + smonth  + "-" + sday);

        // Set week number in Gui
        Integer weeknow = calendar.get(Calendar.WEEK_OF_YEAR);
        TextView curinfo=(TextView)findViewById(R.id.curinfo);
        String info = String.valueOf(weeknow);
        curinfo.append(info);

        // Find date for last monday
        String date = findLastMon();
        Log.d("Last monday was: ", date);

        // Show weekdays and dates in Gui. Provide date for last monday, = start date for list
        listDays(date);

        // Convert string info to int
        Integer weeknowreal=Integer.parseInt(info);
        String nextdate=findnextweek(weeknowreal);

        // List Oskars events
        // From http://developer.android.com/guide/topics/providers/calendar-provider.html
        String calstartdate = date;
        String calenddate = nextdate;
        Log.d("Call: ", "caldatanow");
        //caldata(day, month, year, nextday, nextmonth, nextyear);
        //caldata(calstartdate,calenddate);
        caldatanow(calstartdate,calenddate);

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
        Log.d("Start date: ", date);

        // Split string in year, month, day
        String[] separated = date.split("/");
        String syeardate=separated[0];
        Integer iyeardate=Integer.parseInt(syeardate);
        String smonthdate=separated[1];
        Integer imonthdate=Integer.parseInt(smonthdate)-1;
        String sdaydate=separated[2];
        Integer idaydate=Integer.parseInt(sdaydate);
        //Log.d("Year: ", syeardate);

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

        // Loop through weekdays
        String[] arrDay = new String[8];
        String[] arrDate = new String[8];
        Integer c=0;
        for (c=0; c<7; c++ ) {
            // Get weekday
            arrDay[c] = dayFormat.format(calendar.getTime());
            // Get date
            Integer year = calendar.get(Calendar.YEAR);
            String smonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
            //Integer day = calendar.get(Calendar.DATE);
            String sday = String.format("%02d",calendar.get(Calendar.DATE));

            // Convert to strings
            String syear = String.valueOf(year);
            //String sday = String.valueOf(day);
            arrDate[c] = syear + "/" + smonth  + "/" + sday;
            calendar.add(calendar.DATE,+1);
        }
        // Rewind
        calendar.add(calendar.DATE,-7);
        Integer day = calendar.get(Calendar.DATE);
        String sday = String.valueOf(day);
        Log.d("Rewind to ",sday);

        // Add weekdays to Gui
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
        Log.d("Class: ", "findLastMon");
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
        Log.d("In caldatanow: ", calstartdate +"-"+ calenddate);

        //Oskars calendar has id 12
        String calendarID="12";

        // Split strings in year, month, day
        // Start date
        String[] separated = calstartdate.split("/");
        String syeardate=separated[0];
        Integer iyeardate=Integer.parseInt(syeardate);
        String smonthdate=separated[1];
        Integer imonthdate=Integer.parseInt(smonthdate)-1;
        String sdaydate=separated[2];
        Integer idaydate=Integer.parseInt(sdaydate);
        Log.d("In caldatanow: ", "Start date= "+ sdaydate);

        // End date
        separated = calenddate.split("/");
        syeardate=separated[0];
        Integer ieyeardate=Integer.parseInt(syeardate);
        smonthdate=separated[1];
        Integer iemonthdate=Integer.parseInt(smonthdate)-1;
        sdaydate=separated[2];
        Integer iedaydate=Integer.parseInt(sdaydate);
        Log.d("In caldatanow: ", "End date= "+ sdaydate);

        // Find textviews
        TextView txtact0 = (TextView)findViewById(R.id.act0);
        TextView txtact1 = (TextView)findViewById(R.id.act1);
        TextView txtact2 = (TextView)findViewById(R.id.act2);
        TextView txtact3 = (TextView)findViewById(R.id.act3);
        TextView txtact4 = (TextView)findViewById(R.id.act4);
        TextView txtact5 = (TextView)findViewById(R.id.act5);
        TextView txtact6 = (TextView)findViewById(R.id.act6);

        // Convert dates to epoch
        //http://pastebin.com/mw4fRJ3D
        Time t = new Time();
        t.set(0, 0, 0, idaydate, imonthdate, iyeardate);
        String dtStart = Long.toString(t.toMillis(false));
        t.set(59, 59, 23, iedaydate, iemonthdate, ieyeardate);
        String dtEnd = Long.toString(t.toMillis(false));
        Log.d("caldata - Selection start: ", dtStart);
        Log.d("caldata - Selection end: ", dtEnd);

        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String myselection = "((" + CalendarContract.Events.DTSTART + " <= ?) AND (" + CalendarContract.Events.DTEND + " >= ?) AND (" + CalendarContract.Events.CALENDAR_ID + " = ?))";
        String[] mysqlargs= new String[] {dtEnd, dtStart, calendarID};

        Log.d("In caldate", mysqlargs[0] +"-"+ mysqlargs[1]);

        cur = cr.query(
                uri,
                EVENT_PROJECTION,
                myselection,
                mysqlargs,
                "dtStart ASC");

        //Number of results
        Integer count = cur.getCount();
        String scount = String.valueOf(count);
        Log.d("In caldata, count=", scount);

        // Iterate through results
        cur.moveToFirst();
        Log.d("In caldate", "cur.moveToFirst()");

        while (cur.moveToNext()) {
            Log.d("In caldata", "loop da loop");
            String desc = null;
            long starttime = 0;
            long endtime = 0;

            // Get the field values
            desc = cur.getString(PROJECTION_DESC_INDEX);
            starttime = cur.getLong(PROJECTION_DTSTART_INDEX);
            endtime = cur.getLong(PROJECTION_DTEND_INDEX);

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
        }

        cur.close();
        String dummy="1";
        return(dummy);
    }
    public String caldata(String calstartdate, String calenddate)
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

    public String findnextweek (Integer weeknow) {
        Log.d("Class: ", "findnextweek");
        String sweeknow=String.valueOf(weeknow);
        Log.d("Week now: ", sweeknow);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, weeknow);
        // Next week
        calendar.add(Calendar.WEEK_OF_YEAR,+1);

        //Find next sunday
        // Calendar is at next monday, move one day back
        calendar.add(Calendar.DATE,-1);

        Integer nextyear = calendar.get(Calendar.YEAR);
        String nextsmonth = String.format("%02d",calendar.get(Calendar.MONTH)+1);
        //Integer nextmonth = calendar.get(Calendar.MONTH);
        Integer nextday = calendar.get(Calendar.DATE);
        // Convert to strings
        String nextsyear = String.valueOf(nextyear);
        String nextsday = String.valueOf(nextday);
        String nextdate = nextsyear + "/" + nextsmonth  + "/" + nextsday;
        Log.d("Next sunday is: ", nextdate);
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
            smonth="1" + smonth;
        }

        return syear + "/" + smonth + "/" + sday;

    }

}

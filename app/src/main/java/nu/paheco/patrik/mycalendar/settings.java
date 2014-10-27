package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by user on 10/27/14.
 */
public class settings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Log.d("Settings", "In onCreate()");

        // Find gui elements
        EditText Ekidsname=(EditText)findViewById(R.id.kidsname);
        EditText ekidspersnr=(EditText)findViewById(R.id.kidspersnr);
        EditText edep=(EditText)findViewById(R.id.department);
        EditText eaddress=(EditText)findViewById(R.id.address);
        EditText ephone=(EditText)findViewById(R.id.phone);
        EditText eparent1=(EditText)findViewById(R.id.parent1);
        EditText eparent2=(EditText)findViewById(R.id.parent2);
        EditText ep1persnr=(EditText)findViewById(R.id.p1persnr);
        EditText ep2persnr=(EditText)findViewById(R.id.p2persnr);
        EditText ep1work=(EditText)findViewById(R.id.p1work);
        EditText ep2work=(EditText)findViewById(R.id.p2work);
        EditText ep1workphone=(EditText)findViewById(R.id.p1workphone);
        EditText ep2workphone=(EditText)findViewById(R.id.p2workphone);
        EditText ep1mobile=(EditText)findViewById(R.id.p1mobile);
        EditText ep2mobile=(EditText)findViewById(R.id.p2mobile);
        EditText ep1mail=(EditText)findViewById(R.id.p1mail);
        EditText ep2mail=(EditText)findViewById(R.id.p2mail);

        // Get stored settings
        String kidsname = getPreferences(MODE_PRIVATE).getString("kidsname", "");
        String kidspersnr = getPreferences(MODE_PRIVATE).getString("kidspersnr","");
        String department = getPreferences(MODE_PRIVATE).getString("department","");
        String address = getPreferences(MODE_PRIVATE).getString("address","");
        String phone = getPreferences(MODE_PRIVATE).getString("phone","");
        String parent1 = getPreferences(MODE_PRIVATE).getString("parent1","");
        String parent2 = getPreferences(MODE_PRIVATE).getString("parent2","");
        String p1persnr = getPreferences(MODE_PRIVATE).getString("p1persnr","");
        String p2persnr = getPreferences(MODE_PRIVATE).getString("p2persnr","");
        String p1work = getPreferences(MODE_PRIVATE).getString("p1work","");
        String p2work = getPreferences(MODE_PRIVATE).getString("p2work","");
        String p1workphone = getPreferences(MODE_PRIVATE).getString("p1workphone","");
        String p2workphone = getPreferences(MODE_PRIVATE).getString("p2workphone","");
        String p1mobile = getPreferences(MODE_PRIVATE).getString("p1mobile","");
        String p2mobile = getPreferences(MODE_PRIVATE).getString("p2mobile","");
        String p1mail = getPreferences(MODE_PRIVATE).getString("p1mail","");
        String p2mail = getPreferences(MODE_PRIVATE).getString("p2mail","");

        // Write stored data to gui elements
        Ekidsname.setText(kidsname);
        ekidspersnr.setText(kidspersnr);
        edep.setText(department);
        eaddress.setText(address);
        ephone.setText(phone);
        eparent1.setText(parent1);
        eparent2.setText(parent2);
        ep1persnr.setText(p1persnr);
        ep2persnr.setText(p2persnr);
        ep1work.setText(p1work);
        ep2work.setText(p2work);
        ep1workphone.setText(p1workphone);
        ep2workphone.setText(p2workphone);
        ep1mobile.setText(p1mobile);
        ep2mobile.setText(p2mobile);
        ep1mail.setText(p1mail);
        ep2mail.setText(p2mail);

    }

    @Override
    public void onBackPressed(){
        //saveData();
        Log.d("Settings: ", "Back clicked");
        EditText kidsname=(EditText)findViewById(R.id.kidsname);
        EditText kidspersnr=(EditText)findViewById(R.id.kidspersnr);
        EditText department=(EditText)findViewById(R.id.department);
        EditText address=(EditText)findViewById(R.id.address);
        EditText phone=(EditText)findViewById(R.id.phone);
        EditText parent1=(EditText)findViewById(R.id.parent1);
        EditText parent2=(EditText)findViewById(R.id.parent2);
        EditText p1persnr=(EditText)findViewById(R.id.p1persnr);
        EditText p2persnr=(EditText)findViewById(R.id.p2persnr);
        EditText p1work=(EditText)findViewById(R.id.p1work);
        EditText p2work=(EditText)findViewById(R.id.p2work);
        EditText p1workphone=(EditText)findViewById(R.id.p1workphone);
        EditText p2workphone=(EditText)findViewById(R.id.p2workphone);
        EditText p1mobile=(EditText)findViewById(R.id.p1mobile);
        EditText p2mobile=(EditText)findViewById(R.id.p2mobile);
        EditText p1mail=(EditText)findViewById(R.id.p1mail);
        EditText p2mail=(EditText)findViewById(R.id.p2mail);

        Log.d("Kids name", kidsname.getText().toString());
        getPreferences(MODE_PRIVATE).edit().putString("kidsname",kidsname.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("kidspersnr",kidspersnr.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("department",department.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("address",address.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("phone",phone.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("parent1",parent1.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("parent2",parent2.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p1persnr",p1persnr.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p2persnr",p2persnr.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p1work",p1work.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p2work",p2work.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p1workphone",p1workphone.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p2workphone",p2workphone.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p1mobile",p1mobile.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p2mobile",p2mobile.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p1mail",p1mail.getText().toString()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("p2mail",p2mail.getText().toString()).commit();


        super.onBackPressed();
    }
}

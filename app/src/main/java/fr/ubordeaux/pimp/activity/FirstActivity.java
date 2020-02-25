package fr.ubordeaux.pimp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import fr.ubordeaux.pimp.R;

/**
 * First activity showed by the app, to choose the first picture to edit.
 */
public class FirstActivity extends AppCompatActivity {

    /**
     * Send 0 if user want to open picture from gallery, and 1 if he want from camera
     */
    public static final String LAUNCH_CODE = "fr.ubordeaux.pimp.LAUNCH_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    public void gallery(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LAUNCH_CODE, 0);
        startActivity(intent);
    }

    public void camera(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LAUNCH_CODE, 1);
        startActivity(intent);
    }

}

package fr.ubordeaux.pimp.activity;

import androidx.appcompat.app.AppCompatActivity;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.io.BitmapIO;
import fr.ubordeaux.pimp.util.MainSingleton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize Singleton
        MainSingleton.setContext(this);

        //Loading default image from resources
        Bitmap bmp = BitmapIO.decodeBitmapFromResource(R.drawable.starwars);


        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bmp);
    }




}

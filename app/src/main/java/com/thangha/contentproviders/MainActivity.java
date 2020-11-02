package com.thangha.contentproviders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import utils.ToolUtilities;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickToOpenContact(View view) {
        Intent intent = new Intent(this, ContactContentActivity.class);
        startActivity(intent);
    }

    public void clickToCopy(View view) {
        String desDir = "MyDBs";
        String srcDir = "/data/" + this.getPackageName() + "/databases";
        String result = ToolUtilities.copyFileFromDataDirToSDCard(desDir, srcDir);

        TextView txt = (TextView) findViewById(R.id.txtFileLists);
        txt.setText(result);
    }

    public void clickToOpenOwn(View view) {
        Intent intent = new Intent(this, OwnContentActivity.class);
        startActivity(intent);
    }
}
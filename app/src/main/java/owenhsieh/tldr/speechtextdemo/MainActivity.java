package owenhsieh.tldr.speechtextdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import tldr.speechtextdemo.R;

public class MainActivity extends AppCompatActivity {

    protected static final int REQUEST_SPEECH = 0x1;

    private TextToSpeech textToSpeech;

    private ImageButton speechButton;
    private ImageButton listenButton;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainText = (TextView) findViewById(R.id.main_text);

        speechButton = (ImageButton) findViewById(R.id.speech_button);
        listenButton = (ImageButton) findViewById(R.id.listen_button);

        speechButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().toString());

                try {
                    startActivityForResult(intent, REQUEST_SPEECH);
                    mainText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Your device doesn't support Text To Speech",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        listenButton.setEnabled(false);

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(mainText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //If need, use this to change tts language
                    //textToSpeech.setLanguage(Locale.getDefault());
                    listenButton.setEnabled(true);
                } else {
                    listenButton.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            "Your device doesn't support Text to Speech",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SPEECH:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //using this to get confidence scores.
                    //float[] score = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

                    mainText.setText(text.get(0));
                } else {
                    mainText.setText("not found");
                }
                break;
        }
    }
}
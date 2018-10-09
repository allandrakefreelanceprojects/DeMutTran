package carlo.com.demuttran;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView editText_speaktotext;
    private ImageButton button_speak;
    private Button button_translate;
    private RecyclerView recyclerView;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Integer> mImageUrls = new ArrayList<>();
    private ArrayList<String> content = new ArrayList<>();
    ProgressDialog dialog_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        editText_speaktotext = findViewById(R.id.editText_speaktotext);
        button_speak = findViewById(R.id.button_speak);
        button_translate = findViewById(R.id.button_translate);
        dialog_loader = new ProgressDialog(MainActivity.this);

        button_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        button_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText_speaktotext.getText().toString().matches("")) {
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // Leave blank
                    }

                    mImageUrls.clear();
                    mNames.clear();

                    dialog_loader.setCanceledOnTouchOutside(false);
                    dialog_loader.setCancelable(false);
                    dialog_loader.setMessage("Loading, please wait...");
                    dialog_loader.show();

                    for (int i = 0; i < editText_speaktotext.length(); i++){
                        char c = editText_speaktotext.getText().toString().charAt(i);

                        String result = String.valueOf(c).toUpperCase();
                        if(result.equals(" ") || result.equals(".") || result.equals(",") || result.equals("?") || result.equals("!") || result.equals(":") ||
                                result.equals("-") || result.equals("&") || result.equals("*") || result.equals("@") || result.equals("\\") ||
                                result.equals("/") || result.equals("[") || result.equals("]") || result.equals("(") || result.equals(")")){
                            mImageUrls.add(R.drawable.ic_separator);
                            mNames.add("");
                        } else {
                            String[] array = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
                            int numberOfItems = array.length;
                            for (int i_array =0; i_array<numberOfItems; i_array++)
                            {
                                String name = array[i_array];
                                if(result.equals(name)){
                                    mImageUrls.add(getResources().getIdentifier("ic_" + name.toLowerCase(), "drawable", getPackageName()));
                                    mNames.add(name);
                                }
                            }
                        }
                    }

                    initRecyclerView();

                    Runnable run = new Runnable() {
                        public void run() {
                            dialog_loader.dismiss();
                        }
                    };
                    Handler myHandler = new Handler(Looper.myLooper());
                    myHandler.postDelayed(run, 2000);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter text.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go ahead, I'm listening...");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            // Leave blank
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText_speaktotext.setText(result.get(0));
                }
                break;
            }
        }
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
    }
}
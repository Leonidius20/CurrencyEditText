package com.blackcat.currencyedittexttester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyTextFormatter;
import com.blackcat.currencyedittexttester.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(
                getLayoutInflater()
        );
        setContentView(binding.getRoot());

        configureTestableLocalesTool();
        configureDecimalDigitsTool();

        binding.cetResetButton.setOnClickListener((view) -> onResetClicked());

        binding.button.setOnClickListener((view) -> onRefreshClicked());
    }

    void onResetClicked(){
        ((EditText)binding.cet).setText("");
    }

    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    void onRefreshClicked(){
        Log.d("MainActivity", "Locale: " + getResources().getConfiguration().locale.toString());
        Log.d("MainActivity", "DefaultLocale: " + Locale.getDefault());

        long maxRange = 15000000;
        long randNum = (long) (new Random().nextDouble() * maxRange);
        binding.etRawVal.setText(Long.toString(randNum));

        String result = "oops";
        try{
            DecimalFormat l = (DecimalFormat)DecimalFormat.getCurrencyInstance(Locale.getDefault());
            result = CurrencyTextFormatter.formatText(Long.toString(randNum), l, Locale.getDefault());
        }
        catch(IllegalArgumentException e){
            Log.e("MainActivity", e.getLocalizedMessage());
        }

        binding.etFormattedVal.setText(result);
    }


    private void configureTestableLocalesTool(){

        Locale[] locales = Locale.getAvailableLocales();
        List<String> spinnerContents = new ArrayList<>();

        for (Locale locale : locales) {
            if(locale.getLanguage().equals("") || locale.getCountry().equals("")){
                continue;
            }
            spinnerContents.add(locale.getDisplayName() + ", " + locale.getLanguage() + ", " + locale.getCountry());
        }

        int startingPosition = 0;

        for (int i = 0; i < spinnerContents.size(); i++){
            if (spinnerContents.get(i).equals("en,US")){
                startingPosition = i;
                break;
            }
        }

        Collections.sort(spinnerContents, String::compareToIgnoreCase);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerContents);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTestableLocales.setAdapter(spinnerArrayAdapter);
        binding.spinnerTestableLocales.setSelection(startingPosition);

        configureViewForLocale((String) binding.spinnerTestableLocales.getSelectedItem());

        binding.spinnerTestableLocales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                configureViewForLocale((String) binding.spinnerTestableLocales.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                configureViewForLocale("US");
            }
        });
    }


    private void configureViewForLocale(String locale){
        //Using english for testing as not setting the language field results in odd formatting. Recommend not
        //building locales this way in a production environment if possible
        String[] localeParts = locale.split(", ");
        Locale localeInQuestion = new Locale.Builder().setRegion(localeParts[2]).setLanguage(localeParts[1]).build();
        String localeInfo = "Country: " +
                            localeInQuestion.getDisplayCountry() +
                            System.lineSeparator() +
                            "Country Code: " +
                            localeInQuestion.getCountry() +
                            System.lineSeparator() +
                            "Currency: " +
                            Currency.getInstance(localeInQuestion).getDisplayName() +
                            System.lineSeparator() +
                            "Currency Code: " +
                            Currency.getInstance(localeInQuestion).getCurrencyCode() +
                            System.lineSeparator() +
                            "Currency Symbol: " + Currency.getInstance(localeInQuestion).getSymbol();

        binding.testableLocalesLocaleInfo.setText(localeInfo);
        binding.testableLocalesCet.configureViewForLocale(
                (DecimalFormat)DecimalFormat.getCurrencyInstance(localeInQuestion)
        );
    }

    private void configureDecimalDigitsTool(){
        binding.decimalDigitsToolNumberPicker.setMinValue(0);
        binding.decimalDigitsToolNumberPicker.setMaxValue(340);

        binding.decimalDigitsToolNumberPicker.setValue(2);

        binding.decimalDigitsToolCet.setDecimalDigits(binding.decimalDigitsToolNumberPicker.getValue());

        binding.decimalDigitsToolNumberPicker.setOnValueChangedListener(
            (picker, oldVal, newVal) -> binding.decimalDigitsToolCet.setDecimalDigits(newVal)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}

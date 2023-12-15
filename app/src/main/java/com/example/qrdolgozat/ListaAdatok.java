package com.example.qrdolgozat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaAdatok extends AppCompatActivity {

    private LinearLayout linearLayoutForm;
    private EditText etId;
    private EditText etNev;
    private EditText etJegy;
    private Button buttonModify;
    private Button buttonBack;
    private ListView tanuloList;
    private List<Tanulo> osztaly = new ArrayList<>();
    private String url = "https://retoolapi.dev/5edtfW/dolgozat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_adatok);
        init();
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutForm.setVisibility(View.GONE);
            }
        });
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanuloModositas();
            }
        });
    }

    public void init() {
        linearLayoutForm = findViewById(R.id.linearLayoutForm);
        etId = findViewById(R.id.etId);
        etNev = findViewById(R.id.etNev);
        etJegy = findViewById(R.id.etJegy);
        buttonModify = findViewById(R.id.buttonModify);
        buttonBack = findViewById(R.id.buttonBack);
        tanuloList = findViewById(R.id.tanuloList);
        tanuloList.setAdapter(new TanuloAdapter());
    }

    private class TanuloAdapter extends ArrayAdapter<Tanulo> {
        public TanuloAdapter() {
            super(ListaAdatok.this, R.layout.person_list_adapter,osztaly);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter, null, false);
            Tanulo actualTanulo = osztaly.get(position);
            TextView tvNev = view.findViewById(R.id.textViewName);
            TextView tvJegy = view.findViewById(R.id.textViewJegy);
            TextView tvModify = view.findViewById(R.id.textViewModify);
            tvNev.setText(actualTanulo.getNev());
            tvJegy.setText(actualTanulo.getJegy());
            tvModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayoutForm.setVisibility(View.VISIBLE);
                    etId.setText(String.valueOf(actualTanulo.getId()));
                    etNev.setText(actualTanulo.getNev());
                    etJegy.setText(actualTanulo.getJegy());
                    buttonModify.setVisibility(View.VISIBLE);
                    buttonBack.setVisibility(View.VISIBLE);
                }
            });
            return view;
        }
    }

    private void tanuloModositas() {
        String nev = etNev.getText().toString();
        String jegyText = etJegy.getText().toString();
        String idText = etId.getText().toString();
        Boolean valid = validacio();
        if (valid) {
            Toast.makeText(this, "Minden mezőt kötelező kitölteni!", Toast.LENGTH_SHORT).show();
        } else {
            int id = Integer.parseInt(idText);
            Tanulo tanulo = new Tanulo(id, nev, jegyText);
            Gson jsonConverter = new Gson();
            RequestTask task = new RequestTask(url + "/" + id, "PUT", jsonConverter.toJson(tanulo));
            task.execute();
        }
    }

    private boolean validacio() {
        if (etNev.getText().toString().trim().isEmpty() || etId.getText().toString().trim().isEmpty() || etJegy.getText().toString().trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(ListaAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getRepsponseCode() >= 400) {
                Toast.makeText(ListaAdatok.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getResponseMessage());
            }
            switch (requestType) {
                case "GET":
                    Tanulo[] tanuloArray = converter.fromJson(response.getResponseMessage(), Tanulo[].class);
                    osztaly.clear();
                    osztaly.addAll(Arrays.asList(tanuloArray));
                    Toast.makeText(ListaAdatok.this, ""+osztaly.size(), Toast.LENGTH_SHORT).show();
                    break;
                case "PUT":
                    Tanulo updateTanulo = converter.fromJson(response.getResponseMessage(), Tanulo.class);
                    osztaly.replaceAll(tanulo1 -> tanulo1.getId() == updateTanulo.getId() ? updateTanulo : tanulo1);
                    etNev.setText("");
                    etJegy.setText("");
                    linearLayoutForm.setVisibility(View.GONE);
                    buttonModify.setVisibility(View.GONE);
                    RequestTask task = new RequestTask(url, "GET");
                    task.execute();
                    break;
            }
        }
    }
}
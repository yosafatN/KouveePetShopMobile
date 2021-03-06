package com.example.kouveepetshop.Pengelolaan.Hewan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kouveepetshop.MainActivity;
import com.example.kouveepetshop.Pengelolaan.Layanan.Jenis_layanan_Edit;
import com.example.kouveepetshop.R;
import com.example.kouveepetshop.SharedPrefManager;

import java.util.HashMap;
import java.util.Map;

public class Ukuran_Hewan_Edit extends AppCompatActivity {
    private String ukuran_hewan;
    private Integer id;
    private EditText ukuran_hewan_text;
    private Button edit, delete;

    private ProgressDialog pd;
    private String ip = MainActivity.getIp();
    private String url = MainActivity.getUrl();
    private SharedPrefManager sharedPrefManager;

    private boolean doubleClickDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ukuran__hewan__edit);

        init();

        setText();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    editJenisHewan();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    }, 500);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doubleClickDelete) {
                    deleteJenisHewan();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    }, 500);
                } else {
                    doubleClickDelete = true;
                    Toast.makeText(Ukuran_Hewan_Edit.this, "Tekan Lagi Untuk Delete", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleClickDelete = false;
                        }
                    }, 2000);
                }
            }
        });
    }

    private void editJenisHewan(){
        ukuran_hewan = ukuran_hewan_text.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + this.url + "index.php/ukuranHewan/"+ id;

        Log.d("URL", url);
        Log.d("ID", String.valueOf(id));

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  request = new HashMap<String, String>();
                request.put("nama", ukuran_hewan);
                request.put("updated_by", sharedPrefManager.getSpUsername());
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void deleteJenisHewan(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + this.url + "index.php/ukuranhewan/delete/"+ id;

        Log.d("URL", url);
        Log.d("ID", String.valueOf(id));

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  request = new HashMap<String, String>();
                request.put("updated_by", sharedPrefManager.getSpUsername());
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void setText(){
        if (getIntent().hasExtra("keterangan")) {
            id = getIntent().getIntExtra("id", -1);
            ukuran_hewan_text.setText(getIntent().getStringExtra("keterangan"));
        }
    }

    private void init() {
        pd = new ProgressDialog(this);
        ukuran_hewan_text = findViewById(R.id.ukuran_hewan_edit_jenis);
        edit = findViewById(R.id.ukuran_hewan_edit);
        delete = findViewById(R.id.ukuran_hewan_delete);
        sharedPrefManager = new SharedPrefManager(this);
    }
    private boolean validasi() {
        int cek = 0;

        if (ukuran_hewan_text.getText().toString().equals("")) {
            ukuran_hewan_text.setError("Ukuran Hewan Tidak Boleh Kosong");
            cek = 1;
        }
        else if (ukuran_hewan_text.getText().toString().length() < 3) {
            ukuran_hewan_text.setError("Panjang Ukuran Hewan Minimal 3 Karakter");
            cek = 1;
        }

        else if (!ukuran_hewan_text.getText().toString().matches("[a-zA-Z ]+")) {
            ukuran_hewan_text.setError("Format Ukuran Hewan Salah");
            cek = 1;
        }

        return cek == 0;
    }
}

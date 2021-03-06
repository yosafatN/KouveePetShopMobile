package com.example.kouveepetshop.Pengelolaan.Layanan;

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
import com.example.kouveepetshop.Pengelolaan.Hewan.Jenis_Hewan_Edit;
import com.example.kouveepetshop.R;

import java.util.HashMap;
import java.util.Map;

public class Jenis_layanan_Edit extends AppCompatActivity {

    private String jenis_layanan;
    private Integer id;
    private EditText jenis_layanan_text;
    private Button edit, delete;

    private ProgressDialog pd;
    private String ip = MainActivity.getIp();
    private String url = MainActivity.getUrl();

    private boolean doubleClickDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jenis_layanan__edit);

        init();

        setText();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    editJenisLayanan();
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
                    deleteJenisLayanan();

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
                    Toast.makeText(Jenis_layanan_Edit.this, "Tekan Lagi Untuk Delete", Toast.LENGTH_SHORT).show();

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

    private void editJenisLayanan(){
        jenis_layanan = jenis_layanan_text.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + this.url + "index.php/jenislayanan/"+ id;

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
                request.put("nama", jenis_layanan);
                request.put("updated_by", "KelvinAja");
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void deleteJenisLayanan(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + this.url + "index.php/jenislayanan/delete/"+ id;

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
                request.put("updated_by", "KelvinAja");
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void setText(){
        if (getIntent().hasExtra("keterangan")) {
            id = getIntent().getIntExtra("id", -1);
            jenis_layanan_text.setText(getIntent().getStringExtra("keterangan"));
        }
    }

    private void init() {
        pd = new ProgressDialog(this);
        jenis_layanan_text = findViewById(R.id.jenis_layanan_edit_jenis);
        edit = findViewById(R.id.jenis_layanan_edit);
        delete = findViewById(R.id.jenis_layanan_delete);
    }

    private boolean validasi() {
        int cek = 0;

        if (jenis_layanan_text.getText().toString().equals("")) {
            jenis_layanan_text.setError("Jenis Layanan Tidak Boleh Kosong");
            cek = 1;
        }
        else if (jenis_layanan_text.getText().toString().length() < 3) {
            jenis_layanan_text.setError("Panjang Jenis Layanan Minimal 3 Karakter");
            cek = 1;
        }

        else if (!jenis_layanan_text.getText().toString().matches("[a-zA-Z ]+")) {
            jenis_layanan_text.setError("Format Jenis Layanan Salah");
            cek = 1;
        }

        return cek == 0;
    }
}

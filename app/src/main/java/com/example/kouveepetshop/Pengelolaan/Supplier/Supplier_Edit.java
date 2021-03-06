package com.example.kouveepetshop.Pengelolaan.Supplier;

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
import com.example.kouveepetshop.R;
import com.example.kouveepetshop.SharedPrefManager;

import java.util.HashMap;
import java.util.Map;

public class Supplier_Edit extends AppCompatActivity {

    private Integer id;
    private EditText nama_text, no_telp_text, alamat_text, kota_text;
    private Button edit, delete;
    private String nama, no_telp, alamat, kota;

    private String ip = MainActivity.getIp();
    private String url = MainActivity.getUrl();

    private SharedPrefManager sharedPrefManager;

    private boolean doubleClickDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplier_edit);

        init();

        setText();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    editSupplier();

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
                    deleteSupplier();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    }, 500);
                }
                else {
                    doubleClickDelete = true;
                    Toast.makeText(Supplier_Edit.this, "Tekan Lagi Untuk Delete", Toast.LENGTH_SHORT).show();

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

    private void editSupplier(){
        getValue();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + this.url +"index.php/Supplier/"+ id;
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
                Map<String, String>  request = new HashMap<>();
                request.put("nama", nama);
                request.put("no_telp",  String.valueOf(no_telp));
                request.put("alamat", String.valueOf(alamat));
                request.put("kota", String.valueOf(kota));
                request.put("updated_by", sharedPrefManager.getSpUsername());
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void deleteSupplier(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + this.url +"index.php/Supplier/delete/"+ id;
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
                Map<String, String>  request = new HashMap<>();
                request.put("updated_by", sharedPrefManager.getSpUsername());
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void getValue() {
        nama = nama_text.getText().toString();
        no_telp = no_telp_text.getText().toString();
        alamat = alamat_text.getText().toString();
        kota = kota_text.getText().toString();
    }

    private void setText(){
        if (getIntent().hasExtra("nama")) {
            id = getIntent().getIntExtra("id", -1);
            nama_text.setText(getIntent().getStringExtra("nama"));
            no_telp_text.setText(getIntent().getStringExtra("no_telp"));
            alamat_text.setText(getIntent().getStringExtra("alamat"));
            kota_text.setText(getIntent().getStringExtra("kota"));
        }
    }

    private void init() {
        sharedPrefManager = new SharedPrefManager(this);
        nama_text       = findViewById(R.id.supplier_edit_nama);
        no_telp_text    = findViewById(R.id.supplier_edit_no_telp);
        alamat_text     = findViewById(R.id.supplier_edit_alamat);
        kota_text       = findViewById(R.id.supplier_edit_kota);
        edit            = findViewById(R.id.supplier_edit_edit);
        delete          = findViewById(R.id.supplier_edit_delete);
    }

    private boolean validasi() {
        int cek = 0;
        if (nama_text.getText().toString().equals("")) {
            nama_text.setError("Nama Tidak Boleh Kosong");
            cek = 1;
        }
        else if (nama_text.getText().toString().length() < 3) {
            nama_text.setError("Panjang Nama Minimal 3 Karekter");
            cek = 1;
        }

        else if (!nama_text.getText().toString().matches("[a-zA-Z ]+")) {
            nama_text.setError("Format Nama Salah");
            cek = 1;
        }

        if (no_telp_text.getText().toString().equals("")) {
            no_telp_text.setError("Nomor Telepon Tidak Boleh Kosong");
            cek = 1;
        }
        else if (no_telp_text.getText().toString().length() < 10 || no_telp_text.getText().toString().length() > 13 ) {
            no_telp_text.setError("Nomor Telepon 10 - 13 Karakter");
            cek = 1;
        }

        else if (!String.valueOf(no_telp_text.getText().toString().charAt(0)).equals("0") && !String.valueOf(no_telp_text.getText().toString().charAt(1)).equals("8")) {
            no_telp_text.setError("Format Nomor Telepon Salah");
            cek = 1;
        }

        if (alamat_text.getText().toString().equals("")) {
            alamat_text.setError("Alamat Tidak Boleh Kosong");
            cek = 1;
        }
        else if (alamat_text.getText().toString().length() < 3) {
            alamat_text.setError("Panjang Alamat Minimal 3 Karekter");
            cek = 1;
        }

        if (kota_text.getText().toString().equals("")) {
            kota_text.setError("Kota Tidak Boleh Kosong");
            cek = 1;
        }
        else if (kota_text.getText().toString().length() < 3) {
            kota_text.setError("Panjang Kota Minimal 3 Karekter");
            cek = 1;
        }

        else if (!kota_text.getText().toString().matches("[a-zA-Z ]+")) {
            kota_text.setError("Format Kota Salah");
            cek = 1;
        }
        return cek == 0;
    }
}

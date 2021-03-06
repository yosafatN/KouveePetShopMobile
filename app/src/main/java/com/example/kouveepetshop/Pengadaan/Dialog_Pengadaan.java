package com.example.kouveepetshop.Pengadaan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kouveepetshop.API.Rest_API;
import com.example.kouveepetshop.MainActivity;
import com.example.kouveepetshop.R;
import com.example.kouveepetshop.SharedPrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Dialog_Pengadaan extends AppCompatDialogFragment {
    private TextView nama, tersedia, jumlah;
    private ImageView gambar, add, inc;
    private Button selesai;
    private Integer id, id_produk = 0, isKeranjang, jumlah_produk = 0, jumlah_tersedia, id_pemesanan;

    private String ip = MainActivity.getIp();
    private String url = MainActivity.getUrl();
    private SharedPrefManager sharedPrefManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_detilpengadaan,null);
        builder.setView(view);

        sharedPrefManager = new SharedPrefManager(getContext());

        nama = view.findViewById(R.id.dialog_detilpengadaan_nama);
        tersedia = view.findViewById(R.id.dialog_detilpengadaan_tersedia);
        gambar = view.findViewById(R.id.dialog_detilpengadaan_gambar);
        jumlah = view.findViewById(R.id.dialog_detilpengadaan_jumlah);
        add = view.findViewById(R.id.dialog_detilpengadaan_jumlah_inc);
        inc = view.findViewById(R.id.dialog_detilpengadaan_jumlah_dec);
        selesai = view.findViewById(R.id.dialog_detilpengadaan_selesai);
        isKeranjang = this.getArguments().getInt("isKeranjang");

        id_pemesanan = sharedPrefManager.getSpIdPemesanan();
        if (isKeranjang == 0) {
            id_produk = this.getArguments().getInt("id");
            loadjson();
            getJumlah(id_produk);
        }
        else {
            id = this.getArguments().getInt("id");
            getDetail();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getJumlah(id_produk);
                }
            }, 500);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumlah_produk < jumlah_tersedia) {
                    jumlah_produk = jumlah_produk + 1;
                    jumlah.setText(String.valueOf(jumlah_produk));
                    Log.d("id",String.valueOf(id));
                }
            }
        });

        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumlah_produk != 0) {
                    jumlah_produk = jumlah_produk - 1;
                    jumlah.setText(String.valueOf(jumlah_produk));
                    Log.d("id",String.valueOf(id));
                }
            }
        });

        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKeranjang == 0) {
                    post();
                    Log.d("id",String.valueOf(id));
                }
                else if (isKeranjang == 1){
                    if (jumlah_produk == 0) {
                        delete();
                    }
                    else {
                        update();
                    }
                }
                Intent returnIntent = new Intent();
                ((Activity)getContext()).setResult(1, returnIntent);
                Dialog_Pengadaan.this.dismiss();
            }
        });

        return builder.create();
    }

    private void post(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ip + this.url + "index.php/Detilpemesanan";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (!jsonObject.getString("error").equals("true")) {
                                Log.d("id",String.valueOf(id));
                                Log.d("Status: ", "Tertambah");
                                Log.d("id_produk", String.valueOf(id_produk));
                                Log.d("id_pemesanan",  String.valueOf(id_pemesanan));
                                Log.d("jumlah", String.valueOf(jumlah_produk));
                                Log.d("pegawai", sharedPrefManager.getSpUsername());
                            }
                            Log.d("Response", jsonObject.getString("message"));
                            Log.d("id",String.valueOf(id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                request.put("id_produk", String.valueOf(id_produk));
                request.put("id_pemesanan",  String.valueOf(id_pemesanan));
                request.put("jumlah", String.valueOf(jumlah_produk));
                request.put("pegawai", sharedPrefManager.getSpUsername());
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void update(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ip + this.url + "index.php/DetilPemesanan/"+id;
        Log.d("Link : ", url);
        Log.d("id_produk : ", String.valueOf(id_produk));
        Log.d("id_pemesanan : ", String.valueOf(id_pemesanan));
        Log.d("jumlah : ", String.valueOf(jumlah_produk));
        Log.d("Pegawai : ", sharedPrefManager.getSpUsername());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (!jsonObject.getString("error").equals("true")) {
                                Log.d("Status: ", "Terupdate");
                            }
                            Log.d("Response", jsonObject.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                request.put("id_produk", String.valueOf(id_produk));
                request.put("id_pemesanan",  String.valueOf(id_pemesanan));
                request.put("jumlah", String.valueOf(jumlah_produk));
                request.put("pegawai", sharedPrefManager.getSpUsername());
                return request;
            }
        };
        queue.add(postRequest);
    }


    private void delete(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ip + this.url + "index.php/DetilPemesanan/delete/"+id;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (!jsonObject.getString("error").equals("true")) {
                                Log.d("Status: ", "Terhapus");
                            }
                            Log.d("Response", jsonObject.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void loadjson(){
        String url = ip + this.url + "index.php/Produk/"+id_produk;
        final String link = ip + this.url;

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String substring;

                Log.d("volley", "response : " + response);
                JSONObject jsonObject;
                try {
                    jsonObject = response.getJSONObject("message");
                    nama.setText(jsonObject.getString("nama"));
                    substring = jsonObject.getString("link_gambar").substring(47);
                    final String link_gambar = link + substring;
                    Picasso.get().load(link_gambar).into(gambar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(getContext()).addToRequestQueue(arrayRequest);
    }

    private void getJumlah(Integer id_get){
        String url = ip + this.url + "index.php/Produk/stock/"+id_get;
        Log.d("URL", url);

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("volley", "response : " + response);
                JSONObject jsonObject;
                try {
                    jumlah_tersedia = response.getInt("message");
                    tersedia.setText("Tersedia : " + String.valueOf(jumlah_tersedia));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(getContext()).addToRequestQueue(arrayRequest);
    }

    private void getDetail(){
        String url = ip + this.url + "index.php/DetilPemesanan/detail/"+id;
        final String link = ip + this.url;

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String substring;

                Log.d("volley", "response : " + response);
                try {
                    JSONArray massage = response.getJSONArray("message");
                    JSONObject massageDetail = massage.getJSONObject(0);
                    nama.setText(massageDetail.getString("nama"));
                    jumlah_produk = Integer.parseInt(massageDetail.getString("jumlah"));
                    jumlah.setText(String.valueOf(jumlah_produk));
                    id_produk = Integer.parseInt(massageDetail.getString("id_produk"));
                    Log.d("ID Produk: ", String.valueOf(id_produk));
                    substring = massageDetail.getString("link_gambar").substring(47);
                    final String link_gambar = link + substring;
                    Picasso.get().load(link_gambar).into(gambar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(getContext()).addToRequestQueue(arrayRequest);
    }
}


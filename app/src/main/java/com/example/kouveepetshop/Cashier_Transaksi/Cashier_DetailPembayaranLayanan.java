package com.example.kouveepetshop.Cashier_Transaksi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.example.kouveepetshop.API.Rest_API;
import com.example.kouveepetshop.CS_Transaksi.DetilTransaksiLayananDAO;
import com.example.kouveepetshop.CS_Transaksi.DetilTransaksiPenjualanDAO;
import com.example.kouveepetshop.MainActivity;
import com.example.kouveepetshop.R;
import com.example.kouveepetshop.SharedPrefManager;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Cashier_DetailPembayaranLayanan extends AppCompatActivity {
    private EditText cari;
    private Cashier_DetailPembayaranLayanan_Adapter mAdapter;
    private ArrayList<DetilTransaksiLayananDAO> mItems;
    private ProgressDialog pd;
    private String ip = MainActivity.getIp();
    private String url = MainActivity.getUrl();
    private int id;
    private FloatingActionButton done;
    private SharedPrefManager sharedPrefManager;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashier_pembayaran_penjualan);
        final PullRefreshLayout layout = findViewById(R.id.swipeRefreshLayout);

        init();

        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mItems.clear();
                cari.setText("");
                getLayanan();
                layout.setRefreshing(false);
            }
        });

        getLayanan();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cashier_DetailPembayaranLayanan.this, Cashier_Pembayaran.class);
                intent.putExtra("isTransaksiLayanan", 1);
                startActivity(intent);
            }
        });

        cari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s);
            }
        });
    }

    private void init(){
        pd = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.recycle_cs_transaksi_penjualan_keranjang);
        mItems = new ArrayList<>();
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new Cashier_DetailPembayaranLayanan_Adapter(this, mItems);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(mAdapter);
        cari = findViewById(R.id.cs_transaksi_penjualan_keranjang_search);
        id = getIntent().getIntExtra("id", -1);
        sharedPrefManager = new SharedPrefManager(this);
        done = findViewById(R.id.cs_transaksi_penjualan_keranjang_done);
    }

    private void getLayanan(){
        mItems.clear();
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        String url = ip + this.url + "index.php/DetilTransaksiLayanan/"+id;
        Log.d("URL: ", url + "  " + id);

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("volley", "response : " + response.toString());

                try {
                    JSONArray massage = response.getJSONArray("message");
                    for (int i = massage.length()-1; i > -1 ; i--){
                        JSONObject massageDetail = massage.getJSONObject(i);
                        DetilTransaksiLayananDAO layanan = new DetilTransaksiLayananDAO();
                        layanan.setId(massageDetail.getInt("id"));
                        layanan.setId_layanan(massageDetail.getInt("id_layanan"));
                        layanan.setNama_hewan(massageDetail.getString("nama_hewan"));
                        layanan.setNama_layanan(massageDetail.getString("nama_layanan"));
                        layanan.setHarga(massageDetail.getDouble("harga"));
                        layanan.setGambar(massageDetail.getString("url_gambar"));
                        mItems.add(layanan);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                pd.cancel();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(this).addToRequestQueue(arrayRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            getLayanan();
            mAdapter.notifyDataSetChanged();
        }
    }
}

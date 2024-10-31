package xyz.xoventechdev.monir.appforown;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SmsAdapter.OnItemLongClickListener{
    private RecyclerView recyclerView;
    private TextView tvEmptyInbox, onInternet;
    private SmsAdapter smsAdapter;
    private List<SmsMessageModel> smsList;
    private List<String> smsKeys;

    private ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyInbox = findViewById(R.id.tvEmptyInbox);
        onInternet = findViewById(R.id.onInternet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        smsList = new ArrayList<>();
        smsKeys = new ArrayList<>();
        smsAdapter = new SmsAdapter(smsList, smsKeys, this);
        recyclerView.setAdapter(smsAdapter);

        fetchSmsData();

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!isNetworkConnected()) {
            onInternet.setVisibility(View.VISIBLE);
        }
    }
    private void fetchSmsData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("messages");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                smsList.clear();
                smsKeys.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SmsMessageModel sms = dataSnapshot.getValue(SmsMessageModel.class);
                    String key = dataSnapshot.getKey();
                    smsList.add(sms);
                    smsKeys.add(key);
                }
                Collections.sort(smsList, (o1, o2) -> o2.timestamp.compareTo(o1.timestamp)); // Sort the list by timestamp (newest to oldest)
                smsAdapter.notifyDataSetChanged();
                updateEmptyInboxMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load SMS data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyInboxMessage() {
        if (smsList.isEmpty()) {
            tvEmptyInbox.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyInbox.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemLongClick(int position, String key) {
        showDeleteConfirmationDialog(position, smsKeys.get(position));
    }

    private void showDeleteConfirmationDialog(int position, String key) {
        new AlertDialog.Builder(this)
                .setTitle("Delete SMS")
                .setMessage("Are you sure you want to delete this SMS?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteSms(position, key))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteSms(int position, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("messages").child(key);

        myRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (position >= 0 && position < smsList.size()) {
                    smsList.remove(position);
                    smsKeys.remove(position);
                    smsAdapter.notifyItemRemoved(position);
                }
                updateEmptyInboxMessage();
                Toast.makeText(MainActivity.this, "SMS deleted successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to delete SMS.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNetworkConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
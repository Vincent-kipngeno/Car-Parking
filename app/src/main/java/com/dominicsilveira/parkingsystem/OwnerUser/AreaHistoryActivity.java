package com.dominicsilveira.parkingsystem.OwnerUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.dominicsilveira.parkingsystem.NormalUser.NearByAreaActivity;
import com.dominicsilveira.parkingsystem.R;
import com.dominicsilveira.parkingsystem.classes.BookedSlotKey;
import com.dominicsilveira.parkingsystem.classes.BookedSlots;
import com.dominicsilveira.parkingsystem.utils.AppConstants;
import com.dominicsilveira.parkingsystem.utils.BasicUtils;
import com.dominicsilveira.parkingsystem.utils.adapters.BookingHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AreaHistoryActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase db;
    BasicUtils utils=new BasicUtils();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<BookedSlotKey> bookedSlotKeyList=new ArrayList<BookedSlotKey>();

    AppConstants globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_history);

        initComponents();
        attachListeners();
        if(!utils.isNetworkAvailable(getApplication())){
            Toast.makeText(AreaHistoryActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        globalClass=(AppConstants)getApplicationContext();

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.area_history_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AreaHistoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void attachListeners() {
        db.getReference().child("ParkingAreas").orderByChild("userID").equalTo(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            db.getReference().child("BookedSlots").orderByChild("placeID").equalTo(dataSnapshot.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                BookedSlots bookedSlot = dataSnapshot.getValue(BookedSlots.class);
                                                BookedSlotKey bookedSlotKey=new BookedSlotKey(bookedSlot,dataSnapshot.getKey());
                                                bookedSlotKeyList.add(bookedSlotKey);
                                            }
                                            mAdapter = new BookingHistoryAdapter(bookedSlotKeyList);
                                            recyclerView.setAdapter(mAdapter);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
package com.example.recentverinstacloneforsuccess.Fragment;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.recentverinstacloneforsuccess.Adapter.UserAdapter;
import com.example.recentverinstacloneforsuccess.Model.User;
import com.example.recentverinstacloneforsuccess.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUser;
    EditText search_Bar;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_Bar = view.findViewById(R.id.search_bar);
        mUser = new ArrayList<>();

        userAdapter = new UserAdapter(getContext(), mUser,true);
        recyclerView.setAdapter(userAdapter);

        readUsers();
        search_Bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }
    private void searchUsers(String s)
    {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
        .startAt(s)
        .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                mUser.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    mUser.add(user);
                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private  void readUsers()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot)
            {
                if(search_Bar.getText().toString().equals(""))
                {
                    mUser.clear();
                    for(DataSnapshot snapshot : datasnapshot.getChildren())
                    {
                        User user = snapshot.getValue(User.class);
                        mUser.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
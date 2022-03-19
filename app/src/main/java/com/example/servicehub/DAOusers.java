package com.example.servicehub;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOusers {

    private DatabaseReference databaseReference;

    public DAOusers(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Users.class.getSimpleName());
    }

    public Task<Void> add(Users users){
        return databaseReference.push().setValue(users);
    }
}

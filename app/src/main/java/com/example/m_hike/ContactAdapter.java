package com.example.m_hike;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.HolderContact>{
    private Context context;
    private ArrayList<ContactModel> contactList;

    MyDbHelper dbHelper;

    public ContactAdapter(Context context, ArrayList<ContactModel> contactList) {
        this.context = context;
        this.contactList = contactList;

        dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public HolderContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new HolderContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderContact holder, int position) {
        //get data
        ContactModel model = contactList.get(position);
        String id = model.getId();
        String contactName = model.getName();
        String image = model.getImage();
        String dob = model.getDob();
        String email = model.getEmail();
        //set data
        holder.contactName.setText(contactName);
        holder.dob.setText(dob);
        holder.email.setText(email);

        if(image.equals("null")){
            holder.image.setImageResource(R.drawable.ic_image_hike);
        } else {
            holder.image.setImageURI(Uri.parse(image));
        }
        //handle item clicks (go to detail activity)
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Pass contact id to next activity to show detail
                Intent intent = new Intent(context, ContactDetailActivity.class);
                intent.putExtra("CONTACT_ID", id);
                context.startActivity(intent);
            }
        });
        //handle more_button click
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreDialog(id, contactName, image, email, dob);
            }
        });
    }

    private void showMoreDialog(final String id, String contactName, String image, String dob, String email) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //option: edit
                    Intent intent = new Intent(context, AddUpdateContactsActivity.class);
                    intent.putExtra("ID",id);
                    intent.putExtra("CONTACT_NAME",contactName);
                    intent.putExtra("IMAGE",image);
                    intent.putExtra("EMAIL",email);
                    intent.putExtra("DOB",dob);
                    intent.putExtra("isEditMode",true);
                    context.startActivity(intent);
                }
                else if(i==1){
                    //option: delete
                    dbHelper.deleteContact(id);
                    Toast.makeText(context, "Delete contact: " + contactName + "Successfully!", Toast.LENGTH_SHORT).show();
                    //refresh contacts
                    ((MainActivity)context).onResume();
                }
            }
        });
        builder.create().show();
    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }
    class HolderContact extends RecyclerView.ViewHolder{
        ImageView image;
        TextView contactName, dob, email;
        ImageButton moreBtn;

        public HolderContact(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.nameContact);
            dob = itemView.findViewById(R.id.dobContact);
            email = itemView.findViewById(R.id.emailContact);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            image = itemView.findViewById(R.id.imageContact);
        }
    }
}

package com.boun.volkanyilmaz.walkandwin.navigatorMenus2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.R;
import com.boun.volkanyilmaz.walkandwin.models.Items;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by volkanyilmaz on 20/02/18.
 */

public class MyMenuActivity2 extends AppCompatActivity {

  private ListView lv;
  private ArrayAdapter<String> adapter;
  private CustomList list = new CustomList();
  private Dialog d;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private FirebaseAuth auth;
  private static int menuNumber = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mymenu2);

    Toolbar toolbar = findViewById(R.id.toolbar2);
    setSupportActionBar(toolbar);
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress/users");
    lv = findViewById(R.id.lv2);
    if (list != null) {

    }

    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (d != null) {
          if (!d.isShowing()) {
            displayInputDialog(i);
          } else {
            d.dismiss();
          }
        }
      }
    });
    final FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        displayInputDialog(-1);
      }
    });
  }

  private void displayInputDialog(final int pos) {
    d = new Dialog(this);
    d.setTitle("Benim Menüm");
    d.setContentView(R.layout.input_dialog);

    final EditText nameEditTxt = d.findViewById(R.id.nameEditText);
    final EditText priceEditTxt = d.findViewById(R.id.priceEditText);
    Button addBtn = d.findViewById(R.id.addBtn);
    Button updateBtn = d.findViewById(R.id.updateBtn);
    Button deleteBtn = d.findViewById(R.id.deleteBtn);

    if (pos == -1) {
      addBtn.setEnabled(true);
      updateBtn.setEnabled(false);
      deleteBtn.setEnabled(false);
    } else {
      addBtn.setEnabled(true);
      updateBtn.setEnabled(true);
      deleteBtn.setEnabled(true);
      nameEditTxt.setText(list.getItems().get(pos).getName());
      priceEditTxt.setText(list.getItems().get(pos).getPrice2());
    }

    addBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //GET DATA
        String name = nameEditTxt.getText().toString();
        int price = Integer.parseInt(priceEditTxt.getText().toString());

        //VALIDATE
        if (name.length() > 0 && name != null && price > 0) {
          //save
          list.save(name, price);
          nameEditTxt.setText("");
          priceEditTxt.setText("");
          adapter = new ArrayAdapter<>(MyMenuActivity2.this, android.R.layout.simple_list_item_1, list.getNames());
          lv.setAdapter(adapter);


        } else {
          Toast.makeText(MyMenuActivity2.this, "Name or price cannot be empty", Toast.LENGTH_SHORT).show();
        }
      }
    });
    updateBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //GET DATA
        String newName = nameEditTxt.getText().toString();
        int newPrice = Integer.parseInt(priceEditTxt.getText().toString());

        //VALIDATE

        if (newName.length() > 0 && newName != null && newPrice > 0 && priceEditTxt.getText() != null) {
          //save
          if (list.update(pos, newName, newPrice)) {

            nameEditTxt.setText(newName.toString());
            priceEditTxt.setText(String.valueOf(newPrice));
            adapter = new ArrayAdapter<>(MyMenuActivity2.this, android.R.layout.simple_list_item_1, list.getNames());
            lv.setAdapter(adapter);
          }

        } else {
          Toast.makeText(MyMenuActivity2.this, "Name or price cannot be empty", Toast.LENGTH_SHORT).show();
        }
      }
    });
    deleteBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //DELETE
        if (list.delete(pos)) {
          nameEditTxt.setText("");
          priceEditTxt.setText("");
          adapter = new ArrayAdapter<>(MyMenuActivity2.this, android.R.layout.simple_list_item_1, list.getNames());

          lv.setAdapter(adapter);

        }
      }
    });

    d.show();
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.saveMenu:
        if (list != null) {
          AlertDialog.Builder builder = new AlertDialog.Builder(MyMenuActivity2.this);

          builder
            .setMessage("Menünüzü son haliyle kaydetmek istediğinize emin misiniz?")
            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                fetchMenu();
                Toast.makeText(MyMenuActivity2.this, "Menünüz başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                menuNumber = 0;
              }
            })
            .setNegativeButton("Hayır", null)
            .create();
          builder.show();
        } else
          Toast.makeText(MyMenuActivity2.this, "Lütfen menüye item ekleyiniz.", Toast.LENGTH_SHORT).show();
    }
  }

  public void fetchMenu() {
    ref.child(auth.getUid()).child("menu").removeValue();
    for (Items i : list.getItems()) {
      if (i != null) {
        ref.child(auth.getUid()).child("menu").child(String.valueOf(menuNumber)).child("name").setValue(i.getName());
        ref.child(auth.getUid()).child("menu").child(String.valueOf(menuNumber)).child("price").setValue(i.getPrice2());
        menuNumber++;
      }
    }
  }
}

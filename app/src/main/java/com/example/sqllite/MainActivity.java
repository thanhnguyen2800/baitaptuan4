package com.example.sqllite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    ListView listView;
    ArrayList<NotesModel> arrayList;
    NotesAdapter adapter;

    Button buttonAddMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitDatabaseSQLite();
        AnhXa();
        databaseSQLite();

        if (buttonAddMain != null) {
            buttonAddMain.setOnClickListener(v -> openDialogAdd());
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuAdd) {
                openDialogAdd();
                return true;
            }
            return false;
        });

    }

    private void InitDatabaseSQLite() {
        databaseHandler = new DatabaseHandler(this);
        databaseHandler.QueryData(
                "CREATE TABLE IF NOT EXISTS Notes(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "NameNotes VARCHAR(200))"
        );
    }

    private void databaseSQLite() {
        arrayList.clear();
        Cursor cursor = databaseHandler.GetData("SELECT * FROM Notes");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            arrayList.add(new NotesModel(id, name));
        }

        adapter.notifyDataSetChanged();
    }

    private void AnhXa() {
        listView = findViewById(R.id.listView1);
        buttonAddMain = findViewById(R.id.buttonAdd);

        arrayList = new ArrayList<>();
        adapter = new NotesAdapter(this, R.layout.row_notes, arrayList);
        listView.setAdapter(adapter);
    }

    private void openDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_notes);

        EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);

        buttonAdd.setOnClickListener(v -> {
            String name = editText.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseHandler.QueryData(
                    "INSERT INTO Notes(NameNotes) VALUES('" + name + "')"
            );

            databaseSQLite();
            dialog.dismiss();
        });

        buttonHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAdd) {
            openDialogAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialogUpdate(NotesModel notes) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update_notes);

        EditText editText = dialog.findViewById(R.id.editTextUpdateName);
        Button buttonUpdate = dialog.findViewById(R.id.buttonUpdate);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);

        editText.setText(notes.getNameNote());

        buttonUpdate.setOnClickListener(v -> {
            String newName = editText.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseHandler.QueryData(
                    "UPDATE Notes SET NameNotes = '" + newName + "' WHERE Id = " + notes.getIdNote()
            );

            databaseSQLite();
            dialog.dismiss();
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        });

        buttonHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    public void DialogDelete(String name, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xóa Notes " + name + " này không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.QueryData("DELETE FROM Notes WHERE Id = '" + id + "'");
                databaseSQLite();
                Toast.makeText(MainActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                databaseSQLite();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

}

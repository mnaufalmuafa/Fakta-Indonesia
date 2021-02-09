package com.appbasisplatform.faktaindonesia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TambahFaktaDialog.TambahFaktaDialogListener {
    private ActionMode mActionMode;

    private final ArrayList<String> facts = new ArrayList<String>(); // array list untuk menyimpan fakta
    private int selectedFactIndex = 0; // menyimpan index fakta yang ditampilkan

    private int[] colors = {
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
    };
    private int selectedColorIndex = 0;

    private TextView mFact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        facts.add("Indonesia adalah negara kepulauan");
        facts.add("Palangkaraya adalah ibu kota Provinsi Kalimantan Tengah");
        facts.add("Gunung 'Puncak Jaya' adalah gunung tertinggi se-Indonesia");
        mFact = findViewById(R.id.fact);
        mFact.setText(facts.get(selectedFactIndex));

        ImageView arrow_right = findViewById(R.id.arrow_right);
        arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFactIndex == facts.size() - 1) {
                    selectedFactIndex = 0;
                }
                else {
                    selectedFactIndex++;
                }
                mFact.setText(facts.get(selectedFactIndex));
            }
        });

        mFact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("fact", facts.get(selectedFactIndex));
//                clipboard.setPrimaryClip(clip);
//                Toast.makeText(getApplicationContext(), "Berhasil menyalin fakta", Toast.LENGTH_SHORT).show();
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = startSupportActionMode(mActionModeCallback);
                return true;
            }
        });

        getWindow().getDecorView().setBackgroundColor(colors[selectedColorIndex]);

        Button btnTambahFakta = findViewById(R.id.btnTambahFakta);
        btnTambahFakta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    public void openDialog() {
        TambahFaktaDialog mTambahFaktaDialog = new TambahFaktaDialog();
        mTambahFaktaDialog.show(getSupportFragmentManager(), "Tambah Fakta");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_reset){
            selectedColorIndex = 0;
            selectedFactIndex = 0;
            getWindow().getDecorView().setBackgroundColor(colors[selectedColorIndex]);
            mFact.setText(facts.get(selectedFactIndex));
        }
        else if (item.getItemId() == R.id.menu_change_background_color) {
            if (selectedColorIndex == colors.length - 1) {
                selectedColorIndex = 0;
            }
            else {
                selectedColorIndex++;
            }
            getWindow().getDecorView().setBackgroundColor(colors[selectedColorIndex]);
        }

        return true;
    }

    @Override
    public void applyTexts(String fact) {
        facts.add(fact);
        selectedFactIndex = facts.size() - 1;
        mFact.setText(facts.get(selectedFactIndex));
        Toast.makeText(getApplicationContext(), "Berhasil Menambah Fakta baru", Toast.LENGTH_SHORT).show();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            mode.setTitle("Pilih Aksi");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.copy_menu) {
                // Menyalin fakta ke clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("fact", facts.get(selectedFactIndex));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "Berhasil menyalin fakta", Toast.LENGTH_SHORT).show();

                mode.finish(); // menutup action mode
                return true;
            }
            else if (item.getItemId() == R.id.share_menu) {
                // Share fakta
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,facts.get(selectedFactIndex));
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Fakta");
                startActivity(Intent.createChooser(shareIntent, "Share..."));

                mode.finish(); // menutup action mode
                return true;
            }
            mode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}

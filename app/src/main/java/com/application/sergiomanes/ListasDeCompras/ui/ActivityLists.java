package com.application.sergiomanes.ListasDeCompras.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.application.sergiomanes.ListasDeCompras.R;
import com.application.sergiomanes.ListasDeCompras.database.DatabaseHelper;
import com.application.sergiomanes.ListasDeCompras.model.Lista;
import com.application.sergiomanes.ListasDeCompras.model.Producto;
import com.application.sergiomanes.ListasDeCompras.ui.adapter.ListsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.application.sergiomanes.ListasDeCompras.model.Lista.posInArray;

public class ActivityLists extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton addListButton;
    DatabaseHelper DB;
    ArrayList<Lista> arrayListLists = new ArrayList<>();
    int eventPosition;
    AlertDialog currentDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.application.sergiomanes.ListasDeCompras.R.layout.activity_lists);

        recyclerView = findViewById(R.id.recyclerAllLists);
        addListButton = findViewById(R.id.addList);
        DB = new DatabaseHelper(this);

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLists.this, ABMCompras.class);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        arrayListLists = DB.getAllLists();
        ListsAdapter adapter = new ListsAdapter(arrayListLists, getResources(), new ListsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int pos) {

                Intent intent = new Intent(ActivityLists.this, ABMCompras.class);
                Bundle bundle = new Bundle();
                bundle.putLong("listID", arrayListLists.get(pos).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }, new ListsAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(int pos) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLists.this);
                builder.setTitle(R.string.titulo_pop_up);
                builder.setMessage(com.application.sergiomanes.ListasDeCompras.R.string.re_preguntar);
                eventPosition = pos;

                builder.setPositiveButton(com.application.sergiomanes.ListasDeCompras.R.string.eliminar_lista, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Lista list = arrayListLists.get(eventPosition);
                        DB.deleteList(list);
                        arrayListLists.remove(eventPosition);
                        recyclerView.getAdapter().notifyItemRemoved(eventPosition);
                        dialogInterface.dismiss();
                    }
                });

                builder.setNegativeButton(com.application.sergiomanes.ListasDeCompras.R.string.cambiar_nombre, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int pos) {
                        dialogInterface.dismiss();
                        AlertDialog.Builder newNameBuilder = new AlertDialog.Builder(ActivityLists.this);

                        View view = getLayoutInflater().inflate(R.layout.modal_new_list_name, null);
                        final EditText newNameText = view.findViewById(R.id.editTextNewName);
                        Button buttonOk = view.findViewById(R.id.buttonOk);

                        buttonOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Lista list = arrayListLists.get(eventPosition);
                                list.setName(newNameText.getText().toString());
                                DB.updateList(list);
                                recyclerView.getAdapter().notifyItemChanged(eventPosition);
                                currentDialog.dismiss();
                            }
                        });

                        newNameBuilder.setView(view);
                        currentDialog = newNameBuilder.create();
                        currentDialog.show();
                    }
                });

                builder.setNeutralButton(R.string.duplicarLista, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Lista list = arrayListLists.get(eventPosition);
                        Lista newList = new Lista(getResources().getString(R.string.lista_duplicada), new ArrayList<Producto>(), new Date());
                        Producto product;
                        DB.addList(newList);
                        arrayListLists.add(newList);
                        DB.getAllProductsFromListID(list);
                        for (int i = 0; i < list.getListProducts().size(); i++)
                        {
                            product = list.getListProducts().get(i);
                            product.setPrice(0);
                            DB.addProduct(product, newList);
                        }

                        eventPosition = posInArray(arrayListLists,newList.getId());
                        Intent intent = new Intent(ActivityLists.this, ABMCompras.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("listID", arrayListLists.get(eventPosition).getId());
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });

                currentDialog = builder.create();
                currentDialog.show();

            }
        });

        recyclerView.setAdapter(adapter);
    }
}


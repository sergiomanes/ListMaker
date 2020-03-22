package com.application.sergiomanes.ListasDeCompras.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.sergiomanes.ListasDeCompras.R;
import com.application.sergiomanes.ListasDeCompras.database.DatabaseHelper;
import com.application.sergiomanes.ListasDeCompras.model.Lista;
import com.application.sergiomanes.ListasDeCompras.model.Producto;
import com.application.sergiomanes.ListasDeCompras.ui.adapter.DetailAdapter;

import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ABMCompras extends AppCompatActivity {

    EditText nameProduct,countProduct,priceProduct;
    TextView idProduct,total;
    RecyclerView recyclerView;
    Button addProductbtn,updateProductbtn,deleteProductbtn;
    Lista list;
    View menu;
    DatabaseHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.application.sergiomanes.ListasDeCompras.R.layout.activity_abml);

        nameProduct = findViewById(R.id.productNameEditText);
        countProduct = findViewById(R.id.countEditText);
        priceProduct = findViewById(R.id.priceEditText);
        addProductbtn = findViewById(R.id.addButton);
        updateProductbtn = findViewById(R.id.updateButton);
        deleteProductbtn = findViewById(R.id.deleteButton);
        idProduct = findViewById(R.id.idProductTextView);
        total = findViewById(R.id.totalTextView);
        recyclerView = findViewById(R.id.recyclerList);
        menu = findViewById(R.id.menu);

        DB = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) //Caso mostrar Lista con o sin contenido
        {
            list = DB.getList(bundle.getLong("listID"));
            DB.getAllProductsFromListID(list);
        }
        else // Caso crear Lista nueva
        {
            list = new Lista(getResources().getString(R.string.sin_nombre), new ArrayList<Producto>(), new Date());
            DB.addList(list);

            if (list.getId()==-1){
                Toast.makeText(getApplicationContext(),getResources().
                                                       getString(com.application.sergiomanes.ListasDeCompras.R.string.error_list_insertion)
                                                      ,Toast.LENGTH_SHORT).show();
                return;
            }
        }

        total.setText(String.format("%.2f", list.getSubtotal()));

        final DetailAdapter adapter = new DetailAdapter(list.getListProducts(), getResources(), new DetailAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int pos) {

                Producto producto = DB.getProduct(list.getListProducts().get(pos));
                idProduct.setText(String.valueOf(producto.getCode()));
                nameProduct.setText(String.valueOf(producto.getName()));
                countProduct.setText(String.valueOf(producto.getCount()));
                priceProduct.setText(String.valueOf(producto.getPrice()));
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);


        addProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductProcess(adapter);
            }
        });

        updateProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductProcess(adapter);
            }
        });

        deleteProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!idProduct.getText().toString().equals("")) && (checkFields()))
                {
                    long id = Long.valueOf(idProduct.getText().toString());

                    int posInArray = Producto.posInArray(list.getListProducts(),id);
                    Producto producto = list.getListProducts().get(posInArray);

                    DB.deleteProduct(id);
                    list.getListProducts().remove(posInArray);
                    adapter.notifyItemRemoved(posInArray);

                    list.setSubtotal(Double.parseDouble(total.getText().toString().replace(",", "."))-(producto.getPrice()*producto.getCount()));
                    total.setText(String.format("%.2f", list.getSubtotal()));
                    DB.updateList(list);
                    Toast.makeText(v.getContext(),getResources().getString(R.string.product_deleted),Toast.LENGTH_SHORT).show();

                    clearFields();
                }
                else
                {
                    Toast.makeText(v.getContext(),getResources().getString(R.string.error_empty_fields),Toast.LENGTH_SHORT).show();
                }
            }
        });

        priceProduct.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {

                        if (idProduct.getText().toString().equals(""))
                        {addProductProcess(adapter);}
                        else
                        {updateProductProcess(adapter);}

                    }

                return true;
            }

        });
    }

    public void addProductProcess (DetailAdapter adapter)
    {
        if (checkFields()) {
            Producto producto = new Producto();
            producto.setName(nameProduct.getText().toString());
            producto.setCount(Integer.valueOf(countProduct.getText().toString()));
            producto.setPrice(Double.valueOf(priceProduct.getText().toString()));

            long code = DB.addProduct(producto, list);

            if (code != -1) {
                producto.setCode(code);
                idProduct.setText(String.valueOf(code));
                list.getListProducts().add(producto);
                int posInArray = Producto.posInArray(list.getListProducts(), code);
                adapter.notifyItemInserted(posInArray);
                Toast.makeText(getApplicationContext(), getResources().getString(com.application.sergiomanes.ListasDeCompras.R.string.product_inserted), Toast.LENGTH_SHORT).show();
                list.setSubtotal(list.getSubtotal()+(producto.getPrice()*producto.getCount()));
                total.setText(String.format("%.2f", list.getSubtotal()));
                DB.updateList(list);
                clearFields();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(com.application.sergiomanes.ListasDeCompras.R.string.error_product_insertion), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(com.application.sergiomanes.ListasDeCompras.R.string.error_empty_fields),Toast.LENGTH_SHORT).show();
        }
    }

    public void updateProductProcess(DetailAdapter adapter)
    {
        if ((!idProduct.getText().toString().equals("")) && (checkFields())) {

            int posInArray = Producto.posInArray(list.getListProducts(),Long.valueOf(idProduct.getText().toString()));

            Producto oldProduct = list.getListProducts().get(posInArray);

            list.setSubtotal(list.getSubtotal()-(oldProduct.getPrice()*oldProduct.getCount()));
            double subTotalBefore = list.getSubtotal();
            total.setText(String.format("%.2f", list.getSubtotal()));

            Producto newProducto = new Producto();
            newProducto.setName(nameProduct.getText().toString());
            newProducto.setCount(Integer.valueOf(countProduct.getText().toString()));
            newProducto.setPrice(Double.valueOf(priceProduct.getText().toString()));
            newProducto.setCode(Long.valueOf(idProduct.getText().toString()));


            DB.updateProduct(newProducto);
            list.getListProducts().set(posInArray, newProducto);
            adapter.notifyItemChanged(posInArray);

            Toast.makeText(getApplicationContext(), getResources().getString(com.application.sergiomanes.ListasDeCompras.R.string.product_updated), Toast.LENGTH_SHORT).show();

            list.setSubtotal(subTotalBefore+(newProducto.getPrice()*newProducto.getCount()));
            total.setText(String.format("%.2f", list.getSubtotal()));
            DB.updateList(list);
            clearFields();
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(com.application.sergiomanes.ListasDeCompras.R.string.error_empty_fields),Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFields(){
        idProduct.setText("");
        nameProduct.setText("");
        nameProduct.requestFocus();
        countProduct.setText("");
        priceProduct.setText("");
    }

    public boolean checkFields(){
        boolean check = true;
        if ((nameProduct.getText().toString().equals("")) || (countProduct.getText().toString().equals("")))
        {
            check = false;
        }

        if (priceProduct.getText().toString().equals("")){
            priceProduct.setText("0.0");
        }

        return  check;
    }




}

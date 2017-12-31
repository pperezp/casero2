package casero.cl.casero2;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import casero.cl.casero2.bd.DAO;
import casero.cl.casero2.bd.model.Cliente;
import casero.cl.casero2.bd.model.K;


public class CrearFragment extends Fragment {
    private Button btnVolver;
    private Button btnCrear;
    private EditText txtNombre;
    private EditText txtDireccion;
    private Spinner spiSector;
    private TextView lblCantidad;
    private DAO d;

    public CrearFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crear, container, false);

        d = new DAO(getActivity());

        cargarComponentes(v);
        programarBotones();

        lblCantidad.setText("Clientes: " + d.getCantidadClientes());

        return v;
    }

    private void programarBotones() {
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent i = new Intent(CrearClienteActivity.this, MainActivity.class);
                CrearClienteActivity.this.startActivity(i);*/

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contenedor, new BuscarFragment()).commit();
            }
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Validaciones*/
                if(txtNombre.getText().toString().trim().equals("")){
                    Toast.makeText(
                            getActivity(),
                            "Ingrese el nombre del cliente",
                            Toast.LENGTH_SHORT).show();
                }else if(txtDireccion.getText().toString().trim().equals("")){
                    Toast.makeText(
                            getActivity(),
                            "Ingrese la dirección del cliente",
                            Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        DAO d = new DAO(getActivity());

                        Cliente c = new Cliente();
                        //Movimiento m = new Movimiento();

                        //m.saldo = Integer.parseInt(txtSaldo.getText().toString());

                        //if(m.saldo >= 0){
                        //    m.detalle = txtDetalle.getText().toString();
                        //    m.fecha = new Date();

                        c.nombre = txtNombre.getText().toString();
                        c.direccion = txtDireccion.getText().toString();
                        c.sector = spiSector.getSelectedItem().toString();
                        //c.deuda = m.saldo;
                        c.deuda = 0;

                        d.crearCliente(c);
                        //    m.cliente = d.crearCliente(c);

                        //    d.crearMovimiento(m);

                        //    txtDetalle.setText("");
                        txtNombre.setText("");
                        //    txtSaldo.setText("");
                        txtDireccion.setText("");
                        spiSector.setSelection(0);
                        txtNombre.requestFocus();
                        lblCantidad.setText("Clientes: " + d.getCantidadClientes());
                        Toast.makeText(getActivity(), "Cliente ["+c.nombre+"] creado!", Toast.LENGTH_SHORT).show();

                        K.nombreBusqueda = c.nombre;
                        /*Intent i = new Intent(CrearClienteActivity.this, MainActivity.class);
                        CrearClienteActivity.this.startActivity(i);*/

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.contenedor, new BuscarFragment()).commit();


                        //}else{
                        //  Toast.makeText(
                        //            CrearClienteActivity.this.getApplicationContext(),
                        //            "No puede ingresar saldos negativos",
                        //            Toast.LENGTH_SHORT).show();
                        //}
                    }catch (NumberFormatException ex){
                        Toast.makeText(
                                getActivity(),
                                "Ingrese sólo números en el saldo",
                                Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });
    }

    private void cargarComponentes(View v) {
        btnVolver = (Button) v.findViewById(R.id.btnVolVis);
        btnCrear = (Button) v.findViewById(R.id.btnCrear);
        //txtDetalle = (EditText) findViewById(R.id.txtDetalle);
        //txtDetalle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtNombre = (EditText) v.findViewById(R.id.txtNomCli);
        txtNombre.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        //txtSaldo = (EditText) findViewById(R.id.txtSaldo);
        txtDireccion = (EditText) v.findViewById(R.id.txtDire);
        txtDireccion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        spiSector = (Spinner) v.findViewById(R.id.spiSector);
        lblCantidad = (TextView) v.findViewById(R.id.lblCantidad);
    }
}

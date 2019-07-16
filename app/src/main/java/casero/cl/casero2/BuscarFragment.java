package casero.cl.casero2;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import casero.cl.casero2.adapter.ClienteAdapter;
import casero.cl.casero2.bd.DAO;
import casero.cl.casero2.bd.model.Cliente;
import casero.cl.casero2.bd.model.K;
import casero.cl.casero2.bd.model.Movimiento;
import casero.cl.casero2.bd.model.Util;

public class BuscarFragment extends Fragment {

    private DAO d;

    private EditText txtNombreBuscar;
    private ListView lvClientes;
    private int abono;
    private int montoDevolucion;
    private String detalleDevolucion;
    private String detalleCondonacion;
    private TextView lblResultado;

    /*FECHA!*/
    private DatePickerDialog.OnDateSetListener fechaAbonoListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int anio, int mes, int dia) {
            // el mes comienza de 0
            //Toast.makeText(MainActivity.this.getApplicationContext(),"["+abono+"]["+anio+" - "+(mes+1)+" - "+dia+"]", Toast.LENGTH_SHORT).show();
            // esto se llama cuando el usuario presiona OK en la fecha del abono

            DAO d = new DAO(getActivity());

            Movimiento m = new Movimiento();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            try {
                m.fecha = f.parse(anio+"-"+(mes+1)+"-"+dia);
            }catch (ParseException ex){
            }

            m.cliente = (int) K.id;
            m.detalle = "[Abono]: $"+abono;
            int saldo = d.getDeuda(m.cliente);

            saldo = saldo - abono;
            m.saldo = saldo;

            d.abonar(m, abono);


            /*cargo la lista de clientes de nuevo*/
            String filtro = txtNombreBuscar.getText().toString();
            K.nombreBusqueda = txtNombreBuscar.getText().toString();

            List<Cliente> clientes = d.getClientes(filtro);

            if(!clientes.isEmpty()){
                lvClientes.setAdapter(new ClienteAdapter(getActivity(), clientes));
            }else{
                lvClientes.setAdapter(new ClienteAdapter(getActivity(), new ArrayList<Cliente>()));
            }
            /*cargo la lista de clientes de nuevo*/

        }
    };
    /*FECHA!*/


    /*LISTENER FECHA DEVOLUCIÓN*/
    private DatePickerDialog.OnDateSetListener fechaDevolucionListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int anio, int mes, int dia) {
            // el mes comienza de 0
            //Toast.makeText(MainActivity.this.getApplicationContext(),"["+abono+"]["+anio+" - "+(mes+1)+" - "+dia+"]", Toast.LENGTH_SHORT).show();
            // esto se llama cuando el usuario presiona OK en la fecha del abono

            DAO d = new DAO(getActivity());

            Movimiento m = new Movimiento();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            try {
                m.fecha = f.parse(anio+"-"+(mes+1)+"-"+dia);
            }catch (ParseException ex){
            }

            m.cliente = (int)K.id;
            m.detalle = "[Devolución]: $"+montoDevolucion+"\n[Detalle]: "+detalleDevolucion;
            int saldo = d.getDeuda(m.cliente);

            saldo = saldo - montoDevolucion;
            m.saldo = saldo;

            d.devolver(m, montoDevolucion);


            /*cargo la lista de clientes de nuevo*/
            String filtro = txtNombreBuscar.getText().toString();
            K.nombreBusqueda = txtNombreBuscar.getText().toString();

            List<Cliente> clientes = d.getClientes(filtro);

            if(!clientes.isEmpty()){
                lvClientes.setAdapter(new ClienteAdapter(getActivity(), clientes));
            }else{
                lvClientes.setAdapter(new ClienteAdapter(getActivity(), new ArrayList<Cliente>()));
            }
            /*cargo la lista de clientes de nuevo*/

        }
    };
    /*LISTENER FECHA DEVOLUCIÓN*/


    /*LISTENER FECHA CONDONACIÓN*/
    private DatePickerDialog.OnDateSetListener fechaCondonacion = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int anio, int mes, int dia) {
            // el mes comienza de 0
            //Toast.makeText(MainActivity.this.getApplicationContext(),"["+abono+"]["+anio+" - "+(mes+1)+" - "+dia+"]", Toast.LENGTH_SHORT).show();
            // esto se llama cuando el usuario presiona OK en la fecha del abono

            DAO d = new DAO(getActivity());

            Movimiento m = new Movimiento();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            try {
                m.fecha = f.parse(anio+"-"+(mes+1)+"-"+dia);
            }catch (ParseException ex){
            }

            m.cliente = (int)K.id;
            m.detalle = "[CONDONACIÓN DEUDA]\n[MOTIVO]: "+detalleCondonacion;
            int saldo = d.getDeuda(m.cliente);

            m.saldo = 0;

            d.condonar(m, saldo);


            /*cargo la lista de clientes de nuevo*/
            String filtro = txtNombreBuscar.getText().toString();
            K.nombreBusqueda = txtNombreBuscar.getText().toString();

            List<Cliente> clientes = d.getClientes(filtro);

            if(!clientes.isEmpty()){
                lvClientes.setAdapter(new ClienteAdapter(getActivity(), clientes));
            }else{
                lvClientes.setAdapter(new ClienteAdapter(getActivity(), new ArrayList<Cliente>()));
            }
            /*cargo la lista de clientes de nuevo*/

        }
    };
    /*LISTENER FECHA CONDONACIÓN*/

    public BuscarFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_buscar, container, false);

        cargarComponentes(v);
        programacionBotones();
        onClickListaClientes();

        d = new DAO(getActivity());

        if(K.nombreBusqueda != null){
            txtNombreBuscar.setText(K.nombreBusqueda);
            txtNombreBuscar.setSelection(K.nombreBusqueda.length(), K.nombreBusqueda.length());
        }

        return v;
    }

    private void onClickListaClientes() {
        lvClientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                K.id = id;

                //Toast.makeText(MainActivity.this.getApplicationContext(), "ID: "+id, Toast.LENGTH_SHORT).show();
                CharSequence opciones[] = new CharSequence[] {"Abonar", "Mantención","Devolución", "CONDONAR DEUDA","Ver dirección","Cambiar dirección","Ver detalles"};



                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Escoje una opción:");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this.getApplicationContext(), "OP: "+which, Toast.LENGTH_SHORT).show();

                        switch (which){
                            case 0: // abonar
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Abonar");

// Set up the input
                                final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                                input.setHint("Monto a abonar:");
                                input.requestFocus();
                                builder.setView(input);

// Set up the buttons
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strAbono = input.getText().toString();
                                        try {
                                            abono = Integer.parseInt(strAbono);

                                            /*FECHA!*/
                                            //showDialog(999);
                                            /*FECHA!*/
                                        }catch (NumberFormatException ex){
                                            Toast.makeText(getActivity().getApplicationContext(),"Ingrese sólo números", Toast.LENGTH_SHORT).show();

                                        }
                                        //
                                    }
                                });
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                                break;
                            case 1:// Mantención
                                K.nombreBusqueda = txtNombreBuscar.getText().toString();
                                //Intent i = new Intent(getActivity(), VentaActivity.class);
                                //MainActivity.this.startActivity(i);

                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.contenedor, new VentaFragment()).commit();

                                break;

                            case 2: // Devolución
                                int saldoActual = d.getDeuda((int)K.id);

                                AlertDialog.Builder bui = new AlertDialog.Builder(getActivity());
                                bui.setTitle("Devolución [Saldo Actual: $"+saldoActual+"]");

                                final AlertDialog.Builder bui2 = new AlertDialog.Builder(getActivity());
                                bui2.setTitle("Devolución [Saldo Actual: $"+saldoActual+"]");

                                // Set up the input
                                final EditText inpDetalle = new EditText(getActivity());
                                final EditText inpMonto = new EditText(getActivity());

                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                inpMonto.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                                inpMonto.setHint("Monto de devolución:");
                                inpMonto.requestFocus();
                                bui.setView(inpMonto);


                                inpDetalle.setRawInputType(InputType.TYPE_CLASS_TEXT);
                                inpDetalle.setHint("Detalle de devolución:");
                                inpDetalle.requestFocus();
                                bui2.setView(inpDetalle);



                                // Set up the buttons

                                bui.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bui2.show();
                                    }
                                });
                                bui.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                bui2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strDevolucion = inpMonto.getText().toString();
                                        detalleDevolucion = inpDetalle.getText().toString();
                                        try {
                                            montoDevolucion = Integer.parseInt(strDevolucion);

                                            /*FECHA!*/
                                            //showDialog(1);
                                            /*FECHA!*/
                                        }catch (NumberFormatException ex){
                                            Toast.makeText(getActivity().getApplicationContext(),"Ingrese sólo números", Toast.LENGTH_SHORT).show();

                                        }
                                        //
                                    }
                                });
                                bui2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                bui.show();

                                break;

                            case 3: // CONDONAR DEUDA
                                saldoActual = d.getDeuda((int)K.id);

                                bui = new AlertDialog.Builder(getActivity());
                                bui.setTitle("Condonar deuda [Saldo Actual: $"+saldoActual+"]");

                                // Set up the input

                                final EditText detCond = new EditText(getActivity());

                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                detCond.setRawInputType(InputType.TYPE_CLASS_TEXT);
                                detCond.setHint("Motivo condonación:");
                                detCond.requestFocus();
                                bui.setView(detCond);

                                // Set up the buttons

                                bui.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        detalleCondonacion = detCond.getText().toString();
                                        /*FECHA!*/
                                        //showDialog(2);
                                        /*FECHA!*/
                                    }
                                });
                                bui.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                bui.show();
                                break;

                            case 4:// ver dirección
                                Cliente c = d.getCliente(K.id);

                                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                b.setTitle("Dirección de " + c.nombre);
                                b.setMessage(c.direccion+", "+c.sector);

                                b.setPositiveButton("Ok", null);

                                b.create().show();
                                break;

                            case 5:// cambiar dirección

                                Cliente cli = d.getCliente(id);

                                builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Cambiar dirección");

                                final EditText in = new EditText(getActivity());
                                in.setRawInputType(InputType.TYPE_CLASS_TEXT);
                                in.setHint("Dirección nueva:");
                                in.setText(cli.direccion);
                                in.requestFocus();
                                builder.setView(in);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String dirNueva = in.getText().toString();

                                        d.actualizarDireccion(K.id, dirNueva);

                                        Toast.makeText(getActivity().getApplicationContext(),"Dirección cambiada con éxito", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                                break;

                            case 6: // ver detalles
                                /*Intent i2 = new Intent(getActivity(), VisualizarClienteActivity.class);
                                MainActivity.this.startActivity(i2);*/

                                ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.contenedor, new VisualizarClienteFragment()).commit();

                                break;
                        }
                    }
                });
                builder.show();

                return false;
            }
        });

    }

    /*FECHA!*/
    /*@Override
    protected Dialog onCreateDialog(int id) {

        // el mes comienza de 0
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        if (id == 999) {
            // formulario de fecha de abono
            return new DatePickerDialog(this, fechaAbonoListener, mYear, mMonth, mDay);
        }else if(id == 1){
            // formulario de fecha de devolución
            return new DatePickerDialog(this, fechaDevolucionListener, mYear, mMonth, mDay);
        }else if(id == 2){
            // formulario de fecha de condonacion de deuda
            return new DatePickerDialog(this, fechaCondonacion, mYear, mMonth, mDay);
        }
        return null;
    }*/
    /*FECHA!*/


    private void programacionBotones() {
        txtNombreBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (txtNombreBuscar.getText().toString().equals("")) {
                        lblResultado.setText("0 resultados");
                        lvClientes.setAdapter(new ClienteAdapter(getActivity(), new ArrayList<Cliente>()));
                    } else if (txtNombreBuscar.getText().toString().charAt(0) == '@') {
                        // Quiere ingresar un comando
                        String com = txtNombreBuscar.getText().toString();

                        if(com.charAt(1) == 'h'){ //comando de ayuda
                            String ayuda = "";

                            ayuda += "@m numero: Ver el top de morosos según el número\n";
                            ayuda += "@c comando: Ejecutar comandos\n";
                            ayuda += "Comandos posibles:\n";
                            ayuda += "\tpd: Promedio total de deudas\n";
                            ayuda += "\tcps: Cantidad de clientes por sector\n";

                            Util.mensaje(getActivity(), "Ayuda", ayuda);
                        }else if (com.charAt(1) == 'm') {// Comando morosos
                            String strLimite = com.split(" ")[1];

                            try {
                                int limite = Integer.parseInt(strLimite);

                                DAO d = new DAO(getActivity());
                                List<Cliente> morosos = d.getTopMorosos(limite);

                                lblResultado.setText(morosos.size()+(morosos.size() == 1?" resultado":" resultados"));

                                lvClientes.setAdapter(new ClienteAdapter(getActivity(), morosos));

                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "Top " + limite + " morosos",
                                        Toast.LENGTH_SHORT).show();
                            } catch (NumberFormatException ex) {

                            }
                        }else if(com.charAt(1) == 'c'){ // comando generico
                            String parametro = com.split(" ")[1];

                            DAO d = new DAO(getActivity());

                            switch (parametro){
                                case "pd":
                                    // promedio deuda

                                    Util.mensaje(getActivity(), "Promedio deudas", "$ "+Util.getNumeroConPuntos(d.getPromedioDeuda()));
                                    break;

                                case "cps":
                                    // Cantidad de clientes por sector
                                    String resumen = "";

                                    resumen += "Santa Cruz: "+d.getCantClientes("Santa Cruz")+"\n";
                                    resumen += "Los Boldos: "+d.getCantClientes("Los Boldos")+"\n";
                                    resumen += "Barreales: "+d.getCantClientes("Barreales")+"\n";
                                    resumen += "Palmilla: "+d.getCantClientes("Palmilla")+"\n";
                                    resumen += "Quinahue: "+d.getCantClientes("Quinahue")+"\n";
                                    resumen += "Chépica: "+d.getCantClientes("Chépica");

                                    Util.mensaje(getActivity(), "Clientes por sector", resumen);

                                    break;
                            }
                        }
                    } else if(txtNombreBuscar.getText().toString().toLowerCase().contains("#bue")){
                        String com = txtNombreBuscar.getText().toString().toLowerCase();
                        String strLimite = com.split(" ")[1];

                        try {
                            int limite = Integer.parseInt(strLimite);

                            DAO d = new DAO(getActivity());
                            List<Cliente> clientesBuenos = d.getTopClientesBuenos(limite);

                            lblResultado.setText(clientesBuenos.size()+(clientesBuenos.size() == 1?" resultado":" resultados"));

                            lvClientes.setAdapter(new ClienteAdapter(getActivity(), clientesBuenos));

                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Top " + limite + " clientes buenos",
                                    Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException ex) {

                        }
                    }else if(txtNombreBuscar.getText().toString().toLowerCase().contains("#mor")){
                        String com = txtNombreBuscar.getText().toString().toLowerCase();
                        String strLimite = com.split(" ")[1];

                        try {
                            int limite = Integer.parseInt(strLimite);

                            DAO d = new DAO(getActivity());
                            List<Cliente> cMorosos = d.getTopMorosos(limite);

                            lblResultado.setText(cMorosos.size()+(cMorosos.size() == 1?" resultado":" resultados"));

                            lvClientes.setAdapter(new ClienteAdapter(getActivity(), cMorosos));

                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Top " + limite + " clientes morosos",
                                    Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException ex) {

                        }
                    }else{
                        String filtro = txtNombreBuscar.getText().toString();

                        DAO d = new DAO(getActivity());
                        List<Cliente> clientes = d.getClientes(filtro);

                        lblResultado.setText(clientes.size()+(clientes.size() == 1?" resultado":" resultados"));

                        if (!clientes.isEmpty()) {
                            lvClientes.setAdapter(new ClienteAdapter(getActivity(), clientes));
                        } else {
                            lvClientes.setAdapter(new ClienteAdapter(getActivity(), new ArrayList<Cliente>()));
                        }
                    }
                }catch(StringIndexOutOfBoundsException ex){

                }catch(ArrayIndexOutOfBoundsException ex){

                }
            }
        });


    }

    private void cargarComponentes(View v) {
        txtNombreBuscar = (EditText) v.findViewById(R.id.txtNombreBuscar);
        txtNombreBuscar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        lvClientes = (ListView) v.findViewById(R.id.lvClientes);
        lblResultado = (TextView) v.findViewById(R.id.lblResultados);
    }

    /*
    * if(id == R.id.action_verDeudaTotal){
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle("Deuda total");

        int deuda = d.getDeudaTotal();



        b.setMessage("$ "+ Util.getNumeroConPuntos(deuda));

        b.setPositiveButton("Ok", null);

        b.create().show();
    * */
}

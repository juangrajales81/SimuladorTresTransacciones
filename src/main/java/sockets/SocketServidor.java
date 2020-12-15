package sockets;

import com.google.gson.Gson;
import plain.Cliente;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class SocketServidor implements Runnable {
    private Socket connection;
    private String TimeStamp;
    private int ID;

    /**
     * enum que contiene los tipos de transacciones que puede
     realizar el servidor.
     */
    public enum Transaciones {
        CREACION_CLIENTE,
        CONSULTAR_SALDO_CLIENTE,
        CONSIGNAR_CUENTA_CLIENTE,
        RETIRAR_CUENTA_CLIENTE
    }

    static String url = "jdbc:mysql://localhost:3306/banco?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static String usuario = "banco_db";
    static String contrasena = "BnacoDB";


    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        int port = 19999;
        int count = 0;
        try{
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("Socket Servidor inicializado");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new SocketServidor(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        catch (Exception e) {}
    }
    SocketServidor(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    /**
     *
     */
    public void run() {
        try {
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(is);
            int character;
            StringBuffer process = new StringBuffer();
            while((character = isr.read()) != 13) {
                process.append((char)character);
            }

            Gson g = new Gson();
            Cliente objCliente = g.fromJson(process.toString(), Cliente.class);

            int idCliente = buscarCliente(objCliente.getDocumento());
            float saldoCliente = 0.0f;
            float valorconsignado = 0.0f;
            float valorRetirado = 0.0f;
            String movimiento = "";

            switch(objCliente.getTipoTransaccion()) {
                case CREACION_CLIENTE:
                    if(idCliente == 0){
                        movimiento = "Registro";
                        insertarUsuarioBaseDeDatos(objCliente);
                        idCliente = buscarCliente(objCliente.getDocumento());
                        try {
                            Thread.sleep(2000);
                        }
                        catch (Exception e){}
                        insertarSaldoInicial(idCliente,objCliente);
                    }else{
                        movimiento = "Registro (el usuario ya existe)";
                    }
                    saldoCliente = consultarSaldoCliente(idCliente);
                    break;
                case CONSULTAR_SALDO_CLIENTE:
                    movimiento = "Consulta";
                    saldoCliente = consultarSaldoCliente(idCliente);
                    break;
                case CONSIGNAR_CUENTA_CLIENTE:
                    movimiento = "Consignación";
                    valorconsignado = consignarEnCuentaCliente(idCliente, objCliente);
                    saldoCliente = consultarSaldoCliente(idCliente);
                    break;
                case RETIRAR_CUENTA_CLIENTE:
                    movimiento = "Retiro";
                    valorRetirado = retirarDeCuentaCliente(idCliente, objCliente);
                    saldoCliente = consultarSaldoCliente(idCliente);
                    break;
                default:
                    break;
            }


            //System.out.println(process);
            //espreamos 3 segundos
            try {
                Thread.sleep(2000);
            }
            catch (Exception e){}
            TimeStamp = new java.util.Date().toString();
            //String returnCode = "MultipleSocketServer repsonded at "+ TimeStamp + (char) 13;

            String returnCode = "idCliente: "+ idCliente + " - Movimiento: " +movimiento+ " - Saldo cliente: " + saldoCliente +" - Valor Consignado: " +valorconsignado+ " Valor retirado: "+valorRetirado+ (char) 13;

            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
            osw.write(returnCode);
            osw.flush();

        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                connection.close();
            }
            catch (IOException e){}
        }
    }


    /**
     * Este método inserta un nuevo usuario en la base de datos.
     */
    private static void insertarUsuarioBaseDeDatos(Cliente objCliente) {



        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            String sql = "INSERT INTO clientes(documento, nombres, apellidos, ciudad, telefonos, email)" +
                    "values(\'" +
                    objCliente.getDocumento() + "\',\'" +
                    objCliente.getNombres() + "\',\'" +
                    objCliente.getApellidos() + "\',\'" +
                    objCliente.getCiudad() + "\',\'" +
                    objCliente.getTelefonos() + "\',\'" +
                    objCliente.getEmail() +
                    "\')";
            int setResultados = sentencia.executeUpdate(sql);
            sentencia.close();
            conexion.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * Se crea el primer registro para el cliente en la tabla de saldos
     * @param idCliente
     * @param objCliente
     */
    private static void insertarSaldoInicial(int idCliente, Cliente objCliente) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = sdf.format(new Date());

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            String sql = "INSERT INTO saldos(FechaCreacion, NroCuenta, IdCliente, idCiudad, SaldoActual)" +
                    "values(\'" +
                    date + "\',\'" +
                    objCliente.getNumeroCuenta() + "\'," +
                    idCliente + ",\'" +
                    objCliente.getCiudad() + "\',\'" +
                    objCliente.getValorMovimiento() +
                    "\')";
            int setResultados = sentencia.executeUpdate(sql);
            sentencia.close();
            conexion.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Método para buscar si un cliente ya existe
     * @param documento El documento de identidad del cliente
     * @return EL id del cliente, devuelve cero si no se encuentra
     * @throws SQLException
     */

    private static int buscarCliente(String documento) throws SQLException {
        int idCliente = 0;

        Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
        Statement sentencia = conexion.createStatement();
        String sql2 = "SELECT idCliente FROM clientes WHERE documento = \""+ documento + "\"";
        ResultSet resultado = sentencia.executeQuery(sql2);
        while (resultado.next()) {
            idCliente = resultado.getInt(1);
        }
        sentencia.close();
        conexion.close();

        return idCliente;
    }


    /**
     * Consulta el saldo actual del cliente
     * @param idCliente
     * @return
     */
    private static float consultarSaldoCliente(int idCliente){

        float saldo = 0.00f;

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            String sql = "SELECT SaldoActual FROM saldos WHERE IdCliente = " + idCliente;
            ResultSet resultado = sentencia.executeQuery(sql);
            while (resultado.next()) {
                saldo = resultado.getFloat(1);
            }
            sentencia.close();
            conexion.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return saldo;
    }

    /**
     * Se actualiza el valor del saldo del cliente, aumentando por
     * el valor actual mas el nuevo valor que llega como movimiento del cliente
     * @param idCliente
     * @param objCliente
     * @return
     */
    private static float consignarEnCuentaCliente(int idCliente, Cliente objCliente){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = sdf.format(new Date());

        float valorconsignado = objCliente.getValorMovimiento();

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);

            String sql = "UPDATE saldos SET SaldoActual = SaldoActual + ? , UltimaActualizacion = ? WHERE IdCliente = ?";
            PreparedStatement updateString = conexion.prepareStatement(sql);
            updateString.setFloat(1, valorconsignado);
            updateString.setString(2,date);
            updateString.setInt(3, idCliente);

            updateString.executeUpdate();

            conexion.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return valorconsignado;

    }

    /**
     * Se deduce del valor actual el valor indicado en objeto cliente
      * @param idCliente
     * @param objCliente
     * @return
     */
    private static float retirarDeCuentaCliente(int idCliente, Cliente objCliente){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = sdf.format(new Date());

        float valorRetirado = objCliente.getValorMovimiento();

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);

            String sql = "UPDATE saldos SET SaldoActual = SaldoActual - ? , UltimaActualizacion = ? WHERE IdCliente = ?";
            PreparedStatement updateString = conexion.prepareStatement(sql);
            updateString.setFloat(1, valorRetirado);
            updateString.setString(2,date);
            updateString.setInt(3, idCliente);

            updateString.executeUpdate();

            conexion.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return valorRetirado;
    }
}
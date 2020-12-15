import plain.Cliente;
import sockets.*;

import java.io.IOException;

/*
* Clase main para probar los sockets de cliente y servidor
*
* */
public class Main {


    // NOTA: Ejecutar primero la clase SocketServidor
    public static void main(String[] args) throws IOException {

       SocketServidor.Transaciones tipoTransaccion = SocketServidor.Transaciones.CREACION_CLIENTE;


       // Creamos un objeto cliente para efectos de prueba

        Cliente cliente = new Cliente();
        cliente.setDocumento("10203040");
        cliente.setNombres("Jonh");
        cliente.setApellidos("Doe");
        cliente.setCiudad("1");
        cliente.setTelefonos("50505846");
        cliente.setEmail("mail@sample.com");
        cliente.setNumeroCuenta("104-965-863-14");
        cliente.setTipoTransaccion(tipoTransaccion);
        cliente.setValorMovimiento(500000.00f);

        // Se instancia un nuevo socket de cliente, al pasarle los parámetros
        // de host y puerto hace que el socket servidor escuche
        SocketCliente socketCliente = new SocketCliente("localhost", 19999);

        // pasamos el objeto cliente y la clase socket cliente se encarará de
        // transformarlo en un string
        // Para este caso intenta registrar el cliente si ya existe solo muestra su id y su saldo
        socketCliente.iniciarTransaccion(cliente);

        //Cambiamos los valores del tipo de transacción consulta
        tipoTransaccion = SocketServidor.Transaciones.CONSULTAR_SALDO_CLIENTE;
        cliente.setTipoTransaccion(tipoTransaccion);

        // Se instancia de nuevo el socket del cliente para pasar de nuevo el
        // objeto cliente con los valores que se han modificado
        socketCliente = new SocketCliente("localhost", 19999);
        socketCliente.iniciarTransaccion(cliente);


        // Consignación
        tipoTransaccion = SocketServidor.Transaciones.CONSIGNAR_CUENTA_CLIENTE;
        cliente.setTipoTransaccion(tipoTransaccion);
        cliente.setValorMovimiento(130000f);
        socketCliente = new SocketCliente("localhost", 19999);
        socketCliente.iniciarTransaccion(cliente);

        tipoTransaccion = SocketServidor.Transaciones.CONSULTAR_SALDO_CLIENTE;
        cliente.setTipoTransaccion(tipoTransaccion);
        socketCliente = new SocketCliente("localhost", 19999);
        socketCliente.iniciarTransaccion(cliente);


        // Retiro
        tipoTransaccion = SocketServidor.Transaciones.RETIRAR_CUENTA_CLIENTE;
        cliente.setTipoTransaccion(tipoTransaccion);
        cliente.setValorMovimiento(60000f);
        socketCliente = new SocketCliente("localhost", 19999);
        socketCliente.iniciarTransaccion(cliente);

        tipoTransaccion = SocketServidor.Transaciones.CONSULTAR_SALDO_CLIENTE;
        cliente.setTipoTransaccion(tipoTransaccion);
        socketCliente = new SocketCliente("localhost", 19999);
        socketCliente.iniciarTransaccion(cliente);

    }
}

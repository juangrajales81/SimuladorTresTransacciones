package sockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import plain.Cliente;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * La clase SocketCliente es un ejemplo sencillo de un socket cliente TCP/IP.
 *
 */
public class SocketCliente {

    static String host;
    static int puerto;
    static Socket connection;
    static InetAddress direccionIp;

    private String TimeStamp;

    /**
    * Constructor
    * */
    public SocketCliente(String host, int puerto) throws IOException {
        this.host = host;
        this.puerto = puerto;

        /** Obtiene la dirección IP del host indicado */
        direccionIp = InetAddress.getByName(host);

        /** Crea un nuevo socket de acuerdo la dirección y puerto */
        this.connection = new Socket(direccionIp, puerto);
    }

    public void iniciarTransaccion(Cliente cliente) {

        StringBuffer instr = new StringBuffer();

        String TimeStamp = new java.util.Date().toString();;
        System.out.println("\nSocket cliente inicializado a las: "+TimeStamp);

        try {

            /** Inicializa un objeto BufferedOutputStream */
            BufferedOutputStream bos = new BufferedOutputStream(connection.
                    getOutputStream());

            /**
             *  Instancia un objeto OutputStreamWriter
             */
            OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);


            /** El objeto ObjectMapper nos ayuda a convertir el objeto cliente
             *  en un JSON como una cadena de caracteres
             * */

            String json = "";
            ObjectMapper mapper = new ObjectMapper();
            try {
                json = mapper.writeValueAsString(cliente);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String process = json +  (char) 13;

            /** Se escribe en la  conexión del socket */
            osw.write(process);
            osw.flush();

            /** Se instancia un objeto BufferedInputStream para leer
             * el flujo de entrada
             */
            BufferedInputStream bis = new BufferedInputStream(connection.
                    getInputStream());

            /**
             * Se instancia un objeto InputStreamReader
             */
            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

            /**Lee el flujo de entrada y lo agrega al objeto StringBuffer */
            int c;
            while ( (c = isr.read()) != 13)
                instr.append( (char) c);

            /** Cierra la conexión del socket. */

            connection.close();


            System.out.println(instr);
        }
        catch (IOException f) {
            System.out.println("Excepción: " + f);
        }
        catch (Exception g) {
            System.out.println("Excepción: " + g);
        }
    }

}

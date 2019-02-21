/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Lib;

import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.ProtocoloComunicacionDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.PushDto;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author williamrodriguez
 */
public class AnalisisDatosSocket {

    Gson gson;
    Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroidSenInt;
    String tituloProyecto, puertoSocketUsar;
    HiloInfinito webSocketServer;

    public AnalisisDatosSocket(String titulo, String puerto) {
        gson = new Gson();

        this.listaDispositivosAndroidSenInt = new HashMap<>();
        this.tituloProyecto = titulo;
        this.puertoSocketUsar = puerto;
    }

    AnalisisDatosSocket() {

    }

    public void start() {
        webSocketServer = new HiloInfinito(listaDispositivosAndroidSenInt);
        webSocketServer.run();
    }

    public void stop() {
        try {
            webSocketServer.finalize();
        } catch (Exception e) {
        } catch (Throwable ex) {
            Logger.getLogger(AnalisisDatosSocket.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("No se pudo detener el Socket, por favor comuniquese con el administrador del servidor para que de un 'KILL al apache' y pueda reiniciar el servidor.");
        }

    }

    public ProtocoloComunicacionDto procesarDatos(String proceso, String datosComprimidos, DefaultProcesos defaultProcesos, ProtocoloComunicacionDto respuestaPorDefecto) {
        String procces = proceso;
        String dataCompress = datosComprimidos;

        return respuestaPorDefecto;
    }

    /*
    
            enviar datos a todos los dispositivos conectados
    
     */
    public List<String> enviarNotificacionesTodosDispositivosConectados(PushDto pushDto) {
        List<String> list = new ArrayList<>();
        SendNotification sendNotification = new SendNotification(listaDispositivosAndroidSenInt, tituloProyecto);
        for (Map.Entry<String, Map<Integer, PrintWriter>> senint : listaDispositivosAndroidSenInt.entrySet()) {
            Map.Entry<Integer, PrintWriter> next = senint.getValue().entrySet().iterator().next();
            PrintWriter valueForPrint = next.getValue();
            Integer key = next.getKey();
            Class<? extends Map.Entry> aClass = next.getClass();

            //PrintWriter temporalPrintWriter = new PrintWriter(, true);
            sendNotification.sendNotification(valueForPrint, pushDto, next.getKey());
            list.add(Integer.toString(next.getKey()));
        }
        return list;
    }

    public boolean enviarNotificacionDispositivo(String key, PushDto pushDto) {
        Integer keyInt = Integer.parseInt(key);
        SendNotification sendNotification = new SendNotification(listaDispositivosAndroidSenInt, tituloProyecto);
        for (Map.Entry<String, Map<Integer, PrintWriter>> senint : listaDispositivosAndroidSenInt.entrySet()) {
            Map.Entry<Integer, PrintWriter> next = senint.getValue().entrySet().iterator().next();
            
            String identificador = senint.getKey(); //identificador
            PrintWriter valueForPrint = next.getValue();
            Integer key2 = next.getKey(); //llave
            
            if (key2.intValue() == keyInt.intValue()) {
                String toString = valueForPrint.toString();
                sendNotification.sendNotification(valueForPrint, pushDto, next.getKey());
                return true;
            }
        }
        
        return false;
    }

    /*
    
                ver toda la lista de dispositivos conectados
    
     */
    public Map<String, Map<Integer, PrintWriter>> getListaDispositivosAndroidSenInt() {
        return listaDispositivosAndroidSenInt;
    }

    public List<String> getListaStringKeys() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, PrintWriter>> senint : listaDispositivosAndroidSenInt.entrySet()) {
            Map.Entry<Integer, PrintWriter> next = senint.getValue().entrySet().iterator().next();
            list.add(Integer.toString(next.getKey()));
        }
        return list;
    }

    /*
    
                    Hilo para Socket
    
     */
    private class HiloInfinito implements Runnable {

        Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroidSenInt;

        public HiloInfinito(Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroidSenInt) {
            this.listaDispositivosAndroidSenInt = listaDispositivosAndroidSenInt;
        }

        @Override
        public void run() {
            try {
                int puertoComunicacionSocket = new Integer(puertoSocketUsar);
                if (!isLocalPortInUse(puertoComunicacionSocket)) {
                    System.out.println("Servidor Socket:");
                    try (ServerSocket listener = new ServerSocket(puertoComunicacionSocket)) {
                        while (true) {
                            new SocketServer(listener.accept(), this.listaDispositivosAndroidSenInt, tituloProyecto).start();
                        }
                    }
                } else {
                    System.err.println("El puerto " + Integer.toString(puertoComunicacionSocket) + " ya se encuentra en uso.");
                }
            } catch (Exception exception) {
                Exception(exception);
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        public boolean isLocalPortInUse(Integer port) {
            try {
                new ServerSocket(port).close();
                return false;
            } catch (IOException e) {
                return true;
            }
        }

        public String Exception(Exception ex) {
            StackTraceElement[] elementosRastreo = ex.getStackTrace();
            for (StackTraceElement elementoActual : elementosRastreo) {
                if (elementoActual.getClassName().indexOf("com.sun.xml.internal.ws.transport.http.client.HttpClientTransport") != -1) {
                    if (ex.getCause() != null) {
                        Throwable cause = ex.getCause();
                        if (cause != null && cause.getMessage() != null && cause.getMessage().equals("Connection refused: connect")) {
                            return "Servidor no Encontrado: Enviar correo helpdesk@fcv.org e informar del incidente.";
                        }
                    }
                }
            }
            ex.printStackTrace();
            return "Error desconocido: Enviar correo helpdesk@fcv.org e informar del incidente.";
        }
    }
}

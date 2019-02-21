/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Lib;


import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Diccionarios.DiccionarioErrores;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Diccionarios.DiccionarioProcesos;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.ProtocoloComunicacionDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.PushDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.util.Util;
import com.google.gson.Gson;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author williamrodriguez
 */
public class SendNotification {

    Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroid;
    PushDto pushDto;
    Integer key;
    String tituloProyecto;

    public void setListaDispositivosAndroid(Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroid) {
        this.listaDispositivosAndroid = listaDispositivosAndroid;
    }
    
    public SendNotification(Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroid, String tituloProyecto) {
        this.listaDispositivosAndroid = listaDispositivosAndroid;
        this.tituloProyecto = tituloProyecto;
    }
      
    public void sendNotification(
            PrintWriter Notificacion, 
            PushDto pushDto,  
            Integer key) {
        ProtocoloComunicacionDto protocoloComunicacionDto = new ProtocoloComunicacionDto();
        protocoloComunicacionDto.setEncabezado(tituloProyecto);
        protocoloComunicacionDto.setProceso(DiccionarioProcesos.procesoSocketPush);
        protocoloComunicacionDto.setMsgError(DiccionarioErrores.msgErrorDefault);
        protocoloComunicacionDto.setDatosComprimidos(new Gson().toJson(pushDto));
                
        String mensajeEnviar = new Gson().toJson(protocoloComunicacionDto);
        mensajeEnviar = new Util().encodeUrlBase64(mensajeEnviar);   
        Notificacion.println(mensajeEnviar);
    }

    public File crearArchivoTemporal(String nombreArchivo){
        try {
            File tempFile = new File(Integer.toString(key)+".tmp");
            return tempFile;
        } catch (Exception e) {
            return null;
        }
    }
    
    
}

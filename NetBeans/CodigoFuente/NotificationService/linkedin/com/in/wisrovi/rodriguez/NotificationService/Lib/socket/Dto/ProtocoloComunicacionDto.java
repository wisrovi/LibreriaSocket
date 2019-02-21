/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto;

/**
 *
 * @author williamrodriguez
 */
public class ProtocoloComunicacionDto {
    String encabezado;
    String proceso;
    String datosComprimidos;
    String msgError;

    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getDatosComprimidos() {
        return datosComprimidos;
    }

    public void setDatosComprimidos(String datosComprimidos) {
        this.datosComprimidos = datosComprimidos;
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }
    
    
}

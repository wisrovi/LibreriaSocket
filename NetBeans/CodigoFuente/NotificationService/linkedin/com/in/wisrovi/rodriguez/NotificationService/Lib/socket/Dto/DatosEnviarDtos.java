/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author williamrodriguez
 */
public class DatosEnviarDtos {
    List<MensajeEnviarDto> mensajeEnviarDtos;

    public DatosEnviarDtos() {
        mensajeEnviarDtos = new ArrayList<>();
    }

    public List<MensajeEnviarDto> getMensajeEnviarDtos() {
        return mensajeEnviarDtos;
    }

    public void setMensajeEnviarDtos(List<MensajeEnviarDto> mensajeEnviarDtos) {
        this.mensajeEnviarDtos = mensajeEnviarDtos;
    }
    
    
}

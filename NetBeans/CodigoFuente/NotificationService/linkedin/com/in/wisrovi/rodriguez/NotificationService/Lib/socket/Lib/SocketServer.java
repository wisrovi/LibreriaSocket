package SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Lib;

import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Diccionarios.DiccionarioErrores;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Diccionarios.DiccionarioProcesos;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.DatosEnviarDtos;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.MatriculaDTO;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.MensajeEnviarDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.PinVidaDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.ProtocoloComunicacionDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Dto.PushDto;
import SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.util.Util;
import SenInt2.fcv.org.servlet.ProcesarDatosRecibidosSocket;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer extends Thread {

    // private WebApplicationContext webApplicationContext;
    private String mensajeRecibido;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static Map<String, Map<Integer, PrintWriter>> SenInt;
    ProtocoloComunicacionDto respuestaPorDefecto;
    String tituloProyecto;

    public SocketServer(Socket socket, Map<String, Map<Integer, PrintWriter>> listaDispositivosAndroidSenInt, String tituloProyecto) throws Exception {
        this.socket = socket;
        SenInt = listaDispositivosAndroidSenInt;
        this.tituloProyecto = tituloProyecto;
        System.out.println("Nuevo dispositivo inicio socket");

    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        ProtocoloComunicacionDto respuestaPorDefecto = new ProtocoloComunicacionDto();
        respuestaPorDefecto.setEncabezado(tituloProyecto);

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                Gson gson = new Gson();
                mensajeRecibido = in.readLine();
                if (mensajeRecibido == null) {
                    return;
                }
                mensajeRecibido = AuxSocket.decodeUrlBase64(mensajeRecibido);
                ProtocoloComunicacionDto dtoEncontrado = gson.fromJson(mensajeRecibido, ProtocoloComunicacionDto.class);

                DefaultProcesos defaultProcesos = new DefaultProcesos();

                respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorDefault);
                respuestaPorDefecto.setProceso(dtoEncontrado.getProceso());
                respuestaPorDefecto.setDatosComprimidos("");

                boolean enviarRespuesta = true;

                if (!dtoEncontrado.getDatosComprimidos().equals("")) {
                    ProcesarDatosRecibidosSocket analisisDatosSocket = new ProcesarDatosRecibidosSocket();
                    switch (dtoEncontrado.getProceso()) {
                        case "SendPushDispo": {
                            boolean errorEncontrado = true;
                            DatosEnviarDtos datosEnviarDtos = gson.fromJson(dtoEncontrado.getDatosComprimidos(), DatosEnviarDtos.class);
                            if (datosEnviarDtos != null) {
                                List<MensajeEnviarDto> mensajeEnviarDtos = datosEnviarDtos.getMensajeEnviarDtos();
                                for (Iterator<MensajeEnviarDto> iterator = mensajeEnviarDtos.iterator(); iterator.hasNext();) {
                                    MensajeEnviarDto nextPaquete = iterator.next();
                                    PushDto pushDto = gson.fromJson(nextPaquete.getMensajeEnviar(), PushDto.class);
                                    //System.out.println("Se ha solicitado envio datos a " + nextPaquete.getKey());
                                    for (Map.Entry<String, Map<Integer, PrintWriter>> senint : SenInt.entrySet()) {
                                        Map.Entry<Integer, PrintWriter> nextConexion = senint.getValue().entrySet().iterator().next();
                                        if (nextConexion.getKey().intValue() == nextPaquete.getKey()) {
                                            sendNotification(nextConexion.getValue(), pushDto);
                                            respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorDefault);
                                            errorEncontrado = false;
                                            break;
                                        }
                                    }
                                    
                                }
                            }
                            if (errorEncontrado) {
                                respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorPush);
                            }
                        }
                        break;
                        case DiccionarioProcesos.procesoSocketPinVida: {
                            PinVidaDto datosPinVida = gson.fromJson(dtoEncontrado.getDatosComprimidos(), PinVidaDto.class);
                            String idDispositivo = datosPinVida.getIdDispositivo();
                            String ipDispositivo = datosPinVida.getIpDispositivo();
                            Integer key = 0;
                            if (!idDispositivo.equals("") && !ipDispositivo.equals("")) {
                                if (SenInt.containsKey(idDispositivo)) {
                                    key = SenInt.get(idDispositivo).entrySet().iterator().next().getKey();
                                }
                            } else {
                                respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorPin);
                            }
                            defaultProcesos.setPinVidaKeySocket(key);
                        }
                        break;
                        case DiccionarioProcesos.procesoSocketMatricula: {
                            MatriculaDTO datosMatricula = gson.fromJson(dtoEncontrado.getDatosComprimidos(), MatriculaDTO.class);
                            String identificadorUsuario = datosMatricula.getIdDispositivo();
                            defaultProcesos.setMatricula(false);
                            if (!identificadorUsuario.equals("")) {
                                int size = SenInt.size();
                                boolean yaEstaCreado = false;
                                synchronized (SenInt) {
                                    if (SenInt.containsKey(identificadorUsuario)) {
                                        Map<Integer, PrintWriter> mapSocket
                                                = SenInt.get(identificadorUsuario);
                                        SenInt.remove(identificadorUsuario);
                                        yaEstaCreado = true;
                                    }
                                    Map<Integer, PrintWriter> mapRegistro = new HashMap<>();
                                    mapRegistro.put(socket.getPort(), out);
                                    SenInt.put(identificadorUsuario, mapRegistro);
                                }

                                if (SenInt.containsKey(identificadorUsuario)) {
                                    Integer key = SenInt.get(identificadorUsuario).entrySet().iterator().next().getKey();
                                    datosMatricula.setKey(Integer.toString(key));
                                }
                                int size2 = SenInt.size();
                                if (size == size2 && !yaEstaCreado) {
                                    respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorMatricula);
                                } else {
                                    defaultProcesos.setMatricula(true);
                                    dtoEncontrado.setDatosComprimidos(gson.toJson(datosMatricula));
                                }
                            } else {
                                respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorMatricula);
                            }
                        }
                        break;
                        case DiccionarioProcesos.procesoSocketPrueba: {
                            respuestaPorDefecto.setDatosComprimidos(DiccionarioProcesos.procesoSocketPrueba);
                        }
                        break;
                        case DiccionarioProcesos.procesoSocketBroadcast: {
                            String etiquetaBroadcast = dtoEncontrado.getMsgError();

                            ProtocoloComunicacionDto broadcast = new ProtocoloComunicacionDto();
                            broadcast.setEncabezado(tituloProyecto);
                            broadcast.setProceso(etiquetaBroadcast);
                            broadcast.setDatosComprimidos(dtoEncontrado.getDatosComprimidos());
                            broadcast.setMsgError(DiccionarioErrores.msgErrorDefault);

                            String mensajeEnviar = new Gson().toJson(broadcast);
                            mensajeEnviar = new Util().encodeUrlBase64(mensajeEnviar);
                            for (Map.Entry<String, Map<Integer, PrintWriter>> senint : SenInt.entrySet()) {
                                Map.Entry<Integer, PrintWriter> next = senint.getValue().entrySet().iterator().next();
                                PrintWriter valueForPrint = next.getValue();
                                valueForPrint.println(mensajeEnviar);
                            }

                            dtoEncontrado.setDatosComprimidos(Integer.toString(SenInt.size()));
                        }
                        break;
                        case DiccionarioProcesos.procesoSocketPush: {
                            if (dtoEncontrado.getDatosComprimidos().equals("OK")) {
                                enviarRespuesta = false;
                            }
                        }
                        break;
                    }
                    respuestaPorDefecto = analisisDatosSocket.procesarDatos(dtoEncontrado.getProceso(), dtoEncontrado.getDatosComprimidos(), defaultProcesos, respuestaPorDefecto);
                } else {
                    respuestaPorDefecto.setMsgError(DiccionarioErrores.msgErrorDatos);
                }
                String datoEnviar = AuxSocket.encodeUrlBase64(gson.toJson(respuestaPorDefecto));
                if (enviarRespuesta) {
                    out.println(datoEnviar);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //System.out.println("- FINALIZAR SOCKET por error:");
            if (mensajeRecibido != null) {
                if (mensajeRecibido.split(",").length == 3) {
                    System.out.println("** LIMPIAR DE LA LISTA:");
                    if (SenInt.containsKey(mensajeRecibido.split(",")[2])) {
                        //System.out.println("*** CADENA: " + mensajeRecibido);
                        Map<Integer, PrintWriter> mapSocket = SenInt.get(mensajeRecibido.split(",")[2]);
                        System.out.println("*** IP: " + mensajeRecibido.split(",")[2]);
                        Map.Entry<Integer, PrintWriter> next = mapSocket.entrySet().iterator().next();
                        System.out.println("*** PORT: " + next.getKey());
                        next.getValue().close();
                        SenInt.remove(mensajeRecibido);
                        System.out.println("*** REMOVIDO.");
                    }
                }
            }
//            if (out != null) {
//                writers.remove(out);
//            }
            try {
                //System.out.println("** CERRAR SOCKET:");
                socket.close();
                System.out.println("*** CERRADO.");
            } catch (IOException e) {
            }
        }
    }

    public void sendNotification(
            PrintWriter Notificacion,
            PushDto pushDto) {
        ProtocoloComunicacionDto protocoloComunicacionDto = new ProtocoloComunicacionDto();
        protocoloComunicacionDto.setEncabezado(tituloProyecto);
        protocoloComunicacionDto.setProceso(DiccionarioProcesos.procesoSocketPush);
        protocoloComunicacionDto.setMsgError(DiccionarioErrores.msgErrorDefault);
        protocoloComunicacionDto.setDatosComprimidos(new Gson().toJson(pushDto));

        String mensajeEnviar = new Gson().toJson(protocoloComunicacionDto);
        mensajeEnviar = new Util().encodeUrlBase64(mensajeEnviar);
        Notificacion.println(mensajeEnviar);
    }
}

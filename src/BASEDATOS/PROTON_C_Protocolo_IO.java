package BASEDATOS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 
 * @author R.Cuevas
 */
    
public final class PROTON_C_Protocolo_IO extends PROTON_A_Estadisticas {
 
    //public static boolean x_TPMS;
    private DataInputStream o_Entt=null;
    private DataOutputStream o_Sall=null;  
    private final String x_wel;
    private final boolean x_cks; 
    private final boolean x_ack;
    private String x_VId;
    private int[] x_checksum={0,0};
    private int x_psum=0;
    private boolean x_owe;
    
    public PROTON_C_Protocolo_IO(Socket _soc, boolean _ack, boolean _cks, String _wel) throws Exception {
        x_ack=_ack;
        x_cks=_cks;
        x_wel=_wel;
        try {
            x_owe=!_wel.isEmpty();
            o_Entt=new DataInputStream(_soc.getInputStream());
            o_Sall=new DataOutputStream(_soc.getOutputStream());
        } catch (IOException e) {
            throw new Exception("Error de conexion Socket iostream"); 
        }    
    }
    
    public String Recibir() throws Exception {
        StringBuilder y_trama=new StringBuilder("");
        try {
            do {
                int y_byte =o_Entt.read();    //**ESPERA
                if ((y_byte<=0)||(y_byte==13)) break;
                y_trama.append((char)y_byte);
            } while (true);  
            return y_trama.toString();
        } catch (SocketTimeoutException e1) {
            return "^";
        } catch (IOException | NumberFormatException e2) {
            if (e2.getMessage().contains("Socket Closed")) return "#";
            throw new Exception("[".concat(y_trama.toString()).concat("] ").concat(e2.getMessage()));
        } 
    }

    private String Acknowledge(StringBuilder _trama) {
            return null;       
    }
    
    public String getVId() {
        return x_VId;
    }
    
    public boolean Reconoce_HeartBeat(String _trama) {
        return false;   
    }     
    
    public int getCheck() {
        return this.x_checksum[this.x_psum];
    }
    
    public boolean setCheck(int _sum) {
        if (!this.x_cks) return true;
        if (_sum<=0) return true;
        if (this.x_checksum[1]==_sum) {
            ++this.x_dup;
            return false;
        }
        this.x_checksum[0]=_sum;
        return true;
    }
         
    public synchronized boolean Enviar(String _ota) {
//System.out.println("OTA:" + _OTA);
        if (_ota.isEmpty()) return true;
        try {
            char[] y_bys=_ota.toCharArray();
            byte[] y_byt=new byte[y_bys.length]; 
            for (int y_i=0;y_i<y_bys.length;y_i++) {
                y_byt[y_i]=(byte) y_bys[y_i];
            }
            o_Sall.write(y_byt);
            o_Sall.flush();
            ++this.x_otaup;
//System.out.println(" sal:" + y_ack);
            return true;
        } catch (IOException | NumberFormatException e) {
//System.out.println("Expt:" + e.getMessage());
            return false;
        }    
    }
    
    
    //-------------Envio de comando OTA -----
    
    public String ValidaOTA(String _OTA) {
        try {
            switch (_OTA.toUpperCase()) {
                case "#GETPOS": return this.Polleo_Ubicacion();
                case "#OP1ON":  return this.Salida1(true);
                case "#OP1OFF": return this.Salida1(false);
                case "#OP2ON":  return this.Salida2(true);
                case "#OP2OFF": return this.Salida2(false);     
                default:    
                    return _OTA;
            }        
        } catch (Exception e) {    
            return "";
        }    
    }
  
    private String Polleo_Ubicacion()  throws Exception {
        return "#GETPOS";
    }
    
    private String CheckSuma(String _check) throws Exception {
        try {
            String[] y_dig=_check.split("");
            int y_cs=0;
            boolean y_pn=false;
            String y_hx="0x";
            for (String y_d:y_dig) {
                if (!y_pn) y_hx="0x".concat(y_d);
                else y_cs+=Integer.decode(y_hx.concat(y_d));
                y_pn=!y_pn;
            }
            y_hx=Integer.toHexString(y_cs);
            if (y_hx.length()==1) return "0".concat(y_hx);
            if (y_hx.length()==2) return y_hx;
            if (y_hx.length()>2)  return y_hx.substring(y_hx.length()-2);
            throw new Exception("0<=CheckSum");
        } catch (Exception e) {
            throw new Exception("CheckSum");
        }    
    }
    
    private String Salida1(boolean _on) throws Exception {
        return _on?"#OP1ON":"#OP1OFF";
    }
     
    private String Salida2(boolean _on)  throws Exception {
        return _on?"#OP2ON":"#OP2OFF";
    }
    

}

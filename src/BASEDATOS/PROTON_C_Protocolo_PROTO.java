package BASEDATOS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * @author R.Cuevas
 */
    
public final class PROTON_C_Protocolo_PROTO extends PROTON_A_ProtocoloBase {
 
    public static boolean x_TPMS;
    private final List<String> x_Hexa=new ArrayList<>();
    private List<Integer> o_Tipos=new ArrayList<>();
    private int x_CMD=-1;
    public static int C1=0, C2=0, C3=0;
    public static String XTRM=new String();
    private boolean x_ctp=false;
    
    public PROTON_C_Protocolo_PROTO() {   }

    public PROTON_C_Protocolo_PROTO(List<String> _tipos) {
        if (!_tipos.isEmpty()) {
            for (String y_t:_tipos) {
                try {
                    o_Tipos.add(Integer.valueOf(y_t));
                } catch (NumberFormatException e) {
                    break;
                }    
            }
            x_ctp=true;
        }    
    }
   
    public StringBuilder[] Registro(String _t,
                                    String _trama,
                                    String _vid,
                                    String _hur,
                                    String _ncon,
                                    String _ip) {
        this.o_Query.clear();
        C1=(++C1)%1000000;
        XTRM=_trama;
        this.setComandoGenerico(_vid, _trama, _hur);
        C2=C1;
        StringBuilder[] y_qury=new StringBuilder[2];
        y_qury[0]=new StringBuilder("");
        y_qury[1]=new StringBuilder("");
        for (Entry y_ent:o_Query.entrySet()) {
            y_qury[0].append(y_ent.getKey()).append(",");
            y_qury[1].append(y_ent.getValue()).append(",");       
        }
        try {
            y_qury[1].append("'").append(_ip.split(":")[0].substring(1)).append("'").append(",");
            y_qury[0].append("[XML],");
        } catch (Exception e) {
            y_qury[1].append("'NoIP',");
            y_qury[0].append("[XML],");
        }
        C3=C1;
        y_qury[0].append("[Update_Number]");
        y_qury[1].append(_ncon);
        return y_qury;
    }


}

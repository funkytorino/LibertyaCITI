/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package com.jmm.ExportCITIRG3685.model;
import org.openXpertya.model.*;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Tax
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-09-23 14:00:25.659 */
public class LP_C_Tax extends org.openXpertya.model.MTax
{
/** Constructor estándar */
public LP_C_Tax (Properties ctx, int C_Tax_ID, String trxName)
{
super (ctx, C_Tax_ID, trxName);
/** if (C_Tax_ID == 0)
{
}
 */
}
/** Load Constructor */
public LP_C_Tax (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
public String toString()
{
StringBuffer sb = new StringBuffer ("LP_C_Tax[").append(getID()).append("]");
return sb.toString();
}
public static final int CITIRG3685_AD_Reference_ID = MReference.getReferenceID("CatCITIRG3685");
/** Crédito o Débito Fiscal = CDF */
public static final String CITIRG3685_CréditoODébitoFiscal = "CDF";
/** Percepciones de IVA = PIV */
public static final String CITIRG3685_PercepcionesDeIVA = "PIV";
/** Percepciones de Ingresos Brutos = PIB */
public static final String CITIRG3685_PercepcionesDeIngresosBrutos = "PIB";
/** Percepciones Nacionales = PNC */
public static final String CITIRG3685_PercepcionesNacionales = "PNC";
/** Percepciones Municipales = PMN */
public static final String CITIRG3685_PercepcionesMunicipales = "PMN";
/** Otros Impuestos = OTR */
public static final String CITIRG3685_OtrosImpuestos = "OTR";
/** Exento = EXE */
public static final String CITIRG3685_Exento = "EXE";
/** Set Categoría para CITI RG3685 */
public void setcitiRG3685 (String citiRG3685)
{
if (citiRG3685 == null || citiRG3685.equals("CDF") || citiRG3685.equals("PIV") || citiRG3685.equals("PIB") || citiRG3685.equals("PNC") || citiRG3685.equals("PMN") || citiRG3685.equals("OTR") || citiRG3685.equals("EXE"));
 else throw new IllegalArgumentException ("citiRG3685 Invalid value - Reference = CITIRG3685_AD_Reference_ID - CDF - PIV - PIB - PNC - PMN - OTR - EXE");
if (citiRG3685 != null && citiRG3685.length() > 3)
{
log.warning("Length > 3 - truncated");
citiRG3685 = citiRG3685.substring(0,3);
}
set_Value ("citiRG3685", citiRG3685);
}
/** Get Categoría para CITI RG3685 */
public String getcitiRG3685() 
{
return (String)get_Value("citiRG3685");
}
}

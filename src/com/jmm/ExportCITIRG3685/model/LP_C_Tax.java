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
 *  @version  - 2015-05-06 22:00:57.345 */
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
public static final int CATCITIRG3685_AD_Reference_ID = MReference.getReferenceID("CategoriaCITI");
/** Crédito Fiscal = CRF */
public static final String CATCITIRG3685_CréditoFiscal = "CRF";
/** Exento = EXE */
public static final String CATCITIRG3685_Exento = "EXE";
/** Otros tributos = OTR */
public static final String CATCITIRG3685_OtrosTributos = "OTR";
/** Percepciones de IVA = PIV */
public static final String CATCITIRG3685_PercepcionesDeIVA = "PIV";
/** Percepciones de Ingresos Brutos = PIB */
public static final String CATCITIRG3685_PercepcionesDeIngresosBrutos = "PIB";
/** Percepciones de impuestos municipales = PMN */
public static final String CATCITIRG3685_PercepcionesDeImpuestosMunicipales = "PMN";
/** Percepciones de impuestos nacionales = PNC */
public static final String CATCITIRG3685_PercepcionesDeImpuestosNacionales = "PNC";
/** Set catCITIRG3685 */
public void setcatCITIRG3685 (String catCITIRG3685)
{
if (catCITIRG3685 == null || catCITIRG3685.equals("CRF") || catCITIRG3685.equals("EXE") || catCITIRG3685.equals("OTR") || catCITIRG3685.equals("PIV") || catCITIRG3685.equals("PIB") || catCITIRG3685.equals("PMN") || catCITIRG3685.equals("PNC"));
 else throw new IllegalArgumentException ("catCITIRG3685 Invalid value - Reference = CATCITIRG3685_AD_Reference_ID - CRF - EXE - OTR - PIV - PIB - PMN - PNC");
if (catCITIRG3685 != null && catCITIRG3685.length() > 3)
{
log.warning("Length > 3 - truncated");
catCITIRG3685 = catCITIRG3685.substring(0,3);
}
set_Value ("catCITIRG3685", catCITIRG3685);
}
/** Get catCITIRG3685 */
public String getcatCITIRG3685() 
{
return (String)get_Value("catCITIRG3685");
}
}

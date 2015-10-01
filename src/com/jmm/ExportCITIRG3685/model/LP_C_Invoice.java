/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package com.jmm.ExportCITIRG3685.model;
import org.openXpertya.model.*;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Invoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-09-23 14:00:23.367 */
public class LP_C_Invoice extends org.openXpertya.model.MInvoice
{
/** Constructor estándar */
public LP_C_Invoice (Properties ctx, int C_Invoice_ID, String trxName)
{
super (ctx, C_Invoice_ID, trxName);
/** if (C_Invoice_ID == 0)
{
}
 */
}
/** Load Constructor */
public LP_C_Invoice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
public String toString()
{
StringBuffer sb = new StringBuffer ("LP_C_Invoice[").append(getID()).append("]");
return sb.toString();
}
public static final int AFIPDOCTYPE_AD_Reference_ID = MReference.getReferenceID("DocSubTypeCae");
/** Facturas A = 01 */
public static final String AFIPDOCTYPE_FacturasA = "01";
/** Notas de Debito A = 02 */
public static final String AFIPDOCTYPE_NotasDeDebitoA = "02";
/** Notas de Credito A = 03 */
public static final String AFIPDOCTYPE_NotasDeCreditoA = "03";
/** Recibos A = 04 */
public static final String AFIPDOCTYPE_RecibosA = "04";
/** Notas de Venta al Contado A = 05 */
public static final String AFIPDOCTYPE_NotasDeVentaAlContadoA = "05";
/** Facturas B = 06 */
public static final String AFIPDOCTYPE_FacturasB = "06";
/** Notas de Debito B = 07 */
public static final String AFIPDOCTYPE_NotasDeDebitoB = "07";
/** Notas de Credito B = 08 */
public static final String AFIPDOCTYPE_NotasDeCreditoB = "08";
/** Recibos B = 09 */
public static final String AFIPDOCTYPE_RecibosB = "09";
/** Notas de Venta al contado B = 10 */
public static final String AFIPDOCTYPE_NotasDeVentaAlContadoB = "10";
/** Facturas C = 11 */
public static final String AFIPDOCTYPE_FacturasC = "11";
/** Notas de Debito C = 12 */
public static final String AFIPDOCTYPE_NotasDeDebitoC = "12";
/** Nota de Credito C = 13 */
public static final String AFIPDOCTYPE_NotaDeCreditoC = "13";
/** Liquidación única comercial impositiva Clase A = 27 */
public static final String AFIPDOCTYPE_LiquidaciónÚnicaComercialImpositivaClaseA = "27";
/** Liquidación única comercial impositiva Clase B = 28 */
public static final String AFIPDOCTYPE_LiquidaciónÚnicaComercialImpositivaClaseB = "28";
/** Liquidación única comercial impositiva Clase C = 29 */
public static final String AFIPDOCTYPE_LiquidaciónÚnicaComercialImpositivaClaseC = "29";
/** Liquidación primaria de granos = 33 */
public static final String AFIPDOCTYPE_LiquidaciónPrimariaDeGranos = "33";
/** Nota de Credito Liq. única comercial impositiva Clase B = 43 */
public static final String AFIPDOCTYPE_NotaDeCreditoLiqÚnicaComercialImpositivaClaseB = "43";
/** Nota de Credito Liq. única comercial impositiva Clase C = 44 */
public static final String AFIPDOCTYPE_NotaDeCreditoLiqÚnicaComercialImpositivaClaseC = "44";
/** Nota de Debito Liq. única comercial impositiva Clase A = 45 */
public static final String AFIPDOCTYPE_NotaDeDebitoLiqÚnicaComercialImpositivaClaseA = "45";
/** Nota de Debito Liq. única comercial impositiva Clase B = 46 */
public static final String AFIPDOCTYPE_NotaDeDebitoLiqÚnicaComercialImpositivaClaseB = "46";
/** Nota de Debito Liq. única comercial impositiva Clase C = 47 */
public static final String AFIPDOCTYPE_NotaDeDebitoLiqÚnicaComercialImpositivaClaseC = "47";
/** Nota de Credito Liq. única comercial impositiva Clase A = 48 */
public static final String AFIPDOCTYPE_NotaDeCreditoLiqÚnicaComercialImpositivaClaseA = "48";
/** Facturas M = 51 */
public static final String AFIPDOCTYPE_FacturasM = "51";
/** Notas de Debito M = 52 */
public static final String AFIPDOCTYPE_NotasDeDebitoM = "52";
/** Notas de Credito M = 53 */
public static final String AFIPDOCTYPE_NotasDeCreditoM = "53";
/** Otros comprobantes - Notas de credito = 90 */
public static final String AFIPDOCTYPE_OtrosComprobantes_NotasDeCredito = "90";
/** Otros comprobantes = 99 */
public static final String AFIPDOCTYPE_OtrosComprobantes = "99";
/** Set Tipo de documento AFIP */
public void setafipdoctype (String afipdoctype)
{
if (afipdoctype == null || afipdoctype.equals("01") || afipdoctype.equals("02") || afipdoctype.equals("03") || afipdoctype.equals("04") || afipdoctype.equals("05") || afipdoctype.equals("06") || afipdoctype.equals("07") || afipdoctype.equals("08") || afipdoctype.equals("09") || afipdoctype.equals("10") || afipdoctype.equals("11") || afipdoctype.equals("12") || afipdoctype.equals("13") || afipdoctype.equals("27") || afipdoctype.equals("28") || afipdoctype.equals("29") || afipdoctype.equals("33") || afipdoctype.equals("43") || afipdoctype.equals("44") || afipdoctype.equals("45") || afipdoctype.equals("46") || afipdoctype.equals("47") || afipdoctype.equals("48") || afipdoctype.equals("51") || afipdoctype.equals("52") || afipdoctype.equals("53") || afipdoctype.equals("90") || afipdoctype.equals("99"));
 else throw new IllegalArgumentException ("afipdoctype Invalid value - Reference = AFIPDOCTYPE_AD_Reference_ID - 01 - 02 - 03 - 04 - 05 - 06 - 07 - 08 - 09 - 10 - 11 - 12 - 13 - 27 - 28 - 29 - 33 - 43 - 44 - 45 - 46 - 47 - 48 - 51 - 52 - 53 - 90 - 99");
if (afipdoctype != null && afipdoctype.length() > 3)
{
log.warning("Length > 3 - truncated");
afipdoctype = afipdoctype.substring(0,3);
}
set_Value ("afipdoctype", afipdoctype);
}
/** Get Tipo de documento AFIP */
public String getafipdoctype() 
{
return (String)get_Value("afipdoctype");
}
}

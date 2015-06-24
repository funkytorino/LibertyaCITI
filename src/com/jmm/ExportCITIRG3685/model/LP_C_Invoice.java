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
 *  @version  - 2015-05-06 22:00:55.498 */
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
/** Set AFIPDoctype */
public void setAFIPDoctype (String AFIPDoctype)
{
if (AFIPDoctype != null && AFIPDoctype.length() > 3)
{
log.warning("Length > 3 - truncated");
AFIPDoctype = AFIPDoctype.substring(0,3);
}
set_Value ("AFIPDoctype", AFIPDoctype);
}
/** Get AFIPDoctype */
public String getAFIPDoctype() 
{
return (String)get_Value("AFIPDoctype");
}
}

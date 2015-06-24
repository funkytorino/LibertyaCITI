
/*
 * Extensión de la clase MInvoice para el plugin de exportación de archivos CITI RG3685
 * 
 * Los tipos de documentos definidos por AFIP deben informarse por cada documento. En los informes de ventas
 * no hay inconvenientes, ya que cada tipo de documento (factura, ND, NC) tiene su valor proveniente de DocType.
 * En el caso de las compras, el problema es que hay un solo tipo de documento (Factura de proveedor) y es 
 * necesario definir el tipo en función de la letra del comprobante.
 * Esta extensión agrega el valor correspondiente en la columna AFIPDocType (agregada por el plugin) a cada
 * c_invoice seteando en el postCompleteIt() el valor proveniente de DocType en el caso de ventas y un valor
 * calculado en el caso de compras.  
 *  
 * Autor: Juan Manuel Martínez - jmmartinezsf@gmail.com
 * Versión 0.1 - mayo de 2015
 */

package com.jmm.ExportCITIRG3685.model;
 
import java.util.HashMap;
import java.util.Properties;
import org.openXpertya.model.PO;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatusPO;

public class MInvoice extends MPluginPO {

	public MInvoice(PO po, Properties ctx, String trxName, String aPackage) {
		super(po, ctx, trxName, aPackage);
	}
 
	public MPluginStatusPO postBeforeSave(PO po, boolean newRecord) {
		HashMap<String, Integer> codigos = new HashMap<String, Integer>();
		codigos.put("FA", 1);
		codigos.put("FB", 6);
		codigos.put("FC", 11);
		codigos.put("FM", 51);
		
		//org.openXpertya.model.MInvoice invoice = (org.openXpertya.model.MInvoice)po;
		LP_C_Invoice lp_invoice = (LP_C_Invoice) po;
		
		MDocType dt = MDocType.get(po.getCtx(), lp_invoice.getC_DocTypeTarget_ID());
		if (lp_invoice.isSOTrx())
			lp_invoice.setAFIPDoctype(dt.getdocsubtypecae());
		else{
			int offset = 0;
			switch (dt.getC_DocType_ID()){
			case 1010599: // Nota de débito de proveedor
				offset += 1;
				break;
			case 1010510: // Nota de crédito (abono) de proveedor
				offset += 2;
				break;
			}
			Integer cod = new Integer(codigos.get("F" + lp_invoice.getLetra()) + offset);
			lp_invoice.setAFIPDoctype(String.format("%03d", cod));
		}
				
		return status_po;
	}	
}



	 




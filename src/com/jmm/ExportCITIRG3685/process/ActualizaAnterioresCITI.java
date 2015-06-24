/*
 * Proceso para actualizar códigos AFIP de c_invoices anteriores a la instalación del plugin de
 * exportación de archivos CITI RG3685
 * Diseño de los registros: http://www.afip.gob.ar/comprasyventas/
 * 
 * Autor: Juan Manuel Martínez - jmmartinezsf@gmail.com
 * Versión 0.1 - mayo de 2015
 * 
 */

package com.jmm.ExportCITIRG3685.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;

import com.jmm.ExportCITIRG3685.model.LP_C_Invoice;
import com.jmm.ExportCITIRG3685.model.MInvoice;

public class ActualizaAnterioresCITI extends SvrProcess {

    private Timestamp desde;
    private Timestamp hasta;
    private  ResultSet rs = null;
    
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );

            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equalsIgnoreCase( "FechaDesde" )) {
                desde = ( Timestamp )para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "FechaHasta" )) {
                hasta = ( Timestamp )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
		
	}

	@Override
	protected String doIt() throws Exception {

		HashMap<String, Integer> codigos = new HashMap<String, Integer>();
		codigos.put("FA", 1);
		codigos.put("FB", 6);
		codigos.put("FC", 11);
		codigos.put("FM", 51);
		
		String sql = "SELECT c_invoice_id FROM c_invoice where isactive = 'Y' and dateacct between ? and ? and c_doctype_id not in (1010517, 1010518, 1010519, 1010520)";	        	         	 		
		PreparedStatement pstmt = DB.prepareStatement(sql);			
		pstmt.setTimestamp(1, desde);
		pstmt.setTimestamp(2, hasta);
		log.log( Level.SEVERE,"SQL : " + sql);
		rs = pstmt.executeQuery();

		while(rs.next())
		{
			LP_C_Invoice lp_invoice = new LP_C_Invoice(getCtx(), rs.getInt(1), null);
			MDocType dt = MDocType.get(getCtx(), lp_invoice.getC_DocTypeTarget_ID());
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
			lp_invoice.save();
			}
		}
		return null;
	}
	
	
}
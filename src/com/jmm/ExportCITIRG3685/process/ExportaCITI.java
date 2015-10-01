/*
 * Proceso para generar los archivos exigidos por la RG3685: CITI compras y ventas
 * Diseño de los registros: http://www.afip.gob.ar/comprasyventas/
 * 
 * Autor: Juan Manuel Martínez - jmmartinezsf@gmail.com
 * Versión 0.1 - septiembre de 2015
 * 
 */

package com.jmm.ExportCITIRG3685.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;

public class ExportaCITI extends SvrProcess {

	/*
	 * Constructor de la clase
	 */
	
	public ExportaCITI() {
		super();

		cf = new Double(0);
		p_iva = new Double(0);
		p_iibb = new Double(0);
		p_nac = new Double(0);
		p_mun = new Double(0);
		ot = new Double(0);
		ex = new Double(0);
	}
  
    private Timestamp date_from;
    private Timestamp date_to;
    private String transaction;
    private String directorio="";
    private ResultSet rs = null;
	private Double cf, p_iva, p_iibb, p_nac, p_mun, ot, ex; 
	private int invoice_id;
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );

            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equalsIgnoreCase( "FechaDesde" )) {
                date_from = ( Timestamp )para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "FechaHasta" )) {
                date_to = ( Timestamp )para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "TipoTrans" )) {
            	transaction = (String)para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "Directorio" )) {
            	directorio = (String)para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

	}
	
	protected String doIt() throws java.lang.Exception {
		
		File targetDir = new File (directorio);
		if (!targetDir.exists())
			targetDir.mkdir();
		//String cabecera = directorio + "/REGINFO_CV_CABECERA.txt";
		String archivo_cbte = directorio + "/REGINFO_CV_" + (transaction.equalsIgnoreCase("V") ? "COMPRAS":"VENTAS")  + "_CBTE.txt";
		String archivo_alic = directorio + "/REGINFO_CV_" + (transaction.equalsIgnoreCase("V") ? "COMPRAS":"VENTAS")  + "_ALICUOTAS.txt";
		String result ="";
		String lineSeparator = System.getProperty("line.separator");
        String sql = null;
        FileWriter cbtes = null, alic = null;
        PreparedStatement pstmt = null;
 		
        try
 		{
 			long inicia = System.currentTimeMillis();
 			
 			sql = getSql();
 			/*
 			 * Necesito poder navegar hacia adelante y atrás en el resulset, por eso creo de esta manera
 			 * el preparedstatement.
 			 */
 			pstmt = DB.getConnectionRW().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
 			pstmt.setTimestamp(1, date_from);
 			pstmt.setTimestamp(2, date_to);
 			pstmt.setString(3, (transaction.equalsIgnoreCase("V")?"N":"Y"));
 			rs = pstmt.executeQuery();
 			
 			cbtes = new FileWriter(archivo_cbte);
 			alic = new FileWriter(archivo_alic);
 			int Q = creaArchivos(cbtes, alic);
 			cbtes.close();
 			alic.close();
		
 			long termina = System.currentTimeMillis();
 			long segundos = (termina-inicia)/1000;
 			log.log(Level.SEVERE,"Generar RG3685 Tardo: "+segundos+" segundos - " + Q + " registros.");
 			rs.close();
 			pstmt.close();
 			pstmt = null;
 		}
 		catch (Exception e)
 		{ 
 			log.saveError("Exportacion CITI RG3685 - Prepare", e);
 			e.printStackTrace();
 			
 			try {
				if(cbtes != null) cbtes.close();
				if(alic != null) alic.close();
				if(pstmt != null) pstmt.close();
				if(rs != null)rs.close();					
			} catch (IOException e1) {
				result = "Error al generar los archivos CITI RG3685 " +lineSeparator + e.getLocalizedMessage();
				e1.printStackTrace();
			}	
 		}	
 		return result;
	}				
	
	private int creaArchivos(FileWriter fw_c , FileWriter fw_a) throws IOException, SQLException
	{
		String lineSeparator = System.getProperty("line.separator");
		//int invoice_id = 0;
		int cant = 0;
		int q_alic = 0;
		StringBuffer s; // linea de alícuotas
		StringBuffer c; // línea de comprobantes
		String fecha = new String();
		String tipo_comp = new String();
		String pv = new String();
		String nro = new String();
		String cod_doc = new String();
		String nro_doc = new String();
		String total = new String();
		String moneda = new String();
		String rz = new String();
		String letra = new String();
		/*
		 1 inv.c_invoice_id
		 2 inv.dateacct::date
		 3 inv.dateinvoiced::date
		 4 inv.afipdoctype    - Tipo de comprobante
		 5 inv.documentno
		 6 bp.taxidtype       - Tipo documento, solo para proveedores
		 7 bp.taxid           - Nro de documento, solo para proveedores
		 8 bp.name            - Razón social, solo para proveedores
		 9 inv.grandtotal
		 10 itax.taxbaseamt
		 11 itax.taxamt
		 12 cur.wsfecode
		 13 tax.c_cat_citi_id
		 14 tax.rate
		 15 ltr.letra
		 16 tax.wsfecode
		 */
		
		while(rs.next())
		{
			/* 
			 * Primero genero las líneas de las alícuotas y voy calculando las sumas totalizadoras del
			 * informe por comprobante, que va en un archivo separado.
			*/
			log.log(Level.SEVERE,"Invoice_id" + rs.getInt(1));
			fecha = getDate(rs.getDate(3));
			tipo_comp = pad(rs.getString(4), 3, true);
			pv = pad(rs.getString(5).substring(1, 5), 5, true);
			nro = pad(rs.getString(5).substring(6, 13), 20, true);
			cod_doc = rs.getString(6);
			nro_doc = pad(rs.getString(7).replace("-", ""), 20, true);
			total = pad(getCnvAmt(rs.getDouble(9)), 15, true);
			moneda = rs.getString(12);
			rz = pad(rs.getString(8).toUpperCase(), 30, false);
			letra = rs.getString(15);			

			if (letra.equalsIgnoreCase("A") || letra.equalsIgnoreCase("B") ||letra.equalsIgnoreCase("M") || esOtros(tipo_comp)){ 
				s = new StringBuffer();
				s.append(tipo_comp);
 				if (esOtros(tipo_comp))
 	 				s.append("00000");
 				else
 	 				s.append(pv);
 				s.append(nro);
 				if (transaction.equalsIgnoreCase("V")){			// los campos 6 y 7 no van para informes de ventas
 					s.append(cod_doc);
 					s.append(nro_doc);
 				}

				if (esCreditoDebitoFiscal(rs.getString(13))){
	 				s.append(pad(getCnvAmt(rs.getDouble(10)), 15, true));		// NG
	 				s.append(pad(rs.getString(16), 4, true));					// Alícuota de IVA
	 				s.append(pad(getCnvAmt(rs.getDouble(11)), 15, true));		// IVA liquidado
	 				q_alic++;
	 				s.append(lineSeparator);
	 				fw_a.write(s.toString());
				}else{															// Montos no gravados en Fac A o M 
					if (cf == 0.0 && rs.getString(13).equalsIgnoreCase("EXE")){
						s.append(pad(getCnvAmt(rs.getDouble(10)), 15, true));	// Monto no gravado
						s.append(pad(rs.getString(16), 4, true));				// Alícuota de IVA
		 				s.append(pad("0", 15, true));
		 				q_alic++;
		 				s.append(lineSeparator);
		 				fw_a.write(s.toString());
					}
				}
				s = null;
				//fw_a.write("\r");
			}
			
			/*
			 * AcumulaImportes agrega los montos de impuestos a cada variable. Si devuelve verdadero, es porque
			 * la próxima línea es de otro comprobante por lo que debo generar la línea del comprobante. Lo mismo
			 * ocurre si es la última línea del rs.
			 */
			if (acumulaImportes(rs.getInt(1)) || rs.isLast()){
				c = new StringBuffer();
				c.append(fecha);
				c.append(tipo_comp);
				if (esOtros(tipo_comp))
					c.append("00000");
				else
					c.append(pv);
				c.append(nro);
				if (transaction.equalsIgnoreCase("V"))
					// TODO: las importaciones no están soportadas por esta versión.
					c.append("                ");
				else
					c.append(nro);
				c.append(cod_doc);
				c.append(nro_doc);
				c.append(rz);
				c.append(total);
				// TODO: no se discriminan conceptos no gravados de exentos en esta versión.
				c.append("000000000000000");
				if (letra.equalsIgnoreCase("C") || letra.equalsIgnoreCase("B") ||
						((letra.equalsIgnoreCase("A") || esOtros(tipo_comp)) && cf == 0.0))
					c.append("000000000000000");
				else
					c.append(pad(getCnvAmt(ex), 15, true));
				c.append(pad(getCnvAmt(p_iva), 15, true));
				c.append(pad(getCnvAmt(p_nac), 15, true));
				c.append(pad(getCnvAmt(p_iibb), 15, true));
				c.append(pad(getCnvAmt(p_mun), 15, true));
				// TODO: dar soporte a impuestos internos
				c.append("000000000000000");
				// TODO: dar soporte multimoneda
				c.append(moneda);
				c.append("0001000000");
				c.append(q_alic);
				if ((letra.equalsIgnoreCase("A") || esOtros(tipo_comp)) && cf == 0.0)
					c.append("E");
				else
					c.append("0");
				if (transaction.equalsIgnoreCase("V"))
					c.append(pad(getCnvAmt(cf), 15, true));
				c.append(pad(getCnvAmt(ot), 15, true));
				if (transaction.equalsIgnoreCase("V"))
					// TODO: dar soporte para comisiones de corredores
					c.append("00000000000                              000000000000000");
				if (!transaction.equalsIgnoreCase("V"))
					c.append(fecha);
				c.append(lineSeparator);
 				fw_c.write(c.toString());
 				//fw_c.write("\r");
 				c = null;
 				q_alic = 0;
			}
			
			/*
			 * Guardo el  invoice_id del comprobante por que si en la próxima línea del rs 
			 * el invoice_id tiene el mismo valor entonces tengo que acumular los montos de impuestos.
			 */
			invoice_id = rs.getInt(1);
			cant++;
		}
	
		return cant;
			
	}
	
	/*
	 * Retorna la fecha en formato anio mes dia
	 */
    private String getDate(Date fecha) {
        
    	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(fecha);
    }

    /*
     * Retorna el monto como un string sin el punto decimal 
     */
    private String getCnvAmt(Double amt) {
    	
    	DecimalFormat df = new DecimalFormat("#.00");
    	return df.format(amt).replace(".", "").replace(",", "");
    }
    
    /*
     * Agrega la cantidad necesaria de ceros o espacios al inicio del argumento src
     * para completar el largo indicado
     * flag = true: agrega ceros
     * flag = false: agrega espacios
     */
    private String pad(String src, int largo, Boolean flag){
    	
    	StringBuffer ret = new StringBuffer();
    	String r = new String();
    	
    	if (src == null)
    		src = "";
    	
    	int n = largo - src.length();
    	if (n < 0){
    		r = src.substring(0, largo - 1);
    		n = 1;
    	}
    	else
    		r = src;
    	
    	final char[] arr = new char[n];
    	Arrays.fill(arr, flag?'0':' ');
    	
    	ret.append(arr);
    	ret.append(r);
    	return ret.toString();
    }
    
    /*
     * Borra los montos acumulados 
     */
    private void borraImpuestos(){
    	cf = 0.0;
    	p_iva = 0.0;
    	p_iibb = 0.0;
    	p_nac = 0.0;
    	p_mun = 0.0;
    	ot = 0.0;
    	ex = 0.00;
    }
    
    /*
     * Devuelve verdadero si el id del impuesto consultado corresponde a uno configurado 
     * como crédito o débto fiscal
     */
    private Boolean esCreditoDebitoFiscal(String id){
    	return id.trim().equalsIgnoreCase("CDF");
    }
    
    /*
     * Devuelve verdadero si el tipo de comprobante es "Otros comprobantes" u "Otros comprobantes - credito".
     */
    private Boolean esOtros(String tipo){
    	return (tipo.equals("090") || tipo.equals("099"));
    }
    
    /*
     * Acumula los importes de las líneas de cada factura.
     * Devuelve:
     *   true si la próxima línea del rs corresponde a la misma factura que la actual línea
     *   false en caso contrario
     */
    private Boolean acumulaImportes(int id) throws SQLException{
    	
    	Boolean ret = false;

    	/*
		 * Chequeo si el invoice_id de ésta línea es el mismo que el que está guardado y reseteo los montos
		 * de impuestos de ser necesario
		 */
		if (invoice_id != rs.getInt(1))
			borraImpuestos();

		if (rs.isLast()) // Si estoy en la última línea, devuelvo true para que se guarde el comprobante.
    		ret = true;
		else{
	    	rs.next();
	    	ret = !(rs.getInt(1) == id);
	    	rs.previous();
		}
		
		String v = rs.getString(13).trim();
		if (v.equalsIgnoreCase("CDF"))
			cf +=rs.getDouble(11);
		else if (v.equalsIgnoreCase("PIV"))
			p_iva += rs.getDouble(11);
		else if (v.equalsIgnoreCase("PIB"))
			p_iibb += rs.getDouble(11);
		else if (v.equalsIgnoreCase("PNC"))
			p_nac += rs.getDouble(11);
		else if (v.equalsIgnoreCase("PMN"))
			p_mun += rs.getDouble(11);
		else if (v.equalsIgnoreCase("OTR"))
			ot += rs.getDouble(11);
		else if (v.equalsIgnoreCase("EXE"))
			ex += rs.getDouble(10);
		
		return ret;
    }
    
	private String getSql()
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select inv.c_invoice_id, inv.dateacct::date, inv.dateinvoiced::date, inv.afipdoctype, inv.documentno, COALESCE(bp.taxidtype, '99'), ");
		sql.append("COALESCE(bp.taxid, inv.nroidentificcliente), bp.name, inv.grandtotal, itax.taxbaseamt, itax.taxamt, cur.wsfecode, tax.citirg3685, to_char(tax.rate, '90.00'), ");
		sql.append("ltr.letra, tax.WSFEcode ");
		sql.append("from libertya.c_invoicetax itax ");
		sql.append("join libertya.c_invoice inv on itax.c_invoice_id = inv.c_invoice_id ");
		sql.append("join libertya.c_doctype dt on inv.c_doctype_id = dt.c_doctype_id ");
		sql.append("join libertya.c_bpartner bp on inv.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("join libertya.c_currency cur on inv.c_currency_id = cur.c_currency_id ");
		sql.append("join libertya.c_tax tax on itax.c_tax_id = tax.c_tax_id ");
		sql.append("join libertya.c_letra_comprobante ltr on inv.c_letra_comprobante_id = ltr.c_letra_comprobante_id ");
		sql.append("where inv.dateacct between ? and ? and ");
		sql.append("inv.c_doctype_id not in (1010517, 1010518, 1010519, 1010520) ");
		sql.append("and inv.docstatus = 'CO' ");
		sql.append("and inv.issotrx = ? ");
		sql.append("order by ");
		sql.append("inv.dateinvoiced asc, bp.name, inv.documentno, tax.citirg3685");
		return sql.toString();
	}
}

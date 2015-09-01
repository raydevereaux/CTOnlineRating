package com.bc.ct.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import com.bc.ct.ws.model.RateQuote;
import com.bc.ct.ws.model.RateQuote.Charges.Charge;
import com.bc.ct.ws.model.RateQuote.TariffAuth;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateResponse;
import com.google.common.base.Preconditions;

/**
 * Creates an internal excel workbook for dealer sales differences.
 * @author dda07o
 */
public class RateQuoteExcel{

	private CellStyle boldStyle;
	private CellStyle dollarFormatStyle;
	private CellStyle numberFormatStyle;
	private CellStyle numberFormatOneDecStyle;
	private Workbook workbook;
	private String fileName;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
	/**
	 * Creates an internal excel XSLX workbook.  Use the write method to write it to an outputstream.
	 * @param ddsList
	 */
	public RateQuoteExcel(RateRequest request, RateResponse response) {
		Preconditions.checkNotNull(request, "Rate request cannot be null");
		Preconditions.checkNotNull(response, "Rate request cannot be null");
		this.workbook = new XSSFWorkbook();
		fillWorkbook(request, response);
		setFileName("rateQuote" + StringUtils.trimAllWhitespace(WordUtils.capitalizeFully(request.getDest().getCity())));
	}
	
	private void createHeaderRows(Sheet sheet, RateRequest request) {
		StringBuilder row0Builder = new StringBuilder()
		.append("Origin: ").append(request.getOrigin().getCity()).append(",").append(request.getOrigin().getState())
		.append(" Destination: ").append(request.getDest().getCity()).append(",").append(request.getDest().getState())
		.append(" Ship Date: ").append(sdf.format(request.getShipDate()))
		.append(" Created on: ").append(sdf.format(GregorianCalendar.getInstance().getTime()));
		StringBuilder row1Builder = new StringBuilder()
		.append("Commodity: ").append(request.getCommoditys().get(0).getCommodity().getDesc())
		.append(" Weight: ").append(request.getCommoditys().get(0).getCommodity().getWgt())
		.append(" Carrier: ").append(request.getShipMode().value());

		Row headerRow0Params = sheet.createRow(0);
		Cell cell0 = headerRow0Params.createCell(0);
		cell0.setCellValue(row0Builder.toString());
		cell0.setCellStyle(getBoldStyle());
		sheet.addMergedRegion(new CellRangeAddress(
	            0, //first row (0-based)
	            0, //last row  (0-based)
	            0, //first column (0-based)
	            9  //last column  (0-based)
	    ));
		Row headerRow1Params = sheet.createRow(1);
		Cell cell1 = headerRow1Params.createCell(0);
		cell1.setCellValue(row1Builder.toString());
		cell1.setCellStyle(getBoldStyle());
		sheet.addMergedRegion(new CellRangeAddress(
	            1, //first row (0-based)
	            1, //last row  (0-based)
	            0, //first column (0-based)
	            9  //last column  (0-based)
	    ));
		
		Row headerRow = sheet.createRow(2);
		Cell dlrHdrCell = headerRow.createCell(0);
		dlrHdrCell.setCellValue("Rate Number");
		Cell salesCell = headerRow.createCell(1);
		salesCell.setCellValue("Equipment Type");
		Cell prevSalesCell = headerRow.createCell(2);
		prevSalesCell.setCellValue("Route");
		Cell pctSalesCell = headerRow.createCell(3);
		pctSalesCell.setCellValue("Total Freight");
		Cell ftgCell = headerRow.createCell(4);
		ftgCell.setCellValue("Total Miles");
		Cell prevFtgCell = headerRow.createCell(5);
		prevFtgCell.setCellValue("SCAC");
		Cell pctFtgCell = headerRow.createCell(6);
		pctFtgCell.setCellValue("Currency");
		
		for(int i = 0; i < headerRow.getLastCellNum(); i++){//For each cell in the row 
			headerRow.getCell(i).setCellStyle(getBoldStyle());//Set the style
	    }
	}
	
	private void fillWorkbook(RateRequest request, RateResponse response) {
		Sheet sheet = workbook.createSheet("CT Rate");
		createHeaderRows(sheet, request);
		int rowNum = 3;
		for (int i=0; i<response.getQuotes().size(); i++) {
			RateQuote quote = response.getQuotes().get(i);
			Row row = sheet.createRow(rowNum++);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(i+1);
			Cell cell2 = row.createCell(1);
			cell2.setCellValue(quote.getEquipment().getType());
			Cell cell3 = row.createCell(2);
			cell3.setCellValue(quote.getRoute());
			Cell cell4 = row.createCell(3);
			cell4.setCellValue(quote.getTotalAmt());
			cell4.setCellStyle(getDollarFormatStyle());
			Cell cell5 = row.createCell(4);
			cell5.setCellValue(quote.getMiles());
			cell5.setCellStyle(getNumberFormatStyle());
			Cell cell6 = row.createCell(5);
			cell6.setCellValue(quote.getScac());
			Cell cell7 = row.createCell(6);
			cell7.setCellValue(quote.getCurrency());
		}
		
		fillRateDetailSheets(response.getQuotes());
		
		//Auto size all the columns
	    for (int x = 0; x < sheet.getRow(2).getPhysicalNumberOfCells(); x++) {
	    	sheet.autoSizeColumn(x);
	    }
	}
	
	private void fillRateDetailSheets(List<RateQuote> quotes) {
		int quoteNum = 1;
		for (RateQuote quote : quotes) {
			Sheet sheet = workbook.createSheet("Rate " + quoteNum++);
			createRateDetailHeaderRow(sheet);
			int chargeIndex = 1;
			for (Charge charge : quote.getCharges().getCharge()) {
				Row chargeRow = sheet.createRow(chargeIndex++);
				Cell cell0 = chargeRow.createCell(0);
				cell0.setCellValue(charge.getStopNum());
				cell0.setCellStyle(getNumberFormatStyle());
				Cell cell1 = chargeRow.createCell(1);
				cell1.setCellValue(charge.getCode());
				Cell cell2 = chargeRow.createCell(2);
				cell2.setCellValue(charge.getDesc());
				Cell cell3 = chargeRow.createCell(3);
				cell3.setCellValue(charge.getRate());
				cell3.setCellStyle(getNumberFormatOneDecStyle());
				Cell cell4 = chargeRow.createCell(4);
				cell4.setCellValue(charge.getUnits());
				cell4.setCellStyle(getNumberFormatStyle());
				Cell cell5 = chargeRow.createCell(5);
				cell5.setCellValue(charge.getAmt());
				cell5.setCellStyle(getDollarFormatStyle());
			}
			chargeIndex = chargeIndex + 2;
			createTariffAuthHeaderRows(sheet, chargeIndex);
			chargeIndex = chargeIndex + 2;
			for (TariffAuth tariffAuth : quote.getTariffAuth()) {
				Row tariffRow = sheet.createRow(chargeIndex++);
				Cell cell0 = tariffRow.createCell(0);
				cell0.setCellValue(tariffAuth.getType());
				Cell cell1 = tariffRow.createCell(1);
				cell1.setCellValue(sdf.format(tariffAuth.getEffDate()));
				Cell cell2 = tariffRow.createCell(2);
				cell2.setCellValue(sdf.format(tariffAuth.getExpDate()));
			}
			//Auto size all the columns
		    for (int x = 1; x < sheet.getRow(0).getPhysicalNumberOfCells(); x++) {
		    	sheet.autoSizeColumn(x);
		    }
		}
	}
	
	private void createTariffAuthHeaderRows(Sheet sheet, int index) {
		Row titleRow = sheet.createRow(index++);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("Tariff Authority");
		titleCell.setCellStyle(getBoldStyle());
		Row headerRow = sheet.createRow(index++);
		Cell cell0 = headerRow.createCell(0);
		cell0.setCellStyle(getBoldStyle());
		cell0.setCellValue("Type");
		Cell cell1 = headerRow.createCell(1);
		cell1.setCellStyle(getBoldStyle());
		cell1.setCellValue("Effective Date");
		Cell cell2 = headerRow.createCell(2);
		cell2.setCellStyle(getBoldStyle());
		cell2.setCellValue("Expiration Date");
	}
	
	private void createRateDetailHeaderRow(Sheet sheet) {
		Row headerRow = sheet.createRow(0);
		Cell cell0 = headerRow.createCell(0);
		cell0.setCellStyle(getBoldStyle());
		cell0.setCellValue("Stop Number");
		Cell cell1 = headerRow.createCell(1);
		cell1.setCellStyle(getBoldStyle());
		cell1.setCellValue("Code");
		Cell cell2 = headerRow.createCell(2);
		cell2.setCellStyle(getBoldStyle());
		cell2.setCellValue("Description");
		Cell cell3 = headerRow.createCell(3);
		cell3.setCellStyle(getBoldStyle());
		cell3.setCellValue("Rate");
		Cell cell4 = headerRow.createCell(4);
		cell4.setCellStyle(getBoldStyle());
		cell4.setCellValue("Units");
		Cell cell5 = headerRow.createCell(5);
		cell5.setCellStyle(getBoldStyle());
		cell5.setCellValue("Amount");
	}
	
	private CellStyle getBoldStyle() {
		Preconditions.checkNotNull(workbook, "Tried to get a Cell Style when the workbook has not been created yet.");
		if (boldStyle == null) {
			boldStyle = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			boldStyle.setFont(font);
		}
		return boldStyle;
	}
	
	private CellStyle getDollarFormatStyle() {
		Preconditions.checkNotNull(workbook, "Tried to get a Cell Style when the workbook has not been created yet.");
		if (dollarFormatStyle == null) {
			dollarFormatStyle = workbook.createCellStyle();
			DataFormat format = workbook.createDataFormat();
			dollarFormatStyle.setDataFormat(format.getFormat("$#,##0.00"));
		}
		return dollarFormatStyle;
	}
	
	private CellStyle getNumberFormatStyle() {
		Preconditions.checkNotNull(workbook, "Tried to get a Cell Style when the workbook has not been created yet.");
		if (numberFormatStyle == null) {
			numberFormatStyle = workbook.createCellStyle();
			DataFormat format = workbook.createDataFormat();
			numberFormatStyle.setDataFormat(format.getFormat("#,##0"));
		}
		return numberFormatStyle;
	}
	
	private CellStyle getNumberFormatOneDecStyle() {
		Preconditions.checkNotNull(workbook, "Tried to get a Cell Style when the workbook has not been created yet.");
		if (numberFormatOneDecStyle == null) {
			numberFormatOneDecStyle = workbook.createCellStyle();
			DataFormat format = workbook.createDataFormat();
			numberFormatOneDecStyle.setDataFormat(format.getFormat("#,##0.0"));
		}
		return numberFormatOneDecStyle;
	}

	/**
	 * Writes the workbook to an Outputstream
	 * @param os
	 * @throws IOException
	 */
	public void write(OutputStream os) throws IOException {
		workbook.write(os);
	}

	public String getFileName() {
		return fileName;
	}

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}
}

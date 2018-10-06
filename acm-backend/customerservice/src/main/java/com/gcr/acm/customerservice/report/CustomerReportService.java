package com.gcr.acm.customerservice.report;

import com.gcr.acm.customerservice.customer.CustomerInfo;
import com.gcr.acm.customerservice.customer.CustomerService;
import com.gcr.acm.customerservice.customer.SearchCustomerCriteria;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service for customer reports.
 *
 * @author Razvan Dani
 */
@Service
public class CustomerReportService {

	@Autowired
	private CustomerService customerService;

	/**
	 * Gets the customer report in excel format.
	 *
	 * @param searchCustomerCriteria	The SearchCustomerCriteria
	 * @return							The excel file encoded in Base64.
	 */
	@Transactional(readOnly = true)
	public String getCustomerReportInExcelFormat(SearchCustomerCriteria searchCustomerCriteria)
			throws IOException {
		List<CustomerInfo> customerInfoList = customerService.findCustomers(searchCustomerCriteria);

		return exportExcel(customerInfoList);
	}

	public String exportExcel(List<CustomerInfo> customerInfoList) throws IOException {

		// Creation workbook vide
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Creation d'une feuille vierge
		XSSFSheet sheet = workbook.createSheet("Participant");
		int indiceMap = 2;
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] { "CustomerInforst name", "last name" });

		for (CustomerInfo customerInfo : customerInfoList) {
			data.put(Integer.toString(indiceMap),
					new Object[] { customerInfo.getFirstName(), customerInfo.getLastName() });
			indiceMap++;
		}
		// Iteration sur la map data et ecriture dans dans la feuille excel
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
			}
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateToday = dateFormat.format(new Date());

		// Ecriture du fichier excel comme attachement
		// todo muie psd
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte[] outArray = outByteStream.toByteArray();

		return new String(Base64.getEncoder().encode(outArray), "UTF-8");

	}

}

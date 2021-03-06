package com.gcr.acm.customerservice.report;

import com.gcr.acm.customerservice.customer.CustomerDetailsEnums;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.gcr.acm.customerservice.customer.CustomerDetailsEnums.CommissionTypeEnum.*;
import static com.gcr.acm.customerservice.customer.CustomerDetailsEnums.ProductTypeEnum.ELECTRIC_ENERGY;
import static com.gcr.acm.customerservice.customer.CustomerDetailsEnums.ProductTypeEnum.NATURAL_GAS;

/**
 * Service for customer reports.
 *
 * @author Razvan Dani
 */
@Service public class CustomerReportService {

	@Autowired private CustomerService customerService;

	/**
	 * Gets the customer report in excel format.
	 *
	 * @param searchCustomerCriteria    The SearchCustomerCriteria
	 * @return							The excel file encoded in Base64.
	 */
	@Transactional(readOnly = true)
	public String getCustomerReportInExcelFormat(SearchCustomerCriteria searchCustomerCriteria)
			throws Exception {
		List<CustomerInfo> customerInfoList = customerService.findCustomers(searchCustomerCriteria);

		return exportExcel(customerInfoList);
	}

	public String exportExcel(List<CustomerInfo> customerInfoList)
			throws IOException {

		// creates an empty  workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//creates a blank sheet
		XSSFSheet sheet = workbook.createSheet("Participant");

		Map<String, Object[]> data = getCustomerInfoMap(customerInfoList);

		// iteration on the map data and writing in the excel sheet
		createRowsFromMapOfArrays(sheet, data);

		// Writing the excel file as an byte[]
		// todo muie psd
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte[] outArray = outByteStream.toByteArray();

		// apply base64 encoder
		return new String(Base64.getEncoder().encode(outArray), "UTF-8");

	}

	private Map<String, Object[]> getCustomerInfoMap(List<CustomerInfo> customerInfoList) {
		int indiceMap = 1;
		Map<String, Object[]> data = new LinkedHashMap<>();
		Object[] tableHeader = new Object[] { "Numar de contract", "Data Contractului", "Numele",
				"Enegie/gaz", "Tipul de contract", "Categoria", "Judet", "Localitate",
				"Telefon", "Data intrare in furnizare", "Agent", "Status" };
		data.put(Integer.toString(indiceMap), tableHeader);

		if (!customerInfoList.isEmpty()) {
			for (CustomerInfo customerInfo : customerInfoList) {
				indiceMap++;
				String productType = ELECTRIC_ENERGY.getTypeId().equals(customerInfo.getProductType()) ?
						ELECTRIC_ENERGY.getDescription() :
						NATURAL_GAS.getDescription();
				data.put(Integer.toString(indiceMap),
						new Object[] {
								customerInfo.getContractNumber(),
								customerInfo.getContractDate(), customerInfo.getLastName() + " " + customerInfo.getFirstName(), productType,
								getCommissionTypeByTypeId(customerInfo.getCommissionType()),
								CustomerDetailsEnums.CommissionSubcategoryEnum.getCommissionSubcategoryById(customerInfo.getCommissionSubcategory(), customerInfo.getProductType()),
								customerInfo.getCountyName(), customerInfo.getLocation(), customerInfo.getPhoneNumber(),
								customerInfo.getStartDeliveryDate(), customerInfo.getAgentName(), getStatusDescription(customerInfo.getStatus()) });
			}
		}

		return data;
	}

	private Object getStatusDescription(Integer status) {
		String statusDescription = null;

		if (status.equals(CustomerInfo.STATUS_ACTIVE)) {
			statusDescription = "Prelucrare";
		} else if (status.equals(CustomerInfo.STATUS_GAVE_UP)) {
			statusDescription = "Renuntat";
		} else if (status.equals(CustomerInfo.STATUS_WRONG)) {
			statusDescription = "Gresit";
		} else if (status.equals(CustomerInfo.STATUS_APPROVED)) {
			statusDescription = "Aprobat";
		}

		return statusDescription;
	}

	private void createRowsFromMapOfArrays(XSSFSheet sheet, Map<String, Object[]> data) {
		Set<String> keyset = data.keySet();
		int rownum = 0;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		int numberOfColumns = 0;

		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			numberOfColumns = objArr.length;
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);

				if (obj instanceof String) {
					cell.setCellValue((String) obj);

				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof Date) {
					cell.setCellValue(dateFormat.format((Date) obj));
				} else if (obj instanceof BigDecimal) {
					cell.setCellValue(((BigDecimal) obj).doubleValue());
				}

			}
		}

//		for (int i = 0; i < numberOfColumns; i++) {
//			sheet.autoSizeColumn(i);
//		}
	}
}

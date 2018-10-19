package com.gcr.acm.customerservice.customer;

public final class CustomerDetailsEnums {
	public enum ProductTypeEnum {
		ELECTRIC_ENERGY(1, "E.E"),
		NATURAL_GAS(2, "G.N");

		private final Integer typeId;

		private final String description;

		ProductTypeEnum(final Integer typeId, String description) {
			this.typeId = typeId;
			this.description = description;
		}

		public Integer getTypeId() {
			return this.typeId;
		}

		public String getDescription() {
			return description;
		}

	}

	public enum ContractTypeEnum {
		FIX(1, "FIX"),
		E_GO(2, "E-GO"),
		FLEX(3, "FLEX"),
		FLUX(4, "FLUX");

		private final Integer typeId;

		private final String description;

		ContractTypeEnum(final Integer typeId, String description) {
			this.typeId = typeId;
			this.description = description;
		}

		public Integer getTypeId() {
			return this.typeId;
		}

		public String getDescription() {
			return description;
		}

		public static String getContractTypeByTypeId(Integer typeId) {

			ContractTypeEnum contractTypeResult = null;

			for (int i = 0; i < values().length; i++) {
				ContractTypeEnum contractTypeResultEnum = values()[i];

				if (contractTypeResultEnum.getTypeId().equals(typeId)) {
					contractTypeResult = contractTypeResultEnum;
				}
			}
			return contractTypeResult != null ? contractTypeResult.getDescription() : null;
		}
	}

	public enum CommissionSubcategoryEnum {
		B1(1, "B1"),
		B2(2, "B2"),
		B3(3, "B3"),
		B4(4, "B4"),
		KW50(5, "50kw"),
		KW75(6, "75kw"),
		KW100(7, "100kw"),
		KW125(8, "125kw"),
		KW150(9, "150kw"),
		KW175(10, "175kw"),
		KW200(11, "200kw"),
		KW250(12, "250kw"),
		KW300(13, "300kw"),
		KW350(14, "350kw"),
		KW400(15, "400kw"),
		KW450(16, "450kw"),
		KW500(17, "500kw"),
		KW750(18, "750kw"),
		KW1000(19, "1000kw");

		private final Integer id;
		private final String description;

		CommissionSubcategoryEnum(final Integer typeId, String description) {
			this.id = typeId;
			this.description = description;
		}

		public Integer getId() {
			return this.id;
		}

		public String getDescription() {
			return description;
		}

		public static String getCommissionSubcategoryById(Integer id) {
			CommissionSubcategoryEnum commissionSubcategoryResult = null;

			for (int i = 0; i < values().length; i++) {
				CommissionSubcategoryEnum  commissionSubcategoryResultEnum = values()[i];

				if ( commissionSubcategoryResultEnum.getId().equals(id)) {
					commissionSubcategoryResult =  commissionSubcategoryResultEnum;
				}
			}

			return commissionSubcategoryResult != null ? commissionSubcategoryResult.getDescription() : null;
		}
	}


	public enum ActiveEnum {
		ACTIVE(true, "ACTIV"),
		DISCARD(false, "RENUNTAT");

		private final Boolean isActive;

		private final String description;

		ActiveEnum(final Boolean isActive, String description) {
			this.isActive = isActive;
			this.description = description;
		}

		public Boolean getIsActive() {
			return this.isActive;
		}

		public String getDescription() {
			return description;
		}

	}

}

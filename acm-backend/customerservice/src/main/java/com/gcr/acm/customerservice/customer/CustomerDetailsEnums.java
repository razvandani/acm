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
		B4(4, "B4");

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
			if (id <= 4) {
				CommissionSubcategoryEnum commissionSubcategoryResult = null;

				for (int i = 0; i < values().length; i++) {
					CommissionSubcategoryEnum commissionSubcategoryResultEnum = values()[i];

					if (commissionSubcategoryResultEnum.getId().equals(id)) {
						commissionSubcategoryResult = commissionSubcategoryResultEnum;
					}
				}

				return commissionSubcategoryResult != null ? commissionSubcategoryResult.getDescription() : null;
			} else {
				return id + "KW";
			}
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

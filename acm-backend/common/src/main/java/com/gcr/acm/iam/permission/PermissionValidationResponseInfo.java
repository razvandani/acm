package com.gcr.acm.iam.permission;

/**
 * Indicates if a permission is valid.
 *
 * @author Razvan Dani
 */
public class PermissionValidationResponseInfo {
    private Boolean isPermissionValid;
    private Integer permissionId;

    public Boolean getIsPermissionValid() {
        return isPermissionValid;
    }

    public void setIsPermissionValid(Boolean permissionValid) {
        isPermissionValid = permissionValid;
    }

    public Boolean getPermissionValid() {
        return isPermissionValid;
    }

    public void setPermissionValid(Boolean permissionValid) {
        isPermissionValid = permissionValid;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}

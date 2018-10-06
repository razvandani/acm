package com.gcr.acm.iam.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Permission controller.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/{permissionId}/", method = RequestMethod.GET)
    public PermissionInfo getPermission(@PathVariable Integer permissionId) {
        return permissionService.getPermission(permissionId);
    }

    @RequestMapping(value = "/checkPermissionForRequest", method = RequestMethod.POST)
    public PermissionValidationResponseInfo checkPermissionForRequest(@RequestBody RestRequestPermissionValidationInfo restRequestPermissionValidationInfo) {
        return permissionService.checkPermissionForRequest(restRequestPermissionValidationInfo);
    }
}

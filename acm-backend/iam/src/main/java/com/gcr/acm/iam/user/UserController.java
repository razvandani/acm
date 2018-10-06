package com.gcr.acm.iam.user;

import com.gcr.acm.common.utils.SuccessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.math.BigInteger;

/**
 * User controller.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/iam")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getUserByUserName/{username}/", method = RequestMethod.GET)
    public UserInfo getUserByUserName(@PathVariable String username) {
        return userService.getUserByUserName(username);
    }

    @RequestMapping(value = "/{userId}/", method = RequestMethod.GET)
    public UserInfo getUser(@PathVariable BigInteger userId) {
        return userService.getUser(userId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserInfo createUser(@RequestBody UserInfo userInfo, HttpServletRequest httpServletRequest) {
        return userService.saveUser(userInfo);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserInfo updateUser(@RequestBody UserInfo userInfo) {
        return userService.saveUser(userInfo);
    }

    @RequestMapping(value = "/checkUserAndPassword", method = RequestMethod.POST)
    public UserInfo checkUserAndPassword(@RequestBody UserInfo user) {
        return userService.checkUserAndPassword(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserInfo login(@RequestBody UserInfo user) {
        UserInfo userInfo = userService.login(user);

        if (userInfo.getErrorMessage() != null) {
            throw new ValidationException(userInfo.getErrorMessage());
        }

        return userInfo;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SuccessObject logout(@RequestBody UserInfo userPwd) {
        userService.logout(userPwd);

        return new SuccessObject();
    }

    @RequestMapping(value = "/unlockUser", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserInfo unlockUser(@RequestBody UserInfo userInfo) {
        return userService.unlockUser(userInfo);
    }

    @RequestMapping(value = "/role/{roleId}/", method = RequestMethod.GET)
    public RoleInfo getRole(@PathVariable Integer roleId) {
        return userService.getRole(roleId);
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public UserInfoListResponse findUsers(@RequestBody SearchUserCriteria searchUserCriteria) {
        return new UserInfoListResponse(userService.findUsers(searchUserCriteria));
    }

    /**
     * Deletes a user.
     *
     * @param userId    The user id
     * @return              SUCCESS
     */
    @RequestMapping(value = "/{userId}/", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SuccessObject deleteUser(@PathVariable BigInteger userId) {
        userService.deleteUser(userId);

        return new SuccessObject();
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SuccessObject changePassword(@RequestBody UserInfo userPwd) {
        userService.changePassword(userPwd);

        return new SuccessObject();
    }

    @RequestMapping(value = "/checkUserIdAndPassword", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SuccessObject checkUserIdAndPassword(@RequestBody UserInfo userPwd) {
        userService.checkUserIdAndPassword(userPwd);

        return new SuccessObject();
    }
}

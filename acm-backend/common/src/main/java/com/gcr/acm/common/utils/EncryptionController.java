package com.gcr.acm.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Controller for encryption.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/encryption")
public class EncryptionController {
    @Autowired
    private EncryptionUtil encryptionUtil;

    @RequestMapping(method = RequestMethod.POST)
    public String encrypt(@RequestBody String stringToEncrypt) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return encryptionUtil.encrypt(stringToEncrypt);
    }
}

package com.mangopay.core;

import com.mangopay.core.interfaces.IStorageStrategy;
import com.mangopay.core.APIs.ApiBase;
import com.mangopay.MangoPayApi;
import java.security.*;
import java.math.*;

/**
 * Authorization token manager. This class cannot be inherited.
 */
public final class AuthorizationTokenManager extends ApiBase {
    
    private IStorageStrategy _storageStrategy;
    
    /**
     * Instantiates new AuthorizationTokenManager object.
     * @param root Root/parent instance that holds the OAuthToken and Configuration instances.
     */
    public AuthorizationTokenManager(MangoPayApi root) {
        super(root);
        
        this.registerCustomStorageStrategy(new DefaultStorageStrategy());
    }
    
    /**
     * Gets the current authorization token.
     * <p>
     * In the very first call, this method creates a new token before returning.
     * If currently stored token is expired, this method creates a new one.
     * @return Valid OAuthToken instance.
     * @throws Exception
     */
    public OAuthToken getToken() throws Exception {
        OAuthToken token = _storageStrategy.get(GetEnvKey());
        
        if (token == null || token.IsExpired()) {
            storeToken(this._root.AuthenticationManager.createToken());
        }
        
        return _storageStrategy.get(GetEnvKey());
    }
    
    /**
     * Stores authorization token passed as an argument in the underlying storage strategy implementation.
     * @param token Token instance to be stored.
     */
    public void storeToken(OAuthToken token) {
        _storageStrategy.store(token, GetEnvKey());
    }
    
    /**
     * Registers custom storage strategy implementation.
     * <p>
     * By default, the <code>DefaultStorageStrategy</code> instance is used. 
     * There is no need to explicitly call this method until some more complex 
     * storage implementation is needed.
     * @param customStorageStrategy IStorageStrategy interface implementation.
     */
    public void registerCustomStorageStrategy(IStorageStrategy customStorageStrategy) {
        _storageStrategy = customStorageStrategy;
    }
    
    private String GetEnvKey() {
        
        String input = _root.Config.BaseUrl + _root.Config.ClientId;
        String md5 = "";
        
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1,m.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException nsaex) {
            /* Intentionally suppress exception here, as MD5 algorithm for sure exists. */
        }
        return md5;
    }
}

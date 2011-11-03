package com.diwan.loghati.alto.action;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

class MyAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
        // I haven't checked getRequestingScheme() here, since for NTLM
        // and Negotiate, the usrname and password are all the same.
        System.err.println("Feeding username and password for " + getRequestingScheme());
        return (new PasswordAuthentication("IQRAUser", "!QraUs3r".toCharArray()));
    }
}
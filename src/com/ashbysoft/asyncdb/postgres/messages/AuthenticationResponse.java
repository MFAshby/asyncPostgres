package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;

public class AuthenticationResponse extends PostgresMessage {
    public static final char IDENTIFIER = 'R';

    public static final int AUTHENTICATION_OK = 0;
    public static final int AUTHENTICATION_MD5 = 5;

    private static final int MD5_SALT_LENGTH = 4;

    private int authenticationResponse = -1;
    private byte[] md5Salt = null;

    public AuthenticationResponse() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new UnsupportedOperationException("Sending auth response not implemented");
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException("Sending auth response not implemented");
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        authenticationResponse = payloadBuffer.getInt();

        if (authenticationResponse == AUTHENTICATION_MD5) {
            md5Salt = new byte[MD5_SALT_LENGTH];
            payloadBuffer.get(md5Salt);
        }
    }

    public int getAuthenticationResponse() {
        return authenticationResponse;
    }

    public byte[] getMd5Salt() {
        return md5Salt;
    }
}

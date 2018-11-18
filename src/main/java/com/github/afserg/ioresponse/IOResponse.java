package com.github.afserg.ioresponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IOResponse {
    void respond(InputStream is, OutputStream os) throws IOException;
}

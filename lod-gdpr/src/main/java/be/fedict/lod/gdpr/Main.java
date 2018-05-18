/*
 * Copyright (c) 2018, Bart Hanssens <bart.hanssens@bosa.fgov.be>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.fedict.lod.gdpr;

import java.io.IOException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

/**
 *
 * @author Bart Hanssens
 */
public class Main {
	private final static String PATH_TOKEN = "/session/token";
	private final static String CSRF_HEADER = "X-CSRF-Token";
	private final static String MIME_TYPE = "application/hal+json";
	
	private static String token;
    
	private static Request getTokenRequest(String host) throws IOException {
		return Request.Get(host + PATH_TOKEN);
	}
	
	private static Request createRequest(String path) {
		return Request.Post(path)
							.addHeader(CSRF_HEADER, token)
							.addHeader(HttpHeaders.CONTENT_TYPE, MIME_TYPE);
	}
	
	private static Executor createExecutor(String host, String user, String pass) {
		return Executor.newInstance().auth(user, pass);
	}
	
	public static void main(String[] args) throws IOException {
		String host = "https://gdpr.rovin.be/";
		String user = "";
		String password = "";
		
		Executor exec = createExecutor(host, user, password);
		
        token = exec.execute(getTokenRequest(host)).returnContent().asString();
		
		createRequest("/");
	}
}

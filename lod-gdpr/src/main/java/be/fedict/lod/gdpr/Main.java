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

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

/**
 *
 * @author Bart Hanssens
 */
public class Main {
	private final static String FMT = "?_format=hal_json";
	private final static String PATH_TOKEN = "/session/token";
	private final static String CSRF_HEADER = "X-CSRF-Token";
	private final static String MIME_HAL = "application/hal+json";
	private final static ContentType TYPE_HAL = ContentType.create(MIME_HAL);
	
	private final static String PATH_DT_NODE = "/rest/type/node/datatreatment";
	
	private static String host;
	private static String token;
    
	private static Request getTokenRequest(String host) throws IOException {
		return Request.Get(host + PATH_TOKEN);
	}
	
	private static Request createRequest(String path) {
		return Request.Patch(host + path + FMT)
							.addHeader(CSRF_HEADER, token)
							.addHeader(HttpHeaders.CONTENT_TYPE, MIME_HAL)
							.addHeader(HttpHeaders.ACCEPT, MIME_HAL);
	}
	
	private static Executor createExecutor(String user, String pass) {
		return Executor.newInstance().auth(user, pass);
	}
	
	private static JsonObject createPage() { 
		return Json.createObjectBuilder()
			.add("_links", Json.createObjectBuilder()
								.add("type", Json.createObjectBuilder()
												.add("href", host + PATH_DT_NODE)))
			.add("path", Json.createArrayBuilder()
								.add(Json.createObjectBuilder()
										.add("alias", "newie")
										.add("lang", "en")))
			.add("type", Json.createObjectBuilder()
								.add("target_id", "datatreatment"))
			.add("title", Json.createArrayBuilder()
								.add(Json.createObjectBuilder()
									.add("value", "new title goes here again")
									.add("lang", "en")))
			.add("body", Json.createArrayBuilder()
								.add(Json.createObjectBuilder()
									.add("value", "yihaa new body goes here again we go ")
									.add("lang", "en")))
		.build();
	}

	public static void main(String[] args) throws IOException {
		host = "https://gdpr.rovin.be";
		String user = "rest";
		String password = "rest+yada";
		
		Executor exec = createExecutor(user, password);
		
        token = exec.execute(getTokenRequest(host)).returnContent().asString();
		
		Request req = createRequest("/");
		exec.execute(req);

		JsonObject obj = createPage();
		System.err.println(obj.toString());
		req = createRequest("/en/datatreatment/new-title-goes-here-again");
		req.bodyString(obj.toString(), TYPE_HAL);
		System.err.println(exec.execute(req).returnContent().asString());
	}
}

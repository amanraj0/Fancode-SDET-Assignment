package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Util {

	public static RequestSpecification req;
	
	public RequestSpecification requestSpecification() throws IOException {
		if(req==null) {
			PrintStream log = new PrintStream(new FileOutputStream("log.txt"));
			req = new RequestSpecBuilder()
						.setBaseUri(getGlobalProp("baseUrl"))
						.addFilter(RequestLoggingFilter.logRequestTo(log))
						.addFilter(ResponseLoggingFilter.logResponseTo(log))
						.build();
			return req;
		}
		return req;
	}
	
	public static String getGlobalProp(String key) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(Constants.GLOBAL_PROPERTIES);
		prop.load(fis);
		return prop.getProperty(key);
	}
	
	public JsonPath getJson(Response response) {
		String res = response.asString();
		JsonPath js = new JsonPath(res);
		return js;
	}
}

package tests;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import endpoints.Routes;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.Util;

public class Fancode extends Util{
	
	Response response;
	List<String> fancodeUserIdList;
	HashMap<Integer,Double> userTaskCompletion;
	SoftAssert softAssert;
	int numOfUsers;
	
	/*
	 * search for Fancode city user
	 * Fancode city user iff lat>=-40 && lat <=5 AND lang >=5 && lang <=100 
	 * 
	 * */
	@Test(priority=0)
	public void getFancodeUsers() throws IOException {
		response = given()
						.spec(requestSpecification())
						.when()
						.get(Routes.valueOf("UsersApi").getResource());
		
		JsonPath usersJson = getJson(response);
		
		
		fancodeUserIdList = new ArrayList<String>();
		numOfUsers = usersJson.getList("$").size();
		for(int i=0;i<numOfUsers;i++) {
			String lat = usersJson.getString("["+i+"].address.geo.lat");
			String lng = usersJson.getString("["+i+"].address.geo.lng");
			Float latLow = Float.parseFloat(getGlobalProp("latLow"));
			Float latHigh = Float.parseFloat(getGlobalProp("latHigh"));
			Float langLow = Float.parseFloat(getGlobalProp("langLow"));
			Float langHigh = Float.parseFloat(getGlobalProp("langHigh"));
			
			if(Float.parseFloat(lat)>=latLow  && Float.parseFloat(lat) <= latHigh && Float.parseFloat(lng)>=langLow && Float.parseFloat(lng) <= langHigh) {
				fancodeUserIdList.add(usersJson.getString("["+i+"].id"));
			}
		}
		
	}
	
	
	/*
	 * search for completion task % associated with fancode users 
	 * 
	 * */
	@Test(priority=1)
	public void getUsersTask() throws IOException {
		userTaskCompletion = new HashMap<>();
		response = given()
				.spec(requestSpecification())
				.when()
				.get(Routes.valueOf("TodosApi").getResource());
		
		JsonPath tasksJson = getJson(response);
	
		int numOfTask = tasksJson.getList("$").size();
		for(int i=0;i<fancodeUserIdList.size();i++) {
			Boolean calculatePercentage = false;
			int totalUserTaskCount = 0;
			int totalUserTaskCompleted = 0;
			int totalUserTaskInCompleted = 0;
			for(int j=0;j<numOfTask;j++) {
				int userId =  tasksJson.getInt("["+j+"].userId");
				if(Integer.parseInt(fancodeUserIdList.get(i))==userId) {
					calculatePercentage = true;
					Boolean taskStatus = tasksJson.getBoolean("["+j+"].completed");
					totalUserTaskCount++;
					 if (taskStatus) {
				            totalUserTaskCompleted++;
				     } else {
				    	 totalUserTaskInCompleted++;
				     }
				
				}
			}
			/*
			 * if calculatePercentage==false 
			 * then there is no task available for the user
			 * */
			if(calculatePercentage) {
				double completionPercentage = (double) totalUserTaskCompleted / totalUserTaskCount * 100;
				userTaskCompletion.put(Integer.parseInt(fancodeUserIdList.get(i)), Double.valueOf(completionPercentage));
			}else {
				userTaskCompletion.put(Integer.parseInt(fancodeUserIdList.get(i)), Double.valueOf("0.00"));
			}
		}
		for(Integer key : userTaskCompletion.keySet()) {
			System.out.println("UserID: "+key+"\tCompletion %:"+userTaskCompletion.get(key));
		}
	}
	
	
	/*
	 * Iterate over the userTaskCompletion Map and
	 *  verify if the task completion is > 50%
	 * 
	 * */
	@Test(priority=2)
	public void validateTaskCompletion() throws NumberFormatException, IOException {
		softAssert = new SoftAssert();
		
		for(Integer key : userTaskCompletion.keySet()) {
			Double taskCompletionPercentage = userTaskCompletion.get(key);
			int ExpectedCompletionPercentage = Integer.parseInt(getGlobalProp("completionPercentageCutoff"));
			softAssert.assertTrue(taskCompletionPercentage>ExpectedCompletionPercentage,"Task completion for User having UserId: "+key+" is less than 50%.");	
		}
		
		softAssert.assertAll();
	}

}

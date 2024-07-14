package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Listeners implements ITestListener{
	
	String methodName;
	ExtentReports report;
	ExtentTest extentTest;
	
	@Override
	public void onStart(ITestContext context) {
		System.out.println("--------STARTING TEST EXECUTION--------");
		report = Reports.generateReports();
	
	}

	@Override
	public void onTestStart(ITestResult result) {
		methodName = result.getName();
		extentTest = report.createTest(methodName);
		System.out.println("--------EXECUTION STARTED FOR : "+methodName+"--------");
		extentTest.log(Status.INFO,methodName+" execution started!!");
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		extentTest.log(Status.PASS,methodName);
		
	}

	@Override
	public void onTestFailure(ITestResult result) {
		extentTest.log(Status.FAIL, result.getThrowable().getMessage());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
	
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
	}

	@Override
	public void onTestFailedWithTimeout(ITestResult result) {
	
	}

	@Override
	public void onFinish(ITestContext context) {
		report.flush();
		File file = new File(Constants.REPORT_FOLDER);
		try {
			Desktop.getDesktop().browse(file.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("--------TEST EXECUTION COMPLETED--------");
	}


}

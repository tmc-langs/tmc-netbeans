package fi.helsinki.cs.tmc.data.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.helsinki.cs.tmc.data.Exercise.ValgrindStrategy;
import fi.helsinki.cs.tmc.data.TestCaseResult;
import fi.helsinki.cs.tmc.data.TestRunResult;
import fi.helsinki.cs.tmc.data.serialization.cresultparser.CTestResultParser;
import fi.helsinki.cs.tmc.langs.RunResult;
import fi.helsinki.cs.tmc.langs.TestResult;
import fi.helsinki.cs.tmc.testrunner.StackTraceSerializer;
import fi.helsinki.cs.tmc.testrunner.TestCase;
import fi.helsinki.cs.tmc.testrunner.TestCaseList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class TestResultParser {

  private static final Logger log = Logger.getLogger(TestResultParser.class.getName());

    public TestRunResult parseTestResults(File resultsFile) throws IOException {
        String resultsJson = FileUtils.readFileToString(resultsFile, "UTF-8");
        return parseTestResults(resultsJson);
    }

    public TestRunResult parseTestResults(String resultsJson) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(StackTraceElement.class, new StackTraceSerializer())
            .create();

        TestCaseList testCaseRecords = gson.fromJson(resultsJson, TestCaseList.class);
        if (testCaseRecords == null) {
            throw new IllegalArgumentException("Empty result from test runner");
        }

        List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();
        for (TestCase tc : testCaseRecords) {
            testCaseResults.add(TestCaseResult.fromTestCaseRecord(tc));
        }
        return new TestRunResult(testCaseResults);
    }

    public TestRunResult parseCTestResults(File resultsFile, File valgrindLog, ValgrindStrategy valgrindStrategy) throws Exception {
        // CTestResultParser could use refactoring. Duplicates parseTestResults and is kinda messy.
        log.log(INFO, "Starting to parse C test results.");
        CTestResultParser parser = new CTestResultParser(resultsFile, valgrindLog, valgrindStrategy);
        log.log(INFO, "C test results parser created.");
        parser.parseTestOutput();
        log.log(INFO, "C test results parsed.");
        TestRunResult results = new TestRunResult(parser.getTestCaseResults());
        log.log(INFO, "TestRunTesults created.");
        return results;
    }
    
    public TestRunResult parseLangsResults(RunResult runResult) {
        log.log(INFO, "Starting to parse TMC-Langs test results.");
        if (runResult.status == RunResult.Status.TESTS_FAILED || runResult.status == RunResult.Status.PASSED) {
            List<TestCaseResult> testCases = new ArrayList<TestCaseResult>();
            
            for (TestResult testResult : runResult.testResults) {
                testCases.add(TestCaseResult.fromTestResult(testResult));
            }
            
            return new TestRunResult(testCases);
        }
        return new TestRunResult(TestRunResult.Status.COMPILE_FAILED);
    }
}

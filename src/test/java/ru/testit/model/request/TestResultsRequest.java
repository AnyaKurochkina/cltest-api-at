package ru.testit.model.request;

import java.util.*;

public class TestResultsRequest
{
    private List<TestResultRequest> testResults;
    
    public TestResultsRequest() {
        this.testResults = new LinkedList<TestResultRequest>();
    }
    
    public List<TestResultRequest> getTestResults() {
        return this.testResults;
    }
}

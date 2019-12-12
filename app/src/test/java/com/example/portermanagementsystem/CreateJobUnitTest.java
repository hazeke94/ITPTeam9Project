package com.example.portermanagementsystem;



import com.example.portermanagementsystem.Controller.JobController;


import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CreateJobUnitTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
    JobController j;

    @Test
    public void caseIDValidator_CorrectPorter(){
        j = new JobController();
        Assert.assertTrue(j.validateCaseID("I190012123","porter"));
    }

    @Test
    public void caseIDValidator_EmptyPorter(){
        j = new JobController();
        Assert.assertTrue(j.validateCaseID(" ","porter"));
    }

    @Test
    public void caseIDValidator_WrongCharPorter(){
        j = new JobController();
        Assert.assertTrue(j.validateCaseID("U190012345","porter"));
    }

    @Test
    public void caseIDValidator_WrongInputPorter(){
        j = new JobController();
        Assert.assertTrue(j.validateCaseID("I191234","porter"));
    }

    @Test
    public void caseIDValidator_CorrectReceptionist(){
        j = new JobController();
        Assert.assertTrue(j.validateCaseID("I190012123","Receptionist"));
    }

    @Test
    public void caseIDValidator_WrongReceptionist(){
        j = new JobController();
        Assert.assertFalse(j.validateCaseID("","Receptionist"));
    }
//    @Test
//    public void locationToValidator_Correct(){
//        Assert.assertTrue(j.validateLocation("Business Office (BO)","MCD Dr Clinic"));
//    }
//
//    @Test
//    public void locationToValidator_Empty(){
//        Assert.assertFalse(j.validateLocation("", ""));
//    }

//    @Test
//    public void locationToValidator_SameLocation(){
//        Assert.assertFalse(j.validateLocation("MCD Dr Clinic", "MCD Dr Clinic"));
//    }

    @Test
    public void jobTypeValidator_Correct(){
        j = new JobController();
        Assert.assertTrue(j.validateJobType("Transport"));
    }

    @Test
    public void jobTypeValidator_Empty(){
        j = new JobController();
        Assert.assertFalse(j.validateJobType(""));
    }

    @Test
    public void jobUrgencyValidator_Correct(){
        j = new JobController();
        Assert.assertTrue(j.validateJobType("Urgent"));
    }

    @Test
    public void jobUrgencyValidator_Empty(){
        j = new JobController();
        Assert.assertFalse(j.validateJobType(""));
    }
}
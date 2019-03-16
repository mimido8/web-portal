package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.WebPortal;
import com.healthware.testing.TestDatabase;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void accountCreationFailsWhenUsernameAlreadyExists() throws Exception {
        //Insert the account to test with.
        int updated = TestDatabase.executeUpdate(
            "INSERT INTO accounts (id, username, email, password_hash, type) VALUES (",
            Utilities.getNextUID(), ", 'existing', '', '', '')");
        assertTrue(updated > 0);

        //try {
            //Try to create an account with the same name.
            Account.create("existing", "", "", Account.Type.PATIENT);

            //Fail if create() does not throw an exception.
            throw new Exception("Should NOT be able to create an account with a username that already exists");
        //} catch (Exception ex) {
            //Pass test.
        //}
    }

    @Test
    public void getByUsername() {
    }
}
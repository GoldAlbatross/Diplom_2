import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RequestUserData;
import org.example.RequestUserNewData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class TestChangeUserData {

    private final String newName;
    private final String newEmail;
    private final ApiService requests = new ApiService();
    private static final String NEW_EMAIL = "newemail@yandex.ru";
    private static final String NEW_NAME = "New Name";

    public TestChangeUserData(String newName, String newEmail) {
        this.newName = newName;
        this.newEmail = newEmail;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { NEW_EMAIL, NEW_NAME },
                { NEW_EMAIL, null },
                { null, NEW_NAME }
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
    }

    @Test
    public void changeUserDataWithAuthorization() {
        RequestUserData requestUserData = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        Response respChangeUserData = requests.createUser(requestUserData, requests.postForCreateUser);
        String accessToken = requests.getAccessTokenFromResponse(respChangeUserData);
        RequestUserNewData newUserData = new RequestUserNewData(newEmail, newName);
        requests.changeUserData(accessToken, requests.postForChangeUserData, newUserData)
                .then()
                .statusCode(Constants.SUCCESS_CODE)
                .body(Constants.SUCCESS, equalTo(true));
        requests.deleteUser(accessToken, requests.postForDeleteUser);
    }

    @Test
    public void changeUserDataWithoutAuthorization() {
        RequestUserNewData newUserData = new RequestUserNewData(newEmail, newName);
        requests.changeUserData("", requests.postForChangeUserData, newUserData)
                .then()
                .statusCode(Constants.UNAUTHORIZED_CODE)
                .body(Constants.SUCCESS, equalTo(false));
    }
}

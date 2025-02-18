import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RequestUserData;
import org.junit.Before;
import org.junit.Test;

public class TestCreateRequestUserData {

    private final ApiService requests = new ApiService();

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
    }

    @Test
    public void createCorrectUser() {
        RequestUserData requestUserData = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        Response resp = requests.createUser(requestUserData, requests.postForCreateUser);
        resp
                .then()
                .statusCode(Constants.SUCCESS_CODE);
        String accessToken = requests.getAccessTokenFromResponse(resp);
        requests.deleteUser(accessToken, requests.postForDeleteUser);
    }

    @Test
    public void createUserAlreadyExist() {
        RequestUserData requestUserData = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        RequestUserData requestUserDataAlreadyExist = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        Response respUser = requests.createUser(requestUserData, requests.postForCreateUser);
        Response respUserAlreadyExist = requests.createUser(requestUserDataAlreadyExist, requests.postForCreateUser);
        respUserAlreadyExist
                .then()
                .statusCode(Constants.FORBIDDEN_CODE);
        String accessToken = requests.getAccessTokenFromResponse(respUser);
        requests.deleteUser(accessToken, requests.postForDeleteUser);
    }

    @Test
    public void createUserWithoutPassword() {
        RequestUserData requestUserData = new RequestUserData(Constants.EMAIL, null, Constants.NAME);
        Response respUserWithoutPassword = requests.createUser(requestUserData, requests.postForCreateUser);
        respUserWithoutPassword
                .then()
                .statusCode(Constants.FORBIDDEN_CODE);
    }
}

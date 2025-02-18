import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RequestUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLoginRequestUserData {

    private final ApiService requests = new ApiService();
    private String accessToken;
    private String refreshToken;
    private RequestUserData requestUserData;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        this.requestUserData = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        Response respUser = requests.createUser(requestUserData, requests.postForCreateUser);
        this.accessToken = requests.getAccessTokenFromResponse(respUser);
        this.refreshToken = requests.getRefreshToken(respUser);
        requests.LogoutUser(refreshToken, requests.postForLogout);
    }

    @After
    public void tearDown() {
        requests.deleteUser(accessToken, requests.postForDeleteUser);
    }

    @Test
    public void authorizationWithCorrectUser() {
        requests.authorizationUser(requests.postForAuthorization, requestUserData.getEmail(), requestUserData.getPassword())
                .then()
                .statusCode(Constants.SUCCESS_CODE);
    }

    @Test
    public void authorizationWithIncorrectLogin() {
        requests.authorizationUser(requests.postForAuthorization, requestUserData.getEmail() + "2", requestUserData.getPassword())
                .then()
                .statusCode(Constants.UNAUTHORIZED_CODE);
    }
}

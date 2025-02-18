import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RequestUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class TestGetOrders {

    private final ApiService requests = new ApiService();
    private String accessToken;
    private List<String> ingredients;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        RequestUserData requestUserData = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        Response respCreateUser = requests.createUser(requestUserData, requests.postForCreateUser);
        this.accessToken = requests.getAccessTokenFromResponse(respCreateUser);
        ingredients = requests.getIngredients(requests.postForGetIngredients)
                        .jsonPath()
                        .get("data._id");
        requests.createOrder(requests.postForGetIngredients, ingredients, accessToken);
    }

    @After
    public void tearDown() {
        requests.deleteUser(accessToken, requests.postForDeleteUser);
    }

    @Test
    public void getOrdersWithAuthorization() {
        requests.getOrders(requests.postForCreateOrder, accessToken)
                .then()
                .statusCode(Constants.SUCCESS_CODE)
                .body(Constants.SUCCESS, equalTo(true));
    }

    @Test
    public void getOrdersWithoutAuthorization() {
        requests.getOrders(requests.postForGetOrders, "")
                .then()
                .statusCode(Constants.UNAUTHORIZED_CODE)
                .body(Constants.SUCCESS, equalTo(false));
    }
}

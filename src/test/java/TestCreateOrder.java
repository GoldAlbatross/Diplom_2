import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RequestUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class TestCreateOrder {

    private final ApiService requests = new ApiService();
    private List<String> ingredients;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        Response respIngredients = requests.getIngredients(requests.postForGetIngredients);
        ingredients = respIngredients.jsonPath()
                .get("data._id");

        RequestUserData requestUserData = new RequestUserData(Constants.EMAIL, Constants.PASSWORD, Constants.NAME);
        Response respCreateUser = requests.createUser(requestUserData, requests.postForCreateUser);
        this.accessToken = requests.getAccessTokenFromResponse(respCreateUser);
    }

    @After
    public void tearDown() {
        requests.deleteUser(accessToken, requests.postForDeleteUser);
    }

    @Test
    public void createOrderWithAuthorization() {
        requests.createOrder(requests.postForCreateOrder, ingredients, accessToken)
                .then()
                .statusCode(Constants.SUCCESS_CODE)
                .body(Constants.SUCCESS, equalTo(true));

    }

    @Test
    public void createOrderWithOutAuthorization() {
        requests.createOrder(requests.postForCreateOrder, ingredients, "")
                .then()
                .statusCode(Constants.SUCCESS_CODE)
                .body(Constants.SUCCESS, equalTo(true));
    }

    @Test
    public void createOrderWithoutIngredients() {
        List<String> emptyListIngredients = new ArrayList<>();
        requests.createOrder(requests.postForCreateOrder, emptyListIngredients, "")
                .then()
                .statusCode(Constants.BAD_REQUEST_CODE)
                .body(Constants.SUCCESS, equalTo(false));
    }

    @Test
    public void createOrderWithInvalidHash() {
        List<String> invalidHashIngredients = new ArrayList<>();
        invalidHashIngredients.add("invalid hash1");
        invalidHashIngredients.add("invalid hash2");
        requests.createOrder(requests.postForCreateOrder, invalidHashIngredients, "")
                .then()
                .statusCode(Constants.BAD_HASH_INGREDIENTS_CODE);
    }
}

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.RequestUserData;
import org.example.RequestUserNewData;

import java.util.*;

import static io.restassured.RestAssured.given;

public class ApiService {

    public String postForCreateUser = "/api/auth/register";
    public String postForDeleteUser = "/api/auth/user";
    public String postForAuthorization = "/api/auth/login";
    public String postForLogout = "/api/auth/logout";
    public String postForChangeUserData = "/api/auth/user";
    public String postForGetIngredients = "/api/ingredients";
    public String postForCreateOrder = "/api/orders";
    public String postForGetOrders = "/api/orders";

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String AUTHORIZATION = "Authorization";

    public String getAccessTokenFromResponse(Response response) {
        return response.jsonPath().get(ACCESS_TOKEN);
    }

    public String getRefreshToken(Response response) {
        return response.jsonPath().get("refreshToken");
    }

    @Step("Создание нового пользователя")
    public Response createUser(RequestUserData requestUserData, String post) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestUserData)
                .when()
                .post(post);

        return response;
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken, String post) {
         Response response = given()
                 .header(AUTHORIZATION, accessToken)
                 .when()
                 .delete(post);

         return response;
    }

    @Step("Авторизация")
    public Response authorizationUser(String post, String email, String password) {
        return given()
                .contentType(ContentType.JSON) // Указываем, что передаем JSON
                .body(Map.of(
                        "email", email,
                        "password", password
                ))
                .when()
                .post(post);
    }

    @Step("Выход из системы")
    public void LogoutUser(String refreshToken, String post) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"token\": \"" + refreshToken + "\"}")
                .when()
                .post(post);
    }

    @Step("Изменение данных пользователя")
    public Response changeUserData(String accessToken, String post, RequestUserNewData newUser) {
         return given()
                .contentType(ContentType.JSON)
                .header(AUTHORIZATION, accessToken)
                .body(newUser)
                .when()
                .patch(post);
    }

    @Step("Получение списка доступных ингредиентов")
    public Response getIngredients(String post) {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(post);
    }

    @Step("Создание заказа")
    public Response createOrder(String post, List<String> ingredients, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header(AUTHORIZATION, accessToken)
                .body(Collections.singletonMap("ingredients", ingredients))
                .when()
                .post(post);
    }

    @Step("Получение заказов пользователя")
    public Response getOrders(String post, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header(AUTHORIZATION, accessToken)
                .when()
                .get(post);
    }
}

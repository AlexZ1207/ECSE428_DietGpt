package com.dietapp.steps;

import com.dietapp.model.User;
import com.dietapp.service.UserService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserStepDefinitions {

    private UserService userService = new UserService();
    private User user;
    private String resultMessage;

    @Given("a new user provides the following information:")
    public void newUserProvidesInfo(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        user = new User();
        user.setName(data.get("Name"));
        user.setEmail(data.get("Email"));
        user.setAge(data.get("Age") != null ? Integer.parseInt(data.get("Age")) : 0);
        user.setWeight(data.get("Weight") != null ? Double.parseDouble(data.get("Weight")) : 0);
        user.setVegetarian(data.get("Vegetarian") != null && Boolean.parseBoolean(data.get("Vegetarian")));
        user.setGlutenFree(data.get("Gluten-Free") != null && Boolean.parseBoolean(data.get("Gluten-Free")));
    }

    @Given("an existing user already has the email {string}")
    public void existingUserWithEmail(String email) {
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID().toString());
        existingUser.setName("Existing User");
        existingUser.setEmail(email);
        existingUser.setAge(30);
        existingUser.setWeight(70);
        existingUser.setVegetarian(false);
        existingUser.setGlutenFree(false);

        userService.createUser(existingUser);
    }

    @When("the user submits the registration form")
    public void submitRegistrationForm() {
        resultMessage = userService.createUser(user);
    }

    @Then("a new user profile is created successfully")
    public void profileCreatedSuccessfully() {
        Assertions.assertEquals("Profile created successfully", resultMessage);
        Assertions.assertNotNull(user.getId());
    }

    @Then("the system displays {string}")
    public void systemDisplays(String message) {
        Assertions.assertEquals(message, resultMessage);
    }

    @Then("the user is assigned a unique user id")
    public void checkUniqueUserId() {
        List<User> users = userService.getUsers();
        Set<String> set = new HashSet<>();
        for (User user : users) {
            set.add(user.getId());
        }
        Assertions.assertEquals(users.size(), set.size());
    }

    @Then("the profile should not be created")
    public void noUser() {
        Assertions.assertEquals(0, userService.getUsers().size());
    }

    @Then("only one user in the system has the email {string}")
    public void uniqueUser(String email) {
        int count = 0;
        for (User user : userService.getUsers()) {
            if (user.getEmail().equals(email)) {
                count++;
            }
        }
        Assertions.assertEquals(1, count);
    }

    @Then("the dietary preferences are saved")
    public void dietaryPreferencesSaved() {
        User user = userService.getUserByEmail(this.user.getEmail());
        Assertions.assertEquals(this.user.isGlutenFree(), user.isGlutenFree());
        Assertions.assertEquals(this.user.isVegetarian(), user.isVegetarian());
    }
}

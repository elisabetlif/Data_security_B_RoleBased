package Implementation;


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRolesandPermissions {
    private static final String JSON_FILE = "src\\main\\java\\resource\\Roles.json";

    private static final String FAIL_MESSAGE1 = "Failed to get user role";

    private static final List<String> FAIL_MESSAGE2 = new ArrayList<>(); // Return an empty list on failure

    public String getRole(String username) {
        try {
            // Parsing the Roles.json file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(JSON_FILE));
            JsonNode rolesNode = rootNode.path("Roles");

            for (JsonNode roleNode : rolesNode) {
                String role = roleNode.path("role").asText();
                JsonNode usersNode = roleNode.path("users");

                for (JsonNode userNode : usersNode) {
                    if (userNode.asText().equalsIgnoreCase(username)) {
                        return role;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
            e.printStackTrace();
        }
        return FAIL_MESSAGE1;
    }
public List<String> getPermissions(String username) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(JSON_FILE));
        JsonNode rolesNode = rootNode.path("Roles");

        for (JsonNode roleNode : rolesNode) {
            JsonNode usersNode = roleNode.path("users");
            JsonNode permissionsNode = roleNode.path("permissions");

            for (JsonNode userNode : usersNode) {
                if (userNode.asText().equalsIgnoreCase(username)) {
                    List<String> permissions = new ArrayList<>();
                    for (JsonNode permissionNode : permissionsNode) {
                        permissions.add(permissionNode.asText());
                    }
                    return permissions;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading the JSON file: " + e.getMessage());
        e.printStackTrace();
    }
    return FAIL_MESSAGE2;
}

} 








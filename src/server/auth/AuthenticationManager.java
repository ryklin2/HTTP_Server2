package server.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import server.err.HTTPException;

public class AuthenticationManager {
    private final String documentRoot;

    public AuthenticationManager(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    public AuthenticationResponse checkAuth(String path, String authHeader) {
        File passwordFile = findPasswordFile(path);
        if (passwordFile == null) {
            return AuthenticationResponse.NO_AUTH_NEEDED;
        }

        HashMap<String, String> credentials;
        try {
            credentials = readPasswordFile(passwordFile);
        } catch (HTTPException e) {
            return AuthenticationResponse.UNAUTHORIZED;
        }
        if (authHeader == null || authHeader.isEmpty()) {
            return AuthenticationResponse.UNAUTHORIZED;
        }
        try {
            if (validateCredentials(authHeader, credentials)) {
                return AuthenticationResponse.AUTHORIZED;
            } else {
                return AuthenticationResponse.FORBIDDEN;
            }
        } catch (HTTPException e) {
            return AuthenticationResponse.FORBIDDEN;
        }
    }

    private File findPasswordFile(String path) {
        File passwordFileDir = new File(documentRoot, path).getParentFile();
        File passwordFile = new File(passwordFileDir, ".password");
        return (passwordFile.exists() && passwordFile.isFile()) ? passwordFile : null;
    }

    private HashMap<String, String> readPasswordFile(File passwordFile) throws HTTPException {
        HashMap<String, String> credentials = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(":");
                if (parts.length == 2) {
                    credentials.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            throw new HTTPException(500, "Error reading password file: " + passwordFile.getAbsolutePath());
        }
        return credentials;
    }

    private boolean validateCredentials(String authHeader, HashMap<String, String> credentials) throws HTTPException {
        if (!authHeader.startsWith("Basic ")) return false;
        String encodedCredentials = authHeader.substring(6);
        String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
        String[] clientCredentials = decodedCredentials.split(":");
        if (clientCredentials.length != 2) {
            throw new HTTPException(400, "Invalid credentials");
        }
        String username = clientCredentials[0].trim();
        String password = clientCredentials[1].trim();
        String serverPass = credentials.get(username);
        return serverPass != null && serverPass.equals(password);
    }
}
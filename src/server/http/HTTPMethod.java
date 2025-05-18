package server.http;

public enum HTTPMethod {
    GET, HEAD, PUT, DELETE;

    public static boolean isValid(String method) {
        if (method == null || method.trim().isEmpty()) return false;
        try {
            HTTPMethod.valueOf(method.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
public interface DatabaseHelperInterface {
    User readUser(String username);
    boolean writeUser(User user);

    // TODO: Add read/write messages and photos
}
